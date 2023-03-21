package com.nada.tech.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.nada.tech.network.Resource
import com.nada.tech.network.ResponseHandler
import com.nada.tech.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    fun login(param: HashMap<String, Any>) = liveData(Dispatchers.IO) {
        emit(Resource.loading(null))
        try {
            ResponseHandler.responseParser(userRepository.login(param), this)
        } catch (e: Exception) {
            emit(Resource.error(data = null, message = ResponseHandler.handleErrorResponse(e)))
        }
    }

    fun getProfile() = liveData(Dispatchers.IO) {
        emit(Resource.loading(null))
        try {
            ResponseHandler.responseParser(userRepository.getProfile(), this)
        } catch (e: Exception) {
            emit(Resource.error(data = null, message = ResponseHandler.handleErrorResponse(e)))
        }
    }
}