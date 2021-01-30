package com.android.test.starwars.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.ExperimentalPagingApi
import com.android.test.starwars.utils.MainCoroutineRule
import com.android.test.starwars.utils.TestUtils
import com.android.test.starwars.data.coroutines.DispatcherProvider
import com.android.test.starwars.data.local.StarWarsDatabase
import com.android.test.starwars.data.local.dao.CharacterDao
import com.android.test.starwars.data.local.dao.RemoteKeysDao
import com.android.test.starwars.data.remote.ApiService
import com.android.test.starwars.data.repository.CharacterRepository
import com.android.test.starwars.data.repository.CharacterRepositoryImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

/**
 * Unit tests for the implementation of [CharacterRepository]
 */
@ExperimentalPagingApi
@ExperimentalCoroutinesApi
class CharacterRepositoryTest {

    @get:Rule
    val mainCoroutinesRule = MainCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var dispatcherProvider: DispatcherProvider
    private lateinit var repository: CharacterRepositoryImpl
    private lateinit var starWarsDatabase: StarWarsDatabase
    private lateinit var apiService: ApiService

    @Before
    fun setUp() {
        starWarsDatabase = mock(StarWarsDatabase::class.java)
        apiService = mock(ApiService::class.java)
        dispatcherProvider = mock(DispatcherProvider::class.java)
        repository = CharacterRepositoryImpl(dispatcherProvider, apiService, starWarsDatabase)
    }

    @Test
    fun `get character with starships emits flow character`() = runBlocking {

        //Stub out database to return a mock dao.
        val dao = mock(CharacterDao::class.java)
        `when`(starWarsDatabase.characterDao()).thenReturn(dao)

        //Stub out dao to return a CharacterWithStarships
        val characterWithStarships = TestUtils.createCharacterWithStarships()
        `when`(dao.getCharacterWithStarshipsById (1)).thenReturn(characterWithStarships)

        //Method under test.
        val flow = repository.getCharacterWithStarshipsDb(1)

        //Verify data in the result.
        flow.collect {
            assertThat(it, `is`(characterWithStarships))
        }
    }

    @Test
    fun `get remote keys emits flow data`() = runBlocking {

        //Stub out database to return a mock dao.
        val dao = mock(RemoteKeysDao::class.java)
        `when`(starWarsDatabase.remoteKeysDao()).thenReturn(dao)

        //Stub out dao to return a RemoteKeys
        val remoteKeys = TestUtils.createRemoteKeys()
        `when`(dao.getRemoteKeys ("Luke Skywalker")).thenReturn(remoteKeys)

        //Method under test.
        val flow = repository.getRemoteKeys("Luke Skywalker")

        //Verify data in the result.
        flow.collect {
            assertThat(it, `is`(remoteKeys))
        }
    }
}