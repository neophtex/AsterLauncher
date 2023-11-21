package com.series.aster.launcher

import android.app.Application
import android.view.WindowManager
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class Application: Application() {
    override fun onCreate() {
        super.onCreate()
    }
}