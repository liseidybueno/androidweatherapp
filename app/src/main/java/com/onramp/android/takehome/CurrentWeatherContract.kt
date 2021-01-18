package com.onramp.android.takehome

import retrofit2.Response

interface CurrentWeatherContract {

    interface View {

        fun getSharedPref(): Array<String?>

        fun displayCurrentWeather(response: Response<CurrentWeatherResponse>)

    }

    interface Model {

        fun getCurrentWeatherData(presenter: Presenter)

    }

    interface Presenter {

        fun getData(): Array<String?>?

        fun onSuccess(response: Response<CurrentWeatherResponse>)

        fun getWeeklyWeatherData()

    }
}