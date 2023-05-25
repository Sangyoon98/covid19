package com.covid19.data.repository

import com.covid19.data.model.CentersResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface RetrofitInterface {
    companion object {
        private const val BASE_URL = "https://api.odcloud.kr/api/"

        fun create(): RetrofitInterface {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RetrofitInterface::class.java)
        }
    }

    @GET("15077586/v1/centers")
    suspend fun getCenters(
        @Query("page") page: Int,
        @Query("perPage") perPage: Int,
        @Query("serviceKey") serviceKey: String
    ): CentersResponse
}