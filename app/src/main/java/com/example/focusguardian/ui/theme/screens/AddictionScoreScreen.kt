package com.example.focusguardian.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.focusguardian.viewmodel.AppUsageViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddictionScoreScreen(navController: NavController, appUsageViewModel: AppUsageViewModel = viewModel()) {

    val totalTimeUsed = appUsageViewModel.totalTimeUsedMinutes.toFloat()
    val totalLimit = appUsageViewModel.totalDailyLimitMinutes.toFloat()

    val score = if (totalLimit > 0) {
        (totalTimeUsed / totalLimit).coerceIn(0f, 1f)
    } else {
        0f
    }

    val scorePercentage = (score * 100).toInt()

    val scoreColor = when {
        scorePercentage <= 33 -> Color(0xFF22C55E) // Green
        scorePercentage <= 66 -> Color(0xFFFACC15) // Yellow
        else -> Color(0xFFEF4444) // Red
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F8FC))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Text(
            text = "Addiction Score",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Text(
            text = "Detailed breakdown of your digital habits and health metrics",
            fontSize = 13.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        // SCORE CARD
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) { 
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { score },
                        modifier = Modifier.size(120.dp),
                        color = scoreColor,
                        strokeWidth = 8.dp
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = scorePercentage.toString(),
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = scoreColor
                        )
                        Text("SCORE", color = Color.Black)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                ScoreRow("Scrolling", 38)
                ScoreRow("Late Night", 11)
                ScoreRow("Mood Drop", 30)
                ScoreRow("Switching", 41)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // HISTORICAL TREND
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Historical Trend", fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "6d ago   5d ago   4d ago   3d ago   2d ago   1d ago   Today",
                    fontSize = 12.sp,
                    color = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // TIPS
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFEFFDF4),
                contentColor = Color.Black
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Tips for Improvement", fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(8.dp))
                Text("• Try to reduce late-night scrolling by enabling Sleep Block.", color = Color.Black)
                Text("• Take frequent breaks to lower your scroll intensity.", color = Color.Black)
                Text("• Focus on one app at a time to reduce switching fatigue.", color = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ABOUT
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF0F7FF),
                contentColor = Color.Black
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("About this Score", fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Your addiction score is calculated based on scrolling intensity, late-night usage, mood impact, and app switching frequency. Lower scores indicate healthier digital habits.",
                    fontSize = 13.sp,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
private fun ScoreRow(label: String, percent: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = Color.Black)
            Text("$percent%", color = Color.Black)
        }
        LinearProgressIndicator(
            progress = { percent / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = Color(0xFFFACC15)
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}
