package com.atech.theme

import android.widget.Toast
import androidx.fragment.app.Fragment


fun Fragment.toast(message: String) =
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()