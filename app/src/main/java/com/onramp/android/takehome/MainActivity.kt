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

        if(user  == "" || user == null){
            presenter.start()
        } else {
            presenter.startLoggedIn(user, units!!)
        }

        println("on create: " + sharedPref!!.getString("name", ""))

        //  get the braodcast receiver
        broadcastReceiver = LocationBroadcastReceiver()

        nextBtn.setOnClickListener{

            presenter.onBtnClick()

        }

        getForecastBtn.setOnClickListener() {

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

        var welcome_text = ""
        var welcome_msg = ""
        var enter_name = ""
        var choose_units = ""
        var metric_text = ""
        var imperial_text = ""
        var next_btn = ""

        welcome_text = "Welcome to Quickcast!"
        welcome_msg = "Thanks for using our app! Since this is your first time here, " +
                    "please set your preferences below."
        enter_name = "Enter your name"
        choose_units = "Choose Units"
        metric_text = "Metric"
        imperial_text = "Imperial"
        next_btn = "Save Info"

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
        //var next_btn = ""
        val get_forecastBtn = "Get Forecast"

        //next_btn = "Get Forecast"

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

        val snackbar = Snackbar.make(relativeLayout, "Fields cannot be empty.", Snackbar.LENGTH_LONG)
        snackbar.show()

    }

    override fun checkFields(): Boolean {

        return !(firstName.text.toString() == "" || unitsradiogroup.getCheckedRadioButtonId() == -1)

    }

    override fun startNewActivity(location: Location){

        val lat = location.latitude.toString()
        val lon = location.longitude.toString()

        val intent = Intent(applicationContext, WeatherActivity::class.java)
        intent.putExtra("latitude", lat)
        intent.putExtra("longitude", lon)

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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)
    }

}