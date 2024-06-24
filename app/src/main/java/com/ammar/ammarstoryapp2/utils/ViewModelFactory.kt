package com.ammar.ammarstoryapp2.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ammar.ammarstoryapp2.injection.Injection
import com.ammar.ammarstoryapp2.repository.UserRepository
import com.ammar.ammarstoryapp2.ui.main.MainViewModel
import com.ammar.ammarstoryapp2.ui.login.LoginViewModel
import com.ammar.ammarstoryapp2.ui.maps.MapsViewModel
import com.ammar.ammarstoryapp2.ui.register.RegisterViewModel
import com.ammar.ammarstoryapp2.ui.story.UploadStoryViewModel
import com.ammar.ammarstoryapp2.ui.welcome.WelcomeViewModel

class ViewModelFactory private constructor(
    private val userRepository: UserRepository,
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(userRepository) as T
        }
        else if(modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(userRepository) as T
        } else if(modelClass.isAssignableFrom(WelcomeViewModel::class.java)) {
            return WelcomeViewModel(userRepository) as T
        } else if(modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(userRepository) as T
        }else if(modelClass.isAssignableFrom(UploadStoryViewModel::class.java)) {
            return UploadStoryViewModel(userRepository) as T
        }else if(modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            return MapsViewModel(userRepository) as T
        }
        throw IllegalArgumentException("No ModelClass: " + modelClass.name)
    }

    companion object {
        fun getInstance(context: Context): ViewModelFactory {
            return ViewModelFactory(Injection.provideRepository(context))
        }
    }
}