package com.android.test.starwars.data.model

import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parcelize

data class CharacterResponse(
        val results: List<Character> = ArrayList(),
        val next: String? = null,
        val previous: String? = null,
        val count: Int = 0
)

@Parcelize
@Entity(tableName = "character")
data class Character(
        @PrimaryKey(autoGenerate = true)
        val id: Int = 0,
        val name: String? = null,
        val gender: String? = null,
        val height: String? = null,
        val starships: List<String> = emptyList()
) : Parcelable

data class StarshipsResponse(val name: String? = null)

@Parcelize
@Entity
data class Starships(
        @PrimaryKey(autoGenerate = true)
        val id: Int = 0,
        val name: String? = null,
        @ColumnInfo(name = "character_id")
        val characterId: Int? = null
) : Parcelable

@Parcelize
data class CharacterWithStarships(
        @Embedded
        val character: Character,
        @Relation(parentColumn = "id", entityColumn = "character_id")
        val starShips: List<Starships>
) : Parcelable