package com.nada.tech.network

data class Resource<out T>(val status: ApiStatus, val data: T?, val message: String = "") {
    companion object {
        fun <T> success(data: T): Resource<T> = Resource(ApiStatus.SUCCESS, data)
        fun <T> error(data: T?, message: String): Resource<T> = Resource(ApiStatus.ERROR, data, message)
        fun <T> loading(data: T?): Resource<T> = Resource(ApiStatus.LOADING, data)
    }
}