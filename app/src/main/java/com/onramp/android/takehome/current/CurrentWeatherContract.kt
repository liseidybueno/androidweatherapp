package com.onramp.android.takehome.current

import com.onramp.android.takehome.WeatherDetails
import com.onramp.android.takehome.networking.CurrentWeatherResponse
import retrofit2.Response

interface CurrentWeatherContract {

    interface View {

        fun getSharedPref(): Array<String?>

//        fun displayCurrentWeather(response: Response<CurrentWeatherResponse>)

        fun displayCurrentWeather(weatherData: CurrentWeatherModel.CurrentWeatherData)

//        fun sendDataToFragment(weatherData: CurrentWeatherModel.CurrentWeatherData): WeatherDetails

    }

    interface Model {

        fun getCurrentWeatherData(presenter: Presenter)

    }

    interface Presenter {

        fun getData(): Array<String?>?

//        fun onSuccess(response: Response<CurrentWeatherResponse>)
        fun onSuccess(weatherData: CurrentWeatherModel.CurrentWeatherData)


        fun getCurrentWeatherData()

      //  fun sendDataToDetails(weatherData: CurrentWeatherModel.CurrentWeatherData)

    }

}