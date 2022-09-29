package com.atech.bit.ui.fragments.event.event_detail

import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.atech.bit.NavGraphDirections
import com.atech.bit.R
import com.atech.bit.databinding.FragmentEventDetailBinding
import com.atech.bit.ui.activity.main_activity.viewmodels.ConnectionManagerViewModel
import com.atech.bit.ui.fragments.notice.ImageGridAdapter
import com.atech.bit.utils.addMenuHost
import com.atech.bit.utils.getDate
import com.atech.bit.utils.loadAdds
import com.atech.bit.utils.openShareDeepLink
import com.atech.core.data.network.notice.Attach
import com.atech.core.data.ui.events.Events
import com.atech.core.data.ui.notice.SendNotice3
import com.atech.core.utils.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import javax.inject.Inject


@AndroidEntryPoint
class EventDetailFragment : Fragment(R.layout.fragment_event_detail) {

    private val binding: FragmentEventDetailBinding by viewBinding()
    private val viewModel: EventDetailViewModel by viewModels()
    private val arg: EventDetailFragmentArgs by navArgs()
    private var isEmpty: Boolean = false
    private var hasAttach = false
    private lateinit var attach: List<Attach>
    private var isNetConnect = true
    private val connectionManager: ConnectionManagerViewModel by activityViewModels()
    private lateinit var event: Events

    @Inject
    lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arg.request == REQUEST_EVENT_FROM_HOME) {
            enterTransition = MaterialSharedAxis(MaterialSharedAxis.X,  true)
            returnTransition = MaterialSharedAxis(MaterialSharedAxis.X,  false)
        } else sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.fragment
            duration = resources.getInteger(R.integer.duration_medium).toLong()
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(Color.TRANSPARENT)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.transitionName = viewModel.path
        setIsConnected()
        getEvent(viewModel.path)
        menuHost()
        detectScroll()

        requireContext().loadAdds(binding.adView)
    }


    private fun getEvent(path: String) = lifecycleScope.launchWhenCreated {
        viewModel.getEvent(path).combine(viewModel.getAttach(path)) { event, attachs ->
            FullEvent(event, attachs)
        }.collect { fullEvent ->
            when (fullEvent.event) {
                DataState.Empty -> {

                }
                is DataState.Error -> {
                    if (fullEvent.event.exception is NoItemFoundException) {
                        binding.imageViewNoData.isVisible = true
                        binding.progressBarLinear.isVisible = false
                        isEmpty = true
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "${fullEvent.event.exception}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                DataState.Loading -> {

                }
                is DataState.Success -> {
                    event = fullEvent.event.data
                    setView(fullEvent.event.data)
                }
            }

            when (fullEvent.attach) {
                DataState.Empty -> {}
                is DataState.Error -> {
                    if (fullEvent.attach.exception is NoItemFoundException) {
                        binding.attachmentRecyclerView.isVisible = false
                        binding.textImage.isVisible = false
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "${fullEvent.attach.exception}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                DataState.Loading -> {

                }
                is DataState.Success -> {
                    hasAttach = fullEvent.attach.data.isNotEmpty()
                    binding.textImage.isVisible = hasAttach
                    attach = fullEvent.attach.data
                    setAttach(fullEvent.attach.data)
                }
            }
        }
    }

    private fun setAttach(data: List<Attach>) = binding.apply {
        val imageGridAdapter = ImageGridAdapter {
            navigateToImageView(it)
        }
        attachmentRecyclerView.apply {
            isVisible = true
            layoutManager = GridLayoutManager(
                requireContext(),
                MAX_SPAWN
            ).apply {
                spanSizeLookup = imageGridAdapter.variableSpanSizeLookup
            }
            adapter = imageGridAdapter
            imageGridAdapter.submitList(attachments = data)
        }
    }

    private fun setView(data: Events) = binding.apply {
        progressBarLinear.isVisible = false
        cardViewEvent.isVisible = true
        subjectTextView.text = data.title
        senderTextView.text = data.society
        bodyTextView.text = data.content
        textViewDate.text = data.created.getDate()
        linkIcon.apply {
            isVisible = data.insta_link.isNotEmpty()
            setOnClickListener {
                requireActivity().openCustomChromeTab(data.insta_link)
            }
        }
        data.logo_link.loadImageCircular(
            binding.root,
            senderProfileImageView,
            progressBarNoticePreview,
            R.drawable.ic_running_error
        )
        videoTextView.apply {
            isVisible = data.video_link.isNotEmpty()
            binding.progressBarThumbnail.isVisible = data.video_link.isNotEmpty()
            binding.imageViewPlay.isVisible = data.video_link.isNotEmpty()
            binding.textVideo.isVisible = data.video_link.isNotEmpty()
            try {
                if (data.video_link.isNotEmpty())
                    getThumbnail(data)
                setOnClickListener {
                    navigateToViewVideo(data.video_link)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun ImageView.getThumbnail(data: Events) = lifecycleScope.launchWhenStarted {
        val requestOptions = RequestOptions()
        requestOptions.isMemoryCacheable
        Glide.with(context).setDefaultRequestOptions(requestOptions)
            .load(data.video_link)
            .error(R.drawable.ic_ayaan_beta)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.progressBarThumbnail.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.progressBarThumbnail.visibility = View.GONE
                    return false
                }

            })
            .into(this@getThumbnail)
    }

    private fun navigateToViewVideo(videoLink: String) {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z,  true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z,  false)
        val action = NavGraphDirections.actionGlobalViewVideoFragment(videoLink)
        findNavController().navigate(action)
    }


    private fun menuHost() {
        addMenuHost(R.menu.notice_description_menu) { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_notice_share -> {
                    shareNotice()
                    true
                }
                else -> false
            }
        }
    }

    private fun shareNotice() {
        if (!isEmpty) {
            if (hasAttach && isNetConnect) {
                try {
                    val action =
                        EventDetailFragmentDirections.actionEventDetailFragmentToChooseImageBottomSheet(
                            SendNotice3(event = event, attach = attach)
                        )
                    findNavController().navigate(action)
                } catch (e: Exception) {
                    Toast.makeText(
                        requireContext(),
                        "Multiple clicks detected",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else if (!hasAttach && isNetConnect) {
                requireActivity().openShareDeepLink(
                    event.title,
                    event.path
                )
            } else {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.no_internet_detected, "Event"),
                    Toast.LENGTH_SHORT
                ).show()
                requireActivity().openShareDeepLink(
                    event.title,
                    event.path
                )
            }
        }
    }

    private fun setIsConnected() {
        connectionManager.isConnected.observe(viewLifecycleOwner) {
            isNetConnect = it
        }
    }

    private fun detectScroll() {
        activity?.onScrollColorChange(binding.nestedScrollViewEvent, {
            activity?.changeStatusBarToolbarColor(
                R.id.toolbar,
                R.attr.bottomBar
            )
        }, {
            activity?.changeStatusBarToolbarColor(
                R.id.toolbar,
                com.google.android.material.R.attr.colorSurface
            )
        })
    }


    private fun navigateToImageView(link: String) {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z,  true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z,  false)
        val action = NavGraphDirections.actionGlobalViewImageFragment(link)
        findNavController().navigate(action)
    }


    private data class FullEvent(
        val event: DataState<Events>,
        val attach: DataState<List<Attach>>
    )
}