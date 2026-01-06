package com.example.focusguardian.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
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
import androidx.navigation.NavController
import com.example.focusguardian.R
import com.example.focusguardian.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseYourRoleScreen(
    navController: NavController,
    onRoleSelected: (String) -> Unit,
    onSignIn: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Focus Guardian") },
                actions = {
                    IconButton(onClick = { navController.navigate(Routes.DASHBOARD) }) {
                        Icon(Icons.Default.Menu, contentDescription = "Dashboard")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
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

            // ✅ SAME LOGO (UNCHANGED)
            Image(
                painter = painterResource(R.drawable.ic_focus_guardian_logo),
                contentDescription = "Logo",
                modifier = Modifier.size(80.dp)
            )

            Spacer(Modifier.height(12.dp))

            Text("Focus Guardian", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text("✨ Smart Social Media Awareness", color = Color(0xFF6B5CFF))

            Spacer(Modifier.height(24.dp))

            // ✅ MAIN CARD
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(26.dp)
            ) {
                Column(Modifier.padding(22.dp)) {

                    Text(
                        text = "Choose Your Role",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Text(
                        text = "Select the role that best describes you",
                        color = Color.Black
                    )

                    Spacer(Modifier.height(16.dp))

                    RoleCard(
                        title = "Child",
                        subtitle = "Under 13 years old with parental controls",
                        color = Color(0xFFFF4D9D)
                    ) { onRoleSelected("child") }

                    RoleCard(
                        title = "Adult",
                        subtitle = "Independent user managing personal wellness",
                        color = Color(0xFF5B6CFF)
                    ) { onRoleSelected("adult") }

                    RoleCard(
                        title = "Parent",
                        subtitle = "Monitor and guide family digital health",
                        color = Color(0xFF18B37E)
                    ) { onRoleSelected("parent") }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ✅ BOTTOM SECTION
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF7A3DF0), Color(0xFFB23BE3))
                        )
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Text(
                        text = "Already a Member?",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = "Sign in to access your personalized dashboard and continue your journey",
                        color = Color.White,
                        fontSize = 13.sp
                    )

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = onSignIn,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text("Sign In", color = Color.Black)
                    }
                }
            }
        }
    }
}

@Composable
private fun RoleCard(
    title: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(color, RoundedCornerShape(12.dp))
            )

            Spacer(Modifier.width(16.dp))

            Column {
                Text(title, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(subtitle, fontSize = 12.sp, color = Color.Black)
            }
        }
    }
}
