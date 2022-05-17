package com.aatec.bit.fragments.society.description

import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.ImageView
import androidx.core.text.HtmlCompat
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.aatec.bit.NavGraphDirections
import com.aatec.bit.R
import com.aatec.bit.databinding.FragmentSocietyDescriptionBinding
import com.aatec.bit.utils.*
import com.aatec.core.utils.*
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SocietyDescriptionFragment : Fragment(R.layout.fragment_society_description) {


    private val binding: FragmentSocietyDescriptionBinding by viewBinding()
    private val viewModel: SocietyViewModel by viewModels()
    private lateinit var handler: Handler
    private lateinit var insta: String
    private lateinit var web: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        setTransitionNameToRoot()
        setHandler()
        setSociety()
        darkWebView()
        setHasOptionsMenu(true)
        handleCustomBackPressed { customAction() }

        detectScroll()
    }

    private fun darkWebView() {
        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    WebSettingsCompat.setForceDark(
                        binding.showContent.settings,
                        WebSettingsCompat.FORCE_DARK_ON
                    )
                }
                Configuration.UI_MODE_NIGHT_NO, Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                    WebSettingsCompat.setForceDark(
                        binding.showContent.settings,
                        WebSettingsCompat.FORCE_DARK_OFF
                    )
                }
                else -> {
                    //
                }
            }
        }
    }

    private fun setSociety() = binding.apply {
        val s = viewModel.society
        s?.let { society ->
            val des = society.des
            val imgLink = society.logo_link
            insta = society.ins

            val body = Html
                .fromHtml(
                    "<![CDATA[<body style=\"text-align:justify;${
                        society.run {
                            "background-color:${
                                getRgbFromHex(
                                    String.format(
                                        "#%06X",
                                        (context?.getColorFromAttr(com.google.android.material.R.attr.colorSurface))
                                    )
                                )
                            };"
                        }
                    } ${society.run { "color:${getColorForText(requireContext())};" }}\">"
                            + des.replaceNewLineWithBreak()
                            + "</body>]]>", HtmlCompat.FROM_HTML_MODE_LEGACY
                ).toString()

            showContent.loadData(
                body,
                "text/html; charset=utf-8",
                "utf-8"
            )
            val initialScale = getScale(400.0)
            showContent.setInitialScale(initialScale)
            imgLink.loadImage(
                binding.root,
                societyImage,
                progressBarDescription,
                DEFAULT_CORNER_RADIUS,
                R.drawable.ic_running_error
            )

            societyImage.apply {
                this.scaleType = ImageView.ScaleType.CENTER
            }
            societyImage.setOnClickListener {
                navigateToImageView(imgLink)
            }
        }
    }



    private fun navigateToImageView(link: String) {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ false)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_event_society_des, menu)
        menu.findItem(R.id.menu_insta).isVisible = viewModel.society!!.ins.isNotBlank()
        menu.findItem(R.id.menu_share).isVisible = false
        menu.findItem(R.id.menu_link).isVisible = false
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