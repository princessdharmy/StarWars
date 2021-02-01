package com.android.test.starwars.utils

import androidx.lifecycle.liveData
import com.android.test.starwars.data.model.*
import com.android.test.starwars.data.networkresource.NetworkStatus
import kotlinx.coroutines.Dispatchers

object TestUtils {

    private val characterStarships = Starships(0, "X-wing", 1)
    private val character = Character(
        1, "Luke Skywalker", "male", "173",
        emptyList()
    )

    fun createCharacterWithStarships() = CharacterWithStarships(character, listOf(characterStarships))

    fun createCharacterWithStarshipsLiveData(data: NetworkStatus<CharacterWithStarships>) =
        liveData(Dispatchers.IO) {
        emit(data)
    }

    fun createRemoteKeys() = RemoteKeys("Luke Skywalker", null, 2)
}