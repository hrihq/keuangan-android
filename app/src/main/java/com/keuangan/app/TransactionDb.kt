package com.keuangan.app

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class TransactionItem(
    val id: Long,
    val title: String,
    val amount: Double,
    val isIncome: Boolean,
    val date: String
)

// ponytail: SQLiteOpenHelper used over Room. Upgrade to Room if reactive flows, complex joins, or automated schema migrations are required.
class TransactionDb(context: Context) : SQLiteOpenHelper(context, "keuangan.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE transactions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                amount REAL NOT NULL,
                is_income INTEGER NOT NULL,
                date TEXT NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS transactions")
        onCreate(db)
    }

    fun insert(title: String, amount: Double, isIncome: Boolean): Long {
        val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        val values = ContentValues().apply {
            put("title", title)
            put("amount", amount)
            put("is_income", if (isIncome) 1 else 0)
            put("date", dateStr)
        }
        return writableDatabase.insert("transactions", null, values)
    }

    fun getAll(): List<TransactionItem> {
        val list = mutableListOf<TransactionItem>()
        val cursor = readableDatabase.rawQuery(
            "SELECT id, title, amount, is_income, date FROM transactions ORDER BY id DESC",
            null
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    TransactionItem(
                        id = it.getLong(0),
                        title = it.getString(1),
                        amount = it.getDouble(2),
                        isIncome = it.getInt(3) == 1,
                        date = it.getString(4)
                    )
                )
            }
        }
        return list
    }

    fun delete(id: Long): Int {
        return writableDatabase.delete("transactions", "id = ?", arrayOf(id.toString()))
    }

    fun getSummary(): Triple<Double, Double, Double> {
        var income = 0.0
        var expense = 0.0
        val cursor = readableDatabase.rawQuery(
            "SELECT is_income, SUM(amount) FROM transactions GROUP BY is_income",
            null
        )
        cursor.use {
            while (it.moveToNext()) {
                val isInc = it.getInt(0) == 1
                val sum = it.getDouble(1)
                if (isInc) income = sum else expense = sum
            }
        }
        return Triple(income, expense, income - expense)
    }
}
