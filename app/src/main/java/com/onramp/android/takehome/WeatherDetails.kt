package com.onramp.android.takehome

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class WeatherDetails : Fragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val args = arguments
        val feelsLike = args?.getString("feelsLike", "")

        println("Args: " + args)

        println("Fragment: " + feelsLike)

        val rootView = inflater.inflate(R.layout.fragment_weather_details, container, false)
        val feelsLikeTextView = rootView.findViewById<TextView>(R.id.feelsLikeText)

        feelsLikeTextView.text = feelsLike

        return rootView
    }

}