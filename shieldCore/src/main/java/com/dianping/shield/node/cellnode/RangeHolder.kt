package com.dianping.shield.node.cellnode

/**
 * Created by zhi.he on 2018/7/24.
 */
interface RangeHolder {

    fun getRange(): Int

    fun registerObserver(observer: RangeChangeObserver)

    fun unregisterObserver(observer: RangeChangeObserver)
}