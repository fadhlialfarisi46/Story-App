package com.example.submissionstoryapp.ui.maps

import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import com.example.submissionstoryapp.data.StoryRepository
import com.example.submissionstoryapp.data.remote.response.StoriesResponses
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class MapsViewModel @Inject constructor(
    private val storyRepository: StoryRepository,
) : ViewModel() {

    fun getAllStoriesWithLocation(): Flow<Result<StoriesResponses>> =
        storyRepository.getAllStoriesWithLocation()
}
