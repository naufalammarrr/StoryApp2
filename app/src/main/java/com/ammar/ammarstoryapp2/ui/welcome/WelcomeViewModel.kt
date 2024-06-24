package com.ammar.ammarstoryapp2.ui.welcome

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.ammar.ammarstoryapp2.repository.UserRepository
import com.ammar.ammarstoryapp2.retrofit.UserModel

class WelcomeViewModel (private val userRepository: UserRepository) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }
}