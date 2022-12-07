package com.mansao.getlocationapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mansao.getlocationapp.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        binding.btnGetLocation.setOnClickListener {
            getLocation()
        }
    }

    private fun isLocationIsEnable(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), permissionId
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionId) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getLocation() {
        if (checkPermission()) {
            if (isLocationIsEnable()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geoCoder = Geocoder(this, Locale.getDefault())
                        val list: MutableList<Address>? =
                            geoCoder.getFromLocation(location.latitude, location.longitude, 1)
                        binding.apply {
                            tvLatitude.text = "Latitude \n ${list?.get(0)?.latitude}"
                            Log.d(TAG, "Latitude : ${list?.get(0)?.latitude}")

                            tvLongitude.text = "Longitude \n ${list?.get(0)?.longitude}"
                            Log.d(TAG, "Latitude : ${list?.get(0)?.longitude}")

                            tvCountry.text = "Country \n ${list?.get(0)?.countryName}"
                            tvLocality.text = "City \n ${list?.get(0)?.locality}"
                            tvAddress.text = "Address \n ${list?.get(0)?.getAddressLine(0)}"
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Please Turn on location", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermission()
        }
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}