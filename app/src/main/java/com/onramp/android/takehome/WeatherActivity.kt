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


class WeatherActivity : AppCompatActivity() {

    private var sharedPref: SharedPreferences?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        sharedPref = getSharedPreferences("loggedIn", Context.MODE_PRIVATE)

        val lat = sharedPref!!.getString("lat", "")
        val lon = sharedPref!!.getString("lon", "")

//        val lat = intent.getStringExtra("latitude")
//        val lon = intent.getStringExtra("longitude")
        val text = "Lat: $lat and Lon: $lon"

        weatherText.text = text


    }
}