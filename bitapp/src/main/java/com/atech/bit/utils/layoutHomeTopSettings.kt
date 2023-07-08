package com.atech.bit.utils

import com.atech.bit.databinding.LayoutHomeTopSettingsBinding
import com.google.android.material.materialswitch.MaterialSwitch

data class HomeTopModel(
    val title: String,
    val onSettingClick: () -> Unit = {},
    val onEditClick: () -> Unit = {},
    val switchAction: MaterialSwitch.() -> Unit = {},
    val switchOnClick: (Boolean) -> Unit = {}
)

fun LayoutHomeTopSettingsBinding.set(model: HomeTopModel) = this.apply {
    layoutTitle.text = model.title
    toggleSwitch.apply {
        model.switchAction(this)
    }
    edit.setOnClickListener {
        model.onEditClick()
    }
    setting.setOnClickListener {
        model.onSettingClick()
    }
}