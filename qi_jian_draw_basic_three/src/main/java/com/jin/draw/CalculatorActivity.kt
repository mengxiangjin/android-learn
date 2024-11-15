package com.jin.draw

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jin.draw.databinding.ActivityCalculatorBinding
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.util.Stack
import kotlin.math.log10
import kotlin.math.pow


class CalculatorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCalculatorBinding
    private var expressList = mutableListOf<OperationNum>()
    private var showStrBuilder = StringBuilder()
    private var operatorStack = Stack<OperationNum>()
    private var numberStack = Stack<OperationNum>()
    private var isDegree = true

    var priorityMap = mapOf(
        "+" to 0,
        "-" to 0,
        "×" to 1,
        "÷" to 1,
        "1/x" to 1,
        "x!" to 7,
        "lg" to 7,
        "x^y" to 7,
        "√" to 8,
        "tan" to 9,
        "cos" to 9,
        "sin" to 9,
        "(" to 10,
        ")" to 10,
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalculatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListener()
    }

    private fun initListener() {
        binding.back.setOnClickListener {
            finish()
        }

        binding.tvReset.setOnClickListener {
            showStrBuilder.clearStr()
        }

        binding.imgSqrt.setOnClickListener {
            //更号
            expressList.add(OperationNum("√", false))
            showStrBuilder.appendStr("√")
        }

        binding.imgDelete.setOnClickListener {
            //删除
            if (expressList.isNotEmpty()) {
                val lastOperationNum = expressList[expressList.size - 1]
                if (lastOperationNum.isDigit) {
                    val lastIndex = lastOperationNum.entity.length - 1
                    if (lastIndex == 0) {
                        expressList.remove(lastOperationNum)
                    } else {
                        lastOperationNum.entity = lastOperationNum.entity.substring(0, lastIndex)
                    }
                    showStrBuilder.removeLastIndex()
                } else {
                    when (lastOperationNum.entity) {
                        "x!" -> {
                            showStrBuilder.removeFromLast("!")
                        }

                        "1/x" -> {
                            showStrBuilder.removeFromLast("1/")
                        }

                        "x^y" -> {
                            showStrBuilder.removeFromLast("^")
                        }

                        else -> {
                            showStrBuilder.removeFromLast(lastOperationNum.entity)
                        }
                    }
                    expressList.remove(lastOperationNum)
                }
            }
        }

        binding.tvResult.setOnClickListener {
            binding.tvError.text = ""
            val isLegalExpressList = checkLegalExpressList()
            if (isLegalExpressList) {
                val suffixExpressList = toSuffixExpress()
                if (suffixExpressList.isEmpty()) {
                    return@setOnClickListener
                }
                calculatorResult(suffixExpressList)
                expressList.clear()
            } else {
                expressList.clear()
                showStrBuilder.clear()
            }

        }

    }

    fun inputNumber(view: View) {
        val inputNumberStr = (view as TextView).text
        Log.d("TAG", "inputNumber: $inputNumberStr")

        when (inputNumberStr) {
            "e" -> {
                expressList.add(OperationNum("${Math.E}", true))
                showStrBuilder.appendStr("e")
            }

            "π" -> {
                expressList.add(OperationNum("${Math.PI}", true))
                showStrBuilder.appendStr("π")
            }

            else -> {
                //0,1,2,3,4,5,6,7,8,9 .
                //是否需要添加新的操作数
                if (expressList.isEmpty() || !expressList[expressList.size - 1].isDigit) {
                    //判断减号还是负值
                    var isNegative = false
                    if (expressList.isNotEmpty() && expressList[expressList.size - 1].entity == "-") {
                        if (expressList.size == 1 || expressList[expressList.size - 2].entity == "(") {
                            isNegative = true
                        }
                    }
                    val numberEntity = if (isNegative) {
                        "-${inputNumberStr}"
                    } else {
                        inputNumberStr
                    }
                    if (isNegative) {
                        expressList.removeAt(expressList.size - 1)
                    }
                    expressList.add(OperationNum("$numberEntity", true))
                    showStrBuilder.appendStr("$inputNumberStr")
                    return
                }
                expressList[expressList.size - 1].entity =
                    expressList[expressList.size - 1].entity + inputNumberStr
                showStrBuilder.appendStr("$inputNumberStr")
            }
        }
    }

    val simpleOperator = listOf(
        "+",
        "-",
        "×",
        "÷",
    )

    fun inputOperator(view: View) {
        val inputOperatorStr = (view as TextView).text
        Log.d("TAG", "inputOperator: $inputOperatorStr")
        when (inputOperatorStr) {
            "lg" -> {
                expressList.add(OperationNum("lg", false))
                showStrBuilder.appendStr("lg")
            }

            "tan" -> {
                expressList.add(OperationNum("tan", false))
                showStrBuilder.appendStr("tan")
            }

            "x!" -> {
                expressList.add(OperationNum("x!", false))
                showStrBuilder.appendStr("!")
            }

            "+" -> {
                expressList.add(OperationNum("+", false))
                showStrBuilder.appendStr("+")
            }

            "cos" -> {
                expressList.add(OperationNum("cos", false))
                showStrBuilder.appendStr("cos")
            }

            "1/x" -> {
                expressList.add(OperationNum("1/x", false))
                showStrBuilder.appendStr("1/")
            }

            "-" -> {
                expressList.add(OperationNum("-", false))
                showStrBuilder.appendStr("-")
            }

            "sin" -> {
                expressList.add(OperationNum("sin", false))
                showStrBuilder.appendStr("sin")
            }

            "x^y" -> {
                expressList.add(OperationNum("x^y", false))
                showStrBuilder.appendStr("^")
            }

            "×" -> {
                expressList.add(OperationNum("×", false))
                showStrBuilder.appendStr("×")
            }

            "(" -> {
                expressList.add(OperationNum("(", false))
                showStrBuilder.appendStr("(")
            }

            ")" -> {
                expressList.add(OperationNum(")", false))
                showStrBuilder.appendStr(")")
            }

            "deg" -> {
                isDegree = true
                binding.tvDeg.text = "rad"
            }

            "rad" -> {
                isDegree = false
                binding.tvDeg.text = "deg"
            }

            "÷" -> {
                expressList.add(OperationNum("÷", false))
                showStrBuilder.appendStr("÷")
            }
        }
    }


    private fun checkLegalExpressList(): Boolean {
        if (expressList.isEmpty()) {
            binding.tvError.text = "输入不能为空"
            return false
        }
        if (expressList.size == 1) {
            //当只有一位字符时，只能是“0123456789ep”中的一个
            if (!expressList[0].isDigit) {
                binding.tvError.text = "带操作数首个不能为非数字"
                return false
            }
        }
        expressList.forEachIndexed { index, operationNum ->
            //1. 非数字且不是 "lg",tan，cos，sin，1/x
            var tempIndex = index
            if (tempIndex == 0) {
                val preOperator = listOf(
                    "lg",
                    "tan",
                    "cos",
                    "sin",
                    "1/x",
                    "√",
                    "("
                )
                if (!operationNum.isDigit && !preOperator.contains(operationNum.entity)) {
                    binding.tvError.text = "首字符不合法:${operationNum.entity} ----> 1"
                    return false
                }
            }

            //2.“+-*÷”后面只能是"0123456789losctg(ep"中的一个
            if (simpleOperator.contains(operationNum.entity)) {
                tempIndex = index
                tempIndex++
                if (tempIndex >= expressList.size) {
                    binding.tvError.text = "结尾字符不合法:${operationNum.entity} ----> 2"
                    return false
                }
                val preOperator = listOf(
                    "lg",
                    "tan",
                    "cos",
                    "sin",
                    "1/x",
                    "√",
                    "("
                )
                if (!expressList[tempIndex].isDigit && !preOperator.contains(expressList[tempIndex].entity)) {
                    binding.tvError.text = "结尾字符不合法:${expressList[tempIndex].entity} ----> 2"
                    return false
                }
            }

            //3.数字不能以.结尾
            if (operationNum.isDigit && operationNum.entity.endsWith(".")) {
                binding.tvError.text = "数字不能以.结尾 ----> 3"
                return false
            }

            //4."x!"后面只能是“+-*÷^)”中的一个
            if (operationNum.entity == "!") {
                val postOperation = listOf(
                    "+",
                    "-",
                    "×",
                    "÷",
                    "x^y",
                    ")"
                )
                tempIndex = index
                tempIndex++
                if (tempIndex < expressList.size) {
                    if (!postOperation.contains(expressList[tempIndex].entity)) {
                        binding.tvError.text =
                            "x!后面字符不合法:${expressList[tempIndex].entity} ----> 4"
                        return false
                    }
                }
            }

            //5.后面只能是“0123456789(ep”中的一个
            if (operationNum.entity == "lg" ||
                operationNum.entity == "sin" ||
                operationNum.entity == "cos" ||
                operationNum.entity == "tan" ||
                operationNum.entity == "√"
            ) {

                tempIndex = index
                tempIndex++
                if (tempIndex >= expressList.size) {
                    binding.tvError.text = "结尾字符不合法:${operationNum.entity} ----> 5"
                    return false
                }
                if (!expressList[tempIndex].isDigit && "(" != expressList[tempIndex].entity) {
                    binding.tvError.text = "结尾字符不合法:${expressList[tempIndex].entity} ----> 5"
                    return false
                }
            }

            //6.数字检查
            if (operationNum.isDigit) {
                val entity = operationNum.entity
                if (entity.isEmpty() || entity[0] == '.') {
                    binding.tvError.text = "数字字符不合法:${expressList[tempIndex].entity} ----> 6"
                    return false
                }
                if (entity[0] == '0') {
                    if (entity.length != 1) {
                        if (!entity.contains('.') || entity[1] == '0') {
                            binding.tvError.text =
                                "数字字符不合法:${expressList[tempIndex].entity} ----> 6"
                            return false
                        }
                    }
                }
            }
            //7、字符的后面必须为对应字符或者null
            if (operationNum.isDigit) {
                tempIndex = index
                tempIndex++
                if (tempIndex < expressList.size) {
                    val postOperation = listOf(
                        "+",
                        "-",
                        "×",
                        "÷",
                        "x^y",
                        ")",
                        "x!"
                    )
                    if (!postOperation.contains(expressList[tempIndex].entity)) {
                        binding.tvError.text =
                            "字符的后面必须为对应字符:${expressList[tempIndex].entity} ----> 7"
                        return false
                    }
                }
            }
            //8."("后面只能是“0123456789locstg()ep”中的一个
            if (operationNum.entity == "(") {
                tempIndex = index
                tempIndex++
                if (tempIndex >= expressList.size) {
                    binding.tvError.text =
                        "结尾字符不能为）:${operationNum.entity} ----> 8"
                    return false
                }
                val postOperation = listOf(
                    "lg",
                    "cos",
                    "sin",
                    "tan",
                    "√",
                )
                if (!expressList[tempIndex].isDigit && !postOperation.contains(expressList[tempIndex].entity)) {
                    binding.tvError.text =
                        "(后所跟字符出错 :${expressList[tempIndex].entity} ----> 8"
                    return false
                }
            }

            //9.")"后面只能是“+-*/!^)”中的一个
            if (operationNum.entity == ")") {
                tempIndex = index
                tempIndex++
                if (tempIndex < expressList.size) {
                    val postOperation = listOf(
                        "+",
                        "-",
                        "×",
                        "÷",
                        "x^y",
                        ")",
                        "x!"
                    )
                    if (!postOperation.contains(expressList[tempIndex].entity)) {
                        binding.tvError.text =
                            ")后所跟字符出错 :${expressList[tempIndex].entity} ----> 9"
                        return false
                    }
                }
            }

            //10.最后一位字符
            if (index == expressList.size - 1) {
                val postOperation = listOf(
                    ")",
                    "x!"
                )
                if (!operationNum.isDigit && !postOperation.contains(operationNum.entity)) {
                    binding.tvError.text =
                        "最后结尾字符出错 :${expressList[tempIndex].entity} ----> 10"
                    return false
                }
            }

            //11.数字不能有多个“.”
            if (operationNum.isDigit) {
                val filterStr = operationNum.entity.filter {
                    it == '.'
                }
                if (filterStr.length > 1) {
                    binding.tvError.text =
                        "数字格式错误 :${operationNum.entity} ----> 11"
                    return false
                }
            }

            //12."ep"后面只能是“+-*/^)”中的一个
            if (operationNum.entity == "${Math.PI}" || operationNum.entity == "${Math.E}") {
                tempIndex = index
                tempIndex++
                if (tempIndex < expressList.size) {
                    val postOperation = listOf(
                        "+",
                        "-",
                        "*",
                        "÷",
                        "x^y",
                        ")"
                    )
                    if (!postOperation.contains(expressList[tempIndex].entity)) {
                        binding.tvError.text =
                            "ep常数后面所跟字符错误 :${expressList[tempIndex].entity} ----> 12"
                        return false
                    }
                }
            }
        }
        return true
    }

    private fun toSuffixExpress(): List<OperationNum> {
        if (expressList.isEmpty()) return emptyList()
        printExpress(expressList, "expressList")
        operatorStack.clear()
        numberStack.clear()

        val suffixExpressList = mutableListOf<OperationNum>()

        var isLegalExpression = true

        run loop@{
            expressList.forEach forEach@{ operationNum ->
                //数字
                if (operationNum.isDigit) {
                    suffixExpressList.add(operationNum)
                    return@forEach
                }
                //直接入栈
                if ((operatorStack.isEmpty() || "(" == operationNum.entity || operationNum.isHighPriority(
                        operatorStack.peek()
                    ) && ")" != operationNum.entity
                            )
                ) {
                    operatorStack.push(operationNum)
                    return@forEach
                }

                //出栈

                //判断 ）
                if (")" == operationNum.entity) {
                    isLegalExpression = false
                    while (operatorStack.isNotEmpty()) {
                        if ("(" == operatorStack.peek().entity) {
                            operatorStack.pop()
                            isLegalExpression = true
                            break
                        }
                        suffixExpressList.add(operatorStack.pop())
                    }
                    if (!isLegalExpression) {
                        return@loop
                    }
                    return@forEach
                }
                //优先级低于等于栈顶的
                if (!operationNum.isHighPriority(operatorStack.peek())) {
                    if ("(" == operatorStack.peek().entity) {
                        operatorStack.push(operationNum)
                        return@forEach
                    }
                    while (operatorStack.isNotEmpty()) {
                        val operator = operatorStack.peek()
                        if (!operationNum.isHighPriority(operator)) {
                            if ("(" == operator.entity) {
                                operatorStack.add(operationNum)
                                return@forEach
                            }
                            suffixExpressList.add(operatorStack.pop())
                        }
                        if (operatorStack.isEmpty() || operationNum.isHighPriority(operator)) {
                            operatorStack.add(operationNum)
                            break
                        }
                    }
                }
            }
        }
        if (!isLegalExpression) {
            Log.d("TAG", "isLegalExpression: ")
            return emptyList()
        }
        while (operatorStack.isNotEmpty()) {
            suffixExpressList.add(operatorStack.pop())
        }
        printExpress(suffixExpressList, "suffixExpressList")
        return suffixExpressList
    }

    private fun calculatorResult(suffixExpressList: List<OperationNum>) {
        var isLegalExpress = true
        run loop@{
            suffixExpressList.forEach { operationNum ->
                if (operationNum.isDigit) {
                    numberStack.push(operationNum)
                    return@forEach
                }

                when (operationNum.entity) {
                    "+" -> {
                        if (numberStack.size < 2) {
                            isLegalExpress = false
                            return@loop
                        }
                        val operationNum1 = numberStack.pop()
                        val operationNum2 = numberStack.pop()
                        numberStack.push(operationNum2.plus(operationNum1))

                    }

                    "-" -> {
                        if (numberStack.size < 2) {
                            isLegalExpress = false
                            return@loop
                        }
                        val operationNum1 = numberStack.pop()
                        val operationNum2 = numberStack.pop()
                        numberStack.push(operationNum2.sub(operationNum1))
                    }

                    "÷" -> {
                        if (numberStack.size < 2) {
                            isLegalExpress = false
                            return@loop
                        }
                        val operationNum1 = numberStack.pop()
                        val operationNum2 = numberStack.pop()
                        numberStack.push(operationNum2.divided(operationNum1))
                    }

                    "×" -> {
                        if (numberStack.size < 2) {
                            isLegalExpress = false
                            return@loop
                        }
                        val operationNum1 = numberStack.pop()
                        val operationNum2 = numberStack.pop()
                        numberStack.push(operationNum2.mul(operationNum1))
                    }

                    "x!" -> {
                        if (numberStack.size < 1) {
                            isLegalExpress = false
                            return@loop
                        }
                        val operationNum = numberStack.pop()
                        val pushObj = operationNum.factorial()
                        if (pushObj == null) {
                            binding.tvError.text =
                                "阶乘必须为整数 :${operationNum.entity} ----> 51"
                            isLegalExpress = false
                            return@loop
                        }
                        numberStack.push(operationNum.factorial())
                    }

                    "1/x" -> {
                        if (numberStack.size < 1) {
                            isLegalExpress = false
                            return@loop
                        }
                        val operationNum = numberStack.pop()
                        if (operationNum.entity.toDouble() == 0.0) {
                            binding.tvError.text =
                                "被除数不可为 :${operationNum.entity} ----> 52"
                            isLegalExpress = false
                            isLegalExpress = false
                            return@loop
                        }
                        numberStack.push(operationNum.dividedByOne())
                    }

                    "x^y" -> {
                        if (numberStack.size < 2) {
                            isLegalExpress = false
                            return@loop
                        }
                        val operationNum1 = numberStack.pop()
                        val operationNum2 = numberStack.pop()
                        numberStack.push(operationNum2.pow(operationNum1))
                    }

                    "cos" -> {
                        if (numberStack.size < 1) {
                            isLegalExpress = false
                            return@loop
                        }
                        val operationNum = numberStack.pop()
                        numberStack.push(operationNum.cos())
                    }

                    "sin" -> {
                        if (numberStack.size < 1) {
                            isLegalExpress = false
                            return@loop
                        }
                        val operationNum = numberStack.pop()
                        numberStack.push(operationNum.sin())
                    }


                    "tan" -> {
                        if (numberStack.size < 1) {
                            isLegalExpress = false
                            return@loop
                        }
                        val operationNum = numberStack.pop()
                        numberStack.push(operationNum.tan())
                    }

                    "√" -> {
                        if (numberStack.size < 1) {
                            isLegalExpress = false
                            return@loop
                        }
                        val operationNum = numberStack.pop()
                        val num = operationNum.sqrt()
                        if (num == null) {
                            binding.tvError.text =
                                "被开方数不能为负值 :${operationNum.entity} ----> 50"
                            isLegalExpress = false
                            return@loop
                        }
                        numberStack.push(num)
                    }

                    "lg" -> {
                        if (numberStack.size < 1) {
                            isLegalExpress = false
                            return@loop
                        }
                        val operationNum = numberStack.pop()
                        numberStack.push(operationNum.lg())
                    }
                }

            }
        }

        if (!isLegalExpress || numberStack.size != 1) {
            //不合法
            return
        }
        val resultOperationNum = numberStack.pop()
        resultOperationNum.entity = formatResult(resultOperationNum.entity)
        Log.d("TAG", "calculatorResult: " + resultOperationNum.entity)
        showStrBuilder.appendStr("\n${resultOperationNum.entity}\n")

    }

    private fun formatResult(result: String): String {
        val indexOfPoint = result.indexOf(".")
        if (indexOfPoint == -1) return result
        var endIndex = result.length - 1
        while (endIndex > indexOfPoint) {
            if (result[endIndex] == '0') {
                endIndex--
            } else {
                break
            }
        }
        if (endIndex != indexOfPoint) {
            endIndex += 1
        }
        return result.substring(0, endIndex)
    }

    private fun StringBuilder.appendStr(str: String) {
        append(str)
        binding.editView.setText(toString())
    }

    private fun StringBuilder.removeFromLast(str: String) {
        if (length <= 1) {
            clear()
        } else {
            val substring = this.toString().substring(0, length - str.length)
            clear()
            append(substring)

        }
        binding.editView.setText(toString())
    }

    private fun StringBuilder.removeLastIndex() {
        if (length <= 1) {
            clear()
        } else {
            val substring = this.toString().substring(0, length - 1)
            clear()
            append(substring)
        }
        binding.editView.setText(toString())
    }

    private fun StringBuilder.clearStr() {
        clear()
        binding.editView.setText("")
    }

    private fun printExpress(expressList: List<OperationNum>, msg: String) {
        var str = ""
        expressList.forEach {
            str += it.entity
        }
        Log.d("TAG", "${msg}: " + str)
    }

    inner class OperationNum(var entity: String = "", var isDigit: Boolean = true) {

        fun isHighPriority(operationNum: OperationNum): Boolean {
            return priorityMap[entity]!! > priorityMap[operationNum.entity]!!
        }

        fun plus(operationNum: OperationNum): OperationNum {
            return OperationNum(
                (entity.toDouble() + operationNum.entity.toDouble()).toString(),
                true
            )
        }

        fun sub(operationNum: OperationNum): OperationNum {
            return OperationNum(
                (entity.toDouble() - operationNum.entity.toDouble()).toString(),
                true
            )
        }

        fun divided(operationNum: OperationNum): OperationNum {
            return OperationNum(
                (entity.toDouble() / operationNum.entity.toDouble()).toString(),
                true
            )
        }

        fun mul(operationNum: OperationNum): OperationNum {
            return OperationNum(
                (entity.toDouble() * operationNum.entity.toDouble()).toString(),
                true
            )
        }

        fun pow(operationNum: OperationNum): OperationNum {
            val resultEntity = entity.toDouble().pow(operationNum.entity.toDouble())
            return OperationNum(resultEntity.toString(), true)
        }

        fun tan(): OperationNum {
            val entityStr = if (isDegree) {
                val value = Math.tan(Math.toRadians(entity.toDouble()))
                val bigDecimal = BigDecimal(value).setScale(2, RoundingMode.HALF_UP)
                bigDecimal.toString()
            } else {
                val value = Math.tan(entity.toDouble())
                val bigDecimal = BigDecimal(value).setScale(2, RoundingMode.HALF_UP)
                bigDecimal.toString()
            }
            return OperationNum(entityStr, true)
        }

        fun sin(): OperationNum {
            val entityStr = if (isDegree) {
                val value = Math.sin(Math.toRadians(entity.toDouble()))
                val bigDecimal = BigDecimal(value).setScale(2, RoundingMode.HALF_UP)
                bigDecimal.toString()
            } else {
                val value = Math.sin(entity.toDouble())
                val bigDecimal = BigDecimal(value).setScale(2, RoundingMode.HALF_UP)
                bigDecimal.toString()
            }
            return OperationNum(entityStr, true)
        }

        fun cos(): OperationNum {
            val entityStr = if (isDegree) {
                val value = Math.cos(Math.toRadians(entity.toDouble()))
                val bigDecimal = BigDecimal(value).setScale(2, RoundingMode.HALF_UP)
                bigDecimal.toString()
            } else {
                val value = Math.cos(entity.toDouble())
                val bigDecimal = BigDecimal(value).setScale(2, RoundingMode.HALF_UP)
                bigDecimal.toString()
            }
            return OperationNum(entityStr, true)
        }

        fun sqrt(): OperationNum? {
            if (entity.toFloat() < 0) {
                return null
            }
            return OperationNum(kotlin.math.sqrt(entity.toFloat()).toString(), true)
        }

        fun lg(): OperationNum {
            return OperationNum(log10(entity.toDouble()).toString(), true)
        }

        fun factorial(): OperationNum? {
            val legalNumber = isInteger()
            return if (!legalNumber) {
                null
            } else {
                val integer = entity.toDouble().toInt()
                var result = BigInteger.valueOf(1)
                for (i in 1..integer) {
                    result = result.multiply(BigInteger.valueOf(i.toLong()))
                }
                OperationNum(result.toString(), true)
            }
        }


        fun dividedByOne(): OperationNum {
            val resultEntity = 1.0 / entity.toDouble()
            return OperationNum(resultEntity.toString(), true)
        }


        //判断double字符串是否为整数 1.0true 1.1 false
        private fun isInteger(): Boolean {
            val digitIndex = entity.indexOf(".")
            return if (digitIndex == -1) {
                true
            } else {
                val tempInteger = entity.substring(0, digitIndex).toInt()
                tempInteger.toDouble() == entity.toDouble()
            }
        }
    }


}