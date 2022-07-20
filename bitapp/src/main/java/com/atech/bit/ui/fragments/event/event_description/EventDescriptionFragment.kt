package com.atech.bit.ui.fragments.event.event_description

import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.util.DisplayMetrics
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.atech.bit.NavGraphDirections
import com.atech.bit.R
import com.atech.bit.databinding.FragmentEventDescriptionBinding
import com.atech.bit.ui.activity.main_activity.viewmodels.ConnectionManagerViewModel
import com.atech.bit.utils.addMenuHost
import com.atech.bit.utils.handleCustomBackPressed
import com.atech.bit.utils.openShareDeepLink
import com.atech.bit.utils.openShareImageDeepLink
import com.atech.core.data.ui.event.Event
import com.atech.core.utils.*
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EventDescriptionFragment : Fragment(R.layout.fragment_event_description) {


    private val binding: FragmentEventDescriptionBinding by viewBinding()
    private val viewModel: EventDescriptionModel by viewModels()
    private val connectionManager: ConnectionManagerViewModel by activityViewModels()
    private lateinit var handler: Handler
    private var event: Event? = null
    private var isNetConnect: Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        setTransitionNameToRoot()


        setHandler()
        setEvent()
        darkWebView()
        handleCustomBackPressed { customAction() }
        setIsConnected()
        detectScroll()
        setMenu()

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
                        binding.apply {
                            relativeLayoutNoData.isVisible = true
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
        val initialScale = getScale()
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

    private fun setMenu() {
        addMenuHost(R.menu.menu_event_society_des, { menu ->
            menu.findItem(R.id.menu_insta).isVisible = event?.ins_link?.isNotBlank() ?: false
            menu.findItem(R.id.menu_link).isVisible = event?.web_link?.isNotBlank() ?: false
            menu.findItem(R.id.menu_share).isVisible = event != null
        }) {
            when (it.itemId) {
                android.R.id.home -> customAction()
                R.id.menu_insta -> {
                    event?.ins_link?.openLinks(requireActivity(), R.string.no_intent_available)
                    true
                }
                R.id.menu_link -> {
                    requireContext().openCustomChromeTab(event?.web_link!!)
                    true
                }
                R.id.menu_share -> {
                    shareEvent()
                    true
                }
                else -> false
            }
        }
    }


    private fun shareEvent() {
        Toast.makeText(requireContext(), "Loading ...", Toast.LENGTH_SHORT).show()
        event?.let { event ->
            if (isNetConnect) {
                activity?.openShareImageDeepLink(
                    requireContext(),
                    event.event_title,
                    event.path,
                    event.poster_link
                )
            } else {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.no_internet_detected, "Event"),
                    Toast.LENGTH_SHORT
                ).show()
                requireActivity().openShareDeepLink(
                    event.event_title,
                    event.path
                )
            }
        }
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


    @Suppress("Deprecation")
    private fun getScale(contentWidth: Double = 400.0): Int {
        return if (this.activity != null) {
            val displayMetrics = DisplayMetrics()
            this.requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
            val `val` = displayMetrics.widthPixels / contentWidth * 100.0
            `val`.toInt()
        } else {
            100
        }
    }

    private fun setIsConnected() {
        connectionManager.isConnected.observe(viewLifecycleOwner) {
            isNetConnect = it
        }
    }
}