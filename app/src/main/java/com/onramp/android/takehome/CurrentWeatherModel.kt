package com.onramp.android.takehome

import com.onramp.android.takehome.weatherNetworking.OWDEndpoints
import com.onramp.android.takehome.weatherNetworking.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class CurrentWeatherModel : CurrentWeatherContract.Model {

    data class CurrentWeatherData(val imageView: Int, val date: String, val dayTemp: String,
                                  val sunrise: String, val sunset: String, val feelsLike: String,
                                  val humidity: String, val main: String, val description: String)

    override fun getCurrentWeatherData(presenter: CurrentWeatherContract.Presenter) {

        val coord = presenter.getData()

        val lat = coord?.get(0).toString()
        val long = coord?.get(1).toString()
        val units = coord?.get(2).toString().toLowerCase(Locale.ROOT)
        val exclude = "minutely,hourly,alerts"
        val apiKey = "cc9e3fecd2292d42405981b808cbca2b"

        val request = ServiceBuilder.buildService(OWDEndpoints::class.java)

        val call = request.getCurrentWeatherData(lat, long, exclude, units, apiKey)

        call.enqueue(object : Callback<CurrentWeatherResponse>{

            override fun onResponse(call: Call<CurrentWeatherResponse>, response: Response<CurrentWeatherResponse>){

                if(response.isSuccessful){

                    presenter.onSuccess(response)

                    println("Success!")



                } else {
                    println("response not successful")
                }
            }

            override fun onFailure(call: Call<CurrentWeatherResponse>, t: Throwable) {
                println("Failed" + t.message)
            }

        })




//        println("Model lat: " + lat)
//        println("Model long: " + long)



    }

}