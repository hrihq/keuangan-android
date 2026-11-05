package com.keuangan.app

import android.app.AlertDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var db: TransactionDb
    private lateinit var tvSummary: TextView
    private lateinit var etTitle: EditText
    private lateinit var etAmount: EditText
    private lateinit var rbIncome: RadioButton
    private lateinit var btnSave: Button
    private lateinit var lvTransactions: ListView

    private var items = listOf<TransactionItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = TransactionDb(this)
        tvSummary = findViewById(R.id.tvSummary)
        etTitle = findViewById(R.id.etTitle)
        etAmount = findViewById(R.id.etAmount)
        rbIncome = findViewById(R.id.rbIncome)
        btnSave = findViewById(R.id.btnSave)
        lvTransactions = findViewById(R.id.lvTransactions)

        btnSave.setOnClickListener { saveTransaction() }

        // ponytail: ListView + simple_list_item_1 instead of RecyclerView. Upgrade to RecyclerView with custom ViewHolder when list styling or animations are needed.
        lvTransactions.setOnItemClickListener { _, _, position, _ ->
            val item = items.getOrNull(position) ?: return@setOnItemClickListener
            AlertDialog.Builder(this)
                .setTitle("Hapus Transaksi")
                .setMessage("Hapus \"${item.title}\"?")
                .setPositiveButton("Hapus") { _, _ ->
                    db.delete(item.id)
                    loadData()
                }
                .setNegativeButton("Batal", null)
                .show()
        }

        loadData()
    }

    private fun saveTransaction() {
        val title = etTitle.text.toString().trim()
        val amountStr = etAmount.text.toString().trim()

        if (title.isEmpty()) {
            etTitle.error = "Keterangan wajib diisi"
            return
        }
        val amount = amountStr.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            etAmount.error = "Nominal harus lebih dari 0"
            return
        }

        val isIncome = rbIncome.isChecked
        db.insert(title, amount, isIncome)

        etTitle.text.clear()
        etAmount.text.clear()
        Toast.makeText(this, "Tersimpan", Toast.LENGTH_SHORT).show()
        loadData()
    }

    private fun loadData() {
        items = db.getAll()
        val (income, expense, balance) = db.getSummary()

        val fmt = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        tvSummary.text = "Saldo: ${fmt.format(balance)}\nMasuk: ${fmt.format(income)} | Keluar: ${fmt.format(expense)}"

        val displayList = items.map {
            val sign = if (it.isIncome) "[+]" else "[-]"
            "$sign ${it.title}\n${fmt.format(it.amount)} • ${it.date}"
        }

        lvTransactions.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            displayList
        )
    }
}
