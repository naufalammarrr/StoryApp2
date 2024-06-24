package com.ammar.ammarstoryapp2.ui.maps

import androidx.lifecycle.ViewModel
import com.ammar.ammarstoryapp2.repository.UserRepository

class MapsViewModel (private val userRepository: UserRepository) : ViewModel() {
    fun getListStoryLocation() = userRepository.getStoryWithLocation()
}