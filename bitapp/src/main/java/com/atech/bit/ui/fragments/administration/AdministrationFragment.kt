package com.atech.bit.ui.fragments.administration

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.atech.bit.R
import com.atech.bit.databinding.FragmentAdministrationBinding
import com.atech.core.retrofit.ApiCases
import com.atech.core.utils.DataState
import com.atech.core.utils.NetworkBoundException
import com.atech.theme.Axis
import com.atech.theme.BaseFragment
import com.atech.theme.ToolbarData
import com.atech.theme.enterTransition
import com.atech.theme.getColorForText
import com.atech.theme.getColorFromAttr
import com.atech.theme.getRgbFromHex
import com.atech.theme.launchWhenStarted
import com.atech.theme.set
import com.google.android.material.color.MaterialColors
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class AdministrationFragment : BaseFragment(R.layout.fragment_administration,Axis.Y) {
    private val binding: FragmentAdministrationBinding by viewBinding()

    @Inject
    lateinit var cases: ApiCases


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            setToolbar()
        }
        loadData()
    }

    private fun loadData() = launchWhenStarted {
        cases.administration.invoke().collectLatest { dataState ->
            when (dataState) {
                is DataState.Success -> {
                    binding.setMarkDownFile(dataState)
                }

                is DataState.Error -> {
                    when (dataState.exception) {
                        // HTTP 504 Unsatisfiable Request (only-if-cached)
                        is NetworkBoundException -> {
                            if ((dataState.exception as NetworkBoundException).code == 504) {
                                setViewsVisible(false)
                                Toast.makeText(
                                    requireContext(),
                                    resources.getString(com.atech.theme.R.string.no_internet),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            if ((dataState.exception as NetworkBoundException).code == 404) {
                                setViewsVisible(false)
                                binding.emptyText.text =
                                    resources.getString(com.atech.theme.R.string.no_data)
                                Toast.makeText(
                                    requireContext(),
                                    resources.getString(com.atech.theme.R.string.no_data),
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

                else -> {}
            }
        }
    }

    private fun FragmentAdministrationBinding.setMarkDownFile(dataState: DataState.Success<String>) {
        markdown.apply {
            setViewsVisible(true)
            setBackgroundColor(
                MaterialColors.getColor(
                    requireView(), com.atech.theme.R.attr.bottomBar
                )
            )
            setMarkDownText(
                dataState.data + "<br> <br><style> body{background-color: ${
                    getRgbFromHex(
                        String.format(
                            "#%06X",
                            (context?.getColorFromAttr(com.atech.theme.R.attr.bottomBar))
                        )
                    )
                } ; color:${getColorForText(requireContext())};}</style>"
            )
        }
    }

    private fun FragmentAdministrationBinding.setToolbar() = this.includeToolbar.apply {
        set(
            ToolbarData(
                title = com.atech.theme.R.string.administration,
                action = {
                    findNavController().navigateUp()
                }
            )
        )
    }

    private fun setViewsVisible(isVisible: Boolean) = binding.apply {
        markdown.isVisible = isVisible
        emptyText.isVisible = !isVisible
        emptyMarkdown.isVisible = !isVisible
    }
}