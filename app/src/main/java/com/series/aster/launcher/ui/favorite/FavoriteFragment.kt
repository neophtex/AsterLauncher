package com.series.aster.launcher.ui.favorite

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.series.aster.launcher.R
import com.series.aster.launcher.data.entities.AppInfo
import com.series.aster.launcher.databinding.FragmentFavoriteBinding
import com.series.aster.launcher.helper.AppHelper
import com.series.aster.launcher.helper.FingerprintHelper
import com.series.aster.launcher.helper.PreferenceHelper
import com.series.aster.launcher.listener.OnItemClickedListener
import com.series.aster.launcher.listener.OnItemMoveListener
import com.series.aster.launcher.ui.bottomsheetdialog.AppInfoBottomSheetFragment
import com.series.aster.launcher.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Collections
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 * Use the [FavoriteFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
@AndroidEntryPoint
class FavoriteFragment : Fragment(), OnItemClickedListener.OnAppsClickedListener,
    OnItemClickedListener.BottomSheetDismissListener,
    OnItemClickedListener.OnAppStateClickListener,
    FingerprintHelper.Callback, OnItemMoveListener.OnItemActionListener {
    private var _binding: FragmentFavoriteBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: AppViewModel by viewModels()

    private lateinit var context: Context

    @Inject
    lateinit var preferenceHelper: PreferenceHelper

    @Inject
    lateinit var fingerHelper: FingerprintHelper

    @Inject
    lateinit var appHelper: AppHelper

    private val favoriteAdapter: FavoriteAdapter by lazy { FavoriteAdapter(this) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        appHelper.dayNightMod(requireContext(), binding.favoriteView)
        super.onViewCreated(view, savedInstanceState)

        context = requireContext()

        setupRecyclerView()
        observeFavorite()
        observeHomeAppOrder()
    }

    private fun setupRecyclerView() {

        binding.favoriteAdapter.apply {
            adapter = favoriteAdapter
            layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            setHasFixedSize(false)
        }
    }

    private fun handleDragAndDrop(oldPosition: Int, newPosition: Int) {
        val items = favoriteAdapter.currentList.toMutableList()
        Collections.swap(items, oldPosition, newPosition)

        // update app_order value
        items.forEachIndexed { index, appInfo ->
            appInfo.appOrder = index
        }

        // Save the updated list of favorite applications to the database on the IO thread
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.updateAppOrder(items)
        }

    }


    private fun observeHomeAppOrder() {
        //binding.favoriteAdapter.adapter = favoriteAdapter
        val listener: OnItemMoveListener.OnItemActionListener = favoriteAdapter

        val simpleItemTouchCallback = object : ItemTouchHelper.Callback() {

            override fun onChildDraw(
                canvas: Canvas, recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder, dX: Float,
                dY: Float, actionState: Int, isCurrentlyActive: Boolean
            ) {
                if (isCurrentlyActive) {
                    viewHolder.itemView.alpha = 0.5f
                } else {
                    viewHolder.itemView.alpha = 1f
                }
                super.onChildDraw(
                    canvas, recyclerView, viewHolder,
                    dX, dY,
                    actionState, isCurrentlyActive
                )
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)
            }

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                val swipeFlags = 0
                return makeMovementFlags(dragFlags, swipeFlags)
            }

            override fun onMove(
                recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {

                val oldPosition = viewHolder.bindingAdapterPosition
                val newPosition = target.bindingAdapterPosition

                handleDragAndDrop(oldPosition, newPosition)

                return listener.onViewMoved(
                    viewHolder.bindingAdapterPosition,
                    target.bindingAdapterPosition
                )

            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                listener.onViewSwiped(viewHolder.adapterPosition)
            }

            override fun isLongPressDragEnabled() = false
        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)

        favoriteAdapter.setItemTouchHelper(itemTouchHelper)
        itemTouchHelper.attachToRecyclerView(binding.favoriteAdapter)
    }

    private fun observeFavorite() {
        viewModel.compareInstalledAppInfo()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.favoriteApps.collect {
                it
                favoriteAdapter.submitList(it)
            }
        }
    }

    private fun observeBioAuthCheck(appInfo: AppInfo) {
        if (!appInfo.lock) {
            appHelper.launchApp(context, appInfo)
        } else {
            fingerHelper.startFingerprintAuth(appInfo, this)
        }
    }

    private fun showSelectedApp(appInfo: AppInfo) {
        val bottomSheetFragment = AppInfoBottomSheetFragment(appInfo)
        bottomSheetFragment.setOnBottomSheetDismissedListener(this)
        bottomSheetFragment.setOnAppStateClickListener(this)
        bottomSheetFragment.show(parentFragmentManager, "BottomSheetDialog")

    }

    override fun onResume() {
        super.onResume()
    }

    /*override fun onAppLongClicked(appInfo: AppInfo) {
        showSelectedApp(appInfo)
    }*/

    override fun onBottomSheetDismissed() {
        TODO("Not yet implemented")
    }

    override fun onAppStateClicked(appInfo: AppInfo) {
        viewModel.update(appInfo)
    }

    override fun onAppClicked(appInfo: AppInfo) {
        observeBioAuthCheck(appInfo)
    }

    override fun onAuthenticationSucceeded(appInfo: AppInfo) {
        Toast.makeText(context, getString(R.string.authentication_succeeded), Toast.LENGTH_SHORT)
            .show()
        appHelper.launchApp(context, appInfo)
    }

    override fun onAuthenticationFailed() {
        Toast.makeText(context, getString(R.string.authentication_failed), Toast.LENGTH_SHORT)
            .show()
    }

    override fun onAuthenticationError(errorCode: Int, errorMessage: CharSequence?) {
        Toast.makeText(context, getString(R.string.authentication_error), Toast.LENGTH_SHORT)
            .show()
    }

    override fun onViewMoved(oldPosition: Int, newPosition: Int): Boolean {
        return true
    }

    override fun onViewSwiped(position: Int) {
        TODO("Not yet implemented")
    }
}