package com.jin.weather_pager.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class WeatherFragmentAdapter(val activity: AppCompatActivity,val fragments: List<Fragment>)
    : FragmentStateAdapter(activity) {




    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}