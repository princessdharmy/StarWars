package com.android.test.starwars.api

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.android.test.starwars.utils.MainCoroutineRule
import com.android.test.starwars.data.remote.ApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@ExperimentalCoroutinesApi
class ApiServiceTest : ApiAbstract<ApiService>() {

    @get: Rule
    val coroutinesRule = MainCoroutineRule()

    @get: Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var service: ApiService

    @Before
    fun initService() {
        service = createService(ApiService::class.java)
    }

    @Throws(IOException::class)
    @Test
    fun getCharactersFromNetwork() = runBlocking {
        enqueueResponse("StarWarsResponse.json")
        val response = service.getAllCharacters(1)
        val responseBody = requireNotNull(response.body())
        mockWebServer.takeRequest()

        val loaded = responseBody.results[0]
        assertThat(responseBody.count, `is`(82))
        assertThat(responseBody.next, `is`("http://swapi.dev/api/people/?page=2"))
        assertThat(loaded.name, `is`("Luke Skywalker"))
        assertThat(loaded.gender, `is`("male"))
        assertThat(loaded.height, `is`("172"))
        assertThat(loaded.starships, `is`(listOf( "http://swapi.dev/api/starships/12/",
                "http://swapi.dev/api/starships/22/")))
    }
}