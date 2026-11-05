package com.fintech.catat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.fintech.catat.ui.screens.HomeScreen
import com.fintech.catat.ui.screens.AddTransactionScreen
import com.fintech.catat.ui.screens.StatisticsScreen
import com.fintech.catat.ui.theme.CatatKeuanganTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CatatKeuanganTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    var currentScreen by remember { mutableStateOf("home") }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = currentScreen == "home",
                    onClick = { currentScreen = "home" },
                    icon = { Icon(Icons.Default.Add, "Home") },
                    label = { Text("Beranda") }
                )
                NavigationBarItem(
                    selected = currentScreen == "stats",
                    onClick = { currentScreen = "stats" },
                    icon = { Icon(Icons.Default.Add, "Stats") },
                    label = { Text("Statistik") }
                )
                NavigationBarItem(
                    selected = currentScreen == "wallet",
                    onClick = { currentScreen = "wallet" },
                    icon = { Icon(Icons.Default.Add, "Wallet") },
                    label = { Text("Dompet") }
                )
                NavigationBarItem(
                    selected = currentScreen == "profile",
                    onClick = { currentScreen = "profile" },
                    icon = { Icon(Icons.Default.Add, "Profile") },
                    label = { Text("Profil") }
                )
            }
        },
        floatingActionButton = {
            if (currentScreen == "home") {
                FloatingActionButton(
                    onClick = { currentScreen = "add" },
                    containerColor = Color(0xFF0B57D0)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
                }
            }
        }
    ) { innerPadding ->
        androidx.compose.foundation.layout.Box(
            modifier = androidx.compose.foundation.layout.Modifier.padding(innerPadding)
        ) {
            when (currentScreen) {
                "home" -> HomeScreen(onAddClick = { currentScreen = "add" })
                "add" -> AddTransactionScreen(onBack = { currentScreen = "home" })
                "stats" -> StatisticsScreen()
                else -> HomeScreen()
            }
        }
    }
}
