package com.nada.tech.network

import com.nada.tech.BuildConfig
import com.nada.tech.utility.LocalDataHelper
import com.nada.tech.utility.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.ConnectionSpec
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(
        headerInterceptor: Interceptor,
        responseInterceptor: ResponseInterceptor,
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
        with(builder)
        {
            connectionSpecs(listOf(ConnectionSpec.CLEARTEXT, ConnectionSpec.MODERN_TLS))
                .connectTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
        }
        val loggingInterceptor =
            if (BuildConfig.DEBUG) HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            else HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)

        builder.addInterceptor(loggingInterceptor)
        builder.addInterceptor(headerInterceptor)
        builder.addInterceptor(responseInterceptor)
        return builder.build()
    }

    @Singleton
    @Provides
    fun provideHeaderInterceptor(localDataHelper: LocalDataHelper): Interceptor {
        return Interceptor { chain ->
            val request = chain.request().newBuilder()
            if (localDataHelper.login) {
                Log.d("authtoken : ${localDataHelper.apiToken.orEmpty()}")
                Log.d("userid : ${localDataHelper.userId}")

                request.addHeader("authtoken", localDataHelper.apiToken.orEmpty())
                request.addHeader("userid", localDataHelper.userId)
            }
            chain.proceed(request.build())
        }
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit) = retrofit.create(ApiService::class.java)
}