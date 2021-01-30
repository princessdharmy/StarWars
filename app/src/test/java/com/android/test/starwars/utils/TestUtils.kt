package com.android.test.starwars.utils

import com.android.test.starwars.data.model.*

object TestUtils {

    private val characterStarships = Starships(0, "X-wing", 1)
    private val character = Character(1, "Luke Skywalker", "male", "173",
        emptyList())

    fun createCharacterWithStarships() = CharacterWithStarships(character, listOf(characterStarships))

    fun createStarship() = Starships(0, "X-wing", 1)

    fun createStarshipResponse() = StarshipsResponse("name")

    fun createRemoteKeys() = RemoteKeys("Luke Skywalker", null, 2)
}