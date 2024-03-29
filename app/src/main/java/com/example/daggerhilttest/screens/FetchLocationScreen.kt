package com.example.daggerhilttest.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentSender
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.daggerhilttest.R
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

@Composable
fun FetchLocationScreen(onLocationFetch: (lat: Double, lng: Double) -> Unit) {
    val context = LocalContext.current
    val activity = remember {
        context.findActivity()
    }
    val locationProviderClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
    )
    var flag by remember {
        mutableIntStateOf(0)
    }
    var showButton by remember {
        mutableStateOf(false)
    }
    val enableLocationDialogLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartIntentSenderForResult(),
            onResult = { activityResult ->
                if (activityResult.resultCode == RESULT_OK) {
                    getCurrentLocation(locationClient = locationProviderClient,
                        context = context,
                        locationCallback = { lat, lng ->
                            Toast.makeText(context, "$lat, $lng", Toast.LENGTH_SHORT).show()
                            onLocationFetch(lat, lng)
                        })
                } else {
                    Toast.makeText(context, "Location is required to continue", Toast.LENGTH_SHORT).show()
                    flag++
                }
            })

    val locationPermissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions(),
            onResult = { permissions ->
                val permissionsGranted = permissions.values.reduce { acc, isPermissionGranted ->
                    acc && isPermissionGranted
                }
                if (permissionsGranted) {
                    displayLocationSettingsRequest(context = context,
                        intentSenderRequestCallback = {
                            enableLocationDialogLauncher.launch(it)
                        },
                        locationPresentCallback = {
                            getCurrentLocation(locationClient = locationProviderClient,
                                context = context,
                                locationCallback = { lat, lng ->
                                    Toast.makeText(context, "$lat, $lng", Toast.LENGTH_SHORT).show()
                                    onLocationFetch(lat, lng)
                                })
                        })
                } else {
                    if (activity?.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) == true) {
                        Toast.makeText(context, "else", Toast.LENGTH_SHORT).show()
                        flag++
                        return@rememberLauncherForActivityResult
                    } else {
                        showButton = true
                        // open settings
                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", context.packageName, null)
                        )
                        context.startActivity(intent)
                    }
                }
            })

    LaunchedEffect(key1 = flag) {
        locationPermissionLauncher.launch(locationPermissions)
    }
    Box(modifier = Modifier.fillMaxSize().background(color = Color(0xFFDED0E5)), contentAlignment = Alignment.Center) {
        Image(painter = painterResource(id = R.drawable.overcast_splash), contentDescription = "splash_image")
    }
    if (showButton) {
        Button(onClick = { locationPermissionLauncher.launch(locationPermissions) }) {
            Text("Enable location by settings")
        }
    }

}

// Call this function only when the location has been turned on
@SuppressLint("MissingPermission")
private fun getCurrentLocation(
    locationClient: FusedLocationProviderClient,
    context: Context,
    locationCallback: (Double, Double) -> Unit,
) {
    // Maybe need to check the location permission, (may cover some edge case)
    locationClient.getCurrentLocation(
        Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token
    ).addOnSuccessListener { location ->
        locationCallback(location.latitude, location.longitude)
    }.addOnFailureListener { exception ->
        Toast.makeText(context, "error fetching location", Toast.LENGTH_SHORT).show()
    }
}

fun displayLocationSettingsRequest(
    context: Context,
    intentSenderRequestCallback: (IntentSenderRequest) -> Unit,
    locationPresentCallback: () -> Unit
) {
    val activity = context.findActivity()
    activity?.let {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        LocationServices.getSettingsClient(it).checkLocationSettings(builder.build())
            .addOnSuccessListener { response ->
                val states = response.locationSettingsStates
                states?.let { locationSettingsStates ->
                    if (locationSettingsStates.isLocationPresent) {
                        locationPresentCallback()
                    }
                }
            }.addOnFailureListener { e ->
                if (e is ResolvableApiException) {
                    try {
                        val intentSenderRequest = IntentSenderRequest.Builder(e.resolution).build()
                        intentSenderRequestCallback(intentSenderRequest)
                    } catch (ex: IntentSender.SendIntentException) {
                        // Ignore the error.
                        Log.d("exception_tag", "$ex")
                        ex.printStackTrace()
                    }
                }
            }
    }
}

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

@Preview
@Composable
fun SplashPreview() {
    Box(modifier = Modifier.fillMaxSize().background(color = Color(0xFFDED0E5)), contentAlignment = Alignment.Center) {
        Image(painter = painterResource(id = R.drawable.overcast_splash), contentDescription = "splash_image")
    }
}
