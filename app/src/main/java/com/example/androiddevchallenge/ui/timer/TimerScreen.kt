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

import androidx.compose.animation.Animatable
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TimerScreen(timerValue: Long, onTimerReset: () -> Unit) {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var timerState by rememberSaveable(
            key = "timerState",
            init = { mutableStateOf<TimerState>(TimerState.Idle(timerValue)) },
            inputs = arrayOf(timerValue)
        )

        Box(
            modifier = Modifier
                .padding(64.dp)
                .fillMaxWidth()
                .aspectRatio(1f),
            contentAlignment = Alignment.Center
        ) {
            var remainingTime by remember { mutableStateOf(timerValue) }
            val startColor = MaterialTheme.colors.primary
            val endColor = startColor.copy(alpha = 0.1f)
            val animatableList = remember { (0..40).map { Animatable(startColor) } }
            AnimatableColorListTimer(
                timerState = timerState,
                startColor,
                endColor,
                animatableList,
                onTick = {
                    remainingTime = it
                    if (it == 0L) {
                        timerState = TimerState.Idle(timerValue)
                        remainingTime = timerValue
                    }
                }
            )
            CountdownView(tickerColors = animatableList, remainingTime)
        }

        val (buttonColor, textColor) = when (timerState) {
            is TimerState.Idle,
            is TimerState.Paused -> MaterialTheme.colors.primary to MaterialTheme.colors.onPrimary
            else -> MaterialTheme.colors.onBackground to MaterialTheme.colors.primary
        }
        val backgroundColor by animateColorAsState(targetValue = buttonColor)
        val texColor by animateColorAsState(targetValue = textColor)

        Button(
            modifier = Modifier
                .width(120.dp)
                .height(52.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = backgroundColor,
                contentColor = texColor
            ),
            onClick = {
                timerState = when (timerState) {
                    is TimerState.Idle -> TimerState.Running(
                        System.currentTimeMillis(),
                        timerValue,
                        timerValue
                    )
                    is TimerState.Paused -> TimerState.Running(
                        System.currentTimeMillis(),
                        timerValue,
                        timerState.remainingTime()
                    )
                    is TimerState.Running -> TimerState.Paused(timerState.remainingTime())
                }
            }
        ) {
            val text = if (timerState is TimerState.Running) "PAUSE" else "START"
            Text(text = text)
        }

        IconButton(
            modifier = Modifier.background(
                color = MaterialTheme.colors.secondary,
                shape = CircleShape
            ),
            onClick = {
                if (timerState is TimerState.Idle) {
                    onTimerReset()
                } else {
                    timerState = TimerState.Idle(timerValue)
                }
            }
        ) {
            val icon = if (timerState !is TimerState.Idle) {
                Icons.Default.Refresh
            } else {
                Icons.Default.Clear
            }
            Icon(icon, contentDescription = "Clear")
        }
    }
}
