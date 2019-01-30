package com.dianping.shield.node.processor.impl.row

import com.dianping.shield.entity.ScrollDirection
import com.dianping.shield.node.adapter.hotzone.HotZone
import com.dianping.shield.node.adapter.hotzone.HotZoneLocation
import com.dianping.shield.node.adapter.hotzone.OnHotZoneStateChangeListener
import com.dianping.shield.node.cellnode.ShieldDisplayNode
import com.dianping.shield.node.cellnode.ShieldRow
import com.dianping.shield.node.useritem.RowItem

/**
 * Created by runqi.wei at 2018/9/11
 */
class RowNodeHotZoneProcessor : RowNodeProcessor() {
    override fun handleRowItem(rowItem: RowItem, shieldRow: ShieldRow): Boolean {
        shieldRow.hotZoneArray = ArrayList()
        shieldRow.hotZoneArray?.let { hotZoneArray ->
            rowItem.hotZoneArrayList?.filter { it.callBackList != null && !it.callBackList.isEmpty() }
                    ?.forEach { hotZoneInfo ->
                        hotZoneArray.add(HotZone().let { hotZone ->
                            hotZone.start = hotZoneInfo.start
                            hotZone.end = hotZoneInfo.end

                            hotZone.listenerArrayList = ArrayList()
                            hotZone.listenerArrayList.add(object : OnHotZoneStateChangeListener {
                                override fun onHotZoneStateChanged(position: Int, currentFirst: Int, currentLast: Int,
                                                                   node: ShieldDisplayNode?, hotZone: HotZone?,
                                                                   oldHotZoneLocation: HotZoneLocation?, hotZoneLocation: HotZoneLocation?,
                                                                   scrollDirection: ScrollDirection?) {
                                    val oldIn = isInHotZone(oldHotZoneLocation)
                                    val newIn = isInHotZone(hotZoneLocation)
                                    if (oldIn != newIn) {
                                        hotZoneInfo.callBackList.forEach {
                                            if (newIn) {
                                                it.scrollReach(node?.path?.section, node?.path?.row, scrollDirection)
                                            } else {
                                                it.scrollOut(node?.path?.section, node?.path?.row, scrollDirection)
                                            }
                                        }
                                    }
                                }
                            })
                            hotZone
                        })
                    }
        }

        return false
    }

    fun isInHotZone(hotZoneLocation: HotZoneLocation?): Boolean {
        return when (hotZoneLocation) {
            null, HotZoneLocation.DETACHED, HotZoneLocation.US_US, HotZoneLocation.BE_BE -> false
            else -> true
        }

    }

}