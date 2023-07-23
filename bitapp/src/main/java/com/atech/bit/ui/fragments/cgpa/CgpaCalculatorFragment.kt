package com.atech.bit.ui.fragments.cgpa

import android.os.Bundle
import android.util.Log
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.atech.bit.R
import com.atech.bit.databinding.FragmentCgpaBinding
import com.atech.core.datastore.Cgpa
import com.atech.core.datastore.DataStoreCases
import com.atech.core.firebase.auth.AuthUseCases
import com.atech.core.firebase.auth.UpdateDataType
import com.atech.core.utils.TAGS
import com.atech.theme.Axis
import com.atech.theme.BaseFragment
import com.atech.theme.ToolbarData
import com.atech.theme.enterTransition
import com.atech.theme.launchWhenCreated
import com.atech.theme.launchWhenStarted
import com.atech.theme.set
import com.atech.theme.showSnackBar
import com.atech.theme.toast
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "CgpaCalculatorFragment"

@AndroidEntryPoint
class CgpaCalculatorFragment : BaseFragment(R.layout.fragment_cgpa, Axis.Y) {
    private val binding: FragmentCgpaBinding by viewBinding()

    @Inject
    lateinit var prefManager: DataStoreCases

    @Inject
    lateinit var authUseCases: AuthUseCases

    private var cgpa: Cgpa = Cgpa()
    private lateinit var course: String
    private lateinit var cgpaAdapter: CgpaAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            setRecyclerView()
            handleSave()
            setToolbar()
        }
        getPref()
    }

    private fun FragmentCgpaBinding.handleSave() {
        buttonCalculate.setOnClickListener {
            Log.d(TAG, "handleSave: ${cgpaAdapter.getAllSemesterValues()}")
            cgpaAdapter.getAllSemesterValues().let {
                if (it.isNotEmpty()) mapToDouble(it)
            }
        }
    }

    private fun mapToDouble(values: List<Pair<String, String>>) {
        val sgpaList = values.map { it.first.toDouble() to it.second.toDouble() }
        val cgpa = sgpaList.calculateCgpa()
        val cgpaModel = sgpaList.toCgpaModel()
        saveToDataStore(cgpaModel.toCgpa(cgpa))
    }

    private fun FragmentCgpaBinding.setRecyclerView() = this.rvCgpa.apply {
        adapter = CgpaAdapter().also { cgpaAdapter = it }
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun clearSavedCgpa() = launchWhenCreated {
        val d = Cgpa()
        prefManager.updateCgpa.invoke(d)
        updateCGPAToDb(d)
        showUndoMessage(cgpa)
    }

    private fun showUndoMessage(cgpa: Cgpa) {
        Log.d(TAG, "showUndoMessage: $cgpa")
        binding.root.showSnackBar(
            getString(com.atech.theme.R.string.cgpa_calculator_clear_message),
            Snackbar.LENGTH_LONG,
            getString(com.atech.theme.R.string.undo)
        ) {
            saveClearCgpa(cgpa)
        }
    }

    private fun saveClearCgpa(cgpa: Cgpa) = launchWhenCreated {
        prefManager.updateCgpa.invoke(cgpa)
        updateCGPAToDb(cgpa)
    }

    private fun updateCGPAToDb(cgpa: Cgpa) {
        authUseCases.uploadData.invoke(
            UpdateDataType.Cgpa(
                cgpa
            )
        ) {
            if (it != null) {
                toast("Something went wrong to upload cgpa")
                Log.e(TAGS.BIT_ERROR.name, "updateCGPAToDb: $it")
            }
        }
    }

    private fun getPref() = launchWhenStarted {
        prefManager.getAll.invoke().observe(viewLifecycleOwner) {
            if (!it.cgpa.isAllZero)
                cgpa = it.cgpa
            course = it.course
            binding.textViewCourseSem.text =
                resources.getString(com.atech.theme.R.string.course_sem_text, course, it.sem)
            cgpaAdapter.list = it.cgpa.toPair().take(it.sem.toInt())
            binding.outlinedTextOverAll.apply {
                visibility = if (cgpa.cgpa != 1.0) View.VISIBLE else View.INVISIBLE
                editText?.setText(cgpa.cgpa.toString())
            }
        }

    }

    private fun saveToDataStore(
        cgpa: Cgpa
    ) = launchWhenStarted {
        prefManager.updateCgpa.invoke(cgpa)
        updateCGPAToDb(cgpa)
    }

    private fun Cgpa.toPair() = listOf(
        sem1 to earnCrSem1,
        sem2 to earnCrSem2,
        sem3 to earnCrSem3,
        sem4 to earnCrSem4,
        sem5 to earnCrSem5,
        sem6 to earnCrSem6
    )

    private fun FragmentCgpaBinding.setToolbar() = this.toolbar.apply {
        set(
            ToolbarData(
                title = com.atech.theme.R.string.cgpa_calculator,
                action = findNavController()::navigateUp,
                endIcon = com.atech.theme.R.drawable.ic_delete_all,
                endAction = ::clearSavedCgpa
            )
        )
    }

    data class CgpaPair(
        val sem1: Pair<Double, Double> = 0.0 to 0.0,
        val sem2: Pair<Double, Double> = 0.0 to 0.0,
        val sem3: Pair<Double, Double> = 0.0 to 0.0,
        val sem4: Pair<Double, Double> = 0.0 to 0.0,
        val sem5: Pair<Double, Double> = 0.0 to 0.0,
        val sem6: Pair<Double, Double> = 0.0 to 0.0,
    )

    private fun CgpaPair.toCgpa(cgpa: Double) = Cgpa(
        sem1.first,
        sem2.first,
        sem3.first,
        sem4.first,
        sem5.first,
        sem6.first,
        cgpa,
        sem1.second,
        sem2.second,
        sem3.second,
        sem4.second,
        sem5.second,
        sem6.second
    )
}