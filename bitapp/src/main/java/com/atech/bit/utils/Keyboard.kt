package com.atech.bit.utils

import android.content.Context
import android.widget.EditText


fun Context.openKeyboard(editText: EditText) {
    editText.requestFocus()
    val imm =
        getSystemService(Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
    imm.showSoftInput(editText, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
}

fun Context.closeKeyboard(editText: EditText) {
    editText.clearFocus()
    val imm =
        getSystemService(Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
    imm.hideSoftInputFromWindow(editText.windowToken, 0)
}