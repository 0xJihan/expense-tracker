package com.jihan.expensetracker

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.jihan.expensetracker.adapter.ViewPagerAdapter
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

        TabLayoutMediator(binding.tabLayout,binding.viewPager2){
            tab,positin->

            if (positin==0){
                tab.text = "Home"
                tab.icon = resources.getDrawable(R.drawable.round_home_24)
            }
            else{
                tab.text = "List"
                tab.icon = getDrawable(R.drawable.baseline_format_list_bulleted_24)

            }

        }.attach()



    }



    private fun loadFragments() {
        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)


        adapter.addFragment(HomeFragment(binding.viewPager2))
        adapter.addFragment(ViewAllFragment())

        binding.viewPager2.adapter = adapter

    }

}