package com.example.submissionstoryapp.di

import android.content.Context
import androidx.room.Room
import com.example.submissionstoryapp.data.local.roomdb.RemoteKeysDao
import com.example.submissionstoryapp.data.local.roomdb.StoryDao
import com.example.submissionstoryapp.data.local.roomdb.StoryDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    fun provideStoryDao(storyDatabase: StoryDatabase): StoryDao = storyDatabase.storyDao()

    @Provides
    fun provideRemoteKeysDao(storyDatabase: StoryDatabase): RemoteKeysDao = storyDatabase.remoteKeysDao()

    @Provides
    @Singleton
    fun provideStoryDatabase(@ApplicationContext context: Context): StoryDatabase{
        return Room.databaseBuilder(
            context.applicationContext,
            StoryDatabase::class.java,
            "story_db"
        ).build()
    }
}
