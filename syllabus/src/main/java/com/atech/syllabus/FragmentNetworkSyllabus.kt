/*
 * BIT Lalpur App
 *
 * Created by Ayaan on 9/28/21, 12:03 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 9/28/21, 11:47 AM
 */



package com.atech.syllabus

import android.os.Bundle
import android.text.Html
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.atech.core.utils.getColorForBackground
import com.atech.core.utils.getColorForText
import com.atech.core.data.room.syllabus.SyllabusModel
import com.atech.syllabus.databinding.FragmentNetworkSyllabusBinding
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class FragmentNetworkSyllabus(
    private val syllabusModel: SyllabusModel? = null
) : Fragment(R.layout.fragment_network_syllabus) {
    private val binding: FragmentNetworkSyllabusBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            syllabusModel?.let {
                val body = Html
                    .fromHtml(
                        "<![CDATA[<body style=\"text-align:justify; background-color:${
                            getColorForBackground(
                                requireContext()
                            )
                        };color:${getColorForText(requireContext())};\">"
                                + it.subjectContent?.content?.trim()
                                + "</body>]]>", HtmlCompat.FROM_HTML_MODE_LEGACY
                    ).toString()
                textViewContent.loadData(body, "text/html; charset=utf-8", "utf-8")
                textViewTitle.text = it.subject
                textViewBooks.text = it.subjectContent?.book
                textViewReference.text = it.subjectContent?.reference
            }
        }
    }
}