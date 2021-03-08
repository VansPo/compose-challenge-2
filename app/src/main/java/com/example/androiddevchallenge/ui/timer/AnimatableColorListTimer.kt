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
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun AnimatableColorListTimer(
    timerState: TimerState,
    startColor: Color,
    endColor: Color,
    animatableList: List<Animatable<Color, AnimationVector4D>>,
    onTick: (remainingTime: Long) -> Unit
) {
    val tickCount = 40
    val currentAnimatableList by rememberUpdatedState(animatableList)
    val currentOnTick by rememberUpdatedState(onTick)

    LaunchedEffect(
        key1 = timerState,
        block = {
            when (timerState) {
                is TimerState.Running -> {
                    val phaseLength = timerState.originalDuration / tickCount
                    val currentIndex = (timerState.remainingTime().toDouble() / phaseLength).roundToInt()

                    currentAnimatableList.forEachIndexed { index, animatable ->
                        if (index > currentIndex) {
                            animatable.snapTo(endColor)
                        } else {
                            val delay =
                                (timerState.originalDuration / tickCount) * (currentIndex - index - 1)
                            launch {
                                animatable.animateTo(
                                    endColor,
                                    tween(
                                        phaseLength.toInt(),
                                        easing = LinearEasing,
                                        delayMillis = delay.toInt()
                                    )
                                )
                            }
                        }
                    }

                    while (isActive && timerState.remainingTime() >= 0) {
                        withFrameMillis { currentOnTick(timerState.remainingTime()) }
                    }
                }
                is TimerState.Paused -> {
                    currentAnimatableList.forEach { it.stop() }
                }
                is TimerState.Idle -> {
                    onTick(0L)
                    currentAnimatableList.forEachIndexed { index, animatable ->
                        launch {
                            animatable.animateTo(
                                startColor,
                                tween(10, index * 5, LinearEasing)
                            )
                        }
                    }
                }
            }
        }
    )
}
