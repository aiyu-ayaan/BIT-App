package com.atech.theme

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import com.atech.theme.databinding.LayoutToolbarBinding
import com.google.android.material.materialswitch.MaterialSwitch

data class ToolbarData(
    @StringRes
    val title: Int = R.string.app_name,
    val titleString: String? = null,
    @DrawableRes
    val icon: Int = R.drawable.round_arrow_back_24,
    val action: () -> Unit,
    @DrawableRes
    val endIcon: Int? = null,
    val endAction: () -> Unit = {},
    @StringRes
    val switchTitle: Int? = null,
    val switchAction: MaterialSwitch.() -> Unit = {},
    val switchOnClick: (Boolean) -> Unit = {}
)

fun LayoutToolbarBinding.set(toolbarData: ToolbarData) = this.apply {
    if (toolbarData.titleString != null) textViewTitle.text = toolbarData.titleString
    else textViewTitle.text = root.context.getText(toolbarData.title)
    materialButtonClose.apply {
        setIconResource(toolbarData.icon)
        setOnClickListener { toolbarData.action() }
    }
    if (toolbarData.switchTitle != null) {
        toggleSwitch.apply {
            toolbarData.switchAction(this)
            setThumbIconResource(R.drawable.round_cloud_off_24)
            isVisible = true
            text = root.context.getText(toolbarData.switchTitle)
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked)
                    setThumbIconResource(R.drawable.round_cloud_24)
                else
                    setThumbIconResource(R.drawable.round_cloud_off_24)
                toolbarData.switchOnClick(isChecked)
            }
        }
        return@apply
    }
    toolbarData.endIcon?.let {
        materialButtonEnd.apply {
            isVisible = true
            setIconResource(it)
            setOnClickListener { toolbarData.endAction() }
        }
    }
}