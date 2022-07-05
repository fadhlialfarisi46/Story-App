package com.example.submissionstoryapp.ui.register

import androidx.lifecycle.ViewModel
import com.example.submissionstoryapp.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    suspend fun userRegister(name: String, email: String, password: String) =
        authRepository.userRegister(name, email, password)
}