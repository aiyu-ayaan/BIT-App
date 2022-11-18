package com.atech.bit.ui.fragments.course.view_syllabus

import android.os.Bundle
import android.util.Log
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.navArgs
import com.atech.bit.R
import com.atech.bit.databinding.FragmentViewSyllabusBinding
import com.atech.bit.utils.loadAdds
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


@AndroidEntryPoint
class ViewSyllabusFragment : Fragment(R.layout.fragment_view_syllabus) {

    private val binding: FragmentViewSyllabusBinding by viewBinding()
    private val arg: ViewSyllabusFragmentArgs by navArgs()

    @Inject
    lateinit var apiRepository: ApiRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val subject = arg.subjectName
        binding.root.transitionName = subject
        binding.apply {
            apiRepository.fetchSyllabusMarkdown(
                arg.courseSem.replace("\\d".toRegex(), ""), arg.courseSem, subject
            ).asLiveData().observe(viewLifecycleOwner) { dataState ->
                when (dataState) {
                    DataState.Empty -> {}
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
                        Log.d("AAA", "onViewCreated: ${dataState.exception}")
                    }

                    DataState.Loading -> {}
                    is DataState.Success -> {
                        setMarkDownFile(dataState)
                    }
                }
            }
        }
    }

    private fun setViewsVisible(isVisible: Boolean) = binding.apply {
        markdown.isVisible = isVisible
        emptyText.isVisible = !isVisible
        emptyMarkdown.isVisible = !isVisible
    }

    private fun FragmentViewSyllabusBinding.setMarkDownFile(dataState: DataState.Success<String>) {
        requireContext().loadAdds(adViewSyllabusLabContent)
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


}