package com.series.aster.launcher.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.series.aster.launcher.listener.ScrollEventListener

class GestureNestedScrollView(context: Context, attrs: AttributeSet) : NestedScrollView(context, attrs){

    private var startY: Float = 0f
    private var startTouchY: Float = 0f
    private var isTopReached: Boolean = false
    private var isBottomReached: Boolean = false
    private var isScrollingUp: Boolean = false
    private var isPullingDown: Boolean = false
    private var isPullingUp: Boolean = false

    var scrollEventListener: ScrollEventListener? = null


    init {
        isNestedScrollingEnabled = true
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                startY = ev.y
                startTouchY = ev.y
                isScrollingUp = false
                isPullingDown = false
                isPullingUp = false
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaY = ev.y - startY
                isTopReached = !canScrollVertically(-1)
                isBottomReached = !canScrollVertically(1)
                isScrollingUp = deltaY < 0 && isTopReached

                // 判断是否再次上拉或下拉触发事件
                val distanceY = ev.y - startTouchY
                val threshold = ViewConfiguration.get(context).scaledTouchSlop.toFloat()
                isPullingDown = distanceY > threshold && isTopReached
                isPullingUp = distanceY < -threshold && isBottomReached
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                startY = ev.y
                startTouchY = ev.y
                isScrollingUp = false
                isPullingDown = false
                isPullingUp = false
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaY = ev.y - startY
                isTopReached = !canScrollVertically(-1)
                isBottomReached = !canScrollVertically(1)
                isScrollingUp = deltaY < 0 && isTopReached

                // 判断是否再次上拉或下拉触发事件
                val distanceY = ev.y - startTouchY
                val threshold = 200
                isPullingDown = distanceY > threshold && isTopReached
                isPullingUp = distanceY < -threshold && isBottomReached
            }
            MotionEvent.ACTION_UP -> {
                startY = 0f // 重置startY的值
                if (isPullingDown) {
                    // 再次下拉事件
                    scrollEventListener?.onTopReached()
                    return true
                } else if (isPullingUp) {
                    // 再次上拉事件
                    scrollEventListener?.onBottomReached()
                    return true
                }
            }
        }
        return super.onTouchEvent(ev)
    }

    fun isTopReached(): Boolean {
        return isTopReached && isScrollingUp
    }

    fun isBottomReached(): Boolean {
        return isBottomReached && !isScrollingUp
    }

    /*fun registerRecyclerView(recyclerView: CustomRecyclerView, eventListener: ScrollEventListener) {
        this.recyclerView = recyclerView
        this.recyclerView.scrollEventListener = eventListener
    }*/

    fun registerRecyclerView(recyclerView: RecyclerView, eventListener: ScrollEventListener) {
        scrollEventListener = eventListener
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val isTopReached = !canScrollVertically(-1)
                val isBottomReached = !canScrollVertically(1)
                scrollEventListener?.onScroll(isTopReached, isBottomReached)
            }
        })
    }
}
