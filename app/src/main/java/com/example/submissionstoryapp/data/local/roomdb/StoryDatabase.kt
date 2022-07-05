package com.example.submissionstoryapp.data.local.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.submissionstoryapp.data.local.entity.RemoteKeys
import com.example.submissionstoryapp.data.local.entity.Story

@Database(entities = [Story::class, RemoteKeys::class], version = 1, exportSchema = false)
abstract class StoryDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao
}