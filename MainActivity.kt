package com.abex.keyboard

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    private fun makeButton(text: String, action: () -> Unit): Button {
        return Button(this).apply {
            this.text = text
            isAllCaps = false
            setOnClickListener { action() }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(24, 24, 24, 24)
        }
        layout.addView(TextView(this).apply {
            text = "ABBX KEYBOARD"
            textSize = 28f
            gravity = Gravity.CENTER
        })
        layout.addView(TextView(this).apply {
            text = "Your keyboard. Your style."
            textSize = 16f
            gravity = Gravity.CENTER
        })
        layout.addView(makeButton("1. Enable ABBX Keyboard") {
            startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
        })
        layout.addView(makeButton("2. Select ABBX Keyboard") {
            val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            manager.showInputMethodPicker()
        })
        layout.addView(makeButton("3. Test Keyboard") {
            startActivity(Intent(this, TestActivity::class.java))
        })
        setContentView(layout)
    }
}
