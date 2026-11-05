package com.fintech.catat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fintech.catat.data.Category
import com.fintech.catat.data.TransactionType
import java.time.LocalDateTime

@Composable
fun AddTransactionScreen(onBack: () -> Unit = {}) {
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(Category.MAKANAN) }
    var transactionType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var selectedDate by remember { mutableStateOf(LocalDateTime.now()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        TopAppBar(
            title = { Text("Catat transaksi") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            )
        )

        Column(modifier = Modifier.padding(16.dp)) {
            // Type Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TransactionType.values().forEach { type ->
                    Button(
                        onClick = { transactionType = type },
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (transactionType == type) Color(0xFF0B57D0) else Color.LightGray
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (type == TransactionType.EXPENSE) "Pengeluaran" else "Pemasukan",
                            fontSize = 12.sp,
                            color = if (transactionType == type) Color.White else Color.Black
                        )
                    }
                }
            }

            // Description Input
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Keterangan") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(12.dp)
            )

            // Amount Input
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it.filter { c -> c.isDigit() } },
                label = { Text("Nominal") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(12.dp)
            )

            // Category Selection
            Text(
                text = "Kategori",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(Category.values()) { cat ->
                    Button(
                        onClick = { selectedCategory = cat },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedCategory == cat) Color(0xFFC4EED0) else Color.LightGray
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = cat.label,
                            color = Color.Black,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Save Button
            Button(
                onClick = { onBack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0B57D0)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Simpan catatan", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Batal", color = Color.Black)
            }
        }
    }
}
