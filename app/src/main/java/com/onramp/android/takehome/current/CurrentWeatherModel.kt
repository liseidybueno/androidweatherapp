package com.onramp.android.takehome.current

import com.onramp.android.takehome.App.App
import com.onramp.android.takehome.R
import com.onramp.android.takehome.networking.CurrentWeatherResponse
import com.onramp.android.takehome.networking.OWDEndpoints
import com.onramp.android.takehome.networking.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class CurrentWeatherModel : CurrentWeatherContract.Model {

    data class CurrentWeatherData(val name: String, val date: String, val temp: String,
                                  val feelsLike: String, val minMax: String, val sunrise: String,
                                  val sunset: String, val humidity: String, val visibility: String,
                                  val mainImg: Int, val weatherImg: Int, val message: String, val description: String)

    override fun getCurrentWeatherData(presenter: CurrentWeatherContract.Presenter) {

        val coord = presenter.getData()

        val lat = coord?.get(0).toString()
        val long = coord?.get(1).toString()
        val units = coord?.get(2).toString().toLowerCase(Locale.ROOT)
        val api_key = App.getResource().getString(R.string.api_key)

        val request = ServiceBuilder.buildService(OWDEndpoints::class.java)

        val call = request.getCurrentWeatherData(lat, long, units, api_key)

        call.enqueue(object : Callback<CurrentWeatherResponse>{

            override fun onResponse(call: Call<CurrentWeatherResponse>, response: Response<CurrentWeatherResponse>){

                if(response.isSuccessful){

                    println("Success!")

                    val data = response.body()
                    val weather = data?.weather
                    val main = data?.main
                    val sys = data?.sys
                    val name = data?.name.toString()
                    val visibility = data?.visibility

                    val main_desc = weather?.get(0)?.main
                    val description = weather?.get(0)?.description?.capitalize()

                    val temp = main?.temp?.roundToInt()
                    val tempLow = main?.temp_min?.roundToInt()
                    val tempHigh = main?.temp_max?.roundToInt()
                    val feelsLike = main?.feels_like?.roundToInt()
                    val humidity = main?.humidity?.roundToInt()

                    val sunrise = sys?.sunrise
                    val sunset = sys?.sunset

                    //create data object

                    //date
                    val unformattedDate = data!!.dt
                    val sdf = SimpleDateFormat("MMMM DD")
                    val newDate = Date(unformattedDate * 1000)
                    val date = sdf.format(newDate).toString() + "in $name"

                    //visibility
                    var visibilityText = ""
                    var visibilityFormatted = 0
                    if(units == "imperial"){
                        if (visibility != null) {
                            visibilityFormatted = (visibility / 1609).toInt()
                        }
                    } else {
                        if(visibility != null) {
                            visibilityFormatted = (visibility / 1000).toInt()
                        }
                    }
                    visibilityText = "Visibility: $visibilityFormatted"

                    //temp
                    var tempText = ""
                    if(units == "imperial"){
                        tempText = "$temp°F"
                    } else {
                        tempText = "$temp°C"
                    }

                    //feels like
                    var feelsLikeText = ""
                    if(units == "imperial"){
                        feelsLikeText = "Feels Like: $feelsLike°F"
                    } else {
                        feelsLikeText = "Feels Like: $feelsLike°C"
                    }

                    //low high
                    var lowHighText = ""
                    if(units == "imperial"){
                        lowHighText = "Low: $tempLow°F \n High: $tempHigh°F"
                    } else {
                        lowHighText = "Low: $tempLow°C \n High $tempHigh°C "
                    }

                    //humidity
                    val humidityText = "Humidity: $humidity%"

                    //get main img
                    val mainImg = getMainImg(temp!!, main_desc!!, units)

                    //get time of day
                    val timeOfDay = if (unformattedDate > data.sys.sunset) {
                        "pm"
                    } else {
                        "am"
                    }

                    //sunrise/sunset
                    val sunriseFormatted = setTime(sunrise)
                    val sunsetFormatted = setTime(sunset)

                    val sunriseText = "Sunrise: $sunriseFormatted"
                    val sunsetText = "Sunset: $sunsetFormatted"

                    //get weather img
                    val weatherImg = getWeatherImg(timeOfDay, main_desc, description!!)

                    //get weather message
                    val message = getMessage(temp, main_desc, description)

                    //create weather object

                    val currentWeather = CurrentWeatherData(name, date, tempText, feelsLikeText,
                    lowHighText, sunriseText, sunsetText, humidityText, visibilityText, mainImg, weatherImg,
                            message, description)

                    presenter.onSuccess(currentWeather)
                } else {
                    println("response not successful")
                }
            }

            override fun onFailure(call: Call<CurrentWeatherResponse>, t: Throwable) {
                println("Failed" + t.message)
            }

        })


    }

    private fun getWeatherImg(timeOfDay: String, main_desc: String, description: String): Int{

        var img = 0

        if(main_desc == "Thunderstorm"){
            img = R.drawable.storm
        } else if(main_desc == "Drizzle" || main_desc == "Rain"){
            img = if(timeOfDay == "pm"){
                R.drawable.rain_night
            } else {
                R.drawable.rain_day
            }
        } else if(main_desc == "Snow"){
            img = R.drawable.snow
        } else if(main_desc == "Mist" || main_desc == "Smoke" || main_desc == "Haze" ||
                main_desc == "Dust" || main_desc == "Fog" || main_desc == "Sand" ||
                main_desc == "Squall" || main_desc == "Ash" || main_desc == "Tornado"){
            img = if(timeOfDay == "pm"){
                R.drawable.haze_day
            } else {
                R.drawable.fog_night
            }
        } else if(main_desc == "Clear"){
            img = if(timeOfDay == "pm"){
                R.drawable.clear_night
            } else {
                R.drawable.clear_day
            }
        } else if(main_desc == "Clouds"){
            img = if(description == "few clouds" || description == "scattered clouds"){
                if(timeOfDay == "pm"){
                    R.drawable.partlycloudy_night
                } else {
                    R.drawable.partlycloudy_day
                }
            } else {
                if(timeOfDay == "pm"){
                    R.drawable.cloudy_day
                } else {
                    R.drawable.cloudy_night
                }
            }
        }

        return img
    }

    private fun getMainImg(temp: Int, main_desc: String, units:String): Int{

        var img = 0

        //if clear, cloudy, or partly cloudy:
        //if temp < 32 F or 0 C, show coat
        if(main_desc == "Clear" || main_desc == "Clouds"){
            if(units == "farenheit"){
                if(temp < 32){
                    img = R.drawable.coat
                } else if(temp > 32 && temp < 50){
                    img = R.drawable.leatherjacket
                } else if(temp > 50 && temp < 80){
                    img = R.drawable.tshirt
                } else {
                    img = R.drawable.icecream
                }
            } else {
                if(temp < 0){
                    img = R.drawable.coat
                } else if(temp > 0 && temp < 10){
                    img = R.drawable.leatherjacket
                } else if(temp > 10 && temp < 27){
                    img = R.drawable.tshirt
                } else {
                    img = R.drawable.icecream
                }
            }

        } else if(main_desc == "Snow"){
            img = R.drawable.snowboots
        } else if(main_desc == "Thunderstorm" || main_desc == "Drizzle" || main_desc=="Rain"){
            img = R.drawable.redumbrella
        }

        return img

    }

    private fun getMessage(temp: Int, main_desc: String, units:String): String{

        var message = ""

        //if clear, cloudy, or partly cloudy:
        //if temp < 32 F or 0 C, show coat
        if(main_desc == "Clear" || main_desc == "Clouds"){
            if(units == "farenheit"){
                message = if(temp < 32){
                    "Brrr, it's cold! Bundle up!"
                } else if(temp in 33..49){
                    "It's a little chilly - grab a jacket!"
                } else if(temp in 51..79){
                    "It's nice and warm out!"
                } else {
                    "Heat wave!"
                }
            } else {
                message = if(temp < 0){
                    "Brrr, it's cold! Bundle up!"
                } else if(temp in 1..9){
                    "It's a little chilly - grab a jacket!"
                } else if(temp in 11..26){
                    "It's nice and warm out!"
                } else {
                    "Heat wave!"
                }
            }

        } else if(main_desc == "Snow"){
            message = "Grab your snow boots!"
        } else if(main_desc == "Thunderstorm" || main_desc == "Drizzle" || main_desc=="Rain"){
            message = "Grab your umbrella!"
        }

        return message

    }

    private fun setTime(time: Long?): String {
        val nonNullable: Long = time!!
        val sdf = SimpleDateFormat("hh:mm a")
        val newTime = Date(nonNullable * 1000)
        val timeFormatted = sdf.format(newTime)
        return timeFormatted

    }
}