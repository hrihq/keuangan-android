package com.keuangan.app

import android.app.DatePickerDialog
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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var db: TransactionDb
    private lateinit var adapter: TransactionAdapter

    private lateinit var tvTotalBalance: TextView
    private lateinit var tvTotalIncome: TextView
    private lateinit var tvTotalExpense: TextView
    private lateinit var tvBudgetSubtitle: TextView
    private lateinit var pbBudget: LinearProgressIndicator
    private lateinit var tvEmptyState: TextView
    private lateinit var rvTransactions: RecyclerView
    private lateinit var fabAdd: FloatingActionButton

    private val indonesianLocale = Locale("id", "ID")
    private val currencyFormat: NumberFormat = NumberFormat.getCurrencyInstance(indonesianLocale).apply {
        maximumFractionDigits = 0
    }
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", indonesianLocale)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = TransactionDb(this)

        initViews()
        setupRecyclerView()
        seedInitialDataIfEmpty()
        loadData()
        setupListeners()
    }

    private fun initViews() {
        tvTotalBalance = findViewById(R.id.tvTotalBalance)
        tvTotalIncome = findViewById(R.id.tvTotalIncome)
        tvTotalExpense = findViewById(R.id.tvTotalExpense)
        tvBudgetSubtitle = findViewById(R.id.tvBudgetSubtitle)
        pbBudget = findViewById(R.id.pbBudget)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        rvTransactions = findViewById(R.id.rvTransactions)
        fabAdd = findViewById(R.id.fabAdd)
    }

    private fun setupRecyclerView() {
        adapter = TransactionAdapter(
            currencyFormat = currencyFormat,
            onItemClick = { item -> showDeleteConfirmDialog(item) }
        )
        rvTransactions.layoutManager = LinearLayoutManager(this)
        rvTransactions.adapter = adapter
    }

    private fun seedInitialDataIfEmpty() {
        if (db.getAll().isEmpty()) {
            db.insert("Gaji Bulanan", 15000000.0, true, "Gaji")
            db.insert("Makan Siang & Kopi", 65000.0, false, "Makanan")
            db.insert("Bensin Motor", 35000.0, false, "Transport")
            db.insert("Tagihan WiFi & Listrik", 450000.0, false, "Tagihan")
            db.insert("Belanja Bulanan", 620000.0, false, "Belanja")
        }
    }

    private fun setupListeners() {
        fabAdd.setOnClickListener {
            showAddTransactionBottomSheet()
        }

        findViewById<View>(R.id.btnNotification)?.setOnClickListener {
            Toast.makeText(this, "Tidak ada notifikasi baru", Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.tabHome)?.setOnClickListener {
            // Already on Beranda
        }

        findViewById<View>(R.id.tabStats)?.setOnClickListener {
            showStatisticsBottomSheet()
        }

        findViewById<View>(R.id.tabWallet)?.setOnClickListener {
            val (_, _, balance) = db.getSummary()
            Toast.makeText(this, "Saldo Dompet: ${currencyFormat.format(balance)}", Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.tabProfile)?.setOnClickListener {
            Toast.makeText(this, "Catat Keuangan v1.3.0", Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.tvSeeAll)?.setOnClickListener {
            val count = db.getAll().size
            Toast.makeText(this, "Total $count transaksi tercatat", Toast.LENGTH_SHORT).show()
        }
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

        // Budget calculation (Monthly target: Rp 5.000.000)
        val monthlyBudget = 5000000.0
        val usedPercentage = if (monthlyBudget > 0) ((expense / monthlyBudget) * 100).toInt().coerceIn(0, 100) else 0
        val remainingBudget = (monthlyBudget - expense).coerceAtLeast(0.0)

        pbBudget.progress = usedPercentage
        tvBudgetSubtitle.text = "$usedPercentage% terpakai · sisa ${currencyFormat.format(remainingBudget)}"
    }

    private fun showAddTransactionBottomSheet() {
        val dialog = BottomSheetDialog(this)
        val sheetView = layoutInflater.inflate(R.layout.bottom_sheet_add_transaction, null)
        dialog.setContentView(sheetView)

        val etAmount = sheetView.findViewById<TextInputEditText>(R.id.etSheetAmount)
        val etTitle = sheetView.findViewById<TextInputEditText>(R.id.etSheetTitle)
        val toggleType = sheetView.findViewById<MaterialButtonToggleGroup>(R.id.toggleSheetType)
        val chipGroup = sheetView.findViewById<ChipGroup>(R.id.chipGroupCategory)
        val btnDate = sheetView.findViewById<MaterialButton>(R.id.btnSheetDate)
        val btnSave = sheetView.findViewById<MaterialButton>(R.id.btnSheetSave)
        val btnClose = sheetView.findViewById<ImageView>(R.id.btnSheetClose)

        val calendar = Calendar.getInstance()
        btnDate.text = dateFormat.format(calendar.time)

        btnDate.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    btnDate.text = dateFormat.format(calendar.time)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        btnSave.setOnClickListener {
            val amountStr = etAmount.text?.toString()?.trim().orEmpty()
            val title = etTitle.text?.toString()?.trim().orEmpty()

            if (amountStr.isEmpty()) {
                etAmount.error = "Masukkan nominal"
                return@setOnClickListener
            }

            val amount = amountStr.toDoubleOrNull()
            if (amount == null || amount <= 0) {
                etAmount.error = "Nominal harus lebih dari 0"
                return@setOnClickListener
            }

            val isIncome = toggleType.checkedButtonId == R.id.btnSheetIncome

            val category = when (chipGroup.checkedChipId) {
                R.id.chipMakanan -> "Makanan"
                R.id.chipTransport -> "Transport"
                R.id.chipBelanja -> "Belanja"
                R.id.chipTagihan -> "Tagihan"
                R.id.chipHiburan -> "Hiburan"
                R.id.chipLainnya -> "Lainnya"
                else -> if (isIncome) "Gaji" else "Makanan"
            }

            val note = if (title.isNotEmpty()) title else category
            db.insert(note, amount, isIncome, category)
            loadData()
            dialog.dismiss()
            Toast.makeText(this, "Transaksi disimpan", Toast.LENGTH_SHORT).show()
        }

        dialog.show()
    }

    private fun showStatisticsBottomSheet() {
        val dialog = BottomSheetDialog(this)
        val sheetView = layoutInflater.inflate(R.layout.bottom_sheet_statistics, null)
        dialog.setContentView(sheetView)

        val transactions = db.getAll()
        val expenseTransactions = transactions.filter { !it.isIncome }
        val totalExpense = expenseTransactions.sumOf { it.amount }

        if (totalExpense > 0) {
            val foodExpense = expenseTransactions.filter { it.category == "Makanan" }.sumOf { it.amount }
            val transportExpense = expenseTransactions.filter { it.category == "Transport" }.sumOf { it.amount }
            val billsExpense = expenseTransactions.filter { it.category == "Tagihan" }.sumOf { it.amount }

            val foodPct = ((foodExpense / totalExpense) * 100).toInt()
            val transportPct = ((transportExpense / totalExpense) * 100).toInt()
            val billsPct = ((billsExpense / totalExpense) * 100).toInt()

            sheetView.findViewById<TextView>(R.id.tvStatFood)?.text = "$foodPct%"
            sheetView.findViewById<LinearProgressIndicator>(R.id.pbStatFood)?.progress = foodPct

            sheetView.findViewById<TextView>(R.id.tvStatTransport)?.text = "$transportPct%"
            sheetView.findViewById<LinearProgressIndicator>(R.id.pbStatTransport)?.progress = transportPct

            sheetView.findViewById<TextView>(R.id.tvStatBills)?.text = "$billsPct%"
            sheetView.findViewById<LinearProgressIndicator>(R.id.pbStatBills)?.progress = billsPct
        }

        dialog.show()
    }

    private fun showDeleteConfirmDialog(item: TransactionItem) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Hapus Transaksi")
            .setMessage("Hapus '${item.title}' sebesar ${currencyFormat.format(item.amount)}?")
            .setPositiveButton("Hapus") { _, _ ->
                db.delete(item.id)
                loadData()
                Toast.makeText(this, "Transaksi dihapus", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    class TransactionAdapter(
        private val currencyFormat: NumberFormat,
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
            private val tvCategory: TextView = view.findViewById(R.id.tvItemCategory)
            private val tvTitle: TextView = view.findViewById(R.id.tvItemTitle)
            private val tvDate: TextView = view.findViewById(R.id.tvItemDate)
            private val tvAmount: TextView = view.findViewById(R.id.tvItemAmount)
            private val ivIcon: ImageView = view.findViewById(R.id.ivCategoryIcon)
            private val flIconContainer: FrameLayout = view.findViewById(R.id.flCategoryIconContainer)

            fun bind(item: TransactionItem) {
                val context = itemView.context
                tvCategory.text = item.category
                tvTitle.text = item.title
                tvDate.text = item.date

                when (item.category.lowercase(Locale.ROOT)) {
                    "makanan" -> ivIcon.setImageResource(R.drawable.ic_restaurant)
                    "transport" -> ivIcon.setImageResource(R.drawable.ic_directions_car)
                    "tagihan" -> ivIcon.setImageResource(R.drawable.ic_receipt)
                    "belanja" -> ivIcon.setImageResource(R.drawable.ic_wallet)
                    else -> {
                        if (item.isIncome) {
                            ivIcon.setImageResource(R.drawable.ic_arrow_upward)
                        } else {
                            ivIcon.setImageResource(R.drawable.ic_arrow_downward)
                        }
                    }
                }

                if (item.isIncome) {
                    tvAmount.text = "+ ${currencyFormat.format(item.amount)}"
                    tvAmount.setTextColor(ContextCompat.getColor(context, R.color.income_green))
                    ivIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.income_green))
                    flIconContainer.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.income_light))
                } else {
                    tvAmount.text = "- ${currencyFormat.format(item.amount)}"
                    tvAmount.setTextColor(ContextCompat.getColor(context, R.color.expense_red))
                    ivIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.brand_blue))
                    flIconContainer.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.brand_light_blue))
                }

                itemView.setOnClickListener { onItemClick(item) }
            }
        }
    }
}
