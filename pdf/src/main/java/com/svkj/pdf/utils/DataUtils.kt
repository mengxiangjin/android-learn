package com.svkj.pdf.utils

import android.util.Log
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DataUtils {

    private val dateFormatDay= SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
    private val dateFormatSecond = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.CHINA)


    fun changeTimeToStrByDay(second: String): String {
        try {
            val second = second.toLong() * 1000
            return dateFormatDay.format(Date(second))
        }catch (e: Exception) {
            Log.d("zyz", "changeTimeToStr: " + e)
            return "data error"
        }
    }

    fun changeTimeToStrBySecond(millsSecond: String): String {
        try {
            return dateFormatSecond.format(Date(millsSecond.toLong()))
        }catch (e: Exception) {
            Log.d("zyz", "changeTimeToStr: " + e)
            return "data error"
        }
    }


    fun changeByteToMB(size: String): String {
        val fileSizeInMB = size.toLong() / 1024.0 / 1024.0
        return BigDecimal(String.format("%.2f",fileSizeInMB)).stripTrailingZeros().toPlainString()
    }
}