package com.example.focusguardian.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.focusguardian.viewmodel.AppUsageViewModel
import com.example.focusguardian.viewmodel.AddictionMetrics
import com.example.focusguardian.viewmodel.DailyUsage
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddictionScoreScreen(navController: NavController, appUsageViewModel: AppUsageViewModel = viewModel()) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val hasUsageAccess = appUsageViewModel.hasUsageAccess(context)
    var metrics by remember { mutableStateOf<AddictionMetrics?>(null) }
    var trend by remember { mutableStateOf<List<DailyUsage>>(emptyList()) }

    LaunchedEffect(hasUsageAccess) {
        if (hasUsageAccess) {
            while (true) {
                metrics = appUsageViewModel.getAddictionMetrics(context)
                trend = appUsageViewModel.getUsageTrend(context)
                delay(60000)
            }
        }
    }

    val scorePercentage = metrics?.scorePercent ?: 0
    val scoreProgress = scorePercentage / 100f

    val scoreColor = when {
        scorePercentage <= 33 -> Color(0xFF22C55E) // Green
        scorePercentage <= 66 -> Color(0xFFFACC15) // Yellow
        else -> Color(0xFFEF4444) // Red
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Text(
            text = "Addiction Score",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Detailed breakdown of your digital habits and health metrics",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // SCORE CARD
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) { 
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { scoreProgress },
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
                        Text("SCORE", color = MaterialTheme.colorScheme.onSurface)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (!hasUsageAccess) {
                    Text(
                        text = "Grant Usage Access to calculate your score.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = {
                        context.startActivity(
                            android.content.Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS)
                        )
                    }) {
                        Text("Open Usage Access")
                    }
                } else {
                    ScoreRow("Scrolling", metrics?.scrollingPercent ?: 0)
                    ScoreRow("Late Night", metrics?.lateNightPercent ?: 0)
                    ScoreRow("Mood Drop", metrics?.moodDropPercent ?: 0)
                    ScoreRow("Switching", metrics?.switchingPercent ?: 0)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // HISTORICAL TREND
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "Historical Trend",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))
                if (!hasUsageAccess) {
                    Text(
                        text = "Grant Usage Access to see your trend.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else if (trend.isEmpty()) {
                    Text(
                        text = "No usage data yet.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    trend.forEach { day ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                day.label,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "${day.minutes}m",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        LinearProgressIndicator(
                            progress = { day.percent / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // TIPS
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Tips for Improvement",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "• Try to reduce late-night scrolling by enabling Sleep Block.",
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "• Take frequent breaks to lower your scroll intensity.",
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "• Focus on one app at a time to reduce switching fatigue.",
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ABOUT
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "About this Score",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Your addiction score is calculated based on scrolling intensity, late-night usage, mood impact, and app switching frequency. Lower scores indicate healthier digital habits.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
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
            Text(label, color = MaterialTheme.colorScheme.onSurface)
            Text("$percent%", color = MaterialTheme.colorScheme.onSurface)
        }
        LinearProgressIndicator(
            progress = { percent / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = MaterialTheme.colorScheme.tertiary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}
