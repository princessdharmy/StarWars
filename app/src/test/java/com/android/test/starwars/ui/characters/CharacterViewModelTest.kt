package com.android.test.starwars.ui.characters

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.paging.ExperimentalPagingApi
import com.android.test.starwars.data.model.CharacterWithStarships
import com.android.test.starwars.data.networkresource.NetworkStatus
import com.android.test.starwars.data.repository.CharacterRepository
import com.android.test.starwars.utils.*
import com.android.test.starwars.utils.TestUtils.createCharacterWithStarships
import com.android.test.starwars.utils.TestUtils.createCharacterWithStarshipsLiveData
import kotlinx.coroutines.*
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.mockito.Mockito.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class CharacterViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var characterViewModel: CharacterViewModel

    @Mock
    lateinit var characterRepository: CharacterRepository

    @Mock
    private lateinit var characterObserver: Observer<NetworkStatus<CharacterWithStarships>>

    @ObsoleteCoroutinesApi
    private val threadContext = newSingleThreadContext("UI thread")

    @ObsoleteCoroutinesApi
    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        Dispatchers.setMain(threadContext)
        characterViewModel = CharacterViewModel(characterRepository)
    }

    @Test
    fun `get character with starships returns error`() {
        runBlocking {

            // Stub the [NetworkStatus] response
            val errorMessage = NetworkStatus.Error<CharacterWithStarships>(
                "Could not reach the server", null
            )
            doReturn(createCharacterWithStarshipsLiveData(errorMessage))
                .`when`(characterRepository).getStarships(listOf("/url/12"), 1)

            // Add lifecycle observers to LiveData
            characterViewModel.starshipsLiveData.observeForever(characterObserver)

            // Make the viewModel call
            characterViewModel.fetchStarships(listOf("/url/12"), 1)

            // Add a latency
            Thread.sleep(100)

            // Verify assertions
            verify(characterObserver).onChanged(errorMessage)

            // Remove observers
            characterViewModel.starshipsLiveData.removeObserver(characterObserver)
        }
    }

    @Test
    fun `get character with starships returns data`() {
        runBlocking {

            // Stub the [NetworkStatus] response
            val data = NetworkStatus.Success(createCharacterWithStarships())
            doReturn(createCharacterWithStarshipsLiveData(data))
                .`when`(characterRepository).getStarships(listOf("/url/12"), 1)

            // Add lifecycle observers to LiveData
            characterViewModel.starshipsLiveData.observeForever(characterObserver)

            // Make the viewModel call
            characterViewModel.fetchStarships(listOf("/url/12"), 1)

            // Add a latency
            Thread.sleep(100)

            // Verify assertions
            verify(characterObserver).onChanged(data)

            // Remove observers
            characterViewModel.starshipsLiveData.removeObserver(characterObserver)
        }
    }
}