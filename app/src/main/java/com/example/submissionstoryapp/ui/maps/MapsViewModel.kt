package com.example.submissionstoryapp.ui.maps

import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import com.example.submissionstoryapp.data.AuthRepository
import com.example.submissionstoryapp.data.StoryRepository
import com.example.submissionstoryapp.data.remote.response.StoriesResponses
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class MapsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val storyRepository: StoryRepository,
) : ViewModel() {
    fun getAuthToken(): Flow<String?> = authRepository.getAuthToken()

    fun getAllStoriesWithLocation(token: String): Flow<Result<StoriesResponses>> =
        storyRepository.getAllStoriesWithLocation(token)
}