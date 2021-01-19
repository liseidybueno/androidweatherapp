package com.onramp.android.takehome.current

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
import android.view.View
import android.view.animation.TranslateAnimation
import androidx.appcompat.app.AppCompatActivity
import com.onramp.android.takehome.R
import com.onramp.android.takehome.networking.CurrentWeatherResponse
import kotlinx.android.synthetic.main.activity_weather.*
import retrofit2.Response

class WeatherActivity : AppCompatActivity(), CurrentWeatherContract.View {

    private var sharedPref: SharedPreferences?= null

    private lateinit var presenter: CurrentWeatherContract.Presenter


    var details: View ?= null

    var isUp: Boolean ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        presenter = CurrentWeatherPresenter(this)
        presenter.getCurrentWeatherData()


        //show and hide fragment 
        details = findViewById(R.id.WeatherDetailsContainer)
        details!!.visibility = View.INVISIBLE
        val btnText = "Show Details"
        showDetailsBtn.text = btnText
        isUp = false

        showDetailsBtn.setOnClickListener{
            if(isUp == true){
                slideDown(details!!)
                val btnText = "Show Details"
                showDetailsBtn.text = btnText
            } else {
                slideUp(details!!)
                val btnText = "Hide Details"
                showDetailsBtn.text = btnText
            }
            isUp = !isUp!!
        }

        }

    private fun slideUp(view: View){
        view.visibility = View.VISIBLE
        val animate = TranslateAnimation(0F, 0F, view.height.toFloat(), 0F)
        animate.duration = 500
        animate.fillAfter = true
        view.startAnimation(animate)
    }

    private fun slideDown(view: View){
        val animate = TranslateAnimation(0F, 0F, 0F, view.height.toFloat())
        animate.duration = 500
        animate.fillAfter = true
        view.startAnimation(animate)
    }

        override fun getSharedPref(): Array<String?> {

            sharedPref = getSharedPreferences("loggedIn", Context.MODE_PRIVATE)

            val lat = sharedPref?.getString("lat", "")
            val lon = sharedPref?.getString("lon", "")
            val units = sharedPref?.getString("units", "")

            val coordinates = arrayOf(lat, lon, units)

            return coordinates

        }

        override fun displayCurrentWeather(response: Response<CurrentWeatherResponse>) {

            //get info from Db
            //no parameters
            //display info

            val data = response.body()
            val weather = data?.weather
            val main = data?.main
            val sys = data?.sys
            val name = data?.name
            val dt = data?.dt
            val visibility = data?.visibility

            val main_desc = weather?.get(0)?.main
            val description = weather?.get(0)?.description

            val temp = main?.temp
            val tempLow = main?.temp_min
            val tempHigh = main?.temp_max
            val feelsLike = main?.feels_like
            val humidity = main?.humidity

            val sunrise = sys?.sunrise
            val sunset = sys?.sunset


        }

    }

