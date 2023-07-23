package com.atech.bit.ui.fragments.about.acknowledgements

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.atech.theme.Axis
import com.atech.theme.BaseFragment
import com.atech.theme.R
import com.atech.theme.ToolbarData
import com.atech.theme.databinding.LayoutRecyclerViewBinding
import com.atech.theme.openLinkToDefaultApp
import com.atech.theme.set

class CreditsFragment : BaseFragment(R.layout.layout_recycler_view, Axis.X) {
    private val binding: LayoutRecyclerViewBinding by viewBinding()
    private lateinit var creditsAdapter: CreditsAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            setToolbar()
            setRecyclerView()
        }
    }

    private fun LayoutRecyclerViewBinding.setRecyclerView() = this.recyclerView.apply {
        adapter = CreditsAdapter(::openLink).also { creditsAdapter = it }
        layoutManager = LinearLayoutManager(requireContext())
        creditsAdapter.list = credits
    }

    private fun LayoutRecyclerViewBinding.setToolbar() = this.includeToolbar.apply {
        set(
            ToolbarData(
                title = R.string.acknowledgements, action = findNavController()::navigateUp
            )
        )
    }

    private fun openLink(credit: Credit) {
        openLinkToDefaultApp(credit.url)
    }
}

enum class Licenses(val title: String) {
    APACHE_2_0("Apache 2.0"), MIT(
        "MIT"
    ),
    BSD("BSD"),
}

data class Credit(
    val title: String, val url: String, val licenses: List<Licenses> = listOf()
)

val credits = listOf(
    Credit(
        "Android-Viewbinding", "https://github.com/yogacp/android-viewbinding", listOf(Licenses.MIT)
    ),
    Credit(
        "Splash-Screen",
        "https://developer.android.com/develop/ui/views/launch/splash-screen",
        listOf(Licenses.APACHE_2_0)
    ),
    Credit(
        "Cryptore", "https://github.com/KazaKago/Cryptore", listOf(Licenses.MIT)
    ),
    Credit(
        "Core-Ktx",
        "https://developer.android.com/jetpack/androidx/releases/core",
        listOf(Licenses.APACHE_2_0)
    ),
    Credit(
        "Exo-Player", "https://github.com/google/ExoPlayer", listOf(Licenses.APACHE_2_0)
    ),
    Credit(
        "Glide", "https://github.com/bumptech/glide", listOf(
            Licenses.APACHE_2_0, Licenses.MIT, Licenses.BSD
        )
    ),
    Credit(
        "Hilt", "https://developer.android.com/training/dependency-injection/hilt-android",
        listOf(Licenses.APACHE_2_0)
    ),
    Credit(
        "App Compat", "https://developer.android.com/jetpack/androidx/releases/appcompat",
        listOf(Licenses.APACHE_2_0)
    ),
    Credit(
        "Constraint Layout",
        "https://developer.android.com/jetpack/androidx/releases/constraintlayout",
        listOf(Licenses.APACHE_2_0)
    ),
    Credit(
        "Fragment Ktx",
        "https://developer.android.com/jetpack/androidx/releases/fragment",
        listOf(Licenses.APACHE_2_0)
    ),
    Credit(
        "Lifecycle Ktx",
        "https://developer.android.com/jetpack/androidx/releases/lifecycle",
        listOf(Licenses.APACHE_2_0)
    ),
    Credit(
        "Material",
        "https://developer.android.com/jetpack/androidx/releases/material",
        listOf(Licenses.APACHE_2_0)
    ),
    Credit(
        "Navigation",
        "https://developer.android.com/jetpack/androidx/releases/navigation",
        listOf(Licenses.APACHE_2_0)
    ),
    Credit(
        "Compact-Calendar-View",
        "https://github.com/SundeepK/CompactCalendarView",
        listOf(Licenses.MIT)
    ),
    Credit(
        "Data-Store",
        "https://developer.android.com/topic/libraries/architecture/datastore",
        listOf(Licenses.APACHE_2_0)
    ),
    Credit(
        "Firebase",
        "https://firebase.google.com/",
        listOf(Licenses.APACHE_2_0)
    ),
    Credit(
        "Mp-Android-Chart",
        "https://github.com/PhilJay/MPAndroidChart",
        listOf(Licenses.APACHE_2_0)
    ),
    Credit(
        "Gson",
        "https://github.com/google/gson",
        listOf(Licenses.APACHE_2_0)
    ),
    Credit(
        "MarkDown-View",
        "https://github.com/mukeshsolanki/MarkdownView-Android",
        listOf(Licenses.MIT)
    ),
    Credit(
        "Recycler-View",
        "https://developer.android.com/jetpack/androidx/releases/recyclerview",
        listOf(Licenses.APACHE_2_0)
    ),
    Credit(
        "Room",
        "https://developer.android.com/jetpack/androidx/releases/room",
        listOf(Licenses.APACHE_2_0)
    ),
    Credit(
        "Retofit",
        "https://github.com/square/retrofit",
        listOf(Licenses.APACHE_2_0)
    ),
    Credit(
        "Lottie",
        "https://github.com/airbnb/lottie-android",
        listOf(Licenses.APACHE_2_0)
    ),
    Credit(
        "Browser",
        "https://developer.android.com/jetpack/androidx/releases/browser",
        listOf(Licenses.APACHE_2_0)
    ),
    Credit(
        "Palette-v7",
        "https://developer.android.com/jetpack/androidx/releases/palette",
        listOf(Licenses.APACHE_2_0)
    ),
    Credit(
        "Play-Services",
        "https://developers.google.com/android/guides/setup",
        listOf(Licenses.APACHE_2_0)
    )
).sortedBy {
    it.title
}