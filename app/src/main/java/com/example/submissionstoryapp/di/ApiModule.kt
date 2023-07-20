package com.example.submissionstoryapp.di

import com.example.submissionstoryapp.data.local.PreferencesHelper
import com.example.submissionstoryapp.data.remote.AuthInterceptor
import com.example.submissionstoryapp.data.remote.network.ApiConfig
import com.example.submissionstoryapp.data.remote.network.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    @Provides
    @Singleton
    fun provideAuthInterceptor(preferencesHelper: PreferencesHelper): AuthInterceptor = AuthInterceptor(preferencesHelper)

    @Provides
    @Singleton
    fun provideApiService(authInterceptor: AuthInterceptor): ApiService = ApiConfig.getApiService(authInterceptor)
}
