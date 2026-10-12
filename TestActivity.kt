package com.abex.keyboard

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView

class TestActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
        }
        layout.addView(TextView(this).apply {
            text = "Test ABBX Keyboard"
            textSize = 24f
            gravity = Gravity.CENTER
        })
        val input = EditText(this).apply {
            hint = "Type here... 😎"
            minLines = 4
            gravity = Gravity.TOP
        }
        layout.addView(input, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ))
        setContentView(layout)
    }
}
