package com.atech.bit.utils

import com.atech.bit.ui.activity.main.MainViewModel
import com.atech.core.utils.MessageTopic


fun mapWithNotificationEnable(
    model: MainViewModel.AppNotificationEnable
): List<Pair<String, Boolean>> {
    val list = mutableListOf<Pair<String, Boolean>>()
    list.add(Pair(MessageTopic.Notice.value, model.notice))
    list.add(Pair(MessageTopic.Event.value, model.event))
    list.add(Pair(MessageTopic.App.value, model.app))
    list.add(Pair(MessageTopic.Update.value, true))
    return emptyList()
}