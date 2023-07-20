package com.example.submissionstoryapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.submissionstoryapp.data.StoryRepository
import com.example.submissionstoryapp.data.local.entity.Story
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val storyRepository: StoryRepository,
) : ViewModel() {

    fun getAllStories(): LiveData<PagingData<Story>> =
        storyRepository.getAllStories().cachedIn(viewModelScope).asLiveData()
}
