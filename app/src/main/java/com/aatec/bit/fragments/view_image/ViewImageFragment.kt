package com.aatec.bit.fragments.view_image

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.palette.graphics.Palette
import com.aatec.bit.R
import com.aatec.bit.databinding.FragmentViewImageBinding
import com.aatec.bit.utils.*
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewImageFragment : Fragment(R.layout.fragment_view_image) {
    private val binding: FragmentViewImageBinding by viewBinding()
    private val args: ViewImageFragmentArgs by navArgs()
    private var isChange: Boolean? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        binding.apply {
            textViewTitle.text = args.title
            args.link.loadImageBitMap(
                binding.root,
                R.drawable.ic_running_error
            ) { bitmap ->
                Palette.from(bitmap!!).generate {
                    it?.let {
                        isChange = !(isColorDark(
                            it.getDominantColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.MainColor
                                )
                            )
                        ))
                        setStatusBarUiTheme(
                            activity,
                            isChange!!
                        )
                        when (isChange!!) {
                            false -> binding.buttonBack.imageTintList =
                                AppCompatResources.getColorStateList(
                                    requireContext(),
                                    R.color.white
                                ).also {
                                    binding.textViewTitle.setTextColor(
                                        ContextCompat.getColor(requireContext(), R.color.white)
                                    )
                                }
                            else -> binding.buttonBack.imageTintList =
                                AppCompatResources.getColorStateList(
                                    requireContext(),
                                    R.color.black
                                ).apply {
                                    binding.textViewTitle.setTextColor(
                                        ContextCompat.getColor(requireContext(), R.color.black)
                                    )
                                }
                        }
                        activity?.changeStatusBarToolbarColorImageView(
                            it.getDominantColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.MainColor
                                )
                            )
                        )
                        activity?.changeBottomNavImageView(
                            it.getDominantColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.MainColor
                                )
                            )
                        )
                        binding.root.setBackgroundColor(
                            it.getDominantColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.MainColor
                                )
                            )
                        )
                    }
                }
            }
            args.link.loadImageDefault(
                binding.root,
                imageLoad,
                progressBarImageFull,
                R.drawable.ic_running_error
            )
            buttonBack.setOnClickListener {
                findNavController().navigateUp()
            }
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        setStatusBarUiTheme(activity, !(requireContext().isDark()))
    }
}