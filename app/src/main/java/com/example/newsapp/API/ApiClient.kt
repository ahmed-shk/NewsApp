package com.example.newsapp.API

import com.example.newsapp.Utils.Constants.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient {
    companion object {
        private val retrofit by lazy {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder().addInterceptor(logging).build()

            Retrofit.Builder().client(client).baseUrl(BASE_URL).addConverterFactory(
                GsonConverterFactory.create()).build()
        }

        val api by lazy {
            retrofit.create(NewsApi::class.java)
        }
    }

}