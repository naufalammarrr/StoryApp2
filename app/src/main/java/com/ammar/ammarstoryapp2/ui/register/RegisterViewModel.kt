package com.ammar.ammarstoryapp2.ui.register

import androidx.lifecycle.ViewModel
import com.ammar.ammarstoryapp2.repository.UserRepository

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun registerUser(name: String, email: String, password: String) =
        userRepository.register(name ,email, password)
}