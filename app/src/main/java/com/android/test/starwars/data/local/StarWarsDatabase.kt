package com.android.test.starwars.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.android.test.starwars.data.local.dao.CharacterDao
import com.android.test.starwars.data.local.dao.CharacterDetailsDao
import com.android.test.starwars.data.local.dao.RemoteKeysDao
import com.android.test.starwars.data.model.Character
import com.android.test.starwars.data.model.RemoteKeys
import com.android.test.starwars.data.model.Starships

@Database(entities = [Character::class, Starships::class, RemoteKeys::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class StarWarsDatabase : RoomDatabase() {

    abstract fun characterDao(): CharacterDao
    abstract fun characterDetailsDao(): CharacterDetailsDao
    abstract fun remoteKeysDao(): RemoteKeysDao

}