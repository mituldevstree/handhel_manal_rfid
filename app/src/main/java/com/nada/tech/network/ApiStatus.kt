package com.nada.tech.network

enum class ApiStatus {
    SUCCESS,
    ERROR,
    LOADING
}

fun ApiStatus.typeCall(
    success: () -> Unit,
    error: () -> Unit,
    loading: () -> Unit
) {
    when (this) {
        ApiStatus.SUCCESS -> success.invoke()
        ApiStatus.ERROR -> error.invoke()
        ApiStatus.LOADING -> loading.invoke()
    }
}