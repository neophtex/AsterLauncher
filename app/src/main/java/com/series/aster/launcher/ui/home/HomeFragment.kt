package com.series.aster.launcher.ui.home

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.series.aster.launcher.R
import com.series.aster.launcher.accessibility.MyAccessibilityService
import com.series.aster.launcher.data.entities.AppInfo
import com.series.aster.launcher.databinding.FragmentHomeBinding
import com.series.aster.launcher.helper.AppHelper
import com.series.aster.launcher.helper.FingerprintHelper
import com.series.aster.launcher.helper.PreferenceHelper
import com.series.aster.launcher.listener.OnItemClickedListener
import com.series.aster.launcher.listener.OnSwipeTouchListener
import com.series.aster.launcher.listener.ScrollEventListener
import com.series.aster.launcher.ui.activities.SettingsActivity
import com.series.aster.launcher.ui.bottomsheetdialog.AppInfoBottomSheetFragment
import com.series.aster.launcher.viewmodel.AppViewModel
import com.series.aster.launcher.viewmodel.PreferenceViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
@AndroidEntryPoint
class HomeFragment() : Fragment(), OnItemClickedListener.OnAppsClickedListener,
    OnItemClickedListener.OnAppLongClickedListener,
    OnItemClickedListener.BottomSheetDismissListener,
    OnItemClickedListener.OnAppStateClickListener,
    FingerprintHelper.Callback, ScrollEventListener {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @Inject
    lateinit var preferenceHelper: PreferenceHelper

    @Inject
    lateinit var appHelper: AppHelper

    @Inject
    lateinit var fingerHelper: FingerprintHelper

    private val viewModel: AppViewModel by viewModels()
    private val preferenceViewModel: PreferenceViewModel by viewModels()

    private val homeAdapter: HomeAdapter by lazy { HomeAdapter(this, this, preferenceHelper) }

    private lateinit var batteryReceiver: BroadcastReceiver

    private lateinit var context: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeInjectedDependencies()
        setupBattery()
        setupRecyclerView()
        observeUserInterfaceSettings()

    }

    private fun initializeInjectedDependencies() {
        context = requireContext()

        binding.nestScrollView.scrollEventListener = this

        binding.nestScrollView.registerRecyclerView(binding.appListAdapter, this)

        preferenceViewModel.setShowTime(preferenceHelper.showTime)
        preferenceViewModel.setShowDate(preferenceHelper.showDate)
        preferenceViewModel.setShowDailyWord(preferenceHelper.showDailyWord)

        binding.mainView.setOnTouchListener(getSwipeGestureListener(context))

        binding.clock.setOnClickListener { appHelper.launchClock(context) }
        binding.date.setOnClickListener { appHelper.launchCalendar(context) }
    }

    private fun setupBattery() {
        batteryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

                val batteryLevel = level * 100 / scale.toFloat()
                val batteryLevelText = getString(R.string.battery_level, batteryLevel.toString())
                binding.battery.text = batteryLevelText
            }
        }

        val batteryIntentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        requireActivity().registerReceiver(batteryReceiver, batteryIntentFilter)
    }

    private fun setupRecyclerView() {

        val marginTopInPixels = 128
        val params: ViewGroup.MarginLayoutParams = binding.appListAdapter.layoutParams as ViewGroup.MarginLayoutParams
        params.topMargin = marginTopInPixels
        binding.appListAdapter.layoutParams = params


        binding.appListAdapter.apply {
            adapter = homeAdapter
            setHasFixedSize(false)
            layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            //itemAnimator = false
            isNestedScrollingEnabled = false
        }
    }

    private fun observeFavoriteAppList() {
        viewModel.compareInstalledAppInfo()

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.favoriteApps.flowOn(Dispatchers.Main).collect {
                homeAdapter.submitList(it)
                homeAdapter.updateDataWithStateFlow(it)
            }
        }
    }

    private fun observeUserInterfaceSettings() {
        preferenceViewModel.setShowTime(preferenceHelper.showTime)
        preferenceViewModel.setShowDate(preferenceHelper.showDate)
        preferenceViewModel.setShowDailyWord(preferenceHelper.showDailyWord)
        preferenceViewModel.setShowBattery(preferenceHelper.showBattery)

        preferenceViewModel.showTimeLiveData.observe(viewLifecycleOwner) {
            Log.d("Tag", "ShowTime Home: $it")
            //updateViewVisibility(binding.clock, showTime)
            appHelper.updateUI(binding.clock,
                preferenceHelper.homeTimeAlignment,
                preferenceHelper.timeColor,
                preferenceHelper.timeTextSize,
                preferenceHelper.showTime
            )
        }

        preferenceViewModel.showDateLiveData.observe(viewLifecycleOwner) {
            appHelper.updateUI(binding.date,
                preferenceHelper.homeDateAlignment,
                preferenceHelper.dateColor,
                preferenceHelper.dateTextSize,
                preferenceHelper.showDate
            )
        }
        preferenceViewModel.showBatteryLiveData.observe(viewLifecycleOwner){
            //binding.battery.setTextColor(preferenceHelper.batteryColor)
            appHelper.updateUI(binding.battery, Gravity.END,
                preferenceHelper.batteryColor,
                16.0f,
                preferenceHelper.showBattery
            )
        }

        preferenceViewModel.showDailyWordLiveData.observe(viewLifecycleOwner) {
            //updateViewVisibility(binding.word, showDailyWord)
            //utils.updateUI(binding.word, preferenceHelper.homeDailyWordAlignment, preferenceHelper.dailyWordColor, preferenceHelper.showDailyWord)
        }
        binding.clock.format12Hour = "hh:mm"
        binding.clock.format24Hour = "hh:mm"
        binding.date.format12Hour = "EEE  dd MM"
        binding.date.format24Hour = "EEE  dd MM"
    }

    private fun observeBioAuthCheck(appInfo: AppInfo) {
        if (!appInfo.lock) appHelper.launchApp(context, appInfo) else fingerHelper.startFingerprintAuth(appInfo, this)
    }

    private fun showSelectedApp(appInfo: AppInfo) {
        val bottomSheetFragment = AppInfoBottomSheetFragment(appInfo)
        bottomSheetFragment.setOnBottomSheetDismissedListener(this)
        bottomSheetFragment.setOnAppStateClickListener(this)
        bottomSheetFragment.show(parentFragmentManager, "BottomSheetDialog")

    }

    private fun getSwipeGestureListener(context: Context): View.OnTouchListener {
        return object : OnSwipeTouchListener(context) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()
            }

            override fun onSwipeRight() {
                super.onSwipeRight()
            }

            override fun onSwipeUp() {
                super.onSwipeUp()
            }

            override fun onSwipeDown() {
                super.onSwipeDown()
            }

            override fun onLongClick() {
                super.onLongClick()
                val intent = Intent(requireActivity(), SettingsActivity::class.java)
                requireActivity().startActivity(intent)
                return
            }

            override fun onDoubleClick() {
                super.onDoubleClick()
                if(preferenceHelper.tapLockScreen) { MyAccessibilityService.instance()?.lockScreen() } else { return }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        requireActivity().unregisterReceiver(batteryReceiver)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        observeUserInterfaceSettings()
        observeFavoriteAppList()
    }

    override fun onAppClicked(appInfo: AppInfo) {
        observeBioAuthCheck(appInfo)
    }

    override fun onAppLongClicked(appInfo: AppInfo) {
        showSelectedApp(appInfo)
        Log.d("Tag", "Home LiveData Favorite : ${appInfo.favorite}")
    }

    override fun onBottomSheetDismissed() {
    }

    override fun onAppStateClicked(appInfo: AppInfo) {
        viewModel.update(appInfo)
        Log.d("Tag", "${appInfo.appName} : Home Favorite: ${appInfo.favorite}")
    }

    override fun onAuthenticationSucceeded(appInfo: AppInfo) {
        appHelper.launchApp(context, appInfo)
    }

    override fun onAuthenticationFailed() {
        appHelper.showToast(context, getString(R.string.authentication_failed))
    }

    override fun onAuthenticationError(errorCode: Int, errorMessage: CharSequence?) {
        appHelper.showToast(context, getString(R.string.authentication_error))
    }

    override fun onTopReached() {
        if (preferenceHelper.swipeNotification) appHelper.expandNotificationDrawer(context)
    }

    override fun onBottomReached() {
        if(preferenceHelper.swipeSearch) appHelper.searchView(context)
    }

    override fun onScroll(isTopReached: Boolean, isBottomReached: Boolean) {
        Log.d("Tag", "onScroll")
    }
}