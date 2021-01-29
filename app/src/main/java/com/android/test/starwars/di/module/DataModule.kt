package com.android.test.starwars.di.module

import androidx.paging.ExperimentalPagingApi
import com.android.test.starwars.data.coroutines.DefaultDispatcherProvider
import com.android.test.starwars.data.coroutines.DispatcherProvider
import com.android.test.starwars.data.local.StarWarsDatabase
import com.android.test.starwars.data.remote.ApiService
import com.android.test.starwars.data.repository.CharacterRepository
import com.android.test.starwars.data.repository.CharacterRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@ExperimentalPagingApi
@InstallIn(SingletonComponent::class)
@Module
object DataModule {

    /// Provide DispatcherProvider ///

    @Provides
    @Singleton
    internal fun provideDispatcherProvider(): DispatcherProvider = DefaultDispatcherProvider()


    /// Provide repositories ///

    @Singleton
    @Provides
    fun provideCompetitionsRepository(
        dispatcherProvider: DispatcherProvider,
        apiService: ApiService,
        starWarsDatabase: StarWarsDatabase
    ): CharacterRepository {
        return CharacterRepositoryImpl(dispatcherProvider, apiService, starWarsDatabase)
    }
}