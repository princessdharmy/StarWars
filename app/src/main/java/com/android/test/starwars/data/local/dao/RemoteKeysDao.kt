package com.android.test.starwars.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.android.test.starwars.data.model.RemoteKeys

@Dao
interface RemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRemoteKeys(remoteKey: List<RemoteKeys>)

    @Query("select * from remotekeys where characterName = :name")
    suspend fun getRemoteKeys(name: String): RemoteKeys?

    @Query("delete from remotekeys")
    fun clearRemoteKeys()
}