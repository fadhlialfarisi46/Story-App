package com.example.submissionstoryapp.ui.splashscreen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.ExperimentalPagingApi
import com.example.submissionstoryapp.R
import com.example.submissionstoryapp.data.local.PreferencesHelper
import com.example.submissionstoryapp.databinding.ActivitySplashScreenBinding
import com.example.submissionstoryapp.ui.home.HomeActivity
import com.example.submissionstoryapp.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@ExperimentalPagingApi
@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    private val viewModel: SplashViewModel by viewModels()

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler(mainLooper).postDelayed({
            moveActivity()
        }, 2000)
    }

    private fun moveActivity() {
        val optionsCompat: ActivityOptionsCompat =
            ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                Pair(binding.logo, getString(R.string.story_app)),
            )

        val token = preferencesHelper.token
        Log.d("token", token)
        if (token.isEmpty()) {
            val intent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
            startActivity(intent, optionsCompat.toBundle())
        } else {
            val intent = Intent(this@SplashScreenActivity, HomeActivity::class.java)
            startActivity(intent, optionsCompat.toBundle())
        }
    }
}
