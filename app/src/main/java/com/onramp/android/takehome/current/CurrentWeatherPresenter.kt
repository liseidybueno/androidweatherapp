package com.onramp.android.takehome.current

class CurrentWeatherPresenter(
        view: CurrentWeatherContract.View
) : CurrentWeatherContract.Presenter {

    private var view: CurrentWeatherContract.View = view
    private var model: CurrentWeatherContract.Model = CurrentWeatherModel()

    override fun getData(): Array<String?>? {

        //use getData() to send sharedPref data to model and use to make API call
        return view.getSharedPref()
    }

    override fun onSuccess(weatherData: CurrentWeatherModel.CurrentWeatherData) {

        view.displayCurrentWeather(weatherData)

    }

    override fun getCurrentWeatherData() {

        model.getCurrentWeatherData(this)
    }

}