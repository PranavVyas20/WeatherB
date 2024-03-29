package com.example.daggerhilttest.ui_components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.animateValueAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.daggerhilttest.R
import kotlin.math.cos
import kotlin.math.sin

@Preview
@Composable
fun RepeatingCloudsAnimation() {
        val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp

    // Calculate the starting point as a float value
    val startPoint = -screenWidthDp
    val endingPoint = screenWidthDp

    var animationRunning by remember { mutableStateOf(false) }
    val infiniteTransition = rememberInfiniteTransition()

    val slideOffset by infiniteTransition.animateFloat(
        initialValue = startPoint.toFloat(),
        targetValue = endingPoint.toFloat(),
        animationSpec = infiniteRepeatable(
            repeatMode = RepeatMode.Restart,
            animation = tween(durationMillis = 10000, easing = LinearEasing)
        )
    )
    val fadeAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, 3000),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = { animationRunning = !animationRunning },
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            Text("Start Animation")
        }
        if (animationRunning) {
            Image(
                painter = painterResource(id = R.drawable.clouds_anim_iii),
                contentScale = ContentScale.Crop,
                contentDescription = "Image",
                modifier = Modifier
                    .offset { IntOffset(slideOffset.toInt(), 0) }
//                    .alpha(fadeAlpha)
                    .fillMaxSize()
            )
        }

    }


}