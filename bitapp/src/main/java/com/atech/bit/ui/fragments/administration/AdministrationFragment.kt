package com.atech.bit.ui.fragments.administration

import android.os.Bundle
import android.util.Log
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.atech.bit.R
import com.atech.bit.databinding.FragmentAdministrationBinding
import com.atech.bit.databinding.FragmentViewSyllabusBinding
import com.atech.bit.utils.launchWhenStarted
import com.atech.core.api.ApiRepository
import com.atech.core.utils.DataState
import com.atech.core.utils.getColorForText
import com.atech.core.utils.getColorFromAttr
import com.atech.core.utils.getRgbFromHex
import com.google.android.material.color.MaterialColors
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.HttpException
import javax.inject.Inject

private const val TAG = "AdministrationFragment"

@AndroidEntryPoint
class AdministrationFragment : Fragment(R.layout.fragment_administration) {

    private val binding: FragmentAdministrationBinding by viewBinding()

    @Inject
    lateinit var apiRepository: ApiRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            launchWhenStarted {
                apiRepository.fetAdministration().collect(::collectData)
            }
        }
    }

    private fun collectData(dataState: DataState<String>) {
        when (dataState) {
            DataState.Empty -> {
                Log.d(TAG, "collectData: Empty")
            }

            is DataState.Error -> {
                when (dataState.exception) {
                    // HTTP 504 Unsatisfiable Request (only-if-cached)
                    is HttpException -> {
                        if ((dataState.exception as HttpException).code() == 504) {
                            setViewsVisible(false)
                            Toast.makeText(
                                requireContext(),
                                resources.getString(R.string.no_internet),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        if ((dataState.exception as HttpException).code() == 404) {
                            setViewsVisible(false)
                            binding.emptyText.text =
                                resources.getString(com.atech.syllabus.R.string.no_syllabus_found)
                            Toast.makeText(
                                requireContext(),
                                resources.getString(com.atech.syllabus.R.string.no_syllabus_found),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    else -> {
                        Toast.makeText(
                            requireContext(), "Something went wrong", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            is DataState.Loading -> {}
            is DataState.Success -> {
                binding.setMarkDownFile(dataState)
            }
        }
    }

    private fun FragmentAdministrationBinding.setMarkDownFile(dataState: DataState.Success<String>) {
        markdown.apply {
            setViewsVisible(true)
            setBackgroundColor(
                MaterialColors.getColor(
                    requireView(), me.relex.circleindicator.R.attr.colorSurface
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