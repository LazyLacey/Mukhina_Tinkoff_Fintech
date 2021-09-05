package com.example.developerslife

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.developerslife.databinding.ActivityMainBinding
import com.example.developerslife.services.Constants
import com.example.developerslife.ui.main.Adapter
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tabs = binding.tabs;
        val viewPager2 = binding.viewPager2;
        viewPager2.adapter = Adapter(this)

        TabLayoutMediator(tabs, viewPager2) { tab, position ->
            tab.text = Constants.TAB_TITLES[position]
        }.attach();
    }
}