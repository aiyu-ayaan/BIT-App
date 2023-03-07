package com.atech.bit.utils.delegates

import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.atech.bit.ui.custom_views.SaveScrollNestedScrollViewer


private const val TAG = "RestoreScroll"

interface RestoreScroll {
    fun setLifecycleOwner(lifecycleOwner: LifecycleOwner, scrollView: SaveScrollNestedScrollViewer)

}


class RestoreScrollDelegate : RestoreScroll, LifecycleEventObserver {

    private var state: Parcelable? = null
    private var scrollView: SaveScrollNestedScrollViewer? = null
    override fun setLifecycleOwner(
        lifecycleOwner: LifecycleOwner, scrollView: SaveScrollNestedScrollViewer
    ) {
        lifecycleOwner.lifecycle.addObserver(this)
        this.scrollView = scrollView
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_PAUSE -> {
                state = scrollView?.onSaveInstanceState()
                Log.d(TAG, "onPause: $state")
            }

            Lifecycle.Event.ON_CREATE -> {
                state?.let {
                    Log.d(TAG, "onResume: $it")
                    scrollView?.onRestoreInstanceState(it)
                }
            }

            else -> {

            }
        }
    }

}