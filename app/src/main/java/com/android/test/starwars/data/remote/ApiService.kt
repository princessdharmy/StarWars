package com.android.test.starwars.data.remote

import com.android.test.starwars.data.model.CharacterResponse
import com.android.test.starwars.data.model.StarshipsResponse
import com.android.test.starwars.data.networkresource.ApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {

    @GET("people/")
    suspend fun getAllCharacters(@Query("page") page: Int) : Response<CharacterResponse>

    @GET
    suspend fun getStarships(@Url string: String) : Response<StarshipsResponse>
}