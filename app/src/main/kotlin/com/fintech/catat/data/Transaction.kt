package com.fintech.catat.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val description: String,
    val amount: Long, // in rupiah, as Long to avoid precision issues
    val category: String,
    val type: TransactionType, // INCOME or EXPENSE
    val date: LocalDateTime,
    val notes: String = ""
)

enum class TransactionType {
    INCOME, EXPENSE
}

enum class Category(val label: String) {
    MAKANAN("Makanan"),
    TRANSPORT("Transport"),
    TAGIHAN("Tagihan"),
    BELANJA("Belanja"),
    HIBURAN("Hiburan"),
    KESEHATAN("Kesehatan"),
    LAINNYA("Lainnya");
}
