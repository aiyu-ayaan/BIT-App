package com.atech.bit.ui.fragments.warning

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.atech.bit.BuildConfig
import com.atech.bit.NavGraphDirections
import com.atech.bit.R
import com.atech.bit.databinding.FragmentWarningBinding
import com.atech.core.utils.openPlayStore
import com.google.android.material.transition.MaterialSharedAxis
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.system.exitProcess

@AndroidEntryPoint
class WarningFragment : Fragment(R.layout.fragment_warning) {
    private val binding: FragmentWarningBinding by viewBinding()
    private val args: WarningFragmentArgs by navArgs()

    @Inject
    lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            textViewWarningContent.text = args.title
            buttonOpenApp.setOnClickListener {
                requireActivity().openPlayStore(args.link)
            }
            buttonQuit.setOnClickListener {
                exitProcess(0)
            }

        }
        getWarning()
    }

    private fun getWarning() {
        db.collection("Utils")
            .document("Warning")
            .addSnapshotListener { value, _ ->
                val isEnable = value?.getBoolean("isEnable")
                val minVersion = value?.getDouble("minVersion")
                val isMinEdition = BuildConfig.VERSION_CODE > minVersion!!.toInt()
                isEnable?.let { it ->
                    if (!it || isMinEdition)
                        navigateToHome()
                }
            }
    }

    private fun navigateToHome() {
        try {
            val action = NavGraphDirections.actionGlobalHomeFragment()
            findNavController().navigate(action)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}