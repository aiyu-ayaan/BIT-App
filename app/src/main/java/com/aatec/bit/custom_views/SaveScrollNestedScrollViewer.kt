/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 4/27/22, 1:25 AM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 4/27/22, 1:25 AM
 */

package com.aatec.bit.custom_views

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import androidx.core.widget.NestedScrollView

class SaveScrollNestedScrollViewer : NestedScrollView {
    constructor(context: Context) : super(context)

    constructor(context: Context, attributes: AttributeSet) : super(context, attributes)

    constructor(context: Context, attributes: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributes,
        defStyleAttr
    )


    public override fun onSaveInstanceState(): Parcelable? {
        return super.onSaveInstanceState()
    }

    public override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)
    }
}