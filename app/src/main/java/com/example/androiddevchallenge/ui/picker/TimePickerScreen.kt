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
package com.example.androiddevchallenge.ui.picker

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androiddevchallenge.ui.timer.toDoubleDigits

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TimePickerScreen(onTimerSet: (Long) -> Unit) = Column(
    Modifier.background(MaterialTheme.colors.background).fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceBetween
) {
    var timerMask by rememberSaveable { mutableStateOf(0) }
    Column(
        Modifier
            .padding(top = 64.dp)
            .height(120.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val hours = (timerMask / 10000).toDoubleDigits()
            val minutes = (timerMask / 100 % 100).toDoubleDigits()
            val seconds = (timerMask % 100).toDoubleDigits()
            Spacer(Modifier.width(16.dp))
            Text(
                modifier = Modifier,
                text = "$hours : $minutes : $seconds",
                style = MaterialTheme.typography.h3.copy(textAlign = TextAlign.Center)
            )
            Spacer(Modifier.width(16.dp))
            IconButton(onClick = { timerMask /= 10 }) {
                Icon(Icons.Default.Backspace, contentDescription = "Remove")
            }
        }
        Spacer(Modifier.height(16.dp))
        Divider()
    }

    val numberButtonStyle = TextStyle(
        fontSize = 24.sp,
        color = MaterialTheme.colors.onBackground,
        fontWeight = FontWeight.Medium
    )

    val onNumberClick: (Int) -> Unit = {
        if (timerMask < 100000) {
            timerMask = timerMask * 10 + it
        }
    }

    (0..2).forEach { i ->
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            (1..3).forEach { j ->
                TextButton(
                    modifier = Modifier.size(64.dp),
                    shape = CircleShape,
                    onClick = { onNumberClick(i * 3 + j) }
                ) {
                    Text(text = (i * 3 + j).toString(), style = numberButtonStyle)
                }
            }
        }
    }
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        TextButton(
            modifier = Modifier.size(64.dp),
            shape = CircleShape,
            onClick = { onNumberClick(0) }
        ) {
            Text(text = "0", style = numberButtonStyle)
        }
    }

    val isButtonEnabled = timerMask > 0
    val buttonColor by animateColorAsState(
        targetValue = if (isButtonEnabled) {
            MaterialTheme.colors.primary
        } else {
            MaterialTheme.colors.secondary
        }
    )
    IconButton(
        modifier = Modifier
            .padding(48.dp)
            .size(48.dp),
        onClick = {
            val hours = timerMask / 10000 * 3600000
            val minutes = (timerMask / 100 % 100) * 60000
            val seconds = (timerMask % 100) * 1000
            onTimerSet((hours + minutes + seconds).toLong())
        },
        enabled = isButtonEnabled
    ) {
        Icon(
            Icons.Default.PlayCircle,
            contentDescription = "Start",
            Modifier.fillMaxSize(),
            tint = buttonColor
        )
    }
}
