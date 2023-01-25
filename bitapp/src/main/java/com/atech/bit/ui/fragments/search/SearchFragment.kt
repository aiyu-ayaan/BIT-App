package com.atech.bit.ui.fragments.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.atech.bit.NavGraphDirections
import com.atech.bit.ui.fragments.search.components.SearchScreen
import com.atech.core.data.room.syllabus.SyllabusModel
import com.atech.core.utils.navigateToDestination
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private lateinit var composeView: ComposeView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        ComposeView(requireContext()).also { composeView = it }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        composeView.setContent {
            Mdc3Theme {
                Surface(Modifier.fillMaxSize()) {
                    SearchScreen(modifier = Modifier) {
                        setOnSyllabusClickListener(it)
                    }
                }
            }
        }
    }


    private fun setOnSyllabusClickListener(syllabusModel: SyllabusModel) {
        val action = NavGraphDirections.actionGlobalSubjectHandlerFragment(syllabusModel)
        navigateToDestination(this, action)
    }


}