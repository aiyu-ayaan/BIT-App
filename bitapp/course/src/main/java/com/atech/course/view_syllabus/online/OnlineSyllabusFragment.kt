package com.atech.course.view_syllabus.online

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import com.atech.core.retrofit.ApiCases
import com.atech.core.utils.DataState
import com.atech.core.utils.NetworkBoundException
import com.atech.course.R
import com.atech.course.databinding.FragmentOnlineSyllabusBinding
import com.atech.theme.getColorForText
import com.atech.theme.getColorFromAttr
import com.atech.theme.getRgbFromHex
import com.atech.theme.launchWhenStarted
import com.atech.theme.toast
import com.google.android.material.color.MaterialColors

/**
 * Pair is used to pass the subject name and the course Sem
 */
class OnlineSyllabusFragment : Fragment(R.layout.fragment_online_syllabus) {

    private lateinit var pair: Pair<String, String>
    private lateinit var cases: ApiCases

    lateinit var binding: FragmentOnlineSyllabusBinding

    fun setPairAndCase(pair: Pair<String, String>, cases: ApiCases) {
        this.pair = pair
        this.cases = cases
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOnlineSyllabusBinding.bind(view)
        if (!::pair.isInitialized || !::cases.isInitialized) {
            toast(
                "Something went wrong, please try again later",
            )
            return
        }
        launchWhenStarted {
            fetchData()
        }
    }

    private suspend fun fetchData() {
        cases.syllabusMarkdown.invoke(
            pair.second.replace("\\d".toRegex(), ""), pair.second, pair.first
        ).asLiveData().observe(viewLifecycleOwner) { dataState ->
            when (dataState) {
                is DataState.Error -> {
                    if (dataState.exception is NetworkBoundException) {
                        when (dataState.exception as NetworkBoundException) {
                            NetworkBoundException.NoInternet -> {
                                setViewsVisible(false)
                                Toast.makeText(
                                    requireContext(),
                                    resources.getString(com.atech.theme.R.string.no_internet),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            NetworkBoundException.NotFound -> {
                                setViewsVisible(false)
                                binding.emptyText.text =
                                    resources.getString(com.atech.theme.R.string.no_syllabus_found)
                                Toast.makeText(
                                    requireContext(),
                                    resources.getString(com.atech.theme.R.string.no_syllabus_found),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            is NetworkBoundException.Unknown -> {
                                Toast.makeText(
                                    requireContext(), "Something went wrong", Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }

                is DataState.Success -> binding.setMarkDownFile(dataState)
                else -> Unit
            }
        }
    }

    private fun FragmentOnlineSyllabusBinding.setMarkDownFile(dataState: DataState.Success<String>) {
        markdown.apply {
            setViewsVisible(true)
            setBackgroundColor(
                MaterialColors.getColor(
                    requireView(), android.viewbinding.library.R.attr.colorSurface
                )
            )
            setMarkDownText(
                dataState.data + "<br> <br><style> body{background-color: ${
                    getRgbFromHex(
                        String.format(
                            "#%06X",
                            (context?.getColorFromAttr(com.google.android.material.R.attr.colorSurface))
                        )
                    )
                } ; color:${getColorForText(requireContext())};}</style>"
            )
        }
    }

    private fun setViewsVisible(isVisible: Boolean) = binding.apply {
        markdown.isVisible = isVisible
        emptyText.isVisible = !isVisible
        emptyMarkdown.isVisible = !isVisible
    }
}