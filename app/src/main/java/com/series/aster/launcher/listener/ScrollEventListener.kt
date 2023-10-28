package com.series.aster.launcher.listener

interface ScrollEventListener {
    fun onTopReached()
    fun onBottomReached()
    fun onScroll(isTopReached: Boolean, isBottomReached: Boolean)
}