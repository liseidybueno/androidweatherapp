package com.onramp.android.takehome.main

import android.app.*
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.*
import java.util.concurrent.TimeUnit

class LocationService : Service() {

    private var serviceRunningInForeground = false

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var locationRequest: LocationRequest

    private lateinit var locationCallback: LocationCallback

    private var currentLocation: Location? = null

    private val localBinder = LocalBinder()

    companion object {
        val ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST =
                "com.onramp.android.takehome.action.FOREGROUND_ONLY_LOCATION_BROADCAST"
        internal const val EXTRA_LOCATION = "com.onramp.android.takehome.extra.LOCATION"
    }

    override fun onCreate(){

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest().apply{
            interval = TimeUnit.SECONDS.toMillis((1))
            fastestInterval = TimeUnit.SECONDS.toMillis(.05.toLong())
            numUpdates = 1
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                super.onLocationResult(p0)
                println("last location: " + p0?.lastLocation)
                if(p0?.lastLocation != null){
                    currentLocation = p0.lastLocation

                    println("Current location: " + currentLocation)

                    val intent = Intent(ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST)
                    intent.putExtra(EXTRA_LOCATION, currentLocation)
                    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)

                } else {
                    println("Location missing in callback.")
                }
            }
        }

    }

    override fun onBind(intent: Intent?): IBinder? {
        stopForeground(true)
        serviceRunningInForeground = false
        println("local binder")
        return localBinder
    }

    fun subscribeToLocationUpdates(){

        startService(Intent(applicationContext, LocationService::class.java))

        try {
            println("Subscribed to update")
            fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest, locationCallback, Looper.myLooper()
            )
        } catch(unlikely: SecurityException){
            println("Lost location permissions.")
        }


    }

    inner class LocalBinder : Binder(){
        internal val service: LocationService
            get() = this@LocationService
    }


}