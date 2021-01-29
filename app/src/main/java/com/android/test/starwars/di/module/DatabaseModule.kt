package com.android.test.starwars.di.module

import android.content.Context
import androidx.room.Room
import com.android.test.starwars.data.local.StarWarsDatabase
import com.android.test.starwars.data.local.dao.CharacterDao
import com.android.test.starwars.data.local.dao.CharacterDetailsDao
import com.android.test.starwars.data.local.dao.RemoteKeysDao
import com.android.test.starwars.utils.Constants.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    /*
     * The method returns the Database object
     **/
    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): StarWarsDatabase =
        Room.databaseBuilder(context, StarWarsDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration() // allows database to be cleared after upgrading version
            .build()

    /*
    * We need the respective dao modules.
    * For this, We need the StarWarsDatabase object
    * So we will define the providers for there here in this module.
    * */
    @Singleton
    @Provides
    fun provideCharacterDao(starWarDatabase: StarWarsDatabase):
            CharacterDao = starWarDatabase.characterDao()

    @Singleton
    @Provides
    fun provideCharacterDetailsDao(starWarDatabase: StarWarsDatabase):
            CharacterDetailsDao = starWarDatabase.characterDetailsDao()

    @Singleton
    @Provides
    fun provideRemoteKeysDao(starWarDatabase: StarWarsDatabase):
            RemoteKeysDao = starWarDatabase.remoteKeysDao()
}