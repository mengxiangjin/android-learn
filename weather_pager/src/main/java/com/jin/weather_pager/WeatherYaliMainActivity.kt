package com.jin.weather_pager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.jin.weather_pager.databinding.ActivityYlMainBinding
import com.jin.weather_pager.yl.SVWeatherFragmentYlOne
import com.jin.weather_pager.yl.SVWeatherFragmentYlTwo

class WeatherYaliMainActivity: AppCompatActivity() {


    private lateinit var binding: ActivityYlMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityYlMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var position = intent.getIntExtra("position", 0)
        if (position == 0) {
            var transaction = supportFragmentManager.beginTransaction()
            val ylOneWeatherFragment = SVWeatherFragmentYlOne()
            transaction.add(R.id.frameLayout,ylOneWeatherFragment)
            transaction.commit()
        } else {
            var transaction = supportFragmentManager.beginTransaction()
            val ylTwoWeatherFragment = SVWeatherFragmentYlTwo()
            transaction.add(R.id.frameLayout,ylTwoWeatherFragment)
            transaction.commit()
        }
    }
}