package com.example.daggerhilttest.screens

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.daggerhilttest.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.daggerhilttest.ui.theme.shimmerColor
import com.example.daggerhilttest.ui_components.*
import com.example.daggerhilttest.viewmodels.WeatherViewModel
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun CurrentWeatherScreen(weatherViewModel: WeatherViewModel) {
    val currentWeatherState = weatherViewModel.currentWeatherState.value
    val todayHourlyForecastState = weatherViewModel.todayHourlyForecast.value
    val currentWeatherGraphState = weatherViewModel.currentWeatherGraph.value

    LaunchedEffect(key1 = Unit) {
        val savedLatLong = weatherViewModel.getLatLongFromDataStorePref()
        weatherViewModel.getCurrentWeatherByLatLong(
            savedLatLong.lat!!.toFloat(),
            savedLatLong.long!!.toFloat()
        )
    }

    Column(
        Modifier
            .padding(10.dp)
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ExpandableSearchView(
            locationName = if (currentWeatherState.data != null) currentWeatherState.data.cityName!! else "",
            placeHolderVisibility = currentWeatherState.isLoading,
            "",
            onSearchDisplayClosed = { },
            onSearchDisplayChanged = { }
        )
        CurrentWeatherCard(
            weatherViewModel = weatherViewModel,
            currentWeather = currentWeatherState.data,
            placeHolderVisibility = currentWeatherState.isLoading
        )
        WeatherExtraDetailCard(
            weatherViewModel = weatherViewModel,
            currentWeather = currentWeatherState.data,
            placeHolderVisibility = currentWeatherState.isLoading
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = "Hourly Forecast",
            fontSize = 20.sp,
            style = MaterialTheme.typography.body2,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 10.dp)
        )
        LazyRow(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .placeholder(
                    visible = todayHourlyForecastState.isLoading,
                    color = shimmerColor,
                    shape = RoundedCornerShape(7.dp),
                    highlight = PlaceholderHighlight.shimmer(
                        highlightColor = Color.White,
                    )
                ),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(todayHourlyForecastState.data ?: listOf()) { item ->
                HourlyForecastItem(
//                    if (item.isCurrentWeather) R.drawable.waves else
                    bgImage = R.drawable.white_bg,
                    iconUrl = item.iconUrl!!,
                    temperature = item.temp.toString(),
                    time = item.timeString!!,
                )
            }
        }
        CurrentWeatherGraph(
            currentWeatherGraph = currentWeatherGraphState.data,
            visibility = currentWeatherGraphState.isLoading
        )
        BottomButtonLayout()
    }
}