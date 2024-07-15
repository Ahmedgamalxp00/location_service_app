package com.example.location_service_app

import android.location.GnssStatus
import android.location.LocationManager
import android.location.Location
import android.os.Bundle
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import android.content.Context
import android.Manifest
import android.content.pm.PackageManager

import android.location.LocationListener
import androidx.core.app.ActivityCompat
import io.flutter.plugin.common.MethodCall
import android.os.Build
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationRequest
import androidx.annotation.RequiresApi






class MainActivity: FlutterActivity() {
    private val CHANNEL = "com.example.location_service_app/location"
    private lateinit var locationManager: LocationManager
    private lateinit var gnssStatusCallback: GnssStatus.Callback
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var methodChannel: MethodChannel


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
       
        // gnssStatusCallback = object : GnssStatus.Callback() {
        //     override fun onStarted() {
        //         super.onStarted()
        //         sendGnssStatus()
        //     }

        //     override fun onStopped() {
        //         super.onStopped()
        //         sendGnssStatus()
        //     }

        //     override fun onFirstFix(ttffMillis: Int) {
        //         super.onFirstFix(ttffMillis)
        //         sendGnssStatus()
        //     }

        //     override fun onSatelliteStatusChanged(status: GnssStatus) {
        //         super.onSatelliteStatusChanged(status)
        //         sendGnssStatus()
        //     }
        // }

        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        //     locationManager.registerGnssStatusCallback(gnssStatusCallback)
        // }
    }

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler {
            call, result ->
             when (call.method) {
                
                 "GnssStatus" -> {
                    val provider = call.argument<String>("provider")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    sendGnssStatus(provider, result)
                    } else {
                    result.error("UNSUPPORTED_API_LEVEL", "GNSS not supported on this device", null)
                    }
                }
                "getLocationProvider" -> {
                   val provider = getLocationProvider()
                result.success(provider)
                }
                // "startGnssStatusMonitoring" -> {
                //     startGnssStatusMonitoring()
                //     result.success("Started GNSS status monitoring")
                // }

                "getGPSLocation"-> {
                val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
                } else {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, object : LocationListener {
                        override fun onLocationChanged(location: Location) {
                            val lat = location.latitude
                            val lon = location.longitude
                            val accuracy = location.accuracy
                result.success(mapOf("latitude" to lat, "longitude" to lon, "accuracy" to accuracy))
                locationManager.removeUpdates(this)
                        }
                        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                        override fun onProviderEnabled(provider: String) {}
                        override fun onProviderDisabled(provider: String) {}
                    })}
                }
                "getCurrentLocation" -> {
                    getCurrentLocation(result)
                }
               
                // "startSpecificGnssMonitoring" -> {
                //     val gnssType = call.argument<String>("gnssType")
                //     startSpecificGnssMonitoring(gnssType)
                //     result.success("Started GNSS status monitoring for $gnssType")
                // }
                else -> {
                    result.notImplemented()
                }
            }
        }
    }
   
    private fun getLocationProvider(): String {
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        return if (isGpsEnabled) "GPS" else "Other"
    }

    // private fun startGnssStatusMonitoring() {
    //     gnssStatusCallback = object : GnssStatus.Callback() {
    //         override fun onSatelliteStatusChanged(status: GnssStatus) {
    //             for (i in 0 until status.satelliteCount) {
    //                 val constellationType = status.getConstellationType(i)
    //                 val constellationName = when (constellationType) {
    //                     GnssStatus.CONSTELLATION_GPS -> "GPS or NAVSTAR"
    //                     GnssStatus.CONSTELLATION_GLONASS -> "GLONASS"
    //                     GnssStatus.CONSTELLATION_BEIDOU -> "BeiDou"
    //                     GnssStatus.CONSTELLATION_GALILEO -> "Galileo"
    //                     else -> "Other"
    //                 }
    //                 println("Satellite $i: $constellationName")
    //             }
    //         }
    //     }
    //     locationManager.registerGnssStatusCallback(gnssStatusCallback)
    // }
    // private fun startGnssStatusMonitoring() {
    //     gnssStatusCallback = object : GnssStatus.Callback() {
    //         override fun onSatelliteStatusChanged(status: GnssStatus) {
    //             val satelliteInfoList = mutableListOf<Map<String, Any>>()
    //             for (i in 0 until status.satelliteCount) {
    //                 val constellationType = status.getConstellationType(i)
    //                 val constellationName = when (constellationType) {
    //                     GnssStatus.CONSTELLATION_GPS -> "GPS"
    //                     GnssStatus.CONSTELLATION_GLONASS -> "GLONASS"
    //                     GnssStatus.CONSTELLATION_BEIDOU -> "BeiDou"
    //                     GnssStatus.CONSTELLATION_GALILEO -> "Galileo"
    //                     else -> "Other"
    //                 }
    //                 val satelliteInfo = mapOf(
    //                     "index" to i,
    //                     "constellationName" to constellationName
    //                 )
    //                 satelliteInfoList.add(satelliteInfo)
    //             }
    //             methodChannel.invokeMethod("updateGnssStatus", satelliteInfoList)
    //         }
    //     }
    //     locationManager.registerGnssStatusCallback(gnssStatusCallback)
    // }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.unregisterGnssStatusCallback(gnssStatusCallback)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locationManager.unregisterGnssStatusCallback(gnssStatusCallback)
        }
    }
    // private fun getLocationProviders(): List<String> {
    //     val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    //     return locationManager.allProviders
    // }

    //  private fun startSpecificGnssMonitoring(gnssType: String?) {
    //     val gnssConstellationType = when (gnssType) {
    //         "GPS" -> GnssStatus.CONSTELLATION_GPS
    //         "GLONASS" -> GnssStatus.CONSTELLATION_GLONASS
    //         "BeiDou" -> GnssStatus.CONSTELLATION_BEIDOU
    //         "Galileo" -> GnssStatus.CONSTELLATION_GALILEO
    //         else -> GnssStatus.CONSTELLATION_UNKNOWN
    //     }

    //     gnssStatusCallback = object : GnssStatus.Callback() {
    //         override fun onSatelliteStatusChanged(status: GnssStatus) {
    //             for (i in 0 until status.satelliteCount) {
    //                 if (status.getConstellationType(i) == gnssConstellationType) {
    //                     val satelliteId = status.getSvid(i)
    //                     val signalStrength = status.getCn0DbHz(i)
    //                     println("Satellite ID: $satelliteId, Signal Strength: $signalStrength")
    //                 }
    //             }
    //         }
    //     }
    //     locationManager.registerGnssStatusCallback(gnssStatusCallback)
    // }



    private fun getCurrentLocation(result: MethodChannel.Result) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 1000
            fastestInterval = 1000
            numUpdates = 1
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location: Location? = locationResult.lastLocation
                if (location != null) {
                    val locationData = "${location.latitude},${location.longitude},${location.accuracy}"
                    result.success(locationData)
                    fusedLocationClient.removeLocationUpdates(this)
                } else {
                    result.error("UNAVAILABLE", "Location not available", null)
                }
            }

        }, mainLooper)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun sendGnssStatus(provider: String?, result: MethodChannel.Result? = null) {
        val gnssStatusList = mutableListOf<Map<String, Any>>()

        locationManager.registerGnssStatusCallback(object : GnssStatus.Callback() {
            override fun onSatelliteStatusChanged(status: GnssStatus) {
                val satelliteCount = status.satelliteCount
                for (index in 0 until satelliteCount) {
                    val constellationType = when (status.getConstellationType(index)) {
                        GnssStatus.CONSTELLATION_GPS -> "GPS"
                        GnssStatus.CONSTELLATION_SBAS -> "SBAS"
                        GnssStatus.CONSTELLATION_GLONASS -> "GLONASS"
                        GnssStatus.CONSTELLATION_QZSS -> "QZSS"
                        GnssStatus.CONSTELLATION_BEIDOU -> "BeiDou"
                        GnssStatus.CONSTELLATION_GALILEO -> "Galileo"
                        GnssStatus.CONSTELLATION_IRNSS -> "IRNSS"
                        else -> "Unknown"
                    }

                    if (provider == null || constellationType == provider) {
                        val satelliteMap = mapOf(
                            "constellationType" to constellationType,
                            "svid" to status.getSvid(index).toString(),
                            "cn0DbHz" to status.getCn0DbHz(index).toString(),
                            "azimuthDegrees" to status.getAzimuthDegrees(index).toString(),
                            "elevationDegrees" to status.getElevationDegrees(index).toString(),
                            "hasAlmanacData" to status.hasAlmanacData(index).toString(),
                            "hasEphemerisData" to status.hasEphemerisData(index).toString(),
                            "usedInFix" to status.usedInFix(index).toString()
                        )
                        gnssStatusList.add(satelliteMap)
                    }
                }
                result?.success(gnssStatusList)
                locationManager.unregisterGnssStatusCallback(this)
            }
        })
    }
//    @RequiresApi(Build.VERSION_CODES.N)
//     private fun sendGnssStatus(result: MethodChannel.Result? = null) {
//         val gnssStatusList = mutableListOf<Map<String, Any>>()

//         locationManager.registerGnssStatusCallback(object : GnssStatus.Callback() {
//             override fun onSatelliteStatusChanged(status: GnssStatus) {
//                 val satelliteCount = status.satelliteCount
//                 for (index in 0 until satelliteCount) {
//                     val constellationType = when (status.getConstellationType(index)) {
//                         GnssStatus.CONSTELLATION_GPS -> "GPS(NAVSTAR)"
//                         GnssStatus.CONSTELLATION_SBAS -> "SBAS"
//                         GnssStatus.CONSTELLATION_GLONASS -> "GLONASS"
//                         GnssStatus.CONSTELLATION_QZSS -> "QZSS"
//                         GnssStatus.CONSTELLATION_BEIDOU -> "BeiDou"
//                         GnssStatus.CONSTELLATION_GALILEO -> "Galileo"
//                         GnssStatus.CONSTELLATION_IRNSS -> "IRNSS"
//                         else -> "Unknown"
//                     }

//                     val satelliteMap = mapOf(
//                         "constellationType" to constellationType,
//                         "svid" to status.getSvid(index).toString(),
//                         "cn0DbHz" to status.getCn0DbHz(index).toString(),
//                         "azimuthDegrees" to status.getAzimuthDegrees(index).toString(),
//                         "elevationDegrees" to status.getElevationDegrees(index).toString(),
//                         "hasAlmanacData" to status.hasAlmanacData(index).toString(),
//                         "hasEphemerisData" to status.hasEphemerisData(index).toString(),
//                         "usedInFix" to status.usedInFix(index).toString()
//                     )
//                     gnssStatusList.add(satelliteMap)
//                 }
//                 result?.success(gnssStatusList)
//                 locationManager.unregisterGnssStatusCallback(this)
//             }
//         })
//     }
}

    
    
    