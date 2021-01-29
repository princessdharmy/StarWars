package com.android.test.starwars.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.android.test.starwars.data.local.StarWarsDatabase
import com.android.test.starwars.data.model.RemoteKeys
import com.android.test.starwars.data.model.CharacterWithStarships
import com.android.test.starwars.data.remote.ApiService
import retrofit2.HttpException
import java.io.IOException
import java.io.InvalidObjectException
import javax.inject.Inject

@ExperimentalPagingApi
class CharacterMediator @Inject constructor(
    private val apiService: ApiService,
    private val starWarsDatabase: StarWarsDatabase
) : RemoteMediator<Int, CharacterWithStarships>() {

    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, CharacterWithStarships>
    ): MediatorResult {

        val page = when (val pageKeyData = getKeyPageData(loadType, state)) {
            is MediatorResult.Success -> {
                return pageKeyData
            }
            else -> {
                pageKeyData as Int
            }
        }

        try {
            val response = apiService.getAllCharacters(page)
            val isEndOfList =
                !response.isSuccessful || response.body() == null || response.body()?.results?.isEmpty() == true
            starWarsDatabase.withTransaction {
                // clear all tables in the database
                if (loadType == LoadType.REFRESH) {
                    starWarsDatabase.remoteKeysDao().clearRemoteKeys()
                    starWarsDatabase.characterDao().clearAllCharacters()
                }
                val prevKey = if (page == DEFAULT_PAGE_INDEX) null else page - 1
                val nextKey = if (isEndOfList) null else page + 1
                val keys = response.body()?.results?.map {
                    RemoteKeys(characterName = it.name!!, prevKey = prevKey, nextKey = nextKey)
                }
                if (keys != null)
                    starWarsDatabase.remoteKeysDao().insertAllRemoteKeys(keys)
                if (response.body() != null && response.body()?.results?.isNotEmpty() == true)
                    starWarsDatabase.characterDao().insertAllCharacters(response.body()!!.results)
            }
            return MediatorResult.Success(endOfPaginationReached = isEndOfList)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    /**
     * this returns the page key or the final end of list success result
     */
    private suspend fun getKeyPageData(
        loadType: LoadType,
        state: PagingState<Int, CharacterWithStarships>
    ): Any? {
        return when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getClosestRemoteKey(state)
                remoteKeys?.nextKey?.minus(1) ?: DEFAULT_PAGE_INDEX
            }
            LoadType.APPEND -> {
                val remoteKeys = getLastRemoteKey(state)
                    ?: throw InvalidObjectException("Remote key should not be null for $loadType")
                remoteKeys.nextKey
            }
            LoadType.PREPEND -> {
                val remoteKeys = getFirstRemoteKey(state)
                    ?: throw InvalidObjectException("Invalid state, key should not be null")
                //end of list condition reached
                remoteKeys.prevKey ?: return MediatorResult.Success(endOfPaginationReached = true)
                remoteKeys.prevKey
            }
        }
    }

    /**
     * get the last remote key inserted which had the data
     */
    private suspend fun getLastRemoteKey(state: PagingState<Int, CharacterWithStarships>): RemoteKeys? {
        return state.pages
            .lastOrNull { it.data.isNotEmpty() }
            ?.data?.lastOrNull()
            ?.let { character ->
                starWarsDatabase.remoteKeysDao().getRemoteKeys(character.character.name!!)
            }
    }

    /**
     * get the first remote key inserted which had the data
     */
    private suspend fun getFirstRemoteKey(state: PagingState<Int, CharacterWithStarships>): RemoteKeys? {
        return state.pages
            .firstOrNull { it.data.isNotEmpty() }
            ?.data?.firstOrNull()
            ?.let { character ->
                starWarsDatabase.remoteKeysDao().getRemoteKeys(character.character.name!!)
            }
    }

    /**
     * get the closest remote key inserted which had the data
     */
    private suspend fun getClosestRemoteKey(state: PagingState<Int, CharacterWithStarships>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.character?.name?.let { characterName ->
                starWarsDatabase.remoteKeysDao().getRemoteKeys(characterName)
            }
        }
    }

    companion object {
        const val DEFAULT_PAGE_INDEX = 1
    }
}