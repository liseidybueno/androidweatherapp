package com.onramp.android.takehome

/**NOTES:
 * Move location requests to weather activity page
 * get name & imperial units into shared preferences from main page
 * if they have logged in, go straight to weather activity
 */
import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioButton
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

    private lateinit var presenter: MainContract.Presenter

    private var sharedPref: SharedPreferences ?= null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*if the shared preferences are true (ie they have signed in before, show a welcome
        message with their name and option to change units.
        if the shared preferences are not true, show welcome message where user inputs name
        and chooses units.
         */


        presenter = MainPresenter(this)
        sharedPref = getSharedPreferences("loggedIn", Context.MODE_PRIVATE)

        val user = sharedPref!!.getString("name", "")
        val units = sharedPref!!.getString("units", "")

       // println(AppPref.name)
//        AppPref.setup(this)

//        val user = AppPref.name
//        val units = AppPref.units
//
        if(user  == "" || user == null){
            presenter.start()
        } else {
            presenter.startLoggedIn(user, units!!)
        }

//        println("on create: " + sharedPref!!.getString("name", ""))

        //  get the braodcast receiver
        broadcastReceiver = LocationBroadcastReceiver()

        nextBtn.setOnClickListener{

            presenter.onBtnClick()

        }

        getForecastBtn.setOnClickListener {

            presenter.startWeatherActivity()
        }

    }

    override fun onStart(){
        super.onStart()

        val serviceIntent = Intent(this, LocationService::class.java)
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    //VIEW FUNCTIONS
    override fun getPermissions(){

        if(permissionApproved()){

            locationService?.subscribeToLocationUpdates() ?: println("location service not bound")

        } else {

            requestLocationPermissions()
        }
    }

    override fun setUpUINotLoggedIn(){

        val welcome_text = "Welcome to Quickcast!"
        val welcome_msg = "Thanks for using our app! Since this is your first time here, " +
                    "please set your preferences below."
        val enter_name = "Enter your name"
        val choose_units = "Choose Units"
        val metric_text = "Metric"
        val imperial_text = "Imperial"
        val next_btn = "Save Info"

        welcomeMsg.text = welcome_msg
        firstName.hint = enter_name
        welcomeText.text = welcome_text
        unitsText.hint = choose_units
        metric.text = metric_text
        imperial.text = imperial_text
        nextBtn.text = next_btn

        getForecastBtn.visibility = View.GONE
    }

    override fun setUpUILoggedIn(user: String, units: String){
        val welcome_text = "Welcome back, $user!"
        val welcome_msg = "Your units are set to $units"
        val get_forecastBtn = "Get Forecast"

        welcomeMsg.text = welcome_msg
        firstName.visibility = View.GONE
        unitsText.visibility = View.GONE
        welcomeText.text = welcome_text
        unitsradiogroup.visibility = View.GONE
        nextBtn.visibility = View.GONE
        getForecastBtn.text = get_forecastBtn

    }

    override fun changeUiOnBtnClick(user: String, units: String){

        val thankYouText = "Thanks, $user. \n You preferred units are $units."
        val get_forecastBtn = "Get Forecast"

        welcomeMsg.text = thankYouText
        nextBtn.visibility = View.GONE
        getForecastBtn.visibility = View.VISIBLE
        getForecastBtn.text = get_forecastBtn

        firstName.visibility = View.GONE
        unitsradiogroup.visibility = View.GONE
        unitsText.visibility = View.GONE

        // store into shared preferences
        sharedPref!!.edit().putString("name", user).apply()
        sharedPref!!.edit().putString("units", units).apply()
//        AppPref.name = user
//        AppPref.units = units

    }

    override fun getName(): String{

        val name = firstName
        val firstName = name.text.toString()

        return firstName
    }

    override fun getUnits(): String{
        val radioGroupId = unitsradiogroup.checkedRadioButtonId
        val radioGroup:RadioButton = findViewById(radioGroupId)
        val units = radioGroup.text.toString()

        return units
    }

    override fun createSnackbar(){

        val snackbar = Snackbar.make(mainActivity, "Fields cannot be empty.", Snackbar.LENGTH_LONG)
        snackbar.show()

    }

    override fun checkFields(): Boolean {

        return !(firstName.text.toString() == "" || unitsradiogroup.getCheckedRadioButtonId() == -1)

    }

    fun startNewActivity(){

        val intent = Intent(applicationContext, WeatherActivity::class.java)
        startActivity(intent)

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
        println(grantResults[0])
        when(requestCode) {
            PERMISSION_REQUEST_CODE -> when {
                grantResults.isEmpty() ->
                    println("User interaction cancelled.")
                grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                    println("permission granted")
                    locationService?.subscribeToLocationUpdates()
                }
              else -> {
                  println("permission denied")
                  //create snackbar if permission is denied
                  Snackbar.make(
                          findViewById(R.id.mainActivity),
                          "Location service permission is off. Please turn on to use this app.",
                          Snackbar.LENGTH_LONG
                  )
                          .setAction("GO TO SETTINGS") {
                              //go to app settings
                              val intent = Intent()
                              intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                              val uri = Uri.fromParts(
                                      "package",
                                      BuildConfig.APPLICATION_ID,
                                      null
                              )
                              intent.data = uri
                              intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                              startActivity(intent)
                          }
                          .show()
              }
            }
        }
    }

    inner private class LocationBroadcastReceiver : BroadcastReceiver() {

        private var sharedPref: SharedPreferences?= null


        override fun onReceive(context: Context?, intent: Intent?) {

            sharedPref = getSharedPreferences("loggedIn", Context.MODE_PRIVATE)

            println("received broadcast")

            val location = intent!!.getParcelableExtra<Location>(
                    LocationService.EXTRA_LOCATION
            )

            println("recevied location: " + location)

            if(location != null) {

                sharedPref!!.edit().putString("lat", location.latitude.toString()).apply()
                sharedPref!!.edit().putString("lon", location.longitude.toString()).apply()
                startNewActivity()

            }
        }

    }
}