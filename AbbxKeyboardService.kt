package com.abex.keyboard

import android.inputmethodservice.InputMethodService
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class AbbxKeyboardService : InputMethodService() {
    private var bangla = false
    private var shifted = false
    private var showingEmoji = false
    private lateinit var root: LinearLayout

    private val backgroundColor = Color.rgb(20, 23, 32)
    private val keyColor = Color.rgb(43, 48, 62)
    private val accentColor = Color.rgb(55, 115, 255)
    private val textColor = Color.WHITE

    override fun onCreateInputView(): View {
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(4, 8, 4, 8)
            setBackgroundColor(backgroundColor)
        }
        renderKeyboard()
        return root
    }

    private fun keyBackground(color: Int): GradientDrawable = GradientDrawable().apply {
        setColor(color)
        cornerRadius = 12f
    }

    private fun makeButton(label: String, color: Int = keyColor, weight: Float = 1f, action: () -> Unit): Button {
        return Button(this).apply {
            text = label
            isAllCaps = false
            textSize = 17f
            setTextColor(textColor)
            typeface = Typeface.DEFAULT_BOLD
            background = keyBackground(color)
            minWidth = 0
            minimumWidth = 0
            minHeight = 0
            minimumHeight = 0
            setPadding(1, 0, 1, 0)
            layoutParams = LinearLayout.LayoutParams(0, 46, weight).apply {
                setMargins(2, 3, 2, 3)
            }
            setOnClickListener { action() }
        }
    }

    private fun addRow(labels: List<String>, special: Map<String, () -> Unit> = emptyMap()) {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }
        labels.forEach { label ->
            val action = special[label]
            row.addView(makeButton(label, if (action != null) accentColor else keyColor) {
                if (action != null) action() else typeCharacter(label)
            })
        }
        root.addView(row, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ))
    }

    private fun typeCharacter(character: String) {
        var value = character
        if (!bangla && shifted && value.length == 1) value = value.uppercase()
        currentInputConnection?.commitText(value, 1)
        if (!bangla) shifted = false
        renderKeyboard()
    }

    private fun backspace() {
        val connection = currentInputConnection ?: return
        val before = connection.getTextBeforeCursor(2, 0)
        if (!before.isNullOrEmpty()) {
            val last = before.last()
            val count = if (Character.isLowSurrogate(last) && before.length >= 2 &&
                Character.isHighSurrogate(before[before.length - 2])) 2 else 1
            connection.deleteSurroundingText(count, 0)
        }
    }

    private fun enterKey() {
        val connection = currentInputConnection ?: return
        val info = currentInputEditorInfo
        val action = info?.imeOptions?.and(EditorInfo.IME_MASK_ACTION) ?: EditorInfo.IME_ACTION_NONE
        val handled = if (action != EditorInfo.IME_ACTION_NONE && action != EditorInfo.IME_ACTION_UNSPECIFIED) {
            sendDefaultEditorAction(true)
        } else false
        if (!handled) connection.commitText("\n", 1)
    }

    private fun renderKeyboard() {
        if (!::root.isInitialized) return
        root.removeAllViews()
        if (showingEmoji) {
            renderEmojiPanel()
            return
        }
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        header.addView(TextView(this).apply {
            text = "ABBX"
            textSize = 19f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(accentColor)
            setPadding(8, 4, 8, 4)
        }, LinearLayout.LayoutParams(0, 42, 1f))
        header.addView(makeButton(if (bangla) "বাংলা" else "EN", accentColor) {
            bangla = !bangla
            shifted = false
            renderKeyboard()
        })
        header.addView(makeButton("☺") { showingEmoji = true; renderKeyboard() })
        header.addView(makeButton("⌨") {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.showInputMethodPicker()
        })
        root.addView(header)

        if (bangla) {
            addRow(listOf("ক", "খ", "গ", "ঘ", "ঙ", "চ", "ছ", "জ", "ঝ", "ঞ"))
            addRow(listOf("ট", "ঠ", "ড", "ঢ", "ণ", "ত", "থ", "দ", "ধ", "ন"))
            addRow(listOf("প", "ফ", "ব", "ভ", "ম", "য", "র", "ল", "শ"))
            addRow(listOf("ABC", "া", "ি", "ী", "ু", "ে", "ো", "্", "⌫"), mapOf(
                "ABC" to { bangla = false; renderKeyboard() },
                "⌫" to { backspace() }
            ))
        } else {
            addRow(listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p"))
            addRow(listOf("a", "s", "d", "f", "g", "h", "j", "k", "l"))
            addRow(listOf("⇧", "z", "x", "c", "v", "b", "n", "m", "⌫"), mapOf(
                "⇧" to { shifted = !shifted; renderKeyboard() },
                "⌫" to { backspace() }
            ))
        }
        addRow(listOf("?123", "☺", ",", "English", ".", "↵"), mapOf(
            "?123" to { bangla = !bangla; renderKeyboard() },
            "☺" to { showingEmoji = true; renderKeyboard() },
            "," to { typeCharacter(",") },
            "English" to { bangla = false; shifted = false; renderKeyboard() },
            "." to { typeCharacter(".") },
            "↵" to { enterKey() }
        ))
        val spaceRow = LinearLayout(this).apply { gravity = Gravity.CENTER }
        spaceRow.addView(makeButton("Space", keyColor, 4f) {
            currentInputConnection?.commitText(" ", 1)
        })
        spaceRow.addView(makeButton("Done", accentColor, 1f) { requestHideSelf(0) })
        root.addView(spaceRow)
    }

    private fun renderEmojiPanel() {
        val titleRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        titleRow.addView(TextView(this).apply {
            text = "EMOJI"
            textSize = 18f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(accentColor)
            setPadding(8, 8, 8, 8)
        }, LinearLayout.LayoutParams(0, 44, 1f))
        titleRow.addView(makeButton("ABC", accentColor) {
            showingEmoji = false
            renderKeyboard()
        })
        root.addView(titleRow)
        val emojis = listOf(
            "😀", "😃", "😄", "😁", "😆", "😎", "😂", "🤣", "🥹", "😊", "😍", "🥰",
            "😘", "😜", "🤔", "😭", "😡", "👍", "👎", "👏", "🙏", "💙", "❤️", "🔥",
            "✨", "🎉", "🚀", "⚽", "🐱", "💯", "😴", "🤝", "💻", "🎮", "🌍", "⭐"
        )
        emojis.chunked(6).forEach { group ->
            val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
            group.forEach { emoji ->
                row.addView(makeButton(emoji) {
                    currentInputConnection?.commitText(emoji, 1)
                })
            }
            root.addView(row)
        }
        val bottom = LinearLayout(this).apply { gravity = Gravity.CENTER }
        bottom.addView(makeButton("⌫ Delete") { backspace() })
        bottom.addView(makeButton("Close", accentColor) {
            showingEmoji = false
            renderKeyboard()
        })
        root.addView(bottom)
    }
}
