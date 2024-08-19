package com.jihan.expensetracker

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.jihan.expensetracker.Adapter.ViewPagerAdapter
import com.jihan.expensetracker.databinding.ActivityMainBinding
import com.jihan.expensetracker.fragment.HomeFragment
import com.jihan.expensetracker.fragment.ViewAllFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        loadFragments()



    }



    private fun loadFragments() {
        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)


        adapter.addFragment(HomeFragment(binding.viewPager2))
        adapter.addFragment(ViewAllFragment())

        binding.viewPager2.adapter = adapter

    }

}