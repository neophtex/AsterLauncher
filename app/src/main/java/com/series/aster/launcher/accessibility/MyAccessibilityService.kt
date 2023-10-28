package com.series.aster.launcher.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import java.lang.ref.WeakReference

class MyAccessibilityService : AccessibilityService() {

    private var info: AccessibilityServiceInfo = AccessibilityServiceInfo()

    override fun onServiceConnected() {
        mInstance = WeakReference(this)

        info.apply {
            eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED or AccessibilityEvent.TYPE_VIEW_FOCUSED

            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC

            notificationTimeout = 100
        }

        this.serviceInfo = info
    }

    override fun onUnbind(intent: Intent?): Boolean {
        mInstance = WeakReference(null)

        return super.onUnbind(intent)
    }

    override fun onInterrupt() {}

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    fun lockScreen(): Boolean = performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)

    companion object {
        private var mInstance: WeakReference<MyAccessibilityService> = WeakReference(null)
        fun instance(): MyAccessibilityService? {
            return mInstance.get()
        }
    }
}