package com.series.aster.launcher.listener

import com.series.aster.launcher.data.entities.AppInfo

class OnItemClickedListener {

    interface OnAppsClickedListener{
        fun onAppClicked(appInfo: AppInfo)
    }

    interface OnAppLongClickedListener{
        fun onAppLongClicked(appInfo: AppInfo)
    }
    interface BottomSheetDismissListener {
        fun onBottomSheetDismissed()
    }
    interface OnAppStateClickListener{
        fun onAppStateClicked(appInfo: AppInfo)
    }
}