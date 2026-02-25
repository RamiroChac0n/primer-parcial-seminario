package com.example.primerparcial.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.primerparcial.ui.screens.budget.BudgetScreen
import com.example.primerparcial.ui.screens.dashboard.DashboardScreen
import com.example.primerparcial.ui.screens.habits.HabitsScreen
import com.example.primerparcial.ui.screens.profile.ProfileScreen

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Dashboard : Screen("dashboard", "Inicio", Icons.Default.Home)
    data object Habits : Screen("habits", "Hábitos", Icons.Default.Star)
    data object Budget : Screen("budget", "Presupuesto", Icons.Default.ShoppingCart)
    data object Profile : Screen("profile", "Perfil", Icons.Default.Person)
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val items = listOf(Screen.Dashboard, Screen.Habits, Screen.Budget, Screen.Profile)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy
                            ?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) { DashboardScreen() }
            composable(Screen.Habits.route) { HabitsScreen() }
            composable(Screen.Budget.route) { BudgetScreen() }
            composable(Screen.Profile.route) { ProfileScreen() }
        }
    }
}
