package com.atech.bit.ui.fragments.view_video

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.media3.common.MediaItem
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.findNavController
import com.atech.bit.R
import com.atech.bit.databinding.FragmentViewVideoBinding
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewVideoFragment : Fragment(R.layout.fragment_view_video) {

    private val binding: FragmentViewVideoBinding by viewBinding()
    private val viewModel: VideoVideModel by viewModels()

    private var player: ExoPlayer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z,  true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z,  false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            buttonBack.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun initializePlayer() {
        viewModel.link?.let { link ->
            player = ExoPlayer.Builder(requireContext())
                .build()
                .also { exoPlayer ->
                    binding.videoView.player = exoPlayer
                    val mediaItem = MediaItem.fromUri(link)
                    exoPlayer.setMediaItem(mediaItem)
                    exoPlayer.playWhenReady = viewModel.playWhenReady
                    exoPlayer.seekTo(viewModel.currentItem, viewModel.playbackPosition)
                    exoPlayer.prepare()
                }
        }
    }


    private fun releasePlayer() {
        player?.let { exoPlayer ->
            viewModel.playbackPosition = exoPlayer.currentPosition
            viewModel.currentItem = exoPlayer.currentMediaItemIndex
            viewModel.playWhenReady = exoPlayer.playWhenReady
            exoPlayer.release()
        }
        player = null
    }


    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    override fun onResume() {
        super.onResume()
        if (player == null)
            initializePlayer()
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23)
            releasePlayer()
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }
}