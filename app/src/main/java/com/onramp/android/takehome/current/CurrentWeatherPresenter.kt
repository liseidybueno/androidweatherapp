package com.onramp.android.takehome.current

class CurrentWeatherPresenter(
        view: CurrentWeatherContract.View
) : CurrentWeatherContract.Presenter {

    private var view: CurrentWeatherContract.View = view
    private var model: CurrentWeatherContract.Model = CurrentWeatherModel()

    override fun getData(): Array<String?>? {

        return view.getSharedPref()
    }

//    override fun onSuccess(response: Response<CurrentWeatherResponse>){
//        view.displayCurrentWeather(response)
//    }

    override fun onSuccess(weatherData: CurrentWeatherModel.CurrentWeatherData) {
        view.displayCurrentWeather(weatherData)
        //view.sendDataToFragment(weatherData)
    }
//
//    override fun sendDataToDetails(weatherData: CurrentWeatherModel.CurrentWeatherData){
//        view.sendDataToFragment(weatherData)
//    }

    override fun getCurrentWeatherData() {
        model.getCurrentWeatherData(this)
    }

}