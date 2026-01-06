package com.example.focusguardian.ui.theme.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.focusguardian.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppMenuDrawer(
    navController: NavController,
    closeDrawer: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(280.dp),
        drawerContainerColor = Color.White // Set background to white
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Menu",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black // Ensure heading is visible
                )
                IconButton(onClick = closeDrawer) {
                    Icon(Icons.Default.Close, contentDescription = "Close Menu")
                }
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = Color.LightGray)
            Spacer(Modifier.height(16.dp))

            MenuItem(Icons.Default.Dashboard, "Dashboard") {
                navController.navigate(Routes.DASHBOARD)
                closeDrawer()
            }

            MenuItem(Icons.Default.Notifications, "Notifications") {
                navController.navigate(Routes.NOTIFICATIONS)
                closeDrawer()
            }

            MenuItem(Icons.Default.ShowChart, "Addiction Score") {
                navController.navigate(Routes.ADDICTION_SCORE)
                closeDrawer()
            }

            MenuItem(Icons.Default.Bedtime, "Sleep Cycle") {
                navController.navigate(Routes.SLEEP)
                closeDrawer()
            }

            MenuItem(Icons.Default.EmojiEvents, "Challenges") {
                navController.navigate(Routes.CHALLENGES)
                closeDrawer()
            }

            MenuItem(Icons.Default.BarChart, "Analytics") {
                navController.navigate(Routes.ANALYTICS)
                closeDrawer()
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = Color.LightGray)
            Spacer(Modifier.height(16.dp))

            MenuItem(Icons.Default.Wallpaper, "Wallpaper") {
                navController.navigate(Routes.WALLPAPER)
                closeDrawer()
            }
            MenuItem(Icons.Default.GridView, "Manage Apps") {
                navController.navigate(Routes.MANAGE_APPS)
                closeDrawer()
            }
            MenuItem(Icons.Default.Person, "Profile Settings") {
                navController.navigate(Routes.PROFILE_SETTINGS)
                closeDrawer()
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = Color.LightGray)
            Spacer(Modifier.height(16.dp))

            MenuItem(
                icon = Icons.Default.ExitToApp,
                title = "Sign Out",
                textColor = Color.Red
            ) {
                navController.navigate(Routes.LOGIN)
                closeDrawer()
            }
        }
    }
}

@Composable
private fun MenuItem(
    icon: ImageVector,
    title: String,
    textColor: Color = Color.Black,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = textColor)
        Spacer(Modifier.width(16.dp))
        Text(title, color = textColor)
    }
}
