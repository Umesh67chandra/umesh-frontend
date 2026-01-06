package com.example.focusguardian.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SetDailyLimitDialog(
    appName: String,
    onDismiss: () -> Unit,
    onSave: (Int, Int) -> Unit
) {
    var hours by remember { mutableStateOf(2) }
    var minutes by remember { mutableStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        confirmButton = {},
        dismissButton = {},
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Text(
                    text = "Set Daily Limit",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Choose how much time you want to spend on $appName daily",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TimeBox(value = hours, label = "hours")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(":", fontSize = 22.sp, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.width(8.dp))
                    TimeBox(value = minutes, label = "minutes")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Slider(
                    value = (hours * 60 + minutes).toFloat(),
                    onValueChange = {
                        hours = (it.toInt() / 60)
                        minutes = (it.toInt() % 60)
                    },
                    valueRange = 15f..480f
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Button(onClick = { onSave(hours, minutes) }) {
                        Text("Save Limit")
                    }
                }
            }
        }
    )
}

@Composable
private fun TimeBox(value: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(value.toString(), fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
