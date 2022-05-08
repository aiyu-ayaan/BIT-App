/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 9/1/21, 1:09 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 9/1/21, 11:14 AM
 */



package com.aatec.core.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

/**
 * Return Current fragment.
 * @return Current Fragment
 * @since 4.0
 */
val FragmentManager.currentNavigationFragment: Fragment?
    get() = primaryNavigationFragment?.childFragmentManager?.fragments?.first()