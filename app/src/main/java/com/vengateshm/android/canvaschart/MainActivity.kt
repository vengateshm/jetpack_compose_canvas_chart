package com.vengateshm.android.canvaschart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vengateshm.android.canvaschart.ui.theme.CanvasChartTheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CanvasChartTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent)) {
                        LineChart(getDataPoints())
                    }
                }
            }
        }
    }
}

@Composable
fun LineChart(dataPoints: List<DataPoint>) {
    Canvas(modifier = Modifier
        .fillMaxWidth(1f)
        .fillMaxHeight(0.5f)) {

        // Get canvas width and height
        val width = size.width
        val height = size.height

        // Draw vertical and horizontal axis lines
        val spacingOf16DpInPixels = 16.dp.toPx()
        val verticalAxisLineStartOffset = Offset(spacingOf16DpInPixels, spacingOf16DpInPixels)
        val verticalAxisLineEndOffset = Offset(spacingOf16DpInPixels, height)
        drawLine(Color.Gray,
            verticalAxisLineStartOffset,
            verticalAxisLineEndOffset,
            strokeWidth = Stroke.DefaultMiter)

        val horizontalAxisLineStartOffset = Offset(spacingOf16DpInPixels, height)
        val horizontalAxisLineEndOffset = Offset(width - spacingOf16DpInPixels, height)
        drawLine(Color.Gray,
            horizontalAxisLineStartOffset,
            horizontalAxisLineEndOffset,
            strokeWidth = Stroke.DefaultMiter)

        // Get min max values of x and y
        val xMax = dataPoints.xMax()
        val yMax = dataPoints.yMax()

        val gradientPath = Path()
        gradientPath.moveTo(spacingOf16DpInPixels, height)
        dataPoints.forEachIndexed { index, curDataPoint ->
            // Normalize x and y points to fit nto the canvas limit
            var normX = curDataPoint.x.toRealX(xMax, width)
            val normY = curDataPoint.y.toRealY(yMax, height)

            if (index == 0) normX += spacingOf16DpInPixels
            if (index == dataPoints.size - 1) normX -= spacingOf16DpInPixels

            // Lsat point does not have next point to draw line
            if (index < dataPoints.size - 1) {
                val offsetStart = Offset(normX, normY)
                var nextNormXPoint = dataPoints[index + 1].x.toRealX(xMax, width)

                if (index == dataPoints.size - 2)
                    nextNormXPoint =
                        dataPoints[index + 1].x.toRealX(xMax, width) - spacingOf16DpInPixels
                val nextNormYPoint = dataPoints[index + 1].y.toRealY(yMax, height)
                val offsetEnd = Offset(nextNormXPoint, nextNormYPoint)
                drawLine(Color(0XFF30D6AF),
                    offsetStart,
                    offsetEnd,
                    strokeWidth = Stroke.DefaultMiter)
            }

            drawCircle(Color(0XFF30D6AF),
                radius = 6.dp.toPx(),
                Offset(normX, normY))
            with(gradientPath) {
                lineTo(normX, normY)
            }
        }

        with(gradientPath) {
            lineTo(width - spacingOf16DpInPixels, height)
            lineTo(0f, height)
            close()
            drawPath(this,
                brush = Brush.verticalGradient(colors = listOf(
                    Color(0XFFA4EFEB), Color(0XFFFAFFFC),
                )))
        }
    }
}

data class DataPoint(val x: Float, val y: Float)

fun getDataPoints(): List<DataPoint> {
    val random = Random.Default
    return (0..10).map {
        DataPoint(it.toFloat(), random.nextInt(50).toFloat() + 1f)
    }
}

fun List<DataPoint>.xMax(): Float = maxByOrNull { it.x }?.x ?: 0f
fun List<DataPoint>.yMax(): Float = maxByOrNull { it.y }?.y ?: 0f

fun Float.toRealX(xMax: Float, width: Float) = (this / xMax) * width
fun Float.toRealY(yMax: Float, height: Float) = (this / yMax) * height