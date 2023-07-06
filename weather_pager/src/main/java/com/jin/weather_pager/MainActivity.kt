package com.jin.weather_pager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.jin.weather_pager.adapter.WeatherFragmentAdapter
import com.jin.weather_pager.databinding.ActivityMainBinding
import com.jin.weather_pager.ry.SVWeatherFragmentRyFourBlue
import com.jin.weather_pager.ry.SVWeatherFragmentRyOneBlack
import com.jin.weather_pager.ry.SVWeatherFragmentRyThreeWhite
import com.jin.weather_pager.yl.SVWeatherFragmentYlOne
import com.jin.weather_pager.yl.SVWeatherFragmentYlTwo

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var fragments = mutableListOf<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initFragment()
        initView()
    }

    private fun initFragment() {
        val ryOneWeatherFragment = SVWeatherFragmentRyOneBlack()
        val ryThreeWeatherFragment = SVWeatherFragmentRyThreeWhite()
        val ryFourWeatherFragment = SVWeatherFragmentRyFourBlue()
//        val ylOneWeatherFragment = SVWeatherFragmentYlOne()
//        val ylTwoWeatherFragment = SVWeatherFragmentYlTwo()

        fragments.add(ryOneWeatherFragment)
        fragments.add(ryThreeWeatherFragment)
        fragments.add(ryFourWeatherFragment)
//        fragments.add(ylOneWeatherFragment)
//        fragments.add(ylTwoWeatherFragment)

        binding.btnYlOne.setOnClickListener {
            val intent = Intent(this,WeatherYaliMainActivity::class.java)
            intent.putExtra("position",0)
            startActivity(intent)
        }

        binding.btnYlTwo.setOnClickListener {
            val intent = Intent(this,WeatherYaliMainActivity::class.java)
            intent.putExtra("position",1)
            startActivity(intent)
        }

    }

    private fun initView() {
        val adapter = WeatherFragmentAdapter(this,fragments)
        binding.pagerWeather.adapter = adapter
    }

}