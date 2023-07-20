package com.example.submissionstoryapp.di

import android.content.Context
import android.content.SharedPreferences
import com.example.submissionstoryapp.data.local.PreferencesHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SharedPreferencesModule {

    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        val prefsName = "com.example.submissionstoryapp"
        val privateMode = Context.MODE_PRIVATE

        return context.getSharedPreferences(prefsName, privateMode)
    }

    @Provides
    @Singleton
    fun providePreferencesHelper(sharedPreferences: SharedPreferences): PreferencesHelper = PreferencesHelper(sharedPreferences)
}
