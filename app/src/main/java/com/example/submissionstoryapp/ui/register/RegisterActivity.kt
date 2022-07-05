package com.example.submissionstoryapp.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import com.example.submissionstoryapp.R
import com.example.submissionstoryapp.databinding.ActivityRegisterBinding
import com.example.submissionstoryapp.ui.login.LoginActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@ExperimentalPagingApi
@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private var registerJob: Job = Job()
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        clickButton()
    }

    private fun clickButton() {
        binding.apply {
            tvToMasuk.setOnClickListener {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
            }
            btnDaftar.setOnClickListener {
                registerprocess()
            }
        }
    }

    private fun registerprocess() {
        val name = binding.etNama.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPass.text.toString()
        setLoading(true)

        lifecycleScope.launchWhenResumed {
            // Make sure only one job that handle the registration process
            if (registerJob.isActive) registerJob.cancel()

            registerJob = launch {
                viewModel.userRegister(name, email, password).collect { result ->
                    result.onSuccess {
                        Toast.makeText(
                            this@RegisterActivity,
                            getString(R.string.daftar_berhasil),
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        startActivity(intent)
                    }

                    result.onFailure {
                        Snackbar.make(
                            binding.root,
                           getString(R.string.daftar_gagal),
                            Snackbar.LENGTH_SHORT
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
            etNama.isEnabled = !isLoading
            btnDaftar.isEnabled = !isLoading

            // Animate views alpha
            if (isLoading) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        }
    }
}