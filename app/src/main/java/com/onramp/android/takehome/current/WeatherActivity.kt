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
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.onramp.android.takehome.R
import kotlinx.android.synthetic.main.activity_weather.*

class WeatherActivity : AppCompatActivity(), CurrentWeatherContract.View {

    private var sharedPref: SharedPreferences?= null

    private lateinit var presenter: CurrentWeatherContract.Presenter

    //variables to slide fragment up and down
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

    override fun displayCurrentWeather(weatherData: CurrentWeatherModel.CurrentWeatherData){

        //display in activity
        mainImg.setBackgroundResource(weatherData.mainImg)
        currDate.text = weatherData.date
        city.text = weatherData.name
        message.text = weatherData.message
        currTemp.text = weatherData.temp
        someDetails.text = weatherData.description

        //display in fragment
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        val weatherDetails = WeatherDetails()
        val bundle = Bundle()
        bundle.putString("feelsLike", weatherData.feelsLike)
        bundle.putString("low", weatherData.min)
        bundle.putString("high", weatherData.max)
        bundle.putString("humidity", weatherData.humidity)
        bundle.putString("visibility", weatherData.visibility)
        bundle.putString("sunrise", weatherData.sunrise)
        bundle.putString("sunset", weatherData.sunset)
        bundle.putInt("img", weatherData.weatherImg)
        weatherDetails.arguments = bundle
        fragmentTransaction.add(R.id.WeatherDetailsContainer, weatherDetails).commit()

    }



}

