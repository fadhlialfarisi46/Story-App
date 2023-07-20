package com.example.submissionstoryapp.ui.login

import androidx.lifecycle.ViewModel
import com.example.submissionstoryapp.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    fun userLogin(email: String, password: String) =
        authRepository.userLogin(email, password)
}
