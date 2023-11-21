package com.series.aster.launcher.ui.drawer

import android.view.LayoutInflater
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.series.aster.launcher.data.entities.AppInfo
import com.series.aster.launcher.databinding.ItemDrawBinding
import com.series.aster.launcher.listener.OnItemClickedListener

class DrawAdapter(private val onAppClickedListener: OnItemClickedListener.OnAppsClickedListener,
                  private val onAppLongClickedListener: OnItemClickedListener.OnAppLongClickedListener) :
    ListAdapter<AppInfo, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(
        parent: android.view.ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val binding = ItemDrawBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DrawViewHolder(binding, onAppClickedListener, onAppLongClickedListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val appInfo = getItem(position)
        (holder as DrawViewHolder).bind(appInfo)
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