package com.android.test.starwars.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RemoteKeys(@PrimaryKey val characterName: String, val prevKey: Int?, val nextKey: Int?)
