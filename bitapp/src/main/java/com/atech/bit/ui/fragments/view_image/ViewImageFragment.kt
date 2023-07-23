package com.atech.bit.ui.fragments.view_image

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.palette.graphics.Palette
import com.atech.bit.R
import com.atech.bit.databinding.FragmentViewImageBinding
import com.atech.bit.utils.isColorDark
import com.atech.theme.Axis
import com.atech.theme.base_class.BaseFragment
import com.atech.theme.changeBottomNavImageView
import com.atech.theme.changeStatusBarToolbarColorImageView
import com.atech.theme.loadImage
import com.atech.theme.loadImageBitMap
import com.atech.theme.setStatusBarUiTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewImageFragment : BaseFragment(R.layout.fragment_view_image, Axis.Z) {
    private val binding: FragmentViewImageBinding by viewBinding()
    private val args: ViewImageFragmentArgs by navArgs()
    private var isChange: Boolean? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            textViewTitle.text = args.title
            args.link.loadImageBitMap(
                binding.root,
                com.atech.theme.R.drawable.ic_running_error
            ) { bitmap ->
                Palette.from(bitmap!!).generate {
                    it?.let {
                        isChange = !(isColorDark(
                            it.getDominantColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    com.atech.theme.R.color.MainColor
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
                                    com.atech.theme.R.color.white
                                ).also {
                                    binding.textViewTitle.setTextColor(
                                        ContextCompat.getColor(
                                            requireContext(),
                                            com.atech.theme.R.color.white
                                        )
                                    )
                                }

                            else -> binding.buttonBack.imageTintList =
                                AppCompatResources.getColorStateList(
                                    requireContext(),
                                    com.atech.theme.R.color.black
                                ).apply {
                                    binding.textViewTitle.setTextColor(
                                        ContextCompat.getColor(
                                            requireContext(),
                                            com.atech.theme.R.color.black
                                        )
                                    )
                                }
                        }
                        activity?.changeStatusBarToolbarColorImageView(
                            it.getDominantColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    com.atech.theme.R.color.MainColor
                                )
                            )
                        )
                        activity?.changeBottomNavImageView(
                            it.getDominantColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    com.atech.theme.R.color.MainColor
                                )
                            )
                        )
                        binding.root.setBackgroundColor(
                            it.getDominantColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    com.atech.theme.R.color.MainColor
                                )
                            )
                        )
                    }
                }
            }
            imageLoad.loadImage(args.link)
            buttonBack.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }
}