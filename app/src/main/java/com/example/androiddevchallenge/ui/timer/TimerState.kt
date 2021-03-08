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

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class TimerState : Parcelable {
    @Parcelize
    data class Running(val startTime: Long, val originalDuration: Long, val duration: Long) :
        TimerState() {
        override fun remainingTime() =
            (startTime + duration - System.currentTimeMillis()).coerceAtLeast(0)
    }

    @Parcelize
    data class Paused(val remainingTime: Long) : TimerState() {
        override fun remainingTime(): Long = remainingTime
    }

    @Parcelize
    data class Idle(val setTime: Long) : TimerState() {
        override fun remainingTime(): Long = 0L
    }

    abstract fun remainingTime(): Long
}
