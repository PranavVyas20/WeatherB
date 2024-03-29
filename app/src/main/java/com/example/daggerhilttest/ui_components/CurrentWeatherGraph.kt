package com.example.daggerhilttest.ui_components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.daggerhilttest.models.CurrentWeatherGraph
import com.example.daggerhilttest.ui.theme.graphLineBottomColor
import com.example.daggerhilttest.ui.theme.graphLineColor
import com.example.daggerhilttest.ui.theme.shimmerColor
import com.example.daggerhilttest.util.marker
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.patrykandpatryk.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatryk.vico.compose.axis.vertical.startAxis
import com.patrykandpatryk.vico.compose.chart.Chart
import com.patrykandpatryk.vico.compose.chart.line.lineChart
import com.patrykandpatryk.vico.compose.chart.line.lineSpec
import com.patrykandpatryk.vico.compose.component.shape.shader.verticalGradient
import com.patrykandpatryk.vico.core.axis.AxisPosition
import com.patrykandpatryk.vico.core.axis.formatter.AxisValueFormatter
import java.util.*


private var axisValueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { _, _ -> "" }
private val yAxisValueFormatter = AxisValueFormatter<AxisPosition.Vertical.Start> { i, j ->
    "${i.toInt()}℃"
}

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun CurrentWeatherGraph(currentWeatherGraph: CurrentWeatherGraph?, visibility: Boolean) {

    if (currentWeatherGraph != null) {
        currentWeatherGraph.timeStampMap!!.forEach { (key, _) ->
            currentWeatherGraph.markerMap!![key] = marker()
        }
        axisValueFormatter = AxisValueFormatter { i, _ ->
            currentWeatherGraph.timeStampMap[i].toString()
        }
    }
    Card(
        modifier = Modifier
            .padding(10.dp)
            .wrapContentHeight()
            .placeholder(
                visible = visibility,
                color = shimmerColor,
                shape = RoundedCornerShape(7.dp),
                highlight = PlaceholderHighlight.shimmer(
                    highlightColor = Color.White,
                )
            )
    ) {
        if (currentWeatherGraph != null) {
            Chart(
                chart = lineChart(
                    lines = listOf(
                        lineSpec(
                            lineColor = graphLineColor,
                            lineBackgroundShader = verticalGradient(
                                arrayOf(
                                    graphLineBottomColor.copy(alpha = 0.5f),
                                    graphLineBottomColor.copy(alpha = 0.5f)
                                )
                            )
                        )
                    ),
                    spacing = 60.dp,
                    persistentMarkers = currentWeatherGraph.markerMap,
                ),
                chartModelProducer = currentWeatherGraph.chartProducer!!,
                modifier = Modifier
                    .wrapContentSize()
                    .background(Color.White),
                startAxis = startAxis(
                    valueFormatter = yAxisValueFormatter,
                    guideline = null,
                    tick = null
                ),
                bottomAxis = bottomAxis(
                    valueFormatter = axisValueFormatter,
                    guideline = null,
                    tick = null
                )
            )
        } else {
            Text(text = "Unable to fetch graph data")
        }
    }
}