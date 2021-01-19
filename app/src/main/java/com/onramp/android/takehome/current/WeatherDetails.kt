package com.onramp.android.takehome.current

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.onramp.android.takehome.R

class WeatherDetails : Fragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //get arguments
        val args = arguments
        val feelsLike = args?.getString("feelsLike", "")
        val tempLow = args?.getString("low", "")
        val tempHigh = args?.getString("high", "")
        val humidity = args?.getString("humidity", "")
        val visibility = args?.getString("visibility", "")
        val sunrise = args?.getString("sunrise", "")
        val sunset = args?.getString("sunset", "")
        val img = args?.getInt("img", 0)

        //get views
        val rootView = inflater.inflate(R.layout.fragment_weather_details, container, false)
        val feelsLikeTextView = rootView.findViewById<TextView>(R.id.feelsLikeText)
        val tempMinTextView = rootView.findViewById<TextView>(R.id.tempMinText)
        val tempMaxTextView = rootView.findViewById<TextView>(R.id.tempMaxText)
        val humidityTextView = rootView.findViewById<TextView>(R.id.humidityText)
        val visibilityTextView = rootView.findViewById<TextView>(R.id.visibilityText)
        val sunriseTextView = rootView.findViewById<TextView>(R.id.sunriseText)
        val sunsetTextView = rootView.findViewById<TextView>(R.id.sunsetText)
        val imageView = rootView.findViewById<ImageView>(R.id.weatherImg)

        //set views
        feelsLikeTextView.text = feelsLike
        tempMinTextView.text = tempLow
        tempMaxTextView.text = tempHigh
        humidityTextView.text = humidity
        visibilityTextView.text = visibility
        sunriseTextView.text = sunrise
        sunsetTextView.text = sunset
        imageView.setBackgroundResource(img!!)

        return rootView
    }


}