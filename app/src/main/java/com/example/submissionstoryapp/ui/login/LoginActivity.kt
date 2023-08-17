package com.example.submissionstoryapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import com.example.submissionstoryapp.R
import com.example.submissionstoryapp.data.local.PreferencesHelper
import com.example.submissionstoryapp.databinding.ActivityLoginBinding
import com.example.submissionstoryapp.ui.home.HomeActivity
import com.example.submissionstoryapp.ui.register.RegisterActivity
import com.example.submissionstoryapp.utils.BaseActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalPagingApi
@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>() {

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    private var loginJob: Job = Job()
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        clickButton()
    }

    override fun inflateBinding(layoutInflater: LayoutInflater): ActivityLoginBinding {
        return ActivityLoginBinding.inflate(layoutInflater)
    }

    private fun clickButton() {
        binding.apply {
            tvToDaftar.setOnClickListener {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
            }
            btnLogin.setOnClickListener {
                loginProcess()
            }
        }
    }

    private fun loginProcess() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPass.text.toString()
        setLoading(true)

        lifecycleScope.launchWhenResumed {
            if (loginJob.isActive) loginJob.cancel()

            loginJob = launch {
                viewModel.userLogin(email, password).collect { result ->
                    Log.d("result", result.toString())
                    result.onSuccess { credentials ->
                        credentials.loginResult?.token?.let { token ->
                            Log.d("token api", token)
                            preferencesHelper.token = token
                            Log.d("token pref", preferencesHelper.token)

                            Intent(this@LoginActivity, HomeActivity::class.java).also { intent ->
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            }
                        }
                    }
                    result.onFailure {
                        Snackbar.make(
                            binding.root,
                            getString(R.string.eror_login),
                            Snackbar.LENGTH_SHORT,
                        ).show()

                        setLoading(false)
                    }
                }
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.apply {
            etEmail.isEnabled = !isLoading
            etPass.isEnabled = !isLoading
            btnLogin.isEnabled = !isLoading

            // Animate views alpha
            if (isLoading) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        }
    }
}
