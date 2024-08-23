package com.jihan.expensetracker.fragment

import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jihan.expensetracker.adapter.RecyclerViewAdapter
import com.jihan.expensetracker.databinding.FragmentHomeBinding
import com.jihan.expensetracker.databinding.ItemInsertBinding
import com.jihan.expensetracker.model.Repository
import com.jihan.expensetracker.model.ViewModel
import com.jihan.expensetracker.model.ViewModelFactory
import com.jihan.expensetracker.room.Information
import com.jihan.expensetracker.room.InformationDatabase
import java.util.Date

class HomeFragment(private val viewPager2: ViewPager2) : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: ViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val infoDao = InformationDatabase.getDatabase(requireContext()).getDao()
        viewModel = ViewModelProvider(
            this, ViewModelFactory(Repository(infoDao))
        )[ViewModel::class.java]

        binding.model = viewModel
        binding.lifecycleOwner = this

        setupRecyclerView()
        observeViewModel()

        binding.favAdd.setOnClickListener { showBottomSheetDialog() }
        binding.tvViewAll.setOnClickListener { viewPager2.currentItem = 1 }

        return binding.root
    }

    private fun setupRecyclerView() {
        val adapter = RecyclerViewAdapter(emptyList(), viewModel)
        binding.recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.recentArrayList.observe(viewLifecycleOwner) {
            (binding.recyclerView.adapter as RecyclerViewAdapter).updateList(it)
            binding.recentLittie.visibility = if (it.isNullOrEmpty()) View.VISIBLE else View.GONE
            if (it.isNullOrEmpty()) binding.recentLittie.resumeAnimation() else binding.recentLittie.pauseAnimation()
        }

        viewModel.totalIncome.observe(viewLifecycleOwner) {
            animateNumberChange(binding.tvTotalIncome.text.toString().toFloat(), it.toFloat(), 1000, binding.tvTotalIncome)
        }

        viewModel.totalExpense.observe(viewLifecycleOwner) {
            animateNumberChange(binding.tvTotalExpense.text.toString().toFloat(), it.toFloat(), 1000, binding.tvTotalExpense)
        }

        viewModel.totalBalance.observe(viewLifecycleOwner) {
            loadPieChart()
        }

        loadPieChart()
    }

    private fun showBottomSheetDialog() {
        val item = ItemInsertBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext()).apply {
            setContentView(item.root)
        }

        item.btnInput.setOnClickListener {
            val amountStr = item.edInputAmount.text.toString().trim()
            val notes = item.edInputNotes.text.toString().trim()

            if (amountStr.isNotEmpty() && notes.isNotEmpty()) {
                try {
                    val amount = amountStr.toFloat()
                    viewModel.insertInfo(Information(0, amount, notes, item.chipIncome.isChecked, Date()))
                    dialog.dismiss()
                } catch (e: NumberFormatException) {
                    Toast.makeText(requireContext(), "Invalid amount", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun loadPieChart() {
        val pieChart = binding.piechart
        val entries = listOf(
            PieEntry(viewModel.totalIncome.value?.toFloat() ?: 0f, "Income"),
            PieEntry(viewModel.totalExpense.value?.toFloat() ?: 0f, "Expense")
        )

        val pieDataSet = PieDataSet(entries, "").apply {
            setColors(ColorTemplate.MATERIAL_COLORS.toList())
            valueTextColor = Color.BLACK
            valueTextSize = 14f
        }

        pieChart.apply {
            data = PieData(pieDataSet)
            description.text = "Jihan Khan"
            centerText = "Balance\n${viewModel.totalBalance.value} TK"
            setCenterTextSize(12f)
            setCenterTextColor(Color.BLUE)
            setUsePercentValues(true)
            setEntryLabelColor(Color.WHITE)
            setEntryLabelTextSize(14f)
            animateY(2000)
        }
    }

    private fun animateNumberChange(startValue: Float, endValue: Float, duration: Long, view: TextView) {
        ValueAnimator.ofFloat(startValue, endValue).apply {
            this.duration = duration
            addUpdateListener { animation ->
                view.text = "${animation.animatedValue}"
            }
            start()
        }
    }
}
