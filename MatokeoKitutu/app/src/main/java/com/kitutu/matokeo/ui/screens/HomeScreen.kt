package com.kitutu.matokeo.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))
        Text("MATOKEO", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Text("Sekondari za Kata ya Kitutu", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(32.dp))

        RoleCard(
            title = "Mwalimu",
            subtitle = "Andikisha wanafunzi na jaza alama zao."
        ) { navController.navigate("teacher_schools") }

        Spacer(Modifier.height(16.dp))

        RoleCard(
            title = "Mwanafunzi",
            subtitle = "Tazama matokeo na nafasi zako."
        ) { navController.navigate("student_portal") }

        Spacer(Modifier.height(16.dp))

        RoleCard(
            title = "Admin",
            subtitle = "Simamia mfumo na hifadhi matokeo."
        ) { navController.navigate("admin_login") }
    }
}

@Composable
private fun RoleCard(title: String, subtitle: String, onClick: () -> Unit) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
