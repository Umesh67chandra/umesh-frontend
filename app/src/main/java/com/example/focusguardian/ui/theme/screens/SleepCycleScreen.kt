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
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepCycleScreen(navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F8FC))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Text(
            text = "Sleep Cycle Mode",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Text(
            text = "Optimize your sleep schedule and reduce blue light exposure",
            fontSize = 13.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Sleep Cycle Block
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF3F3D99)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Sleep Cycle Block",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Switch(checked = true, onCheckedChange = {})
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TimeCard("Bedtime", "11:00 PM")
                    TimeCard("Wake Up", "7:00 AM")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Social apps lock 1 hour before bedtime to reduce blue light.",
                    fontSize = 12.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Blue Light Exposure 20%",
                    fontSize = 12.sp,
                    color = Color.White
                )

                LinearProgressIndicator(
                    progress = { 0.2f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = Color.Cyan
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Sleep Schedule
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Sleep Schedule", fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(Color(0xFFEDEBFF), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("23:00 - 07:00", fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Morning Tips
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF3FF))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Morning Routine Tips", fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(8.dp))
                Text("1. Avoid checking phone for first 30 mins", color = Color.Black)
                Text("2. Drink water immediately after waking", color = Color.Black)
                Text("3. Get natural sunlight exposure", color = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Evening Wind-down
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Evening Wind-down", fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Blue light from screens suppresses melatonin production. Enable Auto-lock to help your brain prepare for sleep.",
                    fontSize = 13.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Melatonin Level  •  Rising", fontSize = 12.sp, color = Color.Black)
                LinearProgressIndicator(
                    progress = { 0.7f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = Color(0xFFFF9800)
                )
            }
        }
    }
}

@Composable
private fun TimeCard(label: String, time: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.width(140.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF4F4DB5))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 12.sp, color = Color.White)
            Text(time, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}
