package com.example.primerparcial.ui.screens.budget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.primerparcial.data.entity.BudgetCategoryEntity
import com.example.primerparcial.data.entity.TransactionEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(viewModel: BudgetViewModel = viewModel()) {
    val transactions by viewModel.transactions.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val summary by viewModel.summary.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var showTransactionDialog by remember { mutableStateOf(false) }
    var showCategoryDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Presupuesto") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedTab == 0) showTransactionDialog = true
                    else showCategoryDialog = true
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Movimientos") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Categorías") }
                )
            }
            when (selectedTab) {
                0 -> TransactionsTab(
                    transactions = transactions,
                    summary = summary,
                    onDelete = viewModel::deleteTransaction
                )
                1 -> CategoriesTab(
                    categories = categories,
                    transactions = transactions
                )
            }
        }
    }

    if (showTransactionDialog) {
        AddTransactionDialog(
            categories = categories,
            onDismiss = { showTransactionDialog = false },
            onConfirm = { amount, type, description, categoryId ->
                viewModel.addTransaction(amount, type, description, categoryId)
                showTransactionDialog = false
            }
        )
    }

    if (showCategoryDialog) {
        AddCategoryDialog(
            onDismiss = { showCategoryDialog = false },
            onConfirm = { name, limit ->
                viewModel.addCategory(name, limit)
                showCategoryDialog = false
            }
        )
    }
}

@Composable
private fun TransactionsTab(
    transactions: List<TransactionEntity>,
    summary: BudgetSummary,
    onDelete: (TransactionEntity) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Ingresos", style = MaterialTheme.typography.labelSmall)
                    Text(
                        "+$${String.format("%.2f", summary.totalIncome)}",
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Gastos", style = MaterialTheme.typography.labelSmall)
                    Text(
                        "-$${String.format("%.2f", summary.totalExpenses)}",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Balance", style = MaterialTheme.typography.labelSmall)
                    Text(
                        "$${String.format("%.2f", summary.balance)}",
                        fontWeight = FontWeight.Bold,
                        color = if (summary.balance >= 0)
                            MaterialTheme.colorScheme.tertiary
                        else MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        if (transactions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay movimientos este mes", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(transactions, key = { it.id }) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        onDelete = { onDelete(transaction) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TransactionItem(
    transaction: TransactionEntity,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = if (transaction.type == "income")
                    Icons.Default.ArrowUpward
                else Icons.Default.ArrowDownward,
                contentDescription = null,
                tint = if (transaction.type == "income")
                    MaterialTheme.colorScheme.tertiary
                else MaterialTheme.colorScheme.error
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.description.ifBlank {
                        if (transaction.type == "income") "Ingreso" else "Gasto"
                    },
                    fontWeight = FontWeight.Medium
                )
                Text(transaction.date, style = MaterialTheme.typography.bodySmall)
            }
            Text(
                text = "${if (transaction.type == "income") "+" else "-"}$${
                    String.format("%.2f", transaction.amount)
                }",
                fontWeight = FontWeight.Bold,
                color = if (transaction.type == "income")
                    MaterialTheme.colorScheme.tertiary
                else MaterialTheme.colorScheme.error
            )
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun CategoriesTab(
    categories: List<BudgetCategoryEntity>,
    transactions: List<TransactionEntity>
) {
    if (categories.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("No hay categorías", style = MaterialTheme.typography.bodyLarge)
                Text("Toca + para agregar una", style = MaterialTheme.typography.bodySmall)
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories, key = { it.id }) { category ->
                val spent = transactions
                    .filter { it.categoryId == category.id && it.type == "expense" }
                    .sumOf { it.amount }
                CategoryItem(category = category, spent = spent)
            }
        }
    }
}

@Composable
private fun CategoryItem(
    category: BudgetCategoryEntity,
    spent: Double
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(category.name, fontWeight = FontWeight.Medium)
                Text(
                    "$${String.format("%.2f", spent)} / $${
                        String.format("%.2f", category.monthlyLimit)
                    }"
                )
            }
            val progress = if (category.monthlyLimit > 0)
                (spent / category.monthlyLimit).toFloat().coerceIn(0f, 1f)
            else 0f
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth(),
                color = when {
                    progress >= 0.9f -> MaterialTheme.colorScheme.error
                    progress >= 0.7f -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.primary
                }
            )
            Text(
                text = "${(progress * 100).toInt()}% del límite mensual",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTransactionDialog(
    categories: List<BudgetCategoryEntity>,
    onDismiss: () -> Unit,
    onConfirm: (Double, String, String, Long?) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("expense") }
    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }
    var expanded by remember { mutableStateOf(false) }
    val selectedCategory = categories.find { it.id == selectedCategoryId }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Movimiento") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = type == "income",
                        onClick = { type = "income" },
                        label = { Text("Ingreso") }
                    )
                    FilterChip(
                        selected = type == "expense",
                        onClick = { type = "expense" },
                        label = { Text("Gasto") }
                    )
                }
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Monto *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )
                if (categories.isNotEmpty()) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedCategory?.name ?: "Sin categoría",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Categoría") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Sin categoría") },
                                onClick = {
                                    selectedCategoryId = null
                                    expanded = false
                                }
                            )
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat.name) },
                                    onClick = {
                                        selectedCategoryId = cat.id
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val amountDouble = amount.toDoubleOrNull()
                    if (amountDouble != null && amountDouble > 0) {
                        onConfirm(amountDouble, type, description, selectedCategoryId)
                    }
                },
                enabled = amount.toDoubleOrNull()?.let { it > 0 } == true
            ) { Text("Agregar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
private fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var limit by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Categoría") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = limit,
                    onValueChange = { limit = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Límite mensual") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) onConfirm(name, limit.toDoubleOrNull() ?: 0.0)
                },
                enabled = name.isNotBlank()
            ) { Text("Agregar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
