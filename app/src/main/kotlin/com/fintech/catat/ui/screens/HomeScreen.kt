package com.fintech.catat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fintech.catat.data.Transaction
import com.fintech.catat.data.TransactionType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    transactions: List<Transaction> = emptyList(),
    totalIncome: Long = 0,
    totalExpense: Long = 0,
    onAddClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val balance = totalIncome - totalExpense
    val budgetLimit = 5000000L
    val spent = totalExpense
    val budgetPercent = (spent.toFloat() / budgetLimit * 100).toInt().coerceIn(0, 100)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
    ) {
        // Header
        Text(
            text = "Selamat pagi",
            modifier = Modifier.padding(16.dp),
            fontSize = 14.sp,
            color = Color.Gray
        )

        // Balance Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0B57D0))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Total saldo • Nov 2026",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )
                Text(
                    text = "Rp ${formatCurrency(balance)}",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        // Income, Expense Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard("Pemasukan", "Rp ${formatCurrency(totalIncome)}", Color(0xFFC4EED0), modifier = Modifier.weight(1f))
            StatCard("Pengeluaran", "Rp ${formatCurrency(totalExpense)}", Color(0xFFFFE08A), modifier = Modifier.weight(1f))
        }

        // Budget Progress
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Anggaran bulan ini",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                LinearProgressIndicator(
                    progress = budgetPercent / 100f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .height(8.dp),
                    color = Color(0xFF0B57D0),
                    trackColor = Color.LightGray
                )
                Text(
                    text = "$budgetPercent% terpakai • sisa Rp ${formatCurrency(budgetLimit - spent)}",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        // Recent Transactions
        Text(
            text = "Transaksi terbaru",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(transactions.take(5)) { tx ->
                TransactionItem(tx)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // FAB placeholder (actual FAB in main scaffold)
    }
}

@Composable
fun StatCard(label: String, amount: String, bgColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = label, fontSize = 12.sp, color = Color.Gray)
            Text(
                text = amount,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.description,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${transaction.category} • ${formatDate(transaction.date)}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Text(
                text = "${if (transaction.type == TransactionType.INCOME) "+" else "-"}Rp ${formatCurrency(transaction.amount)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (transaction.type == TransactionType.INCOME) Color(0xFF4CAF50) else Color(0xFFE53935)
            )
        }
    }
}

fun formatCurrency(value: Long): String {
    return String.format("%,d", value).replace(",", ".")
}

fun formatDate(date: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("d MMM")
    return date.format(formatter)
}
