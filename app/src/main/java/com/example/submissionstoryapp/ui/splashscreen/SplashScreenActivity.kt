package com.example.submissionstoryapp.ui.splashscreen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.ExperimentalPagingApi
import com.example.submissionstoryapp.R
import com.example.submissionstoryapp.data.local.PreferencesHelper
import com.example.submissionstoryapp.databinding.ActivitySplashScreenBinding
import com.example.submissionstoryapp.ui.home.HomeActivity
import com.example.submissionstoryapp.ui.login.LoginActivity
import com.example.submissionstoryapp.utils.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@ExperimentalPagingApi
@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : BaseActivity<ActivitySplashScreenBinding>() {

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        Handler(mainLooper).postDelayed({
            moveActivity()
        }, 2000)
    }

    override fun inflateBinding(layoutInflater: LayoutInflater): ActivitySplashScreenBinding {
        return ActivitySplashScreenBinding.inflate(layoutInflater)
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
