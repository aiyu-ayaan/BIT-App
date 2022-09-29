package com.atech.bit.ui.fragments.course.view_syllabus

import android.os.Bundle
import android.text.Html
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.atech.bit.R
import com.atech.bit.databinding.FragmentViewSyllabusBinding
import com.atech.bit.utils.loadAdds
import com.atech.core.utils.REQUEST_VIEW_LAB_SYLLABUS
import com.atech.core.utils.loadImage
import com.google.android.gms.ads.AdView
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewSyllabusFragment : Fragment(R.layout.fragment_view_syllabus) {

    private val binding: FragmentViewSyllabusBinding by viewBinding()
    private val arg: ViewSyllabusFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val subject = arg.subject
        binding.root.transitionName = subject.subjectName
        binding.apply {
            headingTextView.text = subject.subjectName
            setContent(arg.type)
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

    private fun setContent(type: String) = binding.contentLinearLayout.run {
        if (type == REQUEST_VIEW_LAB_SYLLABUS) arg.subject.labContent?.forEach {
            this.addViews(
                R.layout.layout_syllabus_lab_content, it
            ) { content, view ->
                view.findViewById<TextView>(R.id.question_text_view).text =
                    Html.fromHtml(content.question, Html.FROM_HTML_MODE_COMPACT)
                view.findViewById<ImageView>(R.id.question_image_view).apply {
                    isVisible = content.image != null
                    content.image?.loadImage(
                        this@run,
                        view.findViewById(R.id.question_image_view),
                        null,
                        errorImage = R.drawable.ic_running_error
                    )
                    val adsView = view.findViewById<AdView>(R.id.adViewSyllabusLabContent)
                    requireContext().loadAdds(adsView)
                }
            }
        }
        else arg.subject.theoryContents?.forEach {
            this.addViews(
                R.layout.layout_syllabus_content,
                it,
            ) { content, view ->
                val moduleTextView = view.findViewById<TextView>(R.id.module_text_view)
                val contentTextView = view.findViewById<TextView>(R.id.content_text_view)
                val adsView = view.findViewById<AdView>(R.id.adViewSyllabusContent)
                moduleTextView.text = content.module
                contentTextView.text = Html.fromHtml(content.content, Html.FROM_HTML_MODE_COMPACT)
                requireContext().loadAdds(adsView)
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