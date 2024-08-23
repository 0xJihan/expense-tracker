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
import com.jihan.expensetracker.Adapter.RecyclerViewAdapter
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
        )[ViewModel::class]

        binding.model = viewModel // setting view model to binding

        binding.lifecycleOwner = this

        // setting recycler view adapter
        val adapter = RecyclerViewAdapter(emptyList(), viewModel)
        binding.recyclerView.adapter = adapter


        // observing recent array list
        viewModel.recentArrayList.observe(viewLifecycleOwner) {
            adapter.updateList(it)

            // Show or hide the Lottie animation based on the list's size
            if (it.isNullOrEmpty()) {
                binding.recentLittie.visibility = View.VISIBLE
                binding.recentLittie.resumeAnimation()
            } else {
                binding.recentLittie.visibility = View.GONE
                binding.recentLittie.pauseAnimation()
            }

        }


        viewModel.totalIncome.observe(viewLifecycleOwner) {
            val startValue = binding.tvTotalIncome.text.toString().toFloat()
            animateNumberChange(startValue, it.toFloat(), 1000, binding.tvTotalIncome)
        }


        viewModel.totalExpense.observe(viewLifecycleOwner) {
            val startValue = binding.tvTotalExpense.text.toString().toFloat()
            animateNumberChange(startValue, it.toFloat(), 1000, binding.tvTotalExpense)
        }


        // loading pie chart with updated data
        loadPieChart()
        viewModel.totalBalance.observe(viewLifecycleOwner) {

            loadPieChart()
        }


        // inserting data to room database
        binding.favAdd.setOnClickListener {
            showBottomSheetDialog()
        }



        binding.tvViewAll.setOnClickListener {
            viewPager2.currentItem = 1
        }





        return binding.root
    }

    // ======================================================================================================
    // ======================================================================================================
    // ======================================================================================================


    // bottom sheet dialog for inserting data
    private fun showBottomSheetDialog() {
        val item = ItemInsertBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext()) // creating bottom sheet dialog
        dialog.setContentView(item.root) // setting bottom sheet dialog content view

        item.btnInput.setOnClickListener {     // button on click listener

            val amount = item.edInputAmount.text.toString().trim()
            val notes = item.edInputNotes.text.toString().trim()


            if (amount.isNotEmpty() && notes.isNotEmpty()) {


                try {

                    viewModel.insertInfo(
                        Information(
                            0, amount.toFloat(), notes, item.chipIncome.isChecked, Date()
                        )
                    )

                    dialog.dismiss() // dismissing bottom sheet dialog

                } catch (error: NumberFormatException) {
                    Toast.makeText(requireContext(), "Invalid amount", Toast.LENGTH_SHORT).show()
                }


            } else {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT)
                    .show()
            }


        }


        dialog.show()  // showing dialog

    }


    private fun loadPieChart() {
        val pieChart = binding.piechart


        val list: ArrayList<PieEntry> = ArrayList() // creating list of pie entry

        // adding data to list
        list.add(PieEntry(viewModel.totalIncome.value!!.toFloat(), "Income"))
        list.add(PieEntry(viewModel.totalExpense.value!!.toFloat(), "Expense"))


        // creating pie data set
        val pieDataSet = PieDataSet(list, "")


        // setting colors for pie chart background
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS, 210)


        // setting size nad color for values in pie chart
        pieDataSet.valueTextColor = Color.BLACK
        pieDataSet.valueTextSize = 14f

        pieChart.setUsePercentValues(true)

        pieChart.setEntryLabelColor(Color.WHITE)
        pieChart.setEntryLabelTextSize(14f)

        val pieData = PieData(pieDataSet) // creating pie data

        pieChart.data = pieData // setting data to pie data

        pieChart.description.text = "Jihan Khan" // setting description


        // setting center text
        pieChart.centerText = "Balance\n${viewModel.totalBalance.value} TK"
        pieChart.setCenterTextSize(12f)
        pieChart.setCenterTextColor(Color.BLUE)

        pieChart.animateY(2000)
    }


    private fun animateNumberChange(
        startValue: Float,
        endValue: Float,
        duration: Long,
        view: TextView
    ) {
        val animator = ValueAnimator.ofFloat(startValue, endValue)
        animator.duration = duration
        animator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Float
            view.text = "$animatedValue"
        }
        animator.start()
    }


}