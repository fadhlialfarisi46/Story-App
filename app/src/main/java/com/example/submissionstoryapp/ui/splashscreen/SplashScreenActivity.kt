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
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import com.example.submissionstoryapp.R
import com.example.submissionstoryapp.databinding.ActivitySplashScreenBinding
import com.example.submissionstoryapp.ui.home.HomeActivity
import com.example.submissionstoryapp.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@ExperimentalPagingApi
@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

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
        lifecycleScope.launchWhenCreated {
            launch {
                viewModel.getAuthToken().collect { token ->
                    Log.d("token", token.toString())
                    if (token.isNullOrEmpty()) {
                        val intent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
                        startActivity(intent, optionsCompat.toBundle())
                    } else {
                        val intent = Intent(this@SplashScreenActivity, HomeActivity::class.java)
                        startActivity(intent, optionsCompat.toBundle())
                    }
                }
            }
        }

    }
}