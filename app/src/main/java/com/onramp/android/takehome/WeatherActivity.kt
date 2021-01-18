package com.onramp.android.takehome

/**
 * Notes:
 * Save all weather data in a database and load from database into the view
 * Create fragment for weekly data
 * swipe up fragment
 * OR create fragment for what to wear
 */

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_weather.*
import retrofit2.Response


class WeatherActivity : AppCompatActivity(), CurrentWeatherContract.View {

    private var sharedPref: SharedPreferences?= null

    private lateinit var presenter: CurrentWeatherContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        getSharedPref()

        presenter = CurrentWeatherPresenter(this)

        presenter.getWeeklyWeatherData()


    }

    override fun getSharedPref(): Array<String?> {

        sharedPref = getSharedPreferences("loggedIn", Context.MODE_PRIVATE)

        val lat = sharedPref!!.getString("lat", "")
        val lon = sharedPref!!.getString("lon", "")
        val units = sharedPref!!.getString("units", "")

        val coordinates = arrayOf(lat, lon, units)

       return coordinates

    }

    override fun displayCurrentWeather(response: Response<CurrentWeatherResponse>){

        val data = response.body()

        val current = data?.current

        val temp = current?.temp

        println("Current temp: " + temp)


    }
}