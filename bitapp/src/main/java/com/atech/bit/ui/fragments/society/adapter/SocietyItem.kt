package com.atech.bit.ui.fragments.society.adapter

import com.atech.core.retrofit.client.Society

sealed class SocietyItem {
    class Title(val title: String) : SocietyItem()

    class SocietyData(val data: Society) : SocietyItem()

}