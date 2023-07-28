package com.atech.bit.ui.fragments.society.adapter

import com.atech.bit.ui.fragments.home.adapter.HomeItems
import com.atech.core.retrofit.client.Society
import com.atech.theme.AdsUnit

sealed class SocietyItem {
    class Title(val title: String) : SocietyItem()

    class SocietyData(val data: Society) : SocietyItem()

    data class Ads(val ads: AdsUnit) : SocietyItem()

}