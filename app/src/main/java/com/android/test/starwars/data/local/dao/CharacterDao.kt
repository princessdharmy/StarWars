package com.android.test.starwars.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.android.test.starwars.data.model.Character
import com.android.test.starwars.data.model.CharacterWithStarships

@Dao
interface CharacterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCharacters(characterList: List<Character>)

    @Transaction
    @Query("select * from character")
    fun getAllCharacterWithStarShips(): PagingSource<Int, CharacterWithStarships>

    @Query("select * from character where id = :id")
    fun getCharacterWithStarshipsById(id: Int) : CharacterWithStarships

    @Query("delete from character")
    fun clearAllCharacters()
}