package com.example.focusguardian.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F8FC))
            .padding(16.dp)
    ) {

        Text(
            text = "Advanced Analytics",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Text(
            text = "Deep dive into your usage patterns and predictions",
            fontSize = 13.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        AnalyticsCard("Focus Difficulty", "Easy Day")
        AnalyticsCard("AI Usage Prediction", "2h 21m")
        AnalyticsCard("Content Exposure Index", "Moderate")
        AnalyticsCard("Cross-Device Usage", "Total 4h 15m")
    }
}

@Composable
private fun AnalyticsCard(title: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, color = Color(0xFF6A4CF5), fontSize = 16.sp)
        }
    }
}
