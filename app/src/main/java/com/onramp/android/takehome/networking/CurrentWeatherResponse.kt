package com.onramp.android.takehome.networking

import com.google.gson.annotations.SerializedName

data class CurrentWeatherResponse(
        @SerializedName("weather") val weather: List<Weather>,
        @SerializedName("main") val main: Main,
        @SerializedName("sys") val sys: Sys,
        @SerializedName("name") val name: String,
        @SerializedName("dt") val dt: Long,
        @SerializedName("visibility") val visibility: Long
)

data class Weather(
        @SerializedName("main") val main: String,
        @SerializedName("description") val description: String
)

data class  Main(
        @SerializedName("temp") val temp: Double,
        @SerializedName("feels_like") val feels_like: Double,
        @SerializedName("temp_min") val temp_min: Double,
        @SerializedName("temp_max") val temp_max: Double,
        @SerializedName("humidity") val humidity: Double
)

data class Sys (
        @SerializedName("sunrise") val sunrise: Long,
        @SerializedName("sunset") val sunset: Long
)