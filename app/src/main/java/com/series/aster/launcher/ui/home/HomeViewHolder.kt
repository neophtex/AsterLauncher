package com.series.aster.launcher.ui.home

import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.series.aster.launcher.R
import com.series.aster.launcher.data.entities.AppInfo
import com.series.aster.launcher.databinding.ItemHomeBinding
import com.series.aster.launcher.helper.PreferenceHelper
import com.series.aster.launcher.listener.OnItemClickedListener
import javax.inject.Inject


class HomeViewHolder @Inject constructor(
    private val binding: ItemHomeBinding,
    private val onAppClickedListener: OnItemClickedListener.OnAppsClickedListener,
    private val onAppLongClickedListener: OnItemClickedListener.OnAppLongClickedListener,
    private val preferenceHelper: PreferenceHelper
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(appInfo: AppInfo) {

        binding.apply {
            val layoutParams = LinearLayoutCompat.LayoutParams(
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = preferenceHelper.homeAppAlignment
            }

            appHomeName.layoutParams = layoutParams
            appHomeName.text = appInfo.appName
            appHomeName.setTextColor(preferenceHelper.appColor)
            appHomeName.textSize = preferenceHelper.appTextSize

            Log.d("Tag", "Home Adapter Color: ${preferenceHelper.appColor.toString()}")

            appHomeIcon.visibility = View.GONE
        }

        itemView.setOnClickListener { onAppClickedListener.onAppClicked(appInfo) }

        itemView.setOnLongClickListener {
            onAppLongClickedListener.onAppLongClicked(appInfo)
            true
        }
    }

}
