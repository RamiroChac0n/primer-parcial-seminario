package com.example.primerparcial.ui.screens.profile

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.primerparcial.ui.screens.dashboard.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    dashboardViewModel: DashboardViewModel = viewModel()
) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    var userName by remember {
        mutableStateOf(prefs.getString("user_name", "Usuario") ?: "Usuario")
    }
    var isEditing by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(userName) }

    val dashboardState by dashboardViewModel.dashboardState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mi Perfil") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName.take(2).uppercase(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            // Name editor
            if (isEditing) {
                OutlinedTextField(
                    value = editName,
                    onValueChange = { editName = it },
                    label = { Text("Nombre") },
                    trailingIcon = {
                        IconButton(onClick = {
                            userName = editName.ifBlank { "Usuario" }
                            prefs.edit().putString("user_name", userName).apply()
                            isEditing = false
                        }) {
                            Icon(Icons.Default.Check, contentDescription = "Guardar")
                        }
                    },
                    singleLine = true
                )
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = {
                        editName = userName
                        isEditing = true
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar nombre")
                    }
                }
            }

            HorizontalDivider()

            Text(
                "Resumen",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.Start)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SummaryStatCard(
                    modifier = Modifier.weight(1f),
                    title = "Hábitos",
                    value = "${dashboardState.totalHabits}",
                    subtitle = "${dashboardState.completedToday} hoy"
                )
                SummaryStatCard(
                    modifier = Modifier.weight(1f),
                    title = "Racha Máx.",
                    value = "${dashboardState.topStreak}",
                    subtitle = "días"
                )
            }

            SummaryStatCard(
                modifier = Modifier.fillMaxWidth(),
                title = "Balance del Mes",
                value = "$${String.format("%.2f", dashboardState.balance)}",
                subtitle = if (dashboardState.balance >= 0) "Positivo \uD83D\uDCB0" else "Negativo"
            )
        }
    }
}

@Composable
private fun SummaryStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(title, style = MaterialTheme.typography.labelMedium)
            Text(
                value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(subtitle, style = MaterialTheme.typography.bodySmall)
        }
    }
}
