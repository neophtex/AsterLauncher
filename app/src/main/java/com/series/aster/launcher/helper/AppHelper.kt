package com.series.aster.launcher.helper

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.provider.AlarmClock
import android.provider.CalendarContract
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat.startActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.series.aster.launcher.Constants
import com.series.aster.launcher.R
import com.series.aster.launcher.accessibility.MyAccessibilityService
import com.series.aster.launcher.data.entities.AppInfo
import com.series.aster.launcher.ui.activities.LauncherActivity
import javax.inject.Inject


class AppHelper @Inject constructor() {

    fun resetDefaultLauncher(context: Context) {
        context.startActivity(Intent(Settings.ACTION_HOME_SETTINGS))
    }

    @SuppressLint("WrongConstant", "PrivateApi")
    fun expandNotificationDrawer(context: Context) {
        // expand notification
        try {
            val statusBarService = context.getSystemService(Constants.NOTIFICATION_SERVICE)
            val statusBarManager = Class.forName(Constants.NOTIFICATION_MANAGER)
            val method = statusBarManager.getMethod(Constants.NOTIFICATION_METHOD)
            method.invoke(statusBarService)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun searchView(context: Context) {
        // search result
        val intent = Intent(Intent.ACTION_WEB_SEARCH)
        intent.putExtra(SearchManager.QUERY, "")
        context.startActivity(intent)
    }

    fun darkThemes(darkThemes: Boolean){
        if (darkThemes){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    fun dayNightMod(context: Context, view: View) {
        when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> {
                // dark mod
                view.setBackgroundColor(context.resources.getColor(R.color.whiteTrans25))
            }

            Configuration.UI_MODE_NIGHT_NO -> {
                // light mod
                view.setBackgroundColor(context.resources.getColor(R.color.blackTrans50))
            }
        }
    }

    fun updateUI(view: View, gravity: Int, selectColor: Int, textSize: Float, isVisible: Boolean) {
        //textview style
        val layoutParams = view.layoutParams as LinearLayoutCompat.LayoutParams
        layoutParams.gravity = gravity
        view.layoutParams = layoutParams

        if (view is TextView) {
            view.setTextColor(selectColor)
            view.textSize = textSize
            view.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
            view.isClickable = if (isVisible) view.isClickable else !view.isClickable
        }
    }

    fun launchApp(context: Context, appInfo: AppInfo) {
        // launch application
        val intent = context.packageManager.getLaunchIntentForPackage(appInfo.packageName)
        if (intent != null) {
            context.startActivity(intent)
        } else {
            showToast(context, "Failed to open the application")
        }
    }

    fun launchClock(context: Context) {
        // launch clock
        try {
            val intent = Intent(AlarmClock.ACTION_SHOW_ALARMS)
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("launchClock", "Error launching clock app: ${e.message}")
        }
    }

    fun launchCalendar(context: Context) {
        // launch clock
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = CalendarContract.CONTENT_URI
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // If unable to launch the calendar, try using the app picker
            val pickerIntent = Intent(Intent.ACTION_MAIN)
            pickerIntent.addCategory(Intent.CATEGORY_APP_CALENDAR)
            try {
                context.startActivity(pickerIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun unInstallApp(context: Context, appInfo: AppInfo) {
        //uninstall application
        val intent = Intent(Intent.ACTION_DELETE)
        intent.data = Uri.parse("package:${appInfo.packageName}")
        context.startActivity(intent)
    }

    fun appInfo(context: Context, appInfo: AppInfo) {
        //open app info
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", appInfo.packageName, null)
        context.startActivity(intent)
    }

    fun gravityToString(gravity: Int): String? {
        //ui gravity setting
        return when (gravity) {
            Gravity.CENTER -> "CENTER"
            Gravity.START -> "LEFT"
            Gravity.END -> "RIGHT"
            Gravity.TOP -> "TOP"
            Gravity.BOTTOM -> "BOTTOM"
            else -> null
        }
    }

    fun getGravityFromSelectedItem(selectedItem: String): Int {
        //gravity option
        return when (selectedItem) {
            "Left" -> Gravity.START
            "Center" -> Gravity.CENTER
            "Right" -> Gravity.END
            else -> Gravity.START
        }
    }


    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun showStatusBar(window: Window) {
        //show statusBar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.show(WindowInsets.Type.statusBars())
        } else
            @Suppress("DEPRECATION", "InlinedApi")
            window.decorView.apply {
                systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
    }

    fun hideStatusBar(window: Window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        else {
            @Suppress("DEPRECATION")
            window.decorView.apply {
                systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE or View.SYSTEM_UI_FLAG_FULLSCREEN
            }
        }
    }

    fun showSoftKeyboard(context: Context, view: View) {
        if (view.requestFocus()) {
            val inputMethodManager: InputMethodManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    fun hideKeyboard(context: Context, view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(this.windowToken, 0)
    }

    fun rateApp(context: Context){
        val uri = Uri.parse("market://details?id=" + context.packageName)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }

    fun shareApp(context: Context){
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "share application")
        shareIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + context.packageName)
        context.startActivity(Intent.createChooser(shareIntent, "share application"))
    }

    fun github(context: Context){
        val uri = Uri.parse("https://github.com/neophtex/AsterLauncher")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }

    fun feedback(context: Context){
        val emailIntent = Intent(Intent.ACTION_SENDTO)
        emailIntent.data = Uri.parse("mailto:neophtex@gmail.com")
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Aster Launcher")
        context.startActivity(Intent.createChooser(emailIntent, "Choose Mail Application"))
    }
    fun enableAppAsAccessibilityService(context: Context, accessibilityState: Boolean) {
        //lock screen permissions
        val myAccessibilityService = MyAccessibilityService.instance()

        val state: String = if (myAccessibilityService != null) {
            context.getString(R.string.accessibility_settings_disable)
        } else {
            context.getString(R.string.accessibility_settings_enable)
        }

        val builder = MaterialAlertDialogBuilder(context)

        builder.setTitle(R.string.accessibility_settings_title)
        builder.setMessage(R.string.accessibility_service_desc)
        builder.setPositiveButton(state) { _, _ ->
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            context.startActivity(intent)
        }
            .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .show()
    }
}