package com.series.aster.launcher.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.series.aster.launcher.helper.PreferenceHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PreferenceViewModel @Inject constructor(
    private val preferenceHelper: PreferenceHelper)
    : ViewModel() {

    val firstLaunchLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val showStatusBarLiveData: MutableLiveData<Boolean> = MutableLiveData()

    val showTimeLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val showDateLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val showDailyWordLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val showBatteryLiveData: MutableLiveData<Boolean> = MutableLiveData()

    val homeAppAlignmentLiveData: MutableLiveData<Int> = MutableLiveData()
    val homeDateAlignmentLiveData: MutableLiveData<Int> = MutableLiveData()
    val homeTimeAlignmentLiveData: MutableLiveData<Int> = MutableLiveData()
    val homeDailyWordAlignmentLiveData: MutableLiveData<Int> = MutableLiveData()

    val dateColorLiveData: MutableLiveData<Int> = MutableLiveData()
    val timeColorLiveData: MutableLiveData<Int> = MutableLiveData()
    val batteryColorLiveData: MutableLiveData<Int> = MutableLiveData()
    val dailyWordColorLiveData: MutableLiveData<Int> = MutableLiveData()
    val appColorLiveData: MutableLiveData<Int> = MutableLiveData()

    val dateTextSizeLiveData: MutableLiveData<Float> = MutableLiveData()
    val timeTextSizeLiveData: MutableLiveData<Float> = MutableLiveData()
    val appTextSizeLiveData: MutableLiveData<Float> = MutableLiveData()


    val tapLockScreenLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val swipeNotificationLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val swipeSearchLiveData: MutableLiveData<Boolean> = MutableLiveData()

    val darkThemesLiveData: MutableLiveData<Boolean> = MutableLiveData()

    fun setFirstLaunch(firstLaunch: Boolean) {
        preferenceHelper.firstLaunch = firstLaunch
        firstLaunchLiveData.postValue(preferenceHelper.firstLaunch)
    }

    fun setShowStatusBar(showStatusBar: Boolean) {
        preferenceHelper.showStatusBar = showStatusBar
        showStatusBarLiveData.postValue(preferenceHelper.showStatusBar)
    }

    fun setShowTime(showTime: Boolean) {
        preferenceHelper.showTime = showTime
        showTimeLiveData.postValue(preferenceHelper.showTime)
    }

    fun setShowDate(showDate: Boolean) {
        preferenceHelper.showDate = showDate
        showDateLiveData.postValue(preferenceHelper.showDate)
    }

    fun setShowBattery(showBattery: Boolean){
        preferenceHelper.showBattery = showBattery
        showBatteryLiveData.postValue(preferenceHelper.showBattery)
    }

    fun setShowDailyWord(showDailyWord: Boolean) {
        preferenceHelper.showDailyWord = showDailyWord
        showDailyWordLiveData.postValue(preferenceHelper.showDailyWord)
    }

    fun setDailyWordColor(DailyWordColor: Int) {
        preferenceHelper.dailyWordColor = DailyWordColor
        dailyWordColorLiveData.postValue(preferenceHelper.dailyWordColor)
    }

    fun setAppColor(appColor: Int) {
        preferenceHelper.appColor = appColor
        appColorLiveData.postValue(preferenceHelper.appColor)
    }

    fun setDateColor(DateColor: Int) {
        preferenceHelper.dateColor = DateColor
        dateColorLiveData.postValue(preferenceHelper.dateColor)
    }

    fun setTimeColor(TimeColor: Int) {
        preferenceHelper.timeColor = TimeColor
        timeColorLiveData.postValue(preferenceHelper.timeColor)
    }

    fun setBatteryColor(BatteryColor: Int){
        preferenceHelper.batteryColor = BatteryColor
        batteryColorLiveData.postValue(preferenceHelper.batteryColor)
    }

    fun setHomeAppAlignment(homeAppAlignment: Int) {
        preferenceHelper.homeAppAlignment = homeAppAlignment
        homeAppAlignmentLiveData.postValue(preferenceHelper.homeAppAlignment)
    }

    fun setHomeDateAlignment(homeDateAlignment: Int) {
        preferenceHelper.homeDateAlignment = homeDateAlignment
        homeDateAlignmentLiveData.postValue(preferenceHelper.homeDateAlignment)
    }

    fun setHomeTimeAppAlignment(homeTimeAlignment: Int) {
        preferenceHelper.homeTimeAlignment = homeTimeAlignment
        homeTimeAlignmentLiveData.postValue(preferenceHelper.homeTimeAlignment)
    }

    fun setDateTextSize(dateTextSize: Float) {
        preferenceHelper.dateTextSize = dateTextSize
        dateTextSizeLiveData.postValue(preferenceHelper.dateTextSize)
    }

    fun setTimeTextSize(timeTextSize: Float) {
        preferenceHelper.timeTextSize = timeTextSize
        timeTextSizeLiveData.postValue(preferenceHelper.timeTextSize)
    }

    fun setAppTextSize(appTextSize: Float) {
        preferenceHelper.appTextSize = appTextSize
        appTextSizeLiveData.postValue(preferenceHelper.appTextSize)
    }

    fun setDoubleTapLock(tapLockScreen: Boolean){
        preferenceHelper.tapLockScreen = tapLockScreen
        tapLockScreenLiveData.postValue((preferenceHelper.tapLockScreen))
    }

    fun setSwipeNotification(swipeNotification: Boolean){
        preferenceHelper.swipeNotification = swipeNotification
        swipeNotificationLiveData.postValue((preferenceHelper.swipeNotification))
    }

    fun setSwipeSearch(swipeSearch: Boolean){
        preferenceHelper.swipeSearch = swipeSearch
        swipeSearchLiveData.postValue((preferenceHelper.swipeSearch))
    }

    fun setDarkThemes(darkThemes: Boolean){
        preferenceHelper.darkThemes = darkThemes
        darkThemesLiveData.postValue((preferenceHelper.darkThemes))
    }
}