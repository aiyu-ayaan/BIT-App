package com.atech.course.utils

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView


/**
 * Recycler View
 * @since 4.0.4
 * @author Ayaan
 */
inline fun RecyclerView.onScrollChange(
    crossinline topScroll: (RecyclerView) -> Unit = {},
    crossinline bottomScroll: (RecyclerView) -> Unit = {}
) = this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (dy > 0) bottomScroll.invoke(recyclerView)
        else if (dy < 0) topScroll.invoke(recyclerView)
    }
})


fun BottomNavigationView.isShow(isVisible: Boolean) = this.apply {
    if (isVisible) {
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.GONE
    }
}