package com.example.focusguardian.ui.theme.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.focusguardian.R
import com.example.focusguardian.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdultInterestScreen(
    userViewModel: UserViewModel,
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    var selectedInterests by remember { mutableStateOf(setOf<String>()) }
    var isLoading by remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current

    fun onInterestClick(interest: String) {
        selectedInterests = if (selectedInterests.contains(interest)) {
            selectedInterests - interest
        } else {
            selectedInterests + interest
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFD6E4FF), Color(0xFFF0F5FF)) // Blueish theme for Adult
                    )
                )
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(24.dp))

            Image(
                painter = painterResource(R.drawable.ic_focus_guardian_logo),
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
                        "Refine Your Feed",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        "Select topics relevant to your goals",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )

                    Spacer(Modifier.height(16.dp))

                    CategoryRow("Business", "Technology", selectedInterests, ::onInterestClick)
                    CategoryRow("Health", "Travel", selectedInterests, ::onInterestClick)
                    CategoryRow("Finance", "News", selectedInterests, ::onInterestClick)
                    CategoryRow("Lifestyle", "Science", selectedInterests, ::onInterestClick)

                    Spacer(Modifier.height(20.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) {
                            Text("Back")
                        }
                        Button(
                            onClick = {
                                isLoading = true
                                userViewModel.savePreferences(
                                    interests = selectedInterests.toList(),
                                    onSuccess = {
                                        isLoading = false
                                        onContinue()
                                    },
                                    onError = {
                                        isLoading = false
                                        android.widget.Toast.makeText(context, "Failed to save: $it", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                )
                            },
                            modifier = Modifier.weight(1f),
                            enabled = selectedInterests.isNotEmpty()
                        ) {
                            Text("Continue")
                        }
                    }
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun CategoryRow(
    a: String, 
    b: String, 
    selectedInterests: Set<String>, 
    onInterestClick: (String) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        CategoryCard(
            text = a, 
            modifier = Modifier.weight(1f), 
            isSelected = selectedInterests.contains(a)
        ) { onInterestClick(a) }
        CategoryCard(
            text = b, 
            modifier = Modifier.weight(1f),
            isSelected = selectedInterests.contains(b)
        ) { onInterestClick(b) }
    }
    Spacer(Modifier.height(12.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryCard(
    text: String, 
    modifier: Modifier, 
    isSelected: Boolean, 
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(14.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF5B6CFF) else Color.White,
            contentColor = if (isSelected) Color.White else Color.Black
        )
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text)
        }
    }
}
