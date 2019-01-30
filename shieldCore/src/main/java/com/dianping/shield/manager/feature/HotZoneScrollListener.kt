package com.dianping.shield.manager.feature

import android.support.v7.widget.RecyclerView
import com.dianping.shield.node.adapter.HotZoneItemManager
import com.dianping.shield.node.adapter.HotZoneManager
import com.dianping.shield.node.adapter.ShieldDisplayNodeAdapter

/**
 * Created by bingwei on 2018/11/16.
 */

class HotZoneScrollListener(private val nodeAdapter: ShieldDisplayNodeAdapter) : CellManagerScrollListenerInterface {
    @JvmField
    var isScrollingForHotZone: Boolean = false

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (isScrollingForHotZone && newState == RecyclerView.SCROLL_STATE_IDLE) {
            changeHotZoneObserverStatus(true)
            isScrollingForHotZone = false
        }else{
            changeHotZoneObserverStatus(false)
        }
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (!isScrollingForHotZone) {
            changeHotZoneObserverStatus(true)
        }else{
            changeHotZoneObserverStatus(false)
        }
    }

    fun changeHotZoneObserverStatus(needNotify:Boolean){
        val hotZoneItemStatusInterfaceHashMap = nodeAdapter.hotZoneItemStatusInterfaceHashMap
        val hotZoneStatusInterfaceHashMap = nodeAdapter.hotZoneStatusInterfaceHashMap

        for (hotZoneItemManager: HotZoneManager in hotZoneItemStatusInterfaceHashMap.values) {
            hotZoneItemManager.isObserverLocationChanged(needNotify)
        }

        for (hotZoneItemManager: HotZoneManager in hotZoneStatusInterfaceHashMap.values) {
            hotZoneItemManager.isObserverLocationChanged(needNotify)
        }
    }
}