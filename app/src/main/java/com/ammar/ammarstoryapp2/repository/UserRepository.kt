package com.ammar.ammarstoryapp2.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.ammar.ammarstoryapp2.data.StoryRemoteMediator
import com.ammar.ammarstoryapp2.database.StoryDatabase
import com.ammar.ammarstoryapp2.response.ErrorResponse
import com.ammar.ammarstoryapp2.response.ListStoryItem
import com.ammar.ammarstoryapp2.response.LoginResponse
import com.ammar.ammarstoryapp2.response.MapsResponse
import com.ammar.ammarstoryapp2.response.RegisterResponse
import com.ammar.ammarstoryapp2.response.Result
import com.ammar.ammarstoryapp2.response.StoryResponse
import com.ammar.ammarstoryapp2.retrofit.ApiService
import com.ammar.ammarstoryapp2.retrofit.UserModel
import com.ammar.ammarstoryapp2.utils.UserPreference
import com.ammar.ammarstoryapp2.utils.wrapEspressoIdlingResource
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class UserRepository (
    private val apiService: ApiService,
    private val userPreference: UserPreference,
    private val userDatabase: StoryDatabase
) {
    fun register(
        name: String,
        email: String,
        password: String
    ): LiveData<Result<RegisterResponse>> =
        liveData {
            emit(Result.Loading)
            try {
                val registerResponse = apiService.register(name, email, password)
                emit(Result.Success(registerResponse))
            } catch (e: HttpException) {
                val error = e.response()?.errorBody()?.string()
                val errorRes = Gson().fromJson(error, ErrorResponse::class.java)
                Log.d(TAG, "register: ${e.message.toString()}")
                emit(Result.Error(errorRes.message))
            } catch (e: Exception) {
                emit(Result.Error(e.toString()))
            }
        }

    fun login(email: String, password: String): LiveData<Result<LoginResponse>> =
        liveData {
            emit(Result.Loading)
            wrapEspressoIdlingResource {
                try {
                    val loginResponse = apiService.login(email, password)
                    emit(Result.Success(loginResponse))

                } catch (e: HttpException) {
                    val error = e.response()?.errorBody()?.string()
                    val errorRes = Gson().fromJson(error, ErrorResponse::class.java)
                    Log.d(TAG, "login: ${e.message.toString()}")
                    emit(Result.Error(errorRes.message))
                } catch (e: Exception) {
                    emit(Result.Error(e.toString()))
                }
            }
        }

    fun getStory(): LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return try {
            Pager(
                config = PagingConfig(
                    pageSize = 5
                ),
                remoteMediator = StoryRemoteMediator(userDatabase, apiService),
                pagingSourceFactory = {
                    userDatabase.storyDao().getAllStory()
                }
            ).liveData
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    fun uploadImage(imageFile: File, description: String, lat: Double?, lon: Double?) = liveData {
        emit(Result.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        try {
            val successResponse = apiService.uploadImage(multipartBody, requestBody, lat, lon)
            emit(Result.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(Result.Error(errorResponse.message))
        } catch (e: Exception) {
            emit(Result.Error(e.toString()))
        }
    }

    fun getStoryWithLocation(): LiveData<Result<StoryResponse>> =
        liveData {
            emit(Result.Loading)
            try {
                val storyLocationResponse = apiService.getStoriesWithLocation()
                emit(Result.Success(storyLocationResponse))
            } catch (e: HttpException) {
                val error = e.response()?.errorBody()?.string()
                val errorRes = Gson().fromJson(error, MapsResponse::class.java)
                Log.d(TAG, "getStoryWithLocation ${e.message.toString()}")
                emit(Result.Error(errorRes.message))
            } catch (e: Exception) {
                emit(Result.Error(e.toString()))
            }
        }

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    companion object {
        private const val TAG = "UserRepository"
    }
}