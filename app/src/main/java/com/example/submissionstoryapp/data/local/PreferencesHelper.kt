package com.example.submissionstoryapp.data.local

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesHelper
@Inject constructor(
    private val prefs: SharedPreferences,
) {

    companion object {
        private const val ACCESS_TOKEN = "data.source.prefs.ACCESS_TOKEN"
    }

    val editor = prefs.edit()

    var token: String = prefs.getString(ACCESS_TOKEN, "").orEmpty()
        set(value) = value.let {
            editor?.putString(ACCESS_TOKEN, it)?.apply()
        }!!
}
