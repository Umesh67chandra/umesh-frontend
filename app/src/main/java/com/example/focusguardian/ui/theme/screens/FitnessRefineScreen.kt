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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import com.example.focusguardian.R

@Composable
fun FitnessRefineScreen(
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFDAD6FF), Color(0xFFEEDCFF))
                )
            )
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(24.dp))

        Image(
            painter = painterResource(id = R.drawable.ic_focus_guardian_logo),
            contentDescription = "Logo",
            modifier = Modifier.size(80.dp)
        )

        Spacer(Modifier.height(12.dp))

        Text("Focus Guardian", fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text("✨ Smart Social Media Awareness", color = Color(0xFF6B5CFF))

        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(26.dp)
        ) {
            Column(Modifier.padding(22.dp)) {

                Text(
                    text = "Refine your interests",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Choose specific topics",
                    color = Color.Gray
                )

                Spacer(Modifier.height(16.dp))

                Text("Fitness", fontWeight = FontWeight.Bold)

                Spacer(Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TopicCard("Gym", Modifier.weight(1f))
                    TopicCard("Yoga", Modifier.weight(1f))
                }

                Spacer(Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TopicCard("Sports", Modifier.weight(1f))
                    TopicCard("Nutrition", Modifier.weight(1f))
                }

                Spacer(Modifier.height(12.dp))

                TopicCard("Running", Modifier.fillMaxWidth())

                Spacer(Modifier.height(24.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Back")
                    }

                    Button(
                        onClick = onContinue,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Continue")
                    }
                }
            }
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun TopicCard(text: String, modifier: Modifier) {
    Card(
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text)
        }
    }
}
