package com.atech.bit.ui.fragments.cgpa_calculator

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.viewbinding.library.fragment.viewBinding
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.atech.bit.NavGraphDirections
import com.atech.bit.R
import com.atech.bit.databinding.FragmentCgpaCalculatorBinding
import com.atech.bit.ui.activity.main_activity.viewmodels.PreferenceManagerViewModel
import com.atech.bit.utils.*
import com.atech.core.data.preferences.Cgpa
import com.atech.core.utils.*
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat

@AndroidEntryPoint
class CgpaCalculatorFragment : Fragment(R.layout.fragment_cgpa_calculator) {

    private val binding: FragmentCgpaCalculatorBinding by viewBinding()
    private val prefManager: PreferenceManagerViewModel by activityViewModels()
    private val textInputLayoutList = mutableListOf<TextInputLayout>()
    private lateinit var cgpa: Cgpa
    private lateinit var course: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            textInputLayoutList.addAll(
                listOf(
                    outlinedTextSem1,
                    outlinedTextSem2,
                    outlinedTextSem3,
                    outlinedTextSem4,
                    outlinedTextSem5,
                    outlinedTextSem6,
                )
            )
            getPref()
            buttonCalculate.setOnClickListener {
                getNumber()
            }
        }
        setMenu()
        detectScroll()
    }

    private fun detectScroll() {
        activity?.onScrollColorChange(binding.nestedScrollViewCgpa, {
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


    private fun setMenu() {
        addMenuHost(R.menu.menu_clear) {
            when (it.itemId) {
                R.id.menu_clear -> {
                    clearSavedCgpa()
                    true
                }
                R.id.menu_change_sem_course -> {
                    navigateToCoursePre()
                    true
                }
                else -> false
            }

        }
    }

    private fun navigateToCoursePre() {
        val action =
            NavGraphDirections.actionGlobalChooseSemBottomSheet(REQUEST_UPDATE_SEM_FROM_CGPA)
        findNavController().navigate(action)
    }

    private fun clearSavedCgpa() {
        prefManager.updateCgpa(Cgpa())
        binding.editTextSem1.requestFocus()
        showUndoMessage(cgpa)
    }

    private fun showUndoMessage(cgpa: Cgpa) {
        binding.root.showSnackBar(
            getString(R.string.cgpa_calculator_clear_message),
            Snackbar.LENGTH_LONG,
            getString(R.string.undo)
        ) {
            saveClearCgpa(cgpa)
        }
    }

    private fun saveClearCgpa(cgpa: Cgpa) {
        prefManager.updateCgpa(cgpa)
    }


    private fun getPref() = lifecycleScope.launchWhenStarted {
        prefManager.preferencesFlow.asFlow().collect {
            cgpa = it.cgpa
            course = it.course
            binding.textViewCourseSem.text =
                resources.getString(R.string.course_sem_text, course, it.sem)
            setButtons(it.sem.toInt())
            populateEditText(it.cgpa, it.sem.toInt())
            requestFocusToNextButton()
        }

    }

    private fun populateEditText(cgpa: Cgpa, sem: Int) = binding.apply {
        if (cgpa.sem1 != 0.0 || sem >= 1)
            outlinedTextSem1.apply {
                editTextSem1.setText(cgpa.sem1.checkInput())
                visibility = View.VISIBLE
            }
        else
            outlinedTextSem1.apply {
                visibility = View.GONE
                editTextSem1.setText("")
            }
        if (cgpa.sem2 != 0.0 || sem >= 2)
            outlinedTextSem2.apply {
                editTextSem2.setText(cgpa.sem2.checkInput())
                visibility = View.VISIBLE
            }
        else
            outlinedTextSem2.apply {
                visibility = View.GONE
                editTextSem2.setText("")
            }

        if (cgpa.sem3 != 0.0 || sem >= 3)
            outlinedTextSem3.apply {
                editTextSem3.setText(cgpa.sem3.checkInput())
                visibility = View.VISIBLE
            }
        else
            outlinedTextSem3.apply {
                visibility = View.GONE
                editTextSem3.setText("")
            }
        if (cgpa.sem4 != 0.0 || sem >= 4)
            outlinedTextSem4.apply {
                editTextSem4.setText(cgpa.sem4.checkInput())
                visibility = View.VISIBLE
            }
        else
            outlinedTextSem4.apply {
                visibility = View.GONE
                editTextSem4.setText("")
            }
        if (cgpa.sem5 != 0.0 || sem >= 5)
            outlinedTextSem5.apply {
                editTextSem5.setText(cgpa.sem5.checkInput())
                visibility = View.VISIBLE
            }
        else
            outlinedTextSem5.apply {
                visibility = View.GONE
                editTextSem5.setText("")
            }
        if (cgpa.sem6 != 0.0 || sem >= 6)
            outlinedTextSem6.apply {
                editTextSem6.setText(cgpa.sem6.checkInput())
                visibility = View.VISIBLE
            }
        else
            outlinedTextSem6.apply {
                visibility = View.GONE
                editTextSem6.setText("")
            }

        if (cgpa.cgpa != 1.0 && cgpa.cgpa != 0.0)
            outlinedTextOverAll.apply {
                editText?.setText(DecimalFormat("#0.00").format(cgpa.cgpa).toString())
                visibility = View.VISIBLE
            }
        else
            outlinedTextOverAll.apply {
                visibility = View.INVISIBLE
                editText?.setText("")
            }

    }

    private fun requestFocusToNextButton() {
        binding.apply {
            editTextSem1.setNextFocusOfFirstEditTextToAnother(outlinedTextSem2, editTextSem2)
            editTextSem2.setNextFocusOfFirstEditTextToAnother(outlinedTextSem3, editTextSem3)
            editTextSem3.setNextFocusOfFirstEditTextToAnother(outlinedTextSem4, editTextSem4)
            editTextSem4.setNextFocusOfFirstEditTextToAnother(outlinedTextSem5, editTextSem5)
            editTextSem5.setNextFocusOfFirstEditTextToAnother(outlinedTextSem6, editTextSem6)
            outlinedTextSem3.checkBothViewsHidden(linearLayoutSem34, outlinedTextSem4)
            outlinedTextSem5.checkBothViewsHidden(linearLayoutSem56, outlinedTextSem6)
        }
    }

    private fun setButtons(sem: Int) {
        var i = 0
        while (i != sem) {
            textInputLayoutList[i].isVisible = true
            i++
        }
    }

    private fun getNumber() = binding.apply {
        val num1 = outlinedTextSem1.getNumber()
        val num2 = outlinedTextSem2.getNumber()
        val num3 = outlinedTextSem3.getNumber()
        val num4 = outlinedTextSem4.getNumber()
        val num5 = outlinedTextSem5.getNumber()
        val num6 = outlinedTextSem6.getNumber()
        if (validateNumber()) return@apply

        val gradeList = addThemInList(num1, num2, num3, num4, num5, num6)
        val bcaCourseList = addGradesIntoBcaCourse(gradeList)
        val cgpa = calculateCGPA(bcaCourseList)
        saveToDataStore(num1, num2, num3, num4, num5, num6, cgpa)
        outlinedTextOverAll.apply {
            visibility = if (cgpa != 1.0) View.VISIBLE else View.INVISIBLE
            editText?.setText(DecimalFormat("#0.00").format(cgpa).toString())
        }
    }

    private fun FragmentCgpaCalculatorBinding.validateNumber(): Boolean {
        return outlinedTextSem1.setError() || outlinedTextSem2.setError() || outlinedTextSem3.setError() || outlinedTextSem4.setError() || outlinedTextSem5.setError() || outlinedTextSem6.setError() || outlinedTextOverAll.setError()
    }

    private fun saveToDataStore(
        num1: Double,
        num2: Double,
        num3: Double,
        num4: Double,
        num5: Double,
        num6: Double,
        cgpa: Double,
    ) {
        prefManager.updateCgpa(Cgpa(num1, num2, num3, num4, num5, num6, cgpa))

    }

    private fun addGradesIntoBcaCourse(gradeList: List<Double>): List<BcaCourse> {
        val courseCreditList = getCourseCreditsList(getCourse(course))
        val bcaCourse = mutableListOf<BcaCourse>()
        gradeList.forEachIndexed { index, grade ->
            bcaCourse.add(BcaCourse(courseCreditList[index].credit, grade))
        }
        return bcaCourse
    }

    private fun getCourseCreditsList(course: CourseCapital) =
        if (course == CourseCapital.BBA)
            listOfBbaCredits
        else
            listOfBcaCredits

    private fun getCourse(course: String) =
        CourseCapital.valueOf(course)


    private fun addThemInList(
        num1: Double,
        num2: Double,
        num3: Double,
        num4: Double,
        num5: Double,
        num6: Double
    ): List<Double> {
        val gradeList = mutableListOf<Double>()
        if (num1 != 0.0) gradeList.add(num1)
        if (num2 != 0.0) gradeList.add(num2)
        if (num3 != 0.0) gradeList.add(num3)
        if (num4 != 0.0) gradeList.add(num4)
        if (num5 != 0.0) gradeList.add(num5)
        if (num6 != 0.0) gradeList.add(num6)
        return gradeList
    }


    private fun TextInputLayout.getNumber() =
        if (this.editText?.text!!.isBlank()) 0.0 else this.editText!!.text.toString().toDouble()

    private fun TextInputLayout.setError(): Boolean =
        this.run {
            if (this.getNumber() > 10.0) {
                error = "Grade cannot be greater than 10.0"
                true
            } else {
                error = null
                false
            }
        }

    private fun Double.checkInput() =
        if (this == 0.0)
            ""
        else
            this.toString()

    private fun TextInputEditText.setNextFocusOfFirstEditTextToAnother(
        next: TextInputLayout,
        editText: TextInputEditText
    ) =
        this.apply {
            if (next.isVisible) {
                setOnEditorActionListener { _, _, _ ->
                    editText.requestFocus()
                    false
                }
                imeOptions = EditorInfo.IME_ACTION_NEXT
            }
        }

    private fun TextInputLayout.checkBothViewsHidden(parent: LinearLayout, child: TextInputLayout) {
        parent.isVisible = this.isVisible || child.isVisible
    }
}