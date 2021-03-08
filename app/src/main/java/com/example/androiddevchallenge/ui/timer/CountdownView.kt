/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge.ui.timer

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import java.util.Calendar
import java.util.TimeZone

@Composable
fun CountdownView(
    tickerColors: List<Animatable<Color, AnimationVector4D>>,
    remainingTime: Long,
    tickCount: Int = 40,
    strokeWidth: Float = 16f,
) {
    val tickStep = 360 / tickCount
    val calendar = remember { Calendar.getInstance(TimeZone.getTimeZone("UTC")) }
    Canvas(
        modifier = Modifier.fillMaxSize(),
        onDraw = {
            val strokeLength = size.width / 7
            (0 until 360 step tickStep).forEach { degrees ->
                val color = tickerColors[degrees / tickStep].value

                rotate(degrees.toFloat(), Offset(size.width / 2, size.height / 2)) {
                    drawLine(
                        color,
                        Offset(size.width / 2, size.height / 2 - size.width / 2),
                        Offset(
                            size.width / 2,
                            size.height / 2 - size.width / 2 + strokeLength
                        ),
                        strokeWidth,
                        cap = StrokeCap.Round
                    )
                }
            }
        }
    )
    calendar.timeInMillis = remainingTime

    val hours = (calendar.get(Calendar.DAY_OF_YEAR) - 1) * 24 + calendar.get(Calendar.HOUR_OF_DAY)
    val minutes = calendar.get(Calendar.MINUTE)
    val seconds = calendar.get(Calendar.SECOND)
    val millis = calendar.get(Calendar.MILLISECOND) / 10
    val requiredFontSize = when {
        hours > 0 -> 24
        minutes > 0 -> 32
        seconds >= 10 -> 36
        else -> 54
    }
    val fontSize by animateIntAsState(targetValue = requiredFontSize)
    val text = when {
        remainingTime >= 3600000 -> AnnotatedString(
            "${hours.toDoubleDigits()}:${minutes.toDoubleDigits()}:${seconds.toDoubleDigits()}",
            SpanStyle(fontSize = fontSize.sp)
        )
        remainingTime >= 10000 -> AnnotatedString(
            "${minutes.toDoubleDigits()}:${seconds.toDoubleDigits()}",
            SpanStyle(fontSize = fontSize.sp)
        )
        else -> AnnotatedString.Builder().apply {
            // hack to compensate for the millis offset
            pushStyle(SpanStyle(fontSize = 24.sp, color = Color.Transparent))
            append(":${millis.toDoubleDigits()}")
            pop()
            pushStyle(SpanStyle(fontSize = fontSize.sp))
            append("$seconds")
            pop()
            pushStyle(SpanStyle(fontSize = 24.sp))
            append(":${millis.toDoubleDigits()}")
        }.toAnnotatedString()
    }

    Text(
        text = text,
        style = TextStyle(color = MaterialTheme.colors.onSurface, fontWeight = FontWeight.Medium)
    )
}

fun Int.toDoubleDigits() = "%02d".format(this)
