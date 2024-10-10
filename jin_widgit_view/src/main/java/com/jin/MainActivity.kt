package com.jin

import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.jin.adapter.HorizontalViewGroupAdapter
import com.jin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var rvOneDatas = mutableListOf<String>()
    private var rvTwoDatas = mutableListOf<String>()
    private var rvThreeDatas = mutableListOf<String>()
    private var rvFourDatas = mutableListOf<String>()
    private var rvFiveDatas = mutableListOf<String>()
    private var rvSixDatas = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setActivityBarStyle(this)
        initDatas()

        binding.rvOne.adapter = HorizontalViewGroupAdapter(this,rvOneDatas)
        binding.rvOne.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)

        binding.rvTwo.adapter = HorizontalViewGroupAdapter(this,rvTwoDatas)
        binding.rvTwo.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)

        binding.rvThree.adapter = HorizontalViewGroupAdapter(this,rvThreeDatas)
        binding.rvThree.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)

        binding.rvFour.adapter = HorizontalViewGroupAdapter(this,rvFourDatas)
        binding.rvFour.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)

        binding.rvFive.adapter = HorizontalViewGroupAdapter(this,rvFiveDatas)
        binding.rvFive.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)

        binding.rvSix.adapter = HorizontalViewGroupAdapter(this,rvSixDatas)
        binding.rvSix.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)


//        binding.numberPicker.minValue = 10
//        binding.numberPicker.maxValue = 90
//        binding.numberPicker.value = 30

        val nums = mutableListOf<String>()
        var i = 10
        while (i < 100) {
            nums.add("${i}%")
            i+=10
        }
        nums.add(nums.size / 2,"关闭")
        binding.numberPicker.value = nums.size / 2
        binding.numberPicker.displayedValues = nums.toTypedArray()
        binding.numberPicker.maxValue = nums.size
        binding.numberPicker.minValue = 1

        binding.numberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            Log.d("TAG", "onCreate:oldVal " + oldVal)
            Log.d("TAG", "onCreate:newVal " + newVal)
        }

    }

    private fun initDatas() {
        for (i in 65 until 97) {
            rvOneDatas.add(i.toString() + "0")
            rvTwoDatas.add(i.toString() + "1")
            rvThreeDatas.add(i.toString() + "2")
            rvFourDatas.add(i.toString() + "3")
            rvFiveDatas.add(i.toString() + "4")
            rvSixDatas.add(i.toString() + "5")
        }
    }

    private fun setActivityBarStyle(activity: AppCompatActivity) {
        val decorView = activity.window.decorView
        val option = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN //注释掉这行代码
                //View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        decorView.systemUiVisibility = option
        //设置导航栏（顶部和底部）颜色为透明，注释掉这行代码
        //getWindow().setNavigationBarColor(Color.TRANSPARENT);
        //设置通知栏颜色为透明
        activity.window.statusBarColor = Color.TRANSPARENT
        val actionBar = activity.supportActionBar
        actionBar?.hide()
    }
}