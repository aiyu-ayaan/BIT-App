package com.atech.theme

import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout


fun String.checkCgpa() = this.isNotEmpty() && this.toDouble() > 10

fun String.checkEarnCredits() = this.isNotEmpty() && this.toDouble() > 30

inline fun TextInputLayout.combineTextChangeListener(
    other: TextInputLayout,
    crossinline listener: (Pair<String, String>) -> Unit
) = this.apply {
    val editText1 = this.editText
    val editText2 = other.editText

    editText1?.addTextChangedListener(onTextChanged = { text, _, _, _ ->
        if (text.toString().isNotEmpty() && text.toString().toDouble() > 10) {
            this.error = "Invalid CGPA"
            listener(Pair("0", editText2?.text.toString().ifEmpty { "0" }))
            return@addTextChangedListener
        }
        val cgpa = text.toString().ifEmpty { "0" }
        val earnCredits = editText2?.text.toString().ifEmpty { "0" }
        this.error = null
        listener(Pair(cgpa, earnCredits))
    })

    editText2?.addTextChangedListener(onTextChanged = { text, _, _, _ ->
        if (text.toString().isNotEmpty() && text.toString().toDouble() > 30) {
            other.error = "Invalid Credits"
            listener(
                Pair(
                    editText1?.text.toString().ifEmpty { "0" }, "0"
                )
            )
            return@addTextChangedListener
        }
        other.error = null
        val cgpa = editText1?.text.toString().ifEmpty { "0" }
        val earnCredits = text.toString().ifEmpty { "0" }
        listener(Pair(cgpa, earnCredits))
    })
}
