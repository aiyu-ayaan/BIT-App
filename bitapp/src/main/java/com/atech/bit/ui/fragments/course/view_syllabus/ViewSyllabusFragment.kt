package com.atech.bit.ui.fragments.course.view_syllabus

import android.os.Bundle
import android.text.Html
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.atech.bit.R
import com.atech.bit.databinding.FragmentViewSyllabusBinding
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewSyllabusFragment : Fragment(R.layout.fragment_view_syllabus) {

    private val binding: FragmentViewSyllabusBinding by viewBinding()
    private val arg: ViewSyllabusFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val subject = arg.subject
        binding.root.transitionName = subject.subjectName
        binding.apply {
            headingTextView.text = subject.subjectName
            setContent()
            setBook()
        }
    }

    private fun setBook() = binding.apply {
        booksLinearLayout.run {
            arg.subject.books.textBooks.forEach {
                addViews(R.layout.layout_syllabus_book, it) { content, view ->
                    view.findViewById<TextView>(R.id.books_text_view).text = content.bookName
                }
            }
        }
        referenceLinearLayout.run {
            arg.subject.books.referenceBooks.forEach {
                addViews(R.layout.layout_syllabus_book, it) { content, view ->
                    view.findViewById<TextView>(R.id.books_text_view).text = content.bookName
                }
            }
        }
    }

    private fun setContent() = binding.contentLinearLayout.run {
        arg.subject.content.forEach {
            this.addViews(
                R.layout.layout_syllabus_content,
                it,
            ) { content, view ->
                val moduleTextView = view.findViewById<TextView>(R.id.module_text_view)
                val contentTextView = view.findViewById<TextView>(R.id.content_text_view)
                moduleTextView.text = content.module
                contentTextView.text = Html.fromHtml(content.content, Html.FROM_HTML_MODE_COMPACT)
            }
        }
    }


    private fun <T> LinearLayout.addViews(@LayoutRes id: Int, t: T, action: (T, View) -> Unit) =
        this.apply {
            val view = layoutInflater.inflate(id, this, false)
            action(t, view)
            addView(view)
        }
}