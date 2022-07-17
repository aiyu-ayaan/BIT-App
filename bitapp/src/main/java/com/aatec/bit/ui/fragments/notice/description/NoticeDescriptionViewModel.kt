/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 3/13/22, 10:32 AM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 3/12/22, 4:56 PM
 */

package com.aatec.bit.ui.fragments.notice.description

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.aatec.core.data.ui.notice.NoticeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NoticeDescriptionViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val repository: NoticeRepository
) : ViewModel() {
    val path = state.get<String>("path")

    fun notice(path: String) = repository.getNoticeFromPath(path)

    fun attach(path: String) = repository.getAttachFromPath(path)
}