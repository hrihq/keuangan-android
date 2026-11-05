package com.fintech.catat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.fintech.catat.data.CatatDatabase
import com.fintech.catat.data.Transaction
import com.fintech.catat.data.TransactionType
import com.fintech.catat.ui.screens.HomeScreen
import com.fintech.catat.ui.screens.AddTransactionScreen
import com.fintech.catat.ui.screens.StatisticsScreen
import com.fintech.catat.ui.theme.CatatKeuanganTheme
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class MainActivity : ComponentActivity() {
    private lateinit var database: CatatDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = Room.databaseBuilder(
            applicationContext,
            CatatDatabase::class.java,
            "catat_database"
        ).build()

        setContent {
            CatatKeuanganTheme {
                MainApp(database)
            }
        }
    }
}

@Composable
fun MainApp(database: CatatDatabase) {
    var currentScreen by remember { mutableStateOf("home") }
    var transactions by remember { mutableStateOf<List<Transaction>>(emptyList()) }
    var totalIncome by remember { mutableStateOf(0L) }
    var totalExpense by remember { mutableStateOf(0L) }

    val scope = rememberCoroutineScope()
    val dao = database.transactionDao()

    LaunchedEffect(Unit) {
        scope.launch {
            dao.getAllTransactions().collect { txs ->
                transactions = txs
            }
        }
        scope.launch {
            dao.getTotalIncome().collect { income ->
                totalIncome = income ?: 0L
            }
        }
        scope.launch {
            dao.getTotalExpense().collect { expense ->
                totalExpense = expense ?: 0L
            }
        }
    }

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
        when (currentScreen) {
            "home" -> HomeScreen(
                transactions = transactions,
                totalIncome = totalIncome,
                totalExpense = totalExpense,
                onAddClick = { currentScreen = "add" },
                modifier = Modifier.padding(innerPadding)
            )
            "add" -> AddTransactionScreen(
                onBack = { currentScreen = "home" },
                onSave = { desc, amount, category, type ->
                    scope.launch {
                        dao.insert(
                            Transaction(
                                description = desc,
                                amount = amount,
                                category = category,
                                type = type,
                                date = LocalDateTime.now()
                            )
                        )
                        currentScreen = "home"
                    }
                }
            )
            "stats" -> StatisticsScreen(transactions = transactions)
            else -> HomeScreen(transactions = transactions, totalIncome = totalIncome, totalExpense = totalExpense)
        }
    }
}
