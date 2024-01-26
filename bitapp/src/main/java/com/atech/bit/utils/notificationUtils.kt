/*
 *  Created by aiyu
 *  Copyright (c) 2021 . All rights reserved.
 *  BIT App
 *
 */

package com.atech.bit.utils

import com.atech.bit.ui.activity.main.MainViewModel
import com.atech.core.utils.MessageTopic
import com.atech.core.utils.MessageTopicTest


fun mapWithNotificationEnable(
    model: MainViewModel.AppNotificationEnable
): List<Pair<String, Boolean>> = listOf(
    Pair(MessageTopic.Notice.value, model.notice),
    Pair(MessageTopic.Event.value, model.event),
    Pair(MessageTopic.App.value, model.app),
    Pair(MessageTopic.Update.value, true),
)


fun mapWithNotificationEnableTest(
    model: MainViewModel.AppNotificationEnable
): List<Pair<String, Boolean>> = listOf(
    Pair(MessageTopicTest.Notice.value, model.notice),
    Pair(MessageTopicTest.Event.value, model.event),
    Pair(MessageTopicTest.App.value, model.app),
    Pair(MessageTopicTest.Update.value, true)
)
