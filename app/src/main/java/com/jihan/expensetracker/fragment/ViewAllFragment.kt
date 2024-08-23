package com.jihan.expensetracker.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.jihan.expensetracker.Adapter.RecyclerViewAdapter
import com.jihan.expensetracker.R
import com.jihan.expensetracker.databinding.FragmentViewAllBinding
import com.jihan.expensetracker.model.Repository
import com.jihan.expensetracker.model.ViewModel
import com.jihan.expensetracker.model.ViewModelFactory
import com.jihan.expensetracker.room.InformationDatabase

class ViewAllFragment : Fragment() {

    private lateinit var binding: FragmentViewAllBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewAllBinding.inflate(inflater, container, false)

        val infoDao = InformationDatabase.getDatabase(requireContext()).getDao()

        val viewModel =
            ViewModelProvider(this, ViewModelFactory(Repository(infoDao)))[ViewModel::class.java]

        binding.lifecycleOwner = this

        val adapterAll = RecyclerViewAdapter(viewModel.arrayList, viewModel)
        val adapterIncome = RecyclerViewAdapter(viewModel.incomeList, viewModel)
        val adapterExpense = RecyclerViewAdapter(viewModel.expenseList, viewModel)


        // setting adapter to spinner
        val filterType = resources.getStringArray(R.array.filterType)
        val spinnerAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, filterType)
        binding.spinnerFilter.adapter = spinnerAdapter


        // spinner Item Selected Listener
        binding.spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {

                if (position == 0) {
                    // setting adapter to recyclerView

                    binding.viewAllRecyclerView.adapter = adapterAll

                } else if (position == 1) {
                    binding.viewAllRecyclerView.adapter =adapterIncome

                } else {
                    binding.viewAllRecyclerView.adapter =adapterExpense

                }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }


        // observing data changes
        viewModel.arrayList.observe(viewLifecycleOwner) {
            adapterAll.notifyDataSetChanged()

            // showing lottie if arrayList is empty
            if (it.isNullOrEmpty()) {
                binding.viewAllLottie.visibility = View.VISIBLE
                binding.viewAllLottie.resumeAnimation()
            } else {
                binding.viewAllLottie.visibility = View.GONE
                binding.viewAllLottie.pauseAnimation()
            }
        }


        viewModel.incomeList.observe(viewLifecycleOwner){
            adapterIncome.notifyDataSetChanged()

        }

        viewModel.expenseList.observe(viewLifecycleOwner){
            adapterExpense.notifyDataSetChanged()

        }





        return binding.root
    }

}
