package com.example.focusguardian.data.remote

import com.example.focusguardian.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SupabaseClient {
    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("apikey", BuildConfig.SUPABASE_KEY)
                .addHeader("Authorization", "Bearer ${BuildConfig.SUPABASE_KEY}")
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        }
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.SUPABASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: SupabaseService = retrofit.create(SupabaseService::class.java)
}
