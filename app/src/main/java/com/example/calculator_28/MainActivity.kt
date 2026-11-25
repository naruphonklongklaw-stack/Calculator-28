package com.example.calculator_28

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var display: TextView
    private var current = ""
    private var operator = ""
    private var firstNumber = 0.0
    private var secondNumber = 0.0
    private var isNewOperation = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        display = findViewById(R.id.txtDisplay)

        // ปุ่ม 0-9
        val numberButton = listOf(
            R.id.btn0,
            R.id.btn1,
            R.id.btn2,
            R.id.btn3,
            R.id.btn4,
            R.id.btn5,
            R.id.btn6,
            R.id.btn7,
            R.id.btn8,
            R.id.btn9,
        )
        // กำหนด event ให้ปุ่มตัวเลขทั้งหมด
        for (id in numberButton) {
            findViewById<Button>(id).setOnClickListener {
                numberClick((it as Button).text.toString())
            }
        }

        // เชื่อมปุ่มคำนวน
        findViewById<Button>(R.id.btnb).setOnClickListener { operatorClicked("+") }
        findViewById<Button>(R.id.btnlorp).setOnClickListener { operatorClicked("-") }
        findViewById<Button>(R.id.btnkun).setOnClickListener { operatorClicked("*") }
        findViewById<Button>(R.id.btnDiv).setOnClickListener { operatorClicked("/") }

        // เชื่อมปุ่มพิเศษ = AC, DEL, %, ., ±
        findViewById<Button>(R.id.btntorkup).setOnClickListener { calculateResult() }
        findViewById<Button>(R.id.btnAC).setOnClickListener { allClear() }
        findViewById<Button>(R.id.btno).setOnClickListener { addDot() }
        findViewById<Button>(R.id.btnBL).setOnClickListener { toggleSign() }
        findViewById<Button>(R.id.btnDel).setOnClickListener { backspace() }
        findViewById<Button>(R.id.btnMod).setOnClickListener { percentClicked() }
    }

    private fun numberClick(value: String) {
        if (isNewOperation) {
            current = ""
            isNewOperation = false
        }
        if (current == "0") {
            if (value == "0") return
            current = value
        } else {
            current += value
        }
        display.text = current
    }

    private fun operatorClicked(op: String) {
        if (operator.isNotEmpty() && current.isNotEmpty() && !isNewOperation) {
            calculateResult()
        }
        if (current.isNotEmpty()) {
            firstNumber = current.toDoubleOrNull() ?: 0.0
        }
        operator = op
        isNewOperation = true
    }

    private fun calculateResult() {
        if (operator.isEmpty()) return
        secondNumber = if (current.isNotEmpty() && !isNewOperation) {
            current.toDoubleOrNull() ?: 0.0
        } else {
            firstNumber
        }

        val result = when (operator) {
            "+" -> firstNumber + secondNumber
            "-" -> firstNumber - secondNumber
            "*" -> firstNumber * secondNumber
            "/" -> if (secondNumber != 0.0) firstNumber / secondNumber else Double.NaN
            "%" -> if (secondNumber != 0.0) firstNumber % secondNumber else Double.NaN
            else -> 0.0
        }

        if (result.isNaN() || result.isInfinite()) {
            display.text = "Cannot divide by zero"
            current = ""
            operator = ""
            isNewOperation = true
        } else {
            val formatted = formatResult(result)
            display.text = formatted
            current = formatted
            firstNumber = result
            operator = ""
            isNewOperation = true
        }
    }

    private fun allClear() {
        display.text = "0"
        current = ""
        firstNumber = 0.0
        secondNumber = 0.0
        operator = ""
        isNewOperation = true
    }

    private fun percentClicked() {
        if (current.isNotEmpty()) {
            val num = current.toDoubleOrNull() ?: return
            val res = num / 100.0
            current = formatResult(res)
            display.text = current
        }
    }

    private fun addDot() {
        if (isNewOperation || current.isEmpty()) {
            current = "0."
            isNewOperation = false
            display.text = current
            return
        }
        if (!current.contains(".")) {
            current += "."
            display.text = current
        }
    }

    private fun toggleSign() {
        if (current.isNotEmpty() && current != "0") {
            current = if (current.startsWith("-")) {
                current.substring(1)
            } else {
                "-$current"
            }
            display.text = current
        }
    }

    private fun backspace()
    {
        if (isNewOperation) return
        if (current.isNotEmpty()){
            current = current.dropLast(1)
            if (current == "-" || current.isEmpty() ) {
                current = ""
                display.text = "0"
            } else {
                display.text = current
            }

        }
    }

    private fun formatResult(value: Double): String {
        return if (value % 1.0 == 0.0 && value <= Long.MAX_VALUE && value >= Long.MIN_VALUE) {
            value.toLong().toString()
        } else {
            value.toString()
        }
    }
}
