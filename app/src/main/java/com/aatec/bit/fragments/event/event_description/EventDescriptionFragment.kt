package com.aatec.bit.fragments.event.event_description

import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.aatec.bit.NavGraphDirections
import com.aatec.bit.R
import com.aatec.bit.databinding.FragmentEventDescriptionBinding
import com.aatec.bit.utils.*
import com.aatec.core.data.ui.event.Event
import com.aatec.core.utils.*
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EventDescriptionFragment : Fragment(R.layout.fragment_event_description) {


    private val binding: FragmentEventDescriptionBinding by viewBinding()
    private val viewModel: EventDescriptionModel by viewModels()
    private lateinit var handler: Handler
    private lateinit var insta: String
    private lateinit var web: String
    private lateinit var menuInsta: MenuItem
    private lateinit var menuWeb: MenuItem
    private lateinit var menuShare: MenuItem
    private var event: Event? = null


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
        setEvent()
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


    private fun setEvent() = lifecycleScope.launchWhenCreated {
        viewModel.getEvent(viewModel.path!!).collect { dataState ->
            when (dataState) {
                DataState.Empty -> {}
                is DataState.Error -> {
                    if (dataState.exception is NoItemFoundException) {
                        menuShare.isVisible = false
                        menuInsta.isVisible =false
                        menuWeb.isVisible = false
                        binding.apply {
                            imageViewNoData.isVisible = true
                            textViewNoData.isVisible = true
                            relativeLayoutEventContent.isVisible = false
                        }
                    }
                }
                DataState.Loading -> {

                }
                is DataState.Success -> {
                    event = dataState.data

                    setViewsEvent(dataState.data)
                }
            }
        }
    }

    private fun setViewsEvent(event: Event) = binding.apply {
        val des = event.event_body
        val imgLink = event.poster_link
        insta = event.ins_link
        web = event.web_link

        menuInsta.isVisible = insta.isNotBlank()
        menuWeb.isVisible = web.isNotBlank()

        val body = Html
            .fromHtml(
                "<![CDATA[<body style=\"text-align:justify;${
                    event.run {
                        "background-color:${
                            getRgbFromHex(
                                String.format(
                                    "#%06X",
                                    (context?.getColorFromAttr(com.google.android.material.R.attr.colorSurface))
                                )
                            )
                        };"
                    }
                } ${event.run { "color:${getColorForText(requireContext())};" }}\">"
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
        societyImage.setOnClickListener {
            navigateToImageView(imgLink)
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
        menuInsta = menu.findItem(R.id.menu_insta)
        menuShare = menu.findItem(R.id.menu_share)
        menuWeb = menu.findItem(R.id.menu_link)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            android.R.id.home -> customAction()
            R.id.menu_insta -> {
                insta.openLinks(requireActivity(), R.string.no_intent_available)
                true
            }
            R.id.menu_link -> {
                requireContext().openCustomChromeTab(web)
                true
            }
            R.id.menu_share -> {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.loading),
                    Toast.LENGTH_SHORT
                ).show()
                activity?.openShareImageDeepLink(
                    requireContext(),
                    event!!.event_title,
                    event!!.path,
                    event!!.poster_link
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
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