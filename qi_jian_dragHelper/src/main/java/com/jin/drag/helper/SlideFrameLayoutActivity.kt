package com.jin.drag.helper

import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jin.drag.helper.databinding.ActivitySlideBinding

class SlideFrameLayoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySlideBinding

    private lateinit var tvMainContent: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySlideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mainView = layoutInflater.inflate(R.layout.main_view, null, false)
        val slideView = layoutInflater.inflate(R.layout.slide_view, null, false)

        tvMainContent = mainView.findViewById(R.id.tv_content)

        slideView.findViewById<TextView>(R.id.tv_one).setOnClickListener {
            tvMainContent.text = (it as TextView).text
            binding.slideLayout.resetMainView()
        }

        slideView.findViewById<TextView>(R.id.tv_two).setOnClickListener {
            tvMainContent.text = (it as TextView).text
            binding.slideLayout.resetMainView()
        }

        slideView.findViewById<TextView>(R.id.tv_three).setOnClickListener {
            tvMainContent.text = (it as TextView).text
            binding.slideLayout.resetMainView()
        }

        val mainViewLayoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val slideLayoutParams = ViewGroup.LayoutParams(
            resources.getDimension(R.dimen.dp_160).toInt(),
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        binding.slideLayout.addCustomView(
            mainView,
            mainViewLayoutParams,
            slideView,
            slideLayoutParams
        )
    }
}