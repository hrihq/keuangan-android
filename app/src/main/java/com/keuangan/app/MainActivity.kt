package com.keuangan.app

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var db: TransactionDb
    private lateinit var tvTotalBalance: TextView
    private lateinit var tvTotalIncome: TextView
    private lateinit var tvTotalExpense: TextView
    private lateinit var etTitle: TextInputEditText
    private lateinit var etAmount: TextInputEditText
    private lateinit var toggleType: MaterialButtonToggleGroup
    private lateinit var btnSave: MaterialButton
    private lateinit var rvTransactions: RecyclerView
    private lateinit var tvEmptyState: TextView

    private lateinit var adapter: TransactionAdapter
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = TransactionDb(this)
        tvTotalBalance = findViewById(R.id.tvTotalBalance)
        tvTotalIncome = findViewById(R.id.tvTotalIncome)
        tvTotalExpense = findViewById(R.id.tvTotalExpense)
        etTitle = findViewById(R.id.etTitle)
        etAmount = findViewById(R.id.etAmount)
        toggleType = findViewById(R.id.toggleType)
        btnSave = findViewById(R.id.btnSave)
        rvTransactions = findViewById(R.id.rvTransactions)
        tvEmptyState = findViewById(R.id.tvEmptyState)

        adapter = TransactionAdapter(
            onItemClick = { item -> showDeleteDialog(item) }
        )
        rvTransactions.layoutManager = LinearLayoutManager(this)
        rvTransactions.adapter = adapter

        btnSave.setOnClickListener { saveTransaction() }

        loadData()
    }

    private fun showDeleteDialog(item: TransactionItem) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Hapus Transaksi")
            .setMessage("Hapus \"${item.title}\"?")
            .setPositiveButton("Hapus") { _, _ ->
                db.delete(item.id)
                loadData()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun saveTransaction() {
        val title = etTitle.text?.toString()?.trim().orEmpty()
        val amountStr = etAmount.text?.toString()?.trim().orEmpty()

        if (title.isEmpty()) {
            etTitle.error = "Keterangan wajib diisi"
            return
        }
        val amount = amountStr.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            etAmount.error = "Nominal harus lebih dari 0"
            return
        }

        val isIncome = toggleType.checkedButtonId == R.id.btnIncome
        db.insert(title, amount, isIncome)

        etTitle.text?.clear()
        etAmount.text?.clear()
        Toast.makeText(this, "Tersimpan", Toast.LENGTH_SHORT).show()
        loadData()
    }

    private fun loadData() {
        val items = db.getAll()
        val (income, expense, balance) = db.getSummary()

        tvTotalBalance.text = currencyFormat.format(balance)
        tvTotalIncome.text = "+ ${currencyFormat.format(income)}"
        tvTotalExpense.text = "- ${currencyFormat.format(expense)}"

        if (items.isEmpty()) {
            tvEmptyState.visibility = View.VISIBLE
            rvTransactions.visibility = View.GONE
        } else {
            tvEmptyState.visibility = View.GONE
            rvTransactions.visibility = View.VISIBLE
            adapter.submitList(items)
        }
    }

    // ponytail: In-file RecyclerView Adapter. Extract to dedicated file if item animations, swipe-to-delete, or multi-viewholder logic added.
    inner class TransactionAdapter(
        private val onItemClick: (TransactionItem) -> Unit
    ) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

        private var list = listOf<TransactionItem>()

        fun submitList(newList: List<TransactionItem>) {
            list = newList
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_transaction, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(list[position])
        }

        override fun getItemCount() = list.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            private val tvTitle: TextView = view.findViewById(R.id.tvItemTitle)
            private val tvDate: TextView = view.findViewById(R.id.tvItemDate)
            private val tvAmount: TextView = view.findViewById(R.id.tvItemAmount)
            private val ivIcon: ImageView = view.findViewById(R.id.ivTypeIcon)
            private val flIconContainer: FrameLayout = view.findViewById(R.id.flIconContainer)

            fun bind(item: TransactionItem) {
                tvTitle.text = item.title
                tvDate.text = item.date

                val context = itemView.context
                if (item.isIncome) {
                    tvAmount.text = "+ ${currencyFormat.format(item.amount)}"
                    tvAmount.setTextColor(ContextCompat.getColor(context, R.color.income_text))
                    ivIcon.setImageResource(R.drawable.ic_arrow_upward)
                    ivIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.income_green))
                    flIconContainer.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.income_container))
                } else {
                    tvAmount.text = "- ${currencyFormat.format(item.amount)}"
                    tvAmount.setTextColor(ContextCompat.getColor(context, R.color.expense_text))
                    ivIcon.setImageResource(R.drawable.ic_arrow_downward)
                    ivIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.expense_red))
                    flIconContainer.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.expense_container))
                }

                itemView.setOnClickListener { onItemClick(item) }
            }
        }
    }
}
