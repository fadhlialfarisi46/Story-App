package com.example.submissionstoryapp.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.submissionstoryapp.data.local.entity.Story
import com.example.submissionstoryapp.data.local.roomdb.StoryDatabase
import com.example.submissionstoryapp.data.remote.network.ApiService
import com.example.submissionstoryapp.data.remote.response.FileUploadResponse
import com.example.submissionstoryapp.data.remote.response.StoriesResponses
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@ExperimentalPagingApi
class StoryRepository @Inject constructor(
    private val db: StoryDatabase,
    private val apiService: ApiService
) {

    fun getAllStories(token: String): Flow<PagingData<Story>>{
        return Pager(
            config = PagingConfig(pageSize = 10,),
            remoteMediator = StoryRemoteMediator(db, apiService, generateBearerToken(token)),
            pagingSourceFactory = { db.storyDao().getAllStories()}
        ).flow
    }


    fun getAllStoriesWithLocation(token: String): Flow<Result<StoriesResponses>> = flow {
        try {
            val bearerToken = generateBearerToken(token)
            val response = apiService.getAllStories(bearerToken, size = 10, location = 1)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }

    suspend fun uploadImage(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?=null,
        lon: RequestBody?=null
    ): Flow<Result<FileUploadResponse>> = flow {
        try {
            val bearerToken = generateBearerToken(token)
            val response = apiService.uploadImage(bearerToken, file, description, lat, lon)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }

    private fun generateBearerToken(token: String): String {
        return "Bearer $token"
    }
}