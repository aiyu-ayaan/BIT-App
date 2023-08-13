package com.atech.bit.ui.fragments.theme

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.atech.bit.R
import com.atech.bit.databinding.FragmentThemeBinding
import com.atech.core.utils.AppTheme
import com.atech.core.utils.SharePrefKeys
import com.atech.theme.Axis
import com.atech.theme.Theme
import com.atech.theme.ToolbarData
import com.atech.theme.customBackPress
import com.atech.theme.enterTransition
import com.atech.theme.exitTransition
import com.atech.theme.isAPI31AndUp
import com.atech.theme.isDark
import com.atech.theme.navigate
import com.atech.theme.set
import com.atech.theme.setAppTheme
import com.atech.theme.showSnackBar
import com.atech.theme.toast
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.system.exitProcess


@AndroidEntryPoint
class ThemeFragment : Fragment(R.layout.fragment_theme) {
    private val binding: FragmentThemeBinding by viewBinding()


    @Inject
    lateinit var pref: SharedPreferences

    private var dynamicThemeValue: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            setToolbar()
            setUI()
        }
        customBackPress()
    }

    private fun FragmentThemeBinding.setUI() = this.apply {
        imageViewSample.setImageResource(
            listOf(
                com.atech.theme.R.drawable.sample1,
                com.atech.theme.R.drawable.sample2,
                com.atech.theme.R.drawable.sample3,
            ).random()
        )
        toggleDynamicTheme()
        toggleAppTheme()
    }

    private fun FragmentThemeBinding.toggleDynamicTheme() {
        imageViewTypeDynamicColor.setImageResource(com.atech.theme.R.drawable.ic_color)
        textViewHeadingDynamicColor.text = getString(com.atech.theme.R.string.dynamic_theme)
        textViewDescriptionDynamicColor.text =
            getString(com.atech.theme.R.string.apply_color_from_wallpapers_to_the_app_theme)
        isAPI31AndUp {
            toggleSwitchDynamicColor.apply {
                isClickable = false
                isChecked = pref.getBoolean(
                    SharePrefKeys.IsDynamicThemeEnabled.name, true
                ).also { dynamicThemeValue = it }
            }
            cardViewDynamicColor.setOnClickListener {
                toggleSwitchDynamicColor.isChecked = !toggleSwitchDynamicColor.isChecked
                updateDynamicTheme(toggleSwitchDynamicColor.isChecked)
                showSnakeBar()
            }
        } ?: run {
            cardViewDynamicColor.setOnClickListener {
                toast("This feature is only available for Android 12 and above")
            }
            toggleSwitchDynamicColor.isEnabled = false
        }
    }

    private fun showSnakeBar() {
        binding.root.showSnackBar(
            "App will automatically restart to apply changes",
            Snackbar.LENGTH_LONG
        )
    }

    private fun FragmentThemeBinding.toggleAppTheme() {
        cardViewTheme.setOnClickListener {
            navigateToThemeChangeDialog()
        }
        imageViewTypeTheme.setImageResource(com.atech.theme.R.drawable.round_wb_sunny_24)
        textViewHeadingTheme.text = getString(com.atech.theme.R.string.dark_theme)
        textViewDescriptionTheme.text = getString(com.atech.theme.R.string.off)
        toggleSwitchTheme.apply {
            isChecked = context.isDark()
            setOnClickListener {
                if (isChecked) {
                    setAppTheme(Theme.DARK)
                    updatePref(AppTheme.Dark)
                } else {
                    setAppTheme(Theme.LIGHT)
                    updatePref(AppTheme.Light)
                }
            }
        }
    }

    private fun updatePref(appTheme: AppTheme) {
        pref.edit().putString(SharePrefKeys.AppTheme.name, appTheme.name).apply()
    }

    private fun updateDynamicTheme(isEnabled: Boolean) {
        pref.edit().putBoolean(SharePrefKeys.IsDynamicThemeEnabled.name, isEnabled).apply()
    }

    private fun navigateToThemeChangeDialog() {
        exitTransition(Axis.X)
        ThemeFragmentDirections.actionThemeFragmentToThemeChangeDialog().also(this::navigate)
    }

    private fun FragmentThemeBinding.setToolbar() = this.toolbar.apply {
        set(
            ToolbarData(
                title = com.atech.theme.R.string.display, action = ::backLogic
            )
        )
    }

    private fun customBackPress() {
        customBackPress {
            backLogic()
        }
    }

    private fun backLogic() {
        isAPI31AndUp {
            val newValue = binding.toggleSwitchDynamicColor.isChecked
            if (dynamicThemeValue != newValue) {
                restartApp()
            } else findNavController().navigateUp()
        } ?: run {
            findNavController().navigateUp()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun restartApp() {
        val intent = requireContext().packageManager
            .getLaunchIntentForPackage(requireContext().packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent?.let {
            startActivity(it)
        }
        exitProcess(0)
    }

    override fun onResume() {
        super.onResume()
        binding.toggleSwitchTheme.isChecked = context?.isDark() ?: false
    }
}