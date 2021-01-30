package com.android.test.starwars.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.android.test.starwars.data.model.CharacterWithStarships
import com.android.test.starwars.data.model.RemoteKeys
import com.android.test.starwars.data.networkresource.NetworkStatus
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {

    suspend fun getCharacterWithStarshipsFlowDb(): Flow<PagingData<CharacterWithStarships>>

    suspend fun getStarships(urlString: List<String>, characterId: Int):
            LiveData<NetworkStatus<CharacterWithStarships>>

    suspend fun getCharacterWithStarshipsDb(id: Int): Flow<CharacterWithStarships?>

    suspend fun getRemoteKeys(name: String) : Flow<RemoteKeys?>
}