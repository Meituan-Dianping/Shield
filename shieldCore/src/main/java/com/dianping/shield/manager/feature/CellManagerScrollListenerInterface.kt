package com.dianping.shield.manager.feature

import android.support.v7.widget.RecyclerView

interface CellManagerScrollListenerInterface {
    fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int)

    fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int)
}