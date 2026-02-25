package com.example.primerparcial.ui.screens.dashboard

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: DashboardViewModel = viewModel()) {
    val state by viewModel.dashboardState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Dashboard") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Habits card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "Hábitos de Hoy",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    val completionPct = if (state.totalHabits > 0)
                        state.completedToday.toFloat() / state.totalHabits
                    else 0f

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${state.completedToday} / ${state.totalHabits} completados")
                        Text(
                            "${(completionPct * 100).toInt()}%",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    LinearProgressIndicator(
                        progress = { completionPct },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row {
                        AssistChip(
                            onClick = {},
                            label = { Text("\uD83D\uDD25 Racha máx: ${state.topStreak} días") }
                        )
                    }

                    Text("Últimos 7 días", style = MaterialTheme.typography.labelMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val dayLabels = listOf("L", "M", "X", "J", "V", "S", "D")
                        state.weeklyCompletions.forEachIndexed { index, count ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    "$count",
                                    style = MaterialTheme.typography.labelSmall
                                )
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(
                                            color = if (count > 0)
                                                MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.surfaceVariant,
                                            shape = MaterialTheme.shapes.small
                                        )
                                )
                                Text(
                                    dayLabels.getOrElse(index) { "" },
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }

            // Budget card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "Presupuesto del Mes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Ingresos", style = MaterialTheme.typography.labelMedium)
                            Text(
                                "+$${String.format("%.2f", state.totalIncome)}",
                                color = MaterialTheme.colorScheme.tertiary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Gastos", style = MaterialTheme.typography.labelMedium)
                            Text(
                                "-$${String.format("%.2f", state.totalExpenses)}",
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    HorizontalDivider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Balance", style = MaterialTheme.typography.titleSmall)
                        Text(
                            "$${String.format("%.2f", state.balance)}",
                            fontWeight = FontWeight.Bold,
                            color = if (state.balance >= 0)
                                MaterialTheme.colorScheme.tertiary
                            else MaterialTheme.colorScheme.error
                        )
                    }

                    val budgetProgress = if (state.totalIncome > 0)
                        (state.totalExpenses / state.totalIncome).toFloat().coerceIn(0f, 1f)
                    else 0f
                    LinearProgressIndicator(
                        progress = { budgetProgress },
                        modifier = Modifier.fillMaxWidth(),
                        color = when {
                            budgetProgress >= 1f -> MaterialTheme.colorScheme.error
                            budgetProgress >= 0.8f -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.primary
                        }
                    )
                    Text(
                        "${(budgetProgress * 100).toInt()}% del ingreso gastado",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}
