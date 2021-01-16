package com.onramp.android.takehome

/**NOTES:
 * Move location requests to weather activity page
 * get name & imperial units into shared preferences from main page
 * if they have logged in, go straight to weather activity
 */
import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

val PERMISSION_REQUEST_CODE = 200

class MainActivity : AppCompatActivity(), MainContract.View {

    private var locationServiceBound = false

    private var locationService: LocationService? = null

    private lateinit var broadcastReceiver: LocationBroadcastReceiver

    private val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as LocationService.LocalBinder
            locationService = binder.service
            locationServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            locationService = null
            locationServiceBound = false
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*if the shared preferences are true (ie they have signed in before, show a welcome
        message with their name and option to change units.
        if the shared preferences are not true, show welcome message where user inputs name
        and chooses units.
         */

        //  get the braodcast receiver
        broadcastReceiver = LocationBroadcastReceiver()

        nextBtn.setOnClickListener{

            if(permissionApproved()){

                locationService?.subscribeToLocationUpdates() ?: println("location service not bound")

            } else {
                requestLocationPermissions()
            }

        }

    }

    override fun onStart(){
        super.onStart()

        val serviceIntent = Intent(this, LocationService::class.java)
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    //VIEW FUNCTIONS

    override fun createSnackbar(){

        val snackbar = Snackbar.make(relativeLayout, "Fields cannot be empty.", Snackbar.LENGTH_LONG)
        snackbar.show()

    }

    override fun setUpUIFirstTimeLogin(){

        val welcome_text = "Welcome to Quickcast!"
        val welcome_msg = "Thanks for using our app! Since this is your first time here, " +
                "please set your preferences below."
        val enter_name = "Enter your name"
        val choose_units = "Choose Units"
        val metric_text = "Metric"
        val imperial_text = "Imperial"
        val next_btn = "Get Forecast"


        welcomeText.text = welcome_text
        welcomeMsg.text = welcome_msg
        firstName.hint = enter_name
        units.hint = choose_units
        metric.text = metric_text
        imperial.text = imperial_text
        nextBtn.text = next_btn
    }

    //PRIVATE FUNCTIONS - LOCATION SERVICE

    private fun permissionApproved(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun requestLocationPermissions(){

            println("request permission")
            ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSION_REQUEST_CODE
            )

    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(
                LocationBroadcastReceiver(),
                IntentFilter(
                        LocationService.ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST)
        )
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
               LocationBroadcastReceiver()
        )
        super.onPause()
    }

    override fun onStop() {
        if (locationServiceBound) {
            unbindService(serviceConnection)
            locationServiceBound = false
        }

        super.onStop()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            PERMISSION_REQUEST_CODE -> when {
                grantResults.isEmpty() ->
                    println("User interaction cancelled.")
                grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                    println("permission granted")
                    locationService?.subscribeToLocationUpdates()
                }
            } else -> {
                println("permission denied")
            }
        }
    }

    private inner class LocationBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {

            println("received broadcast")

            val location = intent!!.getParcelableExtra<Location>(
                    LocationService.EXTRA_LOCATION
            )

            println("recevied location: " + location)

            if(location != null) {

                startNewActivity(location)

            }
        }

    }

    fun startNewActivity(location: Location){

        val lat = location.latitude.toString()
        val lon = location.longitude.toString()

        val intent = Intent(applicationContext, WeatherActivity::class.java)
        intent.putExtra("latitude", lat)
        intent.putExtra("longitude", lon)

        startActivity(intent)

    }


//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.menu_main, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        val id = item.itemId
//        return if (id == R.id.action_settings) {
//            true
//        } else super.onOptionsItemSelected(item)
//    }

}