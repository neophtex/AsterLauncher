package com.series.aster.launcher.helper

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import javax.inject.Inject

class BottomDialogHelper @Inject constructor() {

    fun setupDialogStyle(dialog: Dialog?) {
        //val bottomSheet = view.parent as View
        //bottomSheet.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#70000000"))
        //bottomSheet.backgroundTintList = ColorStateList.valueOf(Color.BLACK)

        val window = dialog?.window
        if (window != null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    fun getColorText(color: Int): SpannableString {
        val colorText = "#${Integer.toHexString(color)}"
        val spannableString = SpannableString(colorText)
        spannableString.setSpan(
            ForegroundColorSpan(color),
            0,
            colorText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannableString
    }
}