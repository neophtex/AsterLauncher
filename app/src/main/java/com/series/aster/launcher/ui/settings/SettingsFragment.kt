package com.series.aster.launcher.ui.settings

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.series.aster.launcher.R
import com.series.aster.launcher.databinding.FragmentSettingsBinding
import com.series.aster.launcher.helper.AppHelper
import com.series.aster.launcher.helper.PreferenceHelper
import com.series.aster.launcher.listener.ScrollEventListener
import com.series.aster.launcher.ui.bottomsheetdialog.AlignmentBottomSheetDialogFragment
import com.series.aster.launcher.ui.bottomsheetdialog.ColorBottomSheetDialogFragment
import com.series.aster.launcher.ui.bottomsheetdialog.TextBottomSheetDialogFragment
import com.series.aster.launcher.viewmodel.PreferenceViewModel
import dagger.hilt.android.AndroidEntryPoint
import net.mm2d.color.chooser.ColorChooserDialog
import net.mm2d.color.chooser.ColorChooserDialog.TAB_HSV
import net.mm2d.color.chooser.ColorChooserDialog.TAB_PALETTE
import javax.inject.Inject


@AndroidEntryPoint
class SettingsFragment : Fragment(), ScrollEventListener {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    //private val viewModel: AppViewModel by viewModels()
    private val preferenceViewModel: PreferenceViewModel by viewModels()

    @Inject
    lateinit var preferenceHelper: PreferenceHelper

    @Inject
    lateinit var appHelper: AppHelper

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        _binding = binding

        return binding.root
    }

    // Called after the fragment view is created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Set according to the system theme mode
        appHelper.dayNightMod(requireContext(), binding.nestScrollView)
        super.onViewCreated(view, savedInstanceState)

        initializeInjectedDependencies()
        observeClickListener()
    }

    private fun initializeInjectedDependencies() {
        navController = findNavController()
        binding.nestScrollView.scrollEventListener = this

        // Set initial values and listeners for switches
        binding.statueBarSwitchCompat.isChecked = preferenceHelper.showStatusBar
        binding.timeSwitchCompat.isChecked = preferenceHelper.showTime
        binding.dateSwitchCompat.isChecked = preferenceHelper.showDate
        binding.batterySwitchCompat.isChecked = preferenceHelper.showBattery
        binding.gesturesLockSwitchCompat1.isChecked = preferenceHelper.tapLockScreen
    }

    private fun observeClickListener() {
        setupSwitchListeners()

        // Click listener for reset default launcher
        binding.setLauncherSelector.setOnClickListener {
            appHelper.resetDefaultLauncher(requireContext())
        }

        binding.favoriteText.setOnClickListener {
            findNavController().navigate(R.id.action_SettingsFragment_to_FavoriteFragment)
        }

        binding.hiddenText.setOnClickListener {
            findNavController().navigate(R.id.action_SettingsFragment_to_HiddenFragment)
        }

        binding.setAppWallpaper.setOnClickListener {
            val it = Intent(Intent.ACTION_SET_WALLPAPER)
            startActivity(Intent.createChooser(it, "Select Wallpaper"))
        }

        binding.selectAppearanceTextSize.setOnClickListener {
            val bottomSheetFragment = TextBottomSheetDialogFragment(this.requireContext())
            bottomSheetFragment.show(parentFragmentManager, "BottomSheetDialog")
        }

        binding.selectAppearanceAlignment.setOnClickListener {
            val bottomSheetFragment = AlignmentBottomSheetDialogFragment(this.requireContext())
            bottomSheetFragment.show(parentFragmentManager, "BottomSheetDialog")
        }

        binding.selectAppearanceColor.setOnClickListener {
            val bottomSheetFragment = ColorBottomSheetDialogFragment(this.requireContext())
            bottomSheetFragment.show(parentFragmentManager, "BottomSheetDialog")
        }

        binding.rateView.setOnClickListener {
            appHelper.rateApp(requireContext())
        }

        binding.shareView.setOnClickListener {
            appHelper.shareApp(requireContext())
        }

        binding.githubView.setOnClickListener {
            appHelper.github(requireContext())
        }

        binding.feedbackView.setOnClickListener {
            appHelper.feedback(requireContext())
        }

    }

    private fun setupSwitchListeners() {
        binding.statueBarSwitchCompat.setOnCheckedChangeListener { _, isChecked ->
            preferenceViewModel.setShowStatusBar(isChecked)
        }

        binding.timeSwitchCompat.setOnCheckedChangeListener { _, isChecked ->
            preferenceViewModel.setShowTime(isChecked)
        }

        binding.dateSwitchCompat.setOnCheckedChangeListener { _, isChecked ->
            preferenceViewModel.setShowDate(isChecked)
        }
        binding.batterySwitchCompat.setOnCheckedChangeListener { _, isChecked ->
            preferenceViewModel.setShowBattery(isChecked)
        }
        binding.gesturesLockSwitchCompat1.setOnCheckedChangeListener { _, isChecked ->
            appHelper.enableAppAsAccessibilityService(requireContext(), preferenceHelper.tapLockScreen)
            preferenceViewModel.setDoubleTapLock(isChecked)
        }
    }

    override fun onTopReached() {
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    override fun onBottomReached() {
        Log.d("Tag", "onBottomReached")
    }

    override fun onScroll(isTopReached: Boolean, isBottomReached: Boolean) {
        Log.d("Tag", "onScroll")
    }

}
