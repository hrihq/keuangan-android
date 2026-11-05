package com.fintech.catat.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Transaction::class], version = 1, exportSchema = false)
@TypeConverters(DateTimeConverters::class)
abstract class CatatDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}
