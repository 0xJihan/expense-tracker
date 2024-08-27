package com.jihan.expensetracker.fragment

import RecyclerViewAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.jihan.expensetracker.R
import com.jihan.expensetracker.databinding.FragmentViewAllBinding
import com.jihan.expensetracker.model.Repository
import com.jihan.expensetracker.model.ViewModel
import com.jihan.expensetracker.model.ViewModelFactory
import com.jihan.expensetracker.room.InformationDatabase

class ViewAllFragment : Fragment() {

    private lateinit var binding: FragmentViewAllBinding
    private lateinit var viewModel: ViewModel
    private lateinit var adapter: RecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewAllBinding.inflate(inflater, container, false)

        val infoDao = InformationDatabase.getDatabase(requireContext()).getDao()
        viewModel =
            ViewModelProvider(this, ViewModelFactory(Repository(infoDao)))[ViewModel::class.java]

        adapter = RecyclerViewAdapter(emptyList(), viewModel)
        binding.viewAllRecyclerView.adapter = adapter

        setupSpinner()
        observeData()

        binding.searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter(newText.orEmpty())
                return true
            }
        })

        return binding.root
    }

    private fun setupSpinner() {
        val filterType = resources.getStringArray(R.array.filterType)
        val spinnerAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, filterType)
        binding.spinnerFilter.adapter = spinnerAdapter

        binding.spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                updateRecyclerViewBasedOnFilter(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Optional: Handle case when nothing is selected
            }
        }
    }

    private fun updateRecyclerViewBasedOnFilter(position: Int) {
        val list = when (position) {
            1 -> viewModel.incomeList.value
            2 -> viewModel.expenseList.value
            else -> viewModel.arrayList.value
        }
        list?.let {
            adapter.updateList(it)
            updateLottieVisibility(it.isEmpty())
        }
    }

    private fun observeData() {
        viewModel.arrayList.observe(viewLifecycleOwner) {
            if (binding.spinnerFilter.selectedItemPosition == 0) {
                adapter.updateList(it)
                updateLottieVisibility(it.isEmpty())
            }
        }

        viewModel.incomeList.observe(viewLifecycleOwner) {
            if (binding.spinnerFilter.selectedItemPosition == 1) {
                adapter.updateList(it)
                updateLottieVisibility(it.isEmpty())
            }
        }

        viewModel.expenseList.observe(viewLifecycleOwner) {
            if (binding.spinnerFilter.selectedItemPosition == 2) {
                adapter.updateList(it)
                updateLottieVisibility(it.isEmpty())
            }
        }
    }

    private fun updateLottieVisibility(isEmpty: Boolean) {
        if (isEmpty) {
            binding.viewAllLottie.visibility = View.VISIBLE
            binding.viewAllLottie.playAnimation()
        } else {
            binding.viewAllLottie.visibility = View.GONE
            binding.viewAllLottie.pauseAnimation()
        }
    }
}

