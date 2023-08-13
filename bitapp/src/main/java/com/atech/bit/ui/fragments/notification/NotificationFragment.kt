package com.atech.bit.ui.fragments.notification

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.navigation.fragment.findNavController
import com.atech.bit.R
import com.atech.bit.databinding.FragmentNotificationBinding
import com.atech.core.utils.SharePrefKeys
import com.atech.theme.Axis
import com.atech.theme.ToolbarData
import com.atech.theme.base_class.BaseFragment
import com.atech.theme.set
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotificationFragment : BaseFragment(R.layout.fragment_notification, Axis.Y) {
    private val binding: FragmentNotificationBinding by viewBinding()

    @Inject
    lateinit var fcm: FirebaseMessaging

    @Inject
    lateinit var pref: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            setToolbar()
            forNotice()
            forEvent()
            forApp()
        }
    }

    private fun FragmentNotificationBinding.forNotice() = this.switchNotice.apply {
        isChecked = getValue(SharePrefKeys.IsEnableNoticeNotification.name)
        setOnCheckedChangeListener { _, isChecked ->
            updateValue(SharePrefKeys.IsEnableNoticeNotification.name, isChecked)
            if (isChecked) fcm.subscribeToTopic(getString(com.atech.theme.R.string.topic_notice))
            else fcm.unsubscribeFromTopic(getString(com.atech.theme.R.string.topic_notice))
        }
    }

    private fun FragmentNotificationBinding.forEvent() = this.switchEvent.apply {
        isChecked = getValue(SharePrefKeys.IsEnableEventNotification.name)
        setOnCheckedChangeListener { _, isChecked ->
            updateValue(SharePrefKeys.IsEnableEventNotification.name, isChecked)
            if (isChecked) fcm.subscribeToTopic(getString(com.atech.theme.R.string.topic_event))
            else fcm.unsubscribeFromTopic(getString(com.atech.theme.R.string.topic_event))
        }
    }

    private fun FragmentNotificationBinding.forApp() = this.switchApp.apply {
        isChecked = getValue(SharePrefKeys.IsEnableAppNotification.name)
        setOnCheckedChangeListener { _, isChecked ->
            updateValue(SharePrefKeys.IsEnableAppNotification.name, isChecked)
            if (isChecked) fcm.subscribeToTopic(getString(com.atech.theme.R.string.topic_app))
            else fcm.unsubscribeFromTopic(getString(com.atech.theme.R.string.topic_app))
        }
    }

    private fun getValue(key: String) = pref.getBoolean(key, true)
    private fun updateValue(key: String, value: Boolean) {
        pref.edit().putBoolean(key, value).apply()
    }

    private fun FragmentNotificationBinding.setToolbar() = this.includeToolbar.apply {
        set(
            ToolbarData(
                title = com.atech.theme.R.string.notification,
                action = findNavController()::navigateUp
            )
        )
    }
}