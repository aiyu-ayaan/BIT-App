package com.aatec.bit.fragments.course.sem_choose

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.Toast
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aatec.bit.NavGraphDirections
import com.aatec.bit.R
import com.aatec.bit.activity.viewmodels.PreferenceManagerViewModel
import com.aatec.bit.databinding.FragmentSemChooseBinding
import com.aatec.bit.utils.changeStatusBarToolbarColor
import com.aatec.bit.utils.openLinks
import com.aatec.core.data.room.syllabus.SyllabusModel
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialSharedAxis
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SemChooseFragment : Fragment(R.layout.fragment_sem_choose) {

    private val viewModel: ChooseSemViewModel by viewModels()
    private val prefManagerViewModel: PreferenceManagerViewModel by activityViewModels()

    private val binding: FragmentSemChooseBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
    }

    @Inject
    lateinit var db: FirebaseFirestore
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        activity?.changeStatusBarToolbarColor(
            R.id.toolbar,
            R.attr.bottomBar
        )
        restoreScroll(binding)


        val courseTheoryAdapter = SubjectAdapter { syllabusModel, view ->
            syllabusClick(syllabusModel, view)
        }
        courseTheoryAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        val courseLabAdapter = SubjectAdapter { syllabusModel, view ->
            syllabusClick(syllabusModel, view)
        }
        courseLabAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        val coursePeAdapter = SubjectAdapter { syllabusModel, view ->
            syllabusClick(syllabusModel, view)
        }
        coursePeAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        binding.apply {
            semChoseExt.apply {
                showTheory.apply {
                    adapter = courseTheoryAdapter
                    layoutManager = LinearLayoutManager(requireContext())
                }
                showLab.apply {
                    adapter = courseLabAdapter
                    layoutManager = LinearLayoutManager(requireContext())
                }
                showPe.apply {
                    adapter = coursePeAdapter
                    layoutManager = LinearLayoutManager(requireContext())
                }
            }
        }
        viewModel.theory.observe(viewLifecycleOwner) {
            courseTheoryAdapter.submitList(it)
        }
        viewModel.lab.observe(viewLifecycleOwner) {

            binding.semChoseExt.showLab.isVisible = it.isNotEmpty()
            binding.semChoseExt.textView7.isVisible = it.isNotEmpty()
            courseLabAdapter.submitList(it)
        }
        viewModel.pe.observe(viewLifecycleOwner) {
            binding.semChoseExt.showPe.isVisible = it.isNotEmpty()
            binding.semChoseExt.textView8.isVisible = it.isNotEmpty()
            coursePeAdapter.submitList(it)
        }
        prefManagerViewModel.preferencesFlow.observe(viewLifecycleOwner) {
            viewModel.sem.value = "${viewModel.request}${it.semSyllabus}"
            buttonColorChange(it.semSyllabus, binding)
        }
        buttonClick()
        setHasOptionsMenu(true)
    }


    private fun buttonClick() {
        binding.apply {
            bt1.setOnClickListener { prefManagerViewModel.updateSemSyllabus("1") }
            bt2.setOnClickListener { prefManagerViewModel.updateSemSyllabus("2") }
            bt3.setOnClickListener { prefManagerViewModel.updateSemSyllabus("3") }
            bt4.setOnClickListener { prefManagerViewModel.updateSemSyllabus("4") }
            bt5.setOnClickListener { prefManagerViewModel.updateSemSyllabus("5") }
            bt6.setOnClickListener { prefManagerViewModel.updateSemSyllabus("6") }
        }
    }

    private fun syllabusClick(syllabusModel: SyllabusModel, view: View) {
        exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.duration_medium).toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.duration_small).toLong()
        }

        try {
            val extras = FragmentNavigatorExtras(view to syllabusModel.openCode)
            val action =
                NavGraphDirections.actionGlobalSubjectHandlerFragment(
                    syllabusModel
                )
            findNavController().navigate(action, extras)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Press one item at a time !!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_sem_choose, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_download_syllabus -> {
                getSyllabus(viewModel.request ?: "BCA")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getSyllabus(course: String) {
        db.collection("Utils")
            .document("syllabus_download")
            .addSnapshotListener { value, _ ->
                val link = value?.getString(course)
                link?.openLinks(
                    requireActivity(),
                    R.string.no_intent_available
                )
            }
    }

    /**
     * @author Ayaan
     * @since 4.0.3
     */
    private fun restoreScroll(binding: FragmentSemChooseBinding) {
        try {
            if (viewModel.chooseSemNestedViewPosition != null)
                binding.semChoseExt.nestedViewSyllabus.post {
                    binding.semChoseExt.nestedViewSyllabus.scrollTo(
                        0,
                        viewModel.chooseSemNestedViewPosition!!
                    )
                }
        } catch (e: Exception) {
            Log.e("Error", e.message!!)
        }
    }

    override fun onStop() {
        try {
            super.onStop()
            viewModel.chooseSemNestedViewPosition = binding.semChoseExt.nestedViewSyllabus.scrollY
        } catch (e: Exception) {
            Log.e("Error", e.message!!)
        }
    }

    private fun buttonColorChange(request: String, binding: FragmentSemChooseBinding) {
        when (request) {
            "1" -> binding.toggleChip.check(R.id.bt1)
            "2" -> binding.toggleChip.check(R.id.bt2)
            "3" -> binding.toggleChip.check(R.id.bt3)
            "4" -> binding.toggleChip.check(R.id.bt4)
            "5" -> binding.toggleChip.check(R.id.bt5)
            "6" -> binding.toggleChip.check(R.id.bt6)
            else -> Log.d("Error", "buttonColorChange: Error")
        }
    }
}