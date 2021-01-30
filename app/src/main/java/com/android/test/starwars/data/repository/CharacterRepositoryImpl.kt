package com.android.test.starwars.data.repository

import androidx.lifecycle.liveData
import androidx.paging.*
import com.android.test.starwars.data.coroutines.DispatcherProvider
import com.android.test.starwars.data.local.StarWarsDatabase
import com.android.test.starwars.data.model.CharacterWithStarships
import com.android.test.starwars.data.model.RemoteKeys
import com.android.test.starwars.data.model.Starships
import com.android.test.starwars.data.networkresource.NetworkStatus
import com.android.test.starwars.data.networkresource.safeApiCall
import com.android.test.starwars.data.paging.CharacterMediator
import com.android.test.starwars.data.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ExperimentalPagingApi
class CharacterRepositoryImpl @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val apiService: ApiService,
    private val starWarsDatabase: StarWarsDatabase?
) : CharacterRepository {

    override suspend fun getCharacterWithStarshipsFlowDb(): Flow<PagingData<CharacterWithStarships>> =
        withContext(dispatcherProvider.io()) {
            if (starWarsDatabase == null) throw IllegalStateException("Database is not initialized")

            val pagingSourceFactory =
                { starWarsDatabase.characterDao().getAllCharacterWithStarShips() }

            Pager(
                config = getDefaultPageConfig(),
                pagingSourceFactory = pagingSourceFactory,
                remoteMediator = CharacterMediator(apiService, starWarsDatabase)
            ).flow
        }

    /**
     * This is to define the page size in the [PagingConfig]
     */
    private fun getDefaultPageConfig(): PagingConfig {
        return PagingConfig(pageSize = DEFAULT_PAGE_SIZE, enablePlaceholders = false)
    }

    /**
     * To make the respective starship calls
     */
    override suspend fun getStarships(urlString: List<String>, characterId: Int) = liveData {
        emit(NetworkStatus.Loading())
        withContext(dispatcherProvider.io()) {
            urlString.forEach {
                when (val response = safeApiCall { apiService.getStarships(it) }) {
                    is NetworkStatus.Success -> {
                        starWarsDatabase?.characterDetailsDao()?.saveStarShips(
                            Starships(name = response.data?.name, characterId = characterId)
                        )
                        val characterWithStarships = starWarsDatabase?.characterDao()
                            ?.getCharacterWithStarshipsById(characterId)
                        emit(NetworkStatus.Success(characterWithStarships))
                    }
                    is NetworkStatus.Error -> {
                        val characterWithStarships = starWarsDatabase?.characterDao()
                            ?.getCharacterWithStarshipsById(characterId)
                        emit(NetworkStatus.Error(response.message, characterWithStarships))
                    }
                }
            }
        }
    }

    /**
     * For unit testing
     * Never called or used in the view
     */
    override suspend fun getCharacterWithStarshipsDb(id: Int) = flow {
        val data = starWarsDatabase?.characterDao()?.getCharacterWithStarshipsById(id)
        emit(data)
    }.flowOn(Dispatchers.Default)


    override suspend fun getRemoteKeys(name: String) = flow {
        val keys = starWarsDatabase?.remoteKeysDao()?.getRemoteKeys(name)
        emit(keys)
    }.flowOn(Dispatchers.Default)

    companion object {
        const val DEFAULT_PAGE_SIZE = 20
    }

}