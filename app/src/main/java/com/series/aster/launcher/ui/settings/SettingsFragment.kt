package com.series.aster.launcher.ui.settings

import android.content.Intent
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.Scene
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat.recreate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
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
import kotlinx.coroutines.launch
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
        binding.gesturesLockSwitchCompat.isChecked = preferenceHelper.tapLockScreen
        binding.gesturesNotificationSwitchCompat.isChecked = preferenceHelper.swipeNotification
        binding.gesturesSearchSwitchCompat.isChecked = preferenceHelper.swipeSearch
        binding.darkThemesSwitchCompat.isChecked = preferenceHelper.darkThemes
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
            //transition()
        }

        binding.timeSwitchCompat.setOnCheckedChangeListener { _, isChecked ->
            preferenceViewModel.setShowTime(isChecked)
            //transition()
        }

        binding.dateSwitchCompat.setOnCheckedChangeListener { _, isChecked ->
            preferenceViewModel.setShowDate(isChecked)
            //transition()
        }
        binding.batterySwitchCompat.setOnCheckedChangeListener { _, isChecked ->
            preferenceViewModel.setShowBattery(isChecked)
            //transition()
        }
        binding.gesturesLockSwitchCompat.setOnCheckedChangeListener { _, isChecked ->
            appHelper.enableAppAsAccessibilityService(
                requireContext(),
                preferenceHelper.tapLockScreen
            )
            preferenceViewModel.setDoubleTapLock(isChecked)
            //transition()
        }
        binding.gesturesNotificationSwitchCompat.setOnCheckedChangeListener { _, isChecked ->
            preferenceViewModel.setSwipeNotification(isChecked)
            //transition()
        }
        binding.gesturesSearchSwitchCompat.setOnCheckedChangeListener { _, isChecked ->
            preferenceViewModel.setSwipeSearch(isChecked)
            //transition()
        }
        binding.darkThemesSwitchCompat.setOnCheckedChangeListener { _, isChecked ->
            preferenceViewModel.setDarkThemes(isChecked)
            transition()
            appHelper.darkThemes(preferenceHelper.darkThemes)
        }

    }

    private fun transition() {
        lifecycleScope.launch {
            performTransition()
            requireActivity().recreate()
        }
    }

    private fun performTransition() {

        // Before the UI change, define a Scene
        val newScene = Scene.getSceneForLayout(
            binding.root as ViewGroup,
            R.layout.activity_mock,
            requireActivity()
        )
        // Use TransitionManager to achieve smooth transition
        TransitionManager.go(newScene, AutoTransition())

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
