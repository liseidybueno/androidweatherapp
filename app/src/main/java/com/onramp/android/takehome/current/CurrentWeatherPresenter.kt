package com.onramp.android.takehome.current

import com.onramp.android.takehome.networking.CurrentWeatherResponse
import retrofit2.Response

class CurrentWeatherPresenter(
        view: CurrentWeatherContract.View
) : CurrentWeatherContract.Presenter {

    private var view: CurrentWeatherContract.View = view
    private var model: CurrentWeatherContract.Model = CurrentWeatherModel()

    override fun getData(): Array<String?>? {

        val data = view.getSharedPref()

        return data
    }

    override fun onSuccess(response: Response<CurrentWeatherResponse>){
        view.displayCurrentWeather(response)
    }

    override fun getWeeklyWeatherData() {
        model.getCurrentWeatherData(this)
    }

//    override fun showCoords() {
//
//        model.getCurrentWeatherData(this)
//    }

}