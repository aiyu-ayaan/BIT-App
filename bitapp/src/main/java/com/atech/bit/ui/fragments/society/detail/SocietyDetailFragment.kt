package com.atech.bit.ui.fragments.society.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.atech.bit.R
import com.atech.bit.databinding.FragmentSocietyDetailBinding
import com.atech.theme.Axis
import com.atech.theme.ToolbarData
import com.atech.theme.customBackPress
import com.atech.theme.enterTransition
import com.atech.theme.getColorForText
import com.atech.theme.getColorFromAttr
import com.atech.theme.getRgbFromHex
import com.atech.theme.loadCircular
import com.atech.theme.openLinks
import com.atech.theme.set
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SocietyDetailFragment : Fragment(R.layout.fragment_society_detail) {
    private lateinit var handler: Handler
    private lateinit var insta: String

    private val args: SocietyDetailFragmentArgs by navArgs()
    private val binding: FragmentSocietyDetailBinding by viewBinding()

    private val society by lazy { args.society }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition(Axis.X)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        binding.apply {
            setToolbar()
            setSociety()
            setHandler()
            customBackPress { customAction() }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setSociety() = binding.apply {
        val des = society.des
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
            val initialScale = this@SocietyDetailFragment.getScale()
            setInitialScale(initialScale)
            loadData(
                body, "text/html; charset=utf-8", "utf-8"
            )
        }

        binding.showContent.settings.javaScriptEnabled = true

        societyImage.apply {
            loadCircular(society.logo)
            setOnClickListener {
                // FIXME: Implement image view
            }

        }
    }

    private fun setHandler() {
        handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            binding.apply {
                showContent.isVisible = true
                progressBarLoading.isVisible = false
            }
        }, resources.getInteger(com.atech.theme.R.integer.loading_date).toLong())
    }

    private fun customAction(): Boolean {
        binding.apply {
            showContent.isVisible = false
        }
        findNavController().navigateUp()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }


    private fun FragmentSocietyDetailBinding.setToolbar() = this.toolbar.apply {
        set(ToolbarData(titleString = society.name, action = {
            findNavController().navigateUp()
        }, endIcon = com.atech.theme.R.drawable.ic_instagram, endAction = {
            society.ins.openLinks(
                requireActivity(), com.atech.theme.R.string.no_intent_available
            )
        }))
    }

    @Suppress("DEPRECATION")
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

}