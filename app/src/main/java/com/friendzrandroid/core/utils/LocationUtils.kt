package com.friendzrandroid.core.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.friendzrandroid.R
import com.friendzrandroid.home.MainViewModel
import com.friendzrandroid.home.dialog.ConfirmationDialog.ConfirmationDialog
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*

class LocationUtils(val activity: AppCompatActivity, val viewModel: MainViewModel) {


    fun locationStatus() {

        if (!checkLocationPermissionEnabled()) {

            ConfirmationDialog(
                activity,
                activity.resources.getString(R.string.location_permission_text),
                true
            ).showDialog {
                if (it == 1)
                    requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)

            }
            viewModel.isLocationEnabled.value = false

        } else if (!isGpsEnabled()) {
            buildAlertMessageNoGps()
            viewModel.isLocationEnabled.value = false
        } else {

            getDeviceLocation()
            viewModel.isLocationEnabled.value = true
        }

    }

    fun subScribeReceiver() {
        activity.registerReceiver(
            gpsReceiver,
            IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        )

    }


    fun UnRegisterReceiver() {
        activity.unregisterReceiver(gpsReceiver)
    }

    private val gpsReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.action!!.matches(LocationManager.PROVIDERS_CHANGED_ACTION.toRegex())) {
                getDeviceLocation()
            }
        }
    }


    private fun getDeviceLocation() {

        val mLocationManager =
            activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val mFusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(activity)

        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED

            && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED

            && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED

        ) {
            return
        }

        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            mFusedLocationProviderClient.lastLocation.addOnCompleteListener(activity) { task ->
//                var location: Location? = task.result
                if (task.isSuccessful) {
                    val currentLocation = task.result as Location?
                    if (currentLocation != null) {
                        viewModel.currentLocation.value = currentLocation
//                        Toast.makeText(activity,"${currentLocation.latitude} ${currentLocation.longitude}",
//                            Toast.LENGTH_SHORT).show()
                    } else {
                        requestNewLocationData()
                    }
                }
            }

        } else {
            if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                buildAlertMessageNoGps()
            }
        }
    }


    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {

        val mLocationRequest = LocationRequest.create()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        val mFusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(activity)

        Looper.myLooper()?.let {
            mFusedLocationProviderClient.requestLocationUpdates(
                mLocationRequest, object : LocationCallback() {
                    override fun onLocationResult(var1: LocationResult) {
                        var1?.locations?.get(0)?.let {
                            viewModel.currentLocation.value = it
                            //                        Toast.makeText(activity,"${it.latitude} ${it.longitude}", Toast.LENGTH_SHORT).show()

                            //it.latitude, it.longitude
                        }
                    }
                },
                it
            )
        }
    }

    private fun isGpsEnabled(): Boolean {
        val contentResolver: ContentResolver = activity.contentResolver
        // Find out what the settings say about which providers are enabled
        //  String locationMode = "Settings.Secure.LOCATION_MODE_OFF";
        val mode = Settings.Secure.getInt(
            contentResolver,
            Settings.Secure.LOCATION_MODE,
            Settings.Secure.LOCATION_MODE_OFF
        )
        return mode != Settings.Secure.LOCATION_MODE_OFF
    }


    private fun checkLocationPermissionEnabled(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }


    private fun buildAlertMessageNoGps() {
        var builder = AlertDialog.Builder(activity)

        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id ->
                if (!checkLocationPermissionEnabled()) {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.parse("package:" + activity.packageName)
                    getResults.launch(intent)
                } else {
                    enableLocationSettings()
                }
                dialog.dismiss()
            })
        var alert = builder.create()
        alert.setCancelable(true)
        alert.show()
    }


    private val LOCATION_UPDATE_INTERVAL = 300L
    private val LOCATION_UPDATE_FASTEST_INTERVAL = 300L

    private fun enableLocationSettings() {
        val locationRequest = LocationRequest.create()
            .setInterval(LOCATION_UPDATE_INTERVAL)
            .setFastestInterval(LOCATION_UPDATE_FASTEST_INTERVAL)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        val builder: LocationSettingsRequest.Builder =
            LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        LocationServices
            .getSettingsClient(activity)
            .checkLocationSettings(builder.build())
            .addOnSuccessListener(activity) { response: LocationSettingsResponse? ->

            }
            .addOnFailureListener(activity) { ex ->
                if (ex is ResolvableApiException) {
                    // Location settings are NOT satisfied,  but this can be fixed  by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),  and check the result in onActivityResult().
                        val resolvable: ResolvableApiException = ex as ResolvableApiException
                        val intentSenderRequest =
                            IntentSenderRequest.Builder(resolvable.resolution).build()
                        resolutionForResult.launch(intentSenderRequest)
                    } catch (sendEx: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }
                }
            }
    }


    private val getResults =
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                enableLocationSettings()
                // showViewDependOnGps()
            }

        }


    private val resolutionForResult =
        activity.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { activityResult ->
            if (activityResult.resultCode == AppCompatActivity.RESULT_OK) {
                viewModel.isLocationEnabled.value = true
                Toast.makeText(activity, "Location is enabled", Toast.LENGTH_SHORT).show()
                getDeviceLocation()
            } else {
                viewModel.isLocationEnabled.value = false
                Toast.makeText(
                    activity,
                    "we can't determine your location",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private val requestLocationPermission =
        activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                if (!isGpsEnabled()) {
                    buildAlertMessageNoGps()
                } else
                    updateFeedAfterPermission()
            }
        }

    private fun updateFeedAfterPermission() {
        val appReceiverIntent = Intent("refresh-feed")
        LocalBroadcastManager.getInstance(activity).sendBroadcast(appReceiverIntent)
    }
}