package com.android.test.starwars.data.local

import androidx.room.TypeConverter
import com.android.test.starwars.data.model.Character
import com.android.test.starwars.data.model.Starships
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class Converters {

    @TypeConverter
    fun fromStrings(starships: List<String>): String {
        val listType = Types.newParameterizedType(List::class.java, String::class.java)
        val adapter: JsonAdapter<List<String>> = Moshi.Builder().build().adapter(listType)
        return adapter.toJson(starships)
    }

    @TypeConverter
    fun toStrings(starships: String): List<String> {
        val listType = Types.newParameterizedType(List::class.java, String::class.java)
        val adapter: JsonAdapter<List<String>> = Moshi.Builder().build().adapter(listType)
        return adapter.fromJson(starships)!!
    }

    @TypeConverter
    fun fromCharacter(character: Character): String {
        return Moshi.Builder().build().adapter(Character::class.java).toJson(character)
    }

    @TypeConverter
    fun toCharacter(string: String): Character {
        return Moshi.Builder().build().adapter(Character::class.java).fromJson(string)!!
    }

    @TypeConverter
    fun fromStarships(starships: List<Starships>): String {
        val listType = Types.newParameterizedType(List::class.java, Starships::class.java)
        val adapter: JsonAdapter<List<Starships>> = Moshi.Builder().build().adapter(listType)
        return adapter.toJson(starships)
    }

    @TypeConverter
    fun toStarships(starships: String): List<Starships> {
        val listType = Types.newParameterizedType(List::class.java, Starships::class.java)
        val adapter: JsonAdapter<List<Starships>> = Moshi.Builder().build().adapter(listType)
        return adapter.fromJson(starships)!!
    }
}