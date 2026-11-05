package com.fintech.catat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import java.time.YearMonth

@Composable
fun StatisticsScreen(transactions: List<Transaction> = emptyList()) {
    val currentMonth = YearMonth.now()
    val monthTransactions = transactions.filter {
        YearMonth.from(it.date) == currentMonth && it.type == TransactionType.EXPENSE
    }

    val byCategory = monthTransactions.groupBy { it.category }
        .mapValues { (_, txs) -> txs.sumOf { it.amount } }
    val total = byCategory.values.sum()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
    ) {
        Text(
            text = "Statistik",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        Text(
            text = currentMonth.month.toString(),
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // Pie chart placeholder (simplified bar representation)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(byCategory.entries.toList()) { (category, amount) ->
                    val percent = if (total > 0) (amount.toFloat() / total * 100).toInt() else 0
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "$category $percent%", fontSize = 12.sp)
                        LinearProgressIndicator(
                            progress = percent / 100f,
                            modifier = Modifier
                                .weight(1f)
                                .height(8.dp)
                                .padding(horizontal = 8.dp),
                            color = Color(0xFF0B57D0)
                        )
                    }
                }
            }
        }

        // Weekly chart placeholder
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Pengeluaran mingguan",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min").forEach { day ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .width(30.dp)
                                    .height(60.dp)
                                    .background(Color(0xFFC2DAFF), RoundedCornerShape(4.dp))
                            )
                            Text(day, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Summary
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFC4EED0))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Hemat dibanding bulan lalu",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
                Text(
                    text = "Rp 320.000",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = "Pengeluaran makanan turun 12%",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
