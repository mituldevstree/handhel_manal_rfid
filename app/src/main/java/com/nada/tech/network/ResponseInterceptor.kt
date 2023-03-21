package com.nada.tech.network

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.nada.tech.Controller
import com.nada.tech.utility.Constants
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResponseInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        when (response.code) {
            NetworkURL.RES_UNAUTHORISED -> {
                val intent = Intent(Constants.ACTION_FOR_BIDDEN_RESPONSE)
                LocalBroadcastManager.getInstance(Controller.instance).sendBroadcast(intent)
            }
        }
        return response
    }
}