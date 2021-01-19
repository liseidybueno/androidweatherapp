package com.onramp.android.takehome.current

interface CurrentWeatherContract {

    interface View {

        fun getSharedPref(): Array<String?>

        fun displayCurrentWeather(weatherData: CurrentWeatherModel.CurrentWeatherData)

    }

    interface Model {

        fun getCurrentWeatherData(presenter: Presenter)

    }

    interface Presenter {

        fun getData(): Array<String?>?

        fun onSuccess(weatherData: CurrentWeatherModel.CurrentWeatherData)

        fun getCurrentWeatherData()

    }

}