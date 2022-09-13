package com.atech.bit.ui.fragments.theme

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.DialogFragment
import com.atech.bit.R
import com.atech.bit.databinding.DialogThemeChooseBinding
import com.atech.core.utils.setAppTheme
import com.atech.core.utils.AppTheme
import com.atech.core.utils.KEY_APP_THEME
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ThemeChangeDialog : DialogFragment() {


    @Inject
    lateinit var pref: SharedPreferences
    private lateinit var binding: DialogThemeChooseBinding


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogThemeChooseBinding.inflate(layoutInflater)
        binding.apply {
            val appTheme = pref.getString(KEY_APP_THEME, AppTheme.Sys.name)
            setCheckedButton(appTheme)
            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.radio_button_light -> {
                        setAppTheme(AppCompatDelegate.MODE_NIGHT_NO)
                        updatePref(AppTheme.Light)
                    }
                    R.id.radio_button_dark -> {
                        setAppTheme(AppCompatDelegate.MODE_NIGHT_YES)
                        updatePref(AppTheme.Dark)
                    }
                    else -> {
                        setAppTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                        updatePref(AppTheme.Sys)
                    }
                }
            }
        }
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.chooseTheme))
            .setView(binding.root)
            .setPositiveButton(resources.getString(R.string.done)) { _, _ ->
            }
        return dialog.create()
    }

    private fun setCheckedButton(appTheme: String?) {
        appTheme?.let {
            when (it) {
                AppTheme.Sys.name -> binding.radioGroup.check(R.id.radio_button_system)
                AppTheme.Light.name -> binding.radioGroup.check(R.id.radio_button_light)
                AppTheme.Dark.name -> binding.radioGroup.check(R.id.radio_button_dark)
            }
        }
    }

    @SuppressLint("CommitPrefEdits")
    private fun updatePref(appTheme: AppTheme) {
        pref.edit()
            .putString(KEY_APP_THEME, appTheme.name)
            .apply()
    }
}