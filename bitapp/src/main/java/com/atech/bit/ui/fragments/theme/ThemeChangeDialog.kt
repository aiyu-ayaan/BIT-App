package com.atech.bit.ui.fragments.theme

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.findNavController
import com.atech.bit.R
import com.atech.bit.databinding.DialogThemeChooseBinding
import com.atech.core.utils.AppTheme
import com.atech.core.utils.SharePrefKeys
import com.atech.theme.Axis
import com.atech.theme.ToolbarData
import com.atech.theme.base_class.BaseFragment
import com.atech.theme.set
import com.atech.theme.setAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ThemeChangeDialog : BaseFragment(R.layout.dialog_theme_choose, Axis.X) {


    @Inject
    lateinit var pref: SharedPreferences
    private val binding: DialogThemeChooseBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            setToolbar()
            val appTheme = pref.getString(SharePrefKeys.AppTheme.name, AppTheme.Sys.name)
            handleRadioButton()
            setCheckedButton(appTheme)
        }
    }

    private fun DialogThemeChooseBinding.handleRadioButton() {
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
        pref.edit().putString(SharePrefKeys.AppTheme.name, appTheme.name).apply()
    }

    private fun DialogThemeChooseBinding.setToolbar() = this.includeToolbar.apply {
        set(
            ToolbarData(
                title = com.atech.theme.R.string.dark_theme,
                action = findNavController()::navigateUp
            )
        )
    }
}