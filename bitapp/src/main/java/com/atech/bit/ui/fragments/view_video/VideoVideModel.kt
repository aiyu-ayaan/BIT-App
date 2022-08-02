package com.atech.bit.ui.fragments.view_video

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VideoVideModel @Inject constructor(
    private val state: SavedStateHandle
) : ViewModel() {
    val link = state.get<String>("link")

    var playWhenReady = state.get<Boolean>("playWhenReady") ?: true
        set(value) {
            field = value
            state["playWhenReady"] = value
        }
    var currentItem = state.get<Int>("currentItem") ?: 0
        set(value) {
            field = value
            state["currentItem"] = value
        }
    var playbackPosition = state.get<Long>("playbackPosition") ?: 0
        set(value) {
            field = value
            state["playbackPosition"] = value
        }
}