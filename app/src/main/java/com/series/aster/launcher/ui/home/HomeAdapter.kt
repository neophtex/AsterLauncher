package com.series.aster.launcher.ui.home

import android.view.LayoutInflater
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.series.aster.launcher.data.entities.AppInfo
import com.series.aster.launcher.databinding.ItemHomeBinding
import com.series.aster.launcher.helper.PreferenceHelper
import com.series.aster.launcher.listener.OnItemClickedListener
import javax.inject.Inject

class HomeAdapter @Inject constructor(private val onAppClickedListener: OnItemClickedListener.OnAppsClickedListener,
                                      private val onAppLongClickedListener: OnItemClickedListener.OnAppLongClickedListener,
                                      private val preferenceHelperProvider: PreferenceHelper
) :
    ListAdapter<AppInfo,RecyclerView.ViewHolder>(DiffCallback()) {

    private val data: MutableList<AppInfo> = mutableListOf()

    override fun onCreateViewHolder(
        parent: android.view.ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val binding = ItemHomeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        val preferenceHelper = preferenceHelperProvider

        return HomeViewHolder(binding, onAppClickedListener, onAppLongClickedListener, preferenceHelper)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val appInfo = getItem(position)
        (holder as HomeViewHolder).bind(appInfo)
    }

    class DiffCallback : DiffUtil.ItemCallback<AppInfo>()  {
        override fun areItemsTheSame(oldItem: AppInfo, newItem: AppInfo) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: AppInfo, newItem: AppInfo) =
            oldItem == newItem
    }

    fun updateDataWithStateFlow(newData: List<AppInfo>) {
        submitList(newData.toMutableList())
        notifyDataSetChanged()
    }

}
