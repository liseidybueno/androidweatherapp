package com.onramp.android.takehome

import com.google.gson.annotations.SerializedName

data class CurrentWeatherResponse(
        @SerializedName("current") val current: CurrentWeather
)

data class CurrentWeather(
        @SerializedName("dt") val dt: Long,
        @SerializedName("sunrise") val sunrise: Long,
        @SerializedName("sunset") val sunset: Long,
        @SerializedName("temp") val temp: Double,
        @SerializedName("feels_like") val feels_like: Double,
        @SerializedName("humidity") val humidity: Int,
        @SerializedName("weather") val weather: List<WeatherDetails>
)

data class WeatherDetails(
        @SerializedName("main") val main: String,
        @SerializedName("description") val description: String
)