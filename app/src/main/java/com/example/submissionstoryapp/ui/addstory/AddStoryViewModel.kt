package com.example.submissionstoryapp.ui.addstory

import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import com.example.submissionstoryapp.data.AuthRepository
import com.example.submissionstoryapp.data.StoryRepository
import com.example.submissionstoryapp.data.remote.response.FileUploadResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class AddStoryViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val storyRepository: StoryRepository
) : ViewModel() {

    fun getAuthToken(): Flow<String?> = authRepository.getAuthToken()

    suspend fun uploadImage(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ): Flow<Result<FileUploadResponse>> =
        storyRepository.uploadImage(token, file, description, lat, lon)
}