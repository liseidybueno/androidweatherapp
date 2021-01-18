package com.onramp.android.takehome.weatherNetworking

import com.onramp.android.takehome.CurrentWeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OWDEndpoints {

    @GET("data/2.5/onecall")
    fun getCurrentWeatherData(@Query("lat") lat: String, @Query("lon") lon: String, @Query("exclude") exclude: String, @Query("units") units: String, @Query("appid") app_id: String): Call<CurrentWeatherResponse>


}