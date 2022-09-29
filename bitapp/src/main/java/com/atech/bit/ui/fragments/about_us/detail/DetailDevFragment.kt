package com.atech.bit.ui.fragments.about_us.detail

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.atech.bit.NavGraphDirections
import com.atech.bit.R
import com.atech.bit.databinding.FragmentDetailDevBinding
import com.atech.core.utils.loadImageCircular
import com.atech.core.utils.openCustomChromeTab
import com.atech.core.utils.openLinks
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailDevFragment : Fragment(R.layout.fragment_detail_dev) {

    private val binding: FragmentDetailDevBinding by viewBinding()
    private val viewModel: DetailDevViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X,  true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X,  false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.transitionName = viewModel.dev?.name

        binding.apply {
            viewModel.dev?.let { dev ->
                dev.img_link?.loadImageCircular(
                    binding.root,
                    imageViewProfile,
                    binding.progressBarDescription,
                    R.drawable.ic_running_error
                )
                textViewName.text = dev.name
                binding.textViewRole.text = dev.des


                buttonWeb.isVisible = dev.website!!.isNotBlank()
                divWebsite.isVisible = dev.website!!.isNotBlank()

                buttonStackOverFlow.isVisible = dev.stackoverflow!!.isNotBlank()
                divStack.isVisible = dev.stackoverflow!!.isNotBlank()

                buttonGit.isVisible = dev.github!!.isNotBlank()
                divGit.isVisible = dev.github!!.isNotBlank()

                buttonLinkedin.isVisible = dev.linkedin!!.isNotBlank()
                divLink.isVisible = dev.linkedin!!.isNotBlank()


                buttonInstagram.isVisible = dev.instagram!!.isNotBlank()



                buttonStackOverFlow.setOnClickListener {
                    requireActivity().openCustomChromeTab(dev.stackoverflow!!)
                }
                buttonWeb.setOnClickListener {
                    requireActivity().openCustomChromeTab(dev.website!!)
                }
                buttonInstagram.setOnClickListener {
                    dev.instagram?.openLinks(requireActivity(), R.string.no_intent_available)
                }
                buttonGit.setOnClickListener {
                    requireActivity().openCustomChromeTab(dev.github!!)
                }
                buttonLinkedin.setOnClickListener {
                    requireActivity().openCustomChromeTab(dev.linkedin!!)
                }
                imageViewProfile.setOnClickListener {
                    exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z,  true)
                    reenterTransition =
                        MaterialSharedAxis(MaterialSharedAxis.Z,  false)
                    findNavController().navigate(
                        NavGraphDirections.actionGlobalViewImageFragment(
                            dev.img_link!!,
                            dev.name!!
                        )
                    )
                }
            }
        }
    }


}