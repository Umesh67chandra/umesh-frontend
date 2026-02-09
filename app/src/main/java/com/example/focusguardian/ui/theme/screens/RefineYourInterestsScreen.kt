package com.example.focusguardian.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.focusguardian.viewmodel.UserViewModel

@Composable
fun RefineYourInterestsScreen(
    interests: List<String>,
    userViewModel: UserViewModel,
    onBack: () -> Unit,
    onContinue: (Set<String>) -> Unit
) {
    var selectedSubCategories by rememberSaveable { mutableStateOf(setOf<String>()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

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

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(26.dp)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                StepIndicator(currentStep = 3)

                Spacer(Modifier.height(16.dp))

                Text(
                    "Refine Your Interests",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Select sub-categories you'd like to follow",
                    color = Color.Gray,
                    fontSize = 13.sp
                )

                Spacer(Modifier.height(16.dp))

                interests.forEach { interest ->
                    InterestGroup(
                        interest = interest,
                        selectedSubCategories = selectedSubCategories,
                        onSubCategoryClicked = {
                            selectedSubCategories = if (selectedSubCategories.contains(it)) {
                                selectedSubCategories - it
                            } else {
                                selectedSubCategories + it
                            }
                        }
                    )
                }

                Spacer(Modifier.height(20.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) {
                        Text("Back")
                    }
                    Button(
                        onClick = {
                            isLoading = true
                            errorMessage = null
                            userViewModel.savePreferences(selectedSubCategories) { success, message ->
                                isLoading = false
                                if (success) {
                                    onContinue(selectedSubCategories)
                                } else {
                                    errorMessage = message ?: "Failed to save preferences"
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = selectedSubCategories.isNotEmpty() && !isLoading
                    ) {
                        Text(if (isLoading) "Saving..." else "Finish")
                    }
                }

                if (errorMessage != null) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = Color(0xFFD32F2F),
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun InterestGroup(
    interest: String,
    selectedSubCategories: Set<String>,
    onSubCategoryClicked: (String) -> Unit
) {
    val subCategories = when (interest) {
        "Music" -> listOf("Rock", "Pop", "Hip Hop", "Jazz")
        "Educational" -> listOf("Science", "History", "Math", "Programming")
        "Current Affairs" -> listOf("Politics", "World News", "Business", "Technology")
        "Fitness" -> listOf("Workout", "Yoga", "Running", "Healthy Eating")
        "Entertainment" -> listOf("Movies", "TV Shows", "Gaming", "Celebrity News")
        "Lifestyle" -> listOf("Travel", "Food", "Fashion", "DIY")
        else -> emptyList()
    }

    if (subCategories.isNotEmpty()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(interest, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            subCategories.forEach { subCategory ->
                SubCategoryRow(
                    subCategory = subCategory,
                    isSelected = selectedSubCategories.contains(subCategory),
                    onSubCategoryClicked = onSubCategoryClicked
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SubCategoryRow(
    subCategory: String,
    isSelected: Boolean,
    onSubCategoryClicked: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSubCategoryClicked(subCategory) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onSubCategoryClicked(subCategory) }
        )
        Spacer(Modifier.width(8.dp))
        Text(subCategory)
    }
}
