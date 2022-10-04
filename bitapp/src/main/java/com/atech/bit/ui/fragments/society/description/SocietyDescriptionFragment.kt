package com.atech.bit.ui.fragments.society.description

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.ImageView
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.atech.bit.NavGraphDirections
import com.atech.bit.R
import com.atech.bit.databinding.FragmentSocietyDescriptionBinding
import com.atech.bit.utils.addMenuHost
import com.atech.bit.utils.handleCustomBackPressed
import com.atech.bit.utils.loadAdds
import com.atech.core.utils.DEFAULT_CORNER_RADIUS
import com.atech.core.utils.changeStatusBarToolbarColor
import com.atech.core.utils.getColorForText
import com.atech.core.utils.getColorFromAttr
import com.atech.core.utils.getRgbFromHex
import com.atech.core.utils.loadImage
import com.atech.core.utils.onScrollColorChange
import com.atech.core.utils.openLinks
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SocietyDescriptionFragment : Fragment(R.layout.fragment_society_description) {


    private val binding: FragmentSocietyDescriptionBinding by viewBinding()
    private val viewModel: SocietyViewModel by viewModels()
    private lateinit var handler: Handler
    private lateinit var insta: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        setTransitionNameToRoot()
        setHandler()
        setSociety()
        handleCustomBackPressed { customAction() }
        detectScroll()
        setMenu()
        setAds()
    }

    private fun setAds() {
        requireContext().loadAdds(binding.adView)
    }


    private fun setMenu() {
        addMenuHost(R.menu.menu_event_society_des, { menu ->
            menu.findItem(R.id.menu_insta).isVisible = viewModel.society!!.ins.isNotBlank()
            menu.findItem(R.id.menu_share).isVisible = false
            menu.findItem(R.id.menu_link).isVisible = false
        }) {
            when (it.itemId) {
                R.id.menu_insta -> {
                    insta.openLinks(requireActivity(), R.string.no_intent_available)
                    true
                }

                else -> false
            }
        }
    }


    private fun setSociety() = binding.apply {
        val s = viewModel.society
        s?.let { society ->
            val des = society.des
            val imgLink = society.logo_link
            insta = society.ins
            val body = """
                <html>
                <head>
                <meta name="color-scheme" content="dark light">
                <style>
                body{
                    text-align:justify;
                    background-color:${
                getRgbFromHex(
                    String.format(
                        "#%06X",
                        (context?.getColorFromAttr(com.google.android.material.R.attr.colorSurface))
                    )
                )
            };
                color:${getColorForText(requireContext())};
                 }
                 tr{
                    color:rgb(0,0,0);
                 }
                 </style>
                </head>
                <body>
                $des
                </body>
                </html>
            """.trimIndent()

            showContent.apply {
                settings.javaScriptEnabled = true
                val initialScale = getScale(400.0)
                setInitialScale(initialScale)
                loadData(
                    body,
                    "text/html; charset=utf-8",
                    "utf-8"
                )
            }

            imgLink.loadImage(
                binding.root,
                societyImage,
                progressBarDescription,
                DEFAULT_CORNER_RADIUS,
                R.drawable.ic_running_error
            )
            binding.showContent.settings.javaScriptEnabled = true

            societyImage.apply {
                this.scaleType = ImageView.ScaleType.CENTER
            }
            societyImage.setOnClickListener {
                navigateToImageView(imgLink)
            }
        }
    }

    private fun navigateToImageView(link: String) {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        val action = NavGraphDirections.actionGlobalViewImageFragment(link)
        findNavController().navigate(action)
    }


    private fun setTransitionNameToRoot() {
        binding.root.transitionName = viewModel.title
    }

    private fun setHandler() {
        handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            binding.apply {
                showContent.isVisible = true
                progressBarLoading.isVisible = false
            }
        }, resources.getInteger(R.integer.loading_date).toLong())
    }

    /**
     * @author Ayaan
     * @since 4.0.3
     */
    private fun customAction(): Boolean {
        binding.apply {
            showContent.isVisible = false
        }
        findNavController().navigateUp()
        return true
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.showContent.saveState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    /**
     * @since 4.0.4
     * @author Ayaan
     */
    private fun detectScroll() {
        activity?.onScrollColorChange(binding.scrollViewDescription, {
            activity?.changeStatusBarToolbarColor(
                R.id.toolbar,
                com.google.android.material.R.attr.colorSurface
            )
        }, {
            activity?.changeStatusBarToolbarColor(
                R.id.toolbar,
                R.attr.bottomBar
            )
        })
    }

    override fun onPause() {
        super.onPause()
        activity?.changeStatusBarToolbarColor(
            R.id.toolbar,
            com.google.android.material.R.attr.colorSurface
        )
    }

    private fun getScale(contentWidth: Double): Int {
        return if (this.activity != null) {
            val displayMetrics = DisplayMetrics()
            this.requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
            val `val` = displayMetrics.widthPixels / contentWidth * 100.0
            `val`.toInt()
        } else {
            100
        }
    }
}