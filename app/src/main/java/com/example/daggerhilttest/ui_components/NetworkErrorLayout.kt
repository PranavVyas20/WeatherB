package com.example.daggerhilttest.ui_components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.daggerhilttest.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.reflect.KSuspendFunction2

@Composable
fun NetworkErrorLayout(
    currentLat: Float,
    currentLong: Float,
    onRetry: suspend (Float, Float) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.oops),
            contentDescription = "network_error_img",
        )
        Text(text = "Network Error", modifier = Modifier.align(Alignment.CenterHorizontally))
        Button(
            onClick = {
                coroutineScope.launch {
                    onRetry(currentLat, currentLong)
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Retry")
        }

    }
}