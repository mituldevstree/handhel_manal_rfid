package com.nada.tech.network

import android.accounts.NetworkErrorException
import android.util.MalformedJsonException
import androidx.lifecycle.LiveDataScope
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.nada.tech.model.BaseModel
import kotlinx.coroutines.CoroutineExceptionHandler
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import javax.net.ssl.HttpsURLConnection

object ResponseHandler {

    fun handleErrorResponse(error: Throwable): String {
        return when (error) {
            is SocketTimeoutException -> "Timeout, Might be Server not responding"
            is HttpException -> {
                when (error.code()) {
                    HttpsURLConnection.HTTP_UNAUTHORIZED -> "Unauthorised User"
                    HttpsURLConnection.HTTP_FORBIDDEN -> "Forbidden"
                    HttpsURLConnection.HTTP_INTERNAL_ERROR -> "Internal Server Error"
                    HttpsURLConnection.HTTP_BAD_REQUEST -> "Bad Request"
                    else -> error.getLocalizedMessage()
                }
            }
            is JsonSyntaxException, is MalformedJsonException -> "Something Went Wrong. API is not responding properly!"
            is NetworkErrorException, is IOException -> "No Internet Connection"
            else -> error.message.toString()
        }
    }

    fun getErrorMessage(code: Int): String {
        return when (code) {
            HttpsURLConnection.HTTP_UNAUTHORIZED -> "Unauthorised User"
            HttpsURLConnection.HTTP_FORBIDDEN -> "Forbidden"
            HttpsURLConnection.HTTP_INTERNAL_ERROR -> "Internal Server Error"
            HttpsURLConnection.HTTP_BAD_REQUEST -> "Bad Request"
            else -> "Something Went Wrong"
        }
    }

    fun baseError(error: ResponseBody?): BaseModel {
        return try {
            Gson().fromJson(error!!.charStream(), BaseModel::class.java)
        } catch (e: Exception) {
            BaseModel(0, handleErrorResponse(e))
        }
    }

    fun <T> baseError(response: Response<T>): BaseModel {
        return try {
            return if (response.code() == NetworkURL.RESPONSE_OK || response.code() == NetworkURL.RESPONSE_CREATED) {
                val baseModel = Gson().fromJson(response.body().toString(), BaseModel::class.java)
                baseModel.code = response.code()
                baseModel
            } else {
                val error = Gson().fromJson(response.errorBody()?.string(), BaseModel::class.java)
                error.code = response.code()
                error.message = error.message
                error
            }
        } catch (e: Exception) {
            if (response.body() is BaseModel) response.body() as BaseModel
            else BaseModel(response.code(), handleErrorResponse(e))
        }
    }

    fun exceptionHandler(callback: ((errorMessage: String) -> Unit)? = null): CoroutineExceptionHandler {
        return CoroutineExceptionHandler { _, throwable ->
            callback?.invoke(handleErrorResponse(throwable))
        }
    }

    suspend fun <T> responseParser(
        response: Response<T>,
        liveDataScope: LiveDataScope<Resource<T>>,
    ) {
        if (response.isSuccessful) {
            val result = response.body()
            if (result != null) liveDataScope.emit(Resource.success(result))
            else liveDataScope.emit(
                Resource.error(data = null, message = baseError(response).message)
            )
        } else {
            liveDataScope.emit(Resource.error(data = null, message = baseError(response).message))
        }
    }
}