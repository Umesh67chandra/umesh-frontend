package com.example.focusguardian.ui.theme.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun StepIndicator(currentStep: Int, totalSteps: Int = 3) {
    Row {
        for (i in 1..totalSteps) {
            Canvas(
                modifier = Modifier
                    .size(16.dp)
                    .padding(4.dp)
            ) {
                drawCircle(
                    color = if (i <= currentStep) Color(0xFF6A34FF) else Color.LightGray,
                    radius = size.minDimension / 2
                )
            }
        }
    }
}