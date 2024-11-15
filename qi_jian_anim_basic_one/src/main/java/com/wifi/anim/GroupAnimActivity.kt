package com.wifi.anim

import android.animation.LayoutTransition
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.wifi.anim.databinding.ActivityGroupAnimBinding

class GroupAnimActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupAnimBinding

    var childCounts = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupAnimBinding.inflate(layoutInflater)
        setContentView(binding.root)

        addAnimOfViewGroup()
        initListener()
    }

    private fun addAnimOfViewGroup() {
        val layoutTransition = LayoutTransition()
        val enterAnim = ObjectAnimator.ofFloat(null,"rotationY",0f,360f,0f)
        val exitAnim = ObjectAnimator.ofFloat(null,"rotation",0f,90f,0f)
        layoutTransition.setAnimator(LayoutTransition.APPEARING,enterAnim)
        layoutTransition.setAnimator(LayoutTransition.DISAPPEARING,exitAnim)
        binding.llContainer.layoutTransition = layoutTransition
    }

    private fun initListener() {
        binding.btnAdd.setOnClickListener {
            childCounts++
            val button = Button(this)
            button.text = "button${childCounts}"
            binding.llContainer.addView(button,ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            ))

        }
        binding.btnRemove.setOnClickListener {
            if (childCounts == 0) return@setOnClickListener
            childCounts--
            binding.llContainer.removeViewAt(childCounts)
        }
    }

    private fun introduceSetAnimator() {
        val layoutTransition = LayoutTransition()
        /*
        * Int:动画的类型
        *   LayoutTransition.APPEARING:view入场（add）
        *   LayoutTransition.DISAPPEARING:view退场（remove）
        *   LayoutTransition.CHANGE_APPEARING:view入场时(add),其他已有控件需要移动的动画
        *   LayoutTransition.CHANGE_DISAPPEARING:view退场时(remove),其他已有控件需要移动的动画
        * Animator:具体动画(valueAnim,objectAnim)
        * */
        val enterAnim = ObjectAnimator.ofFloat(null,"rotationY",0f,360f,0f)
        layoutTransition.setDuration(3000)
        layoutTransition.setInterpolator(LayoutTransition.APPEARING,LinearInterpolator())
        layoutTransition.addTransitionListener(object : LayoutTransition.TransitionListener {
            override fun startTransition(
                transition: LayoutTransition?,
                container: ViewGroup?,
                view: View?,
                transitionType: Int
            ) {

            }

            override fun endTransition(
                transition: LayoutTransition?,
                container: ViewGroup?,
                view: View?,
                transitionType: Int
            ) {
            }

        })
        layoutTransition.setAnimator(LayoutTransition.APPEARING,enterAnim)
    }
}