package com.android.test.starwars.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.android.test.starwars.data.model.Starships

@Dao
interface CharacterDetailsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveStarShips(starShips: Starships)

}