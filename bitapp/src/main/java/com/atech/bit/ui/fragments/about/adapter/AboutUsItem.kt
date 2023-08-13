package com.atech.bit.ui.fragments.about.adapter

sealed class AboutUsItem {
    data class Title(val title: String) : AboutUsItem()
    data class AppVersion(val version: String) : AboutUsItem()
    data class Dev(val devs: com.atech.core.retrofit.client.Devs) : AboutUsItem()
    object BottomView : AboutUsItem()
}