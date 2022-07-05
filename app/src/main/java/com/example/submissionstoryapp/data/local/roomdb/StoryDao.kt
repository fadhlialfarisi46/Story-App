package com.example.submissionstoryapp.data.local.roomdb

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.submissionstoryapp.data.local.entity.Story

@Dao
interface StoryDao {

    /**
     * Insert story to local database
     *
     * @param story Story to save
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(vararg story: Story)

    /**
     * Get all stories from database
     *
     * @return PagingSource
     */
    @Query("SELECT * FROM story")
    fun getAllStories(): PagingSource<Int, Story>


    /**
     * Delete all saved stories from database
     */
    @Query("DELETE FROM story")
    fun deleteAll()
}