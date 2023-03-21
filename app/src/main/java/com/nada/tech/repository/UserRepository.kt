package com.nada.tech.repository

import com.nada.tech.network.ApiService
import javax.inject.Inject

class UserRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun login(param: HashMap<String, Any>) = apiService.login(param)

    suspend fun getProfile() = apiService.getProfile()
}