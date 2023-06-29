package com.atech.bit.ui.fragments.notice

import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.Fragment
import com.atech.bit.R
import com.atech.bit.databinding.FragmentNoticeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoticeFragment : Fragment(R.layout.fragment_notice) {
    private val binding : FragmentNoticeBinding by viewBinding()
}