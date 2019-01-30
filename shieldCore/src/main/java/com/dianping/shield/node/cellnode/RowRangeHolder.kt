package com.dianping.shield.node.cellnode

/**
 * Created by zhi.he on 2018/7/16.
 */
class RowRangeHolder(@JvmField
                     var dNodeCount: Int = 0) : RangeHolder {
    override fun registerObserver(observer: RangeChangeObserver) {
    }

    override fun unregisterObserver(observer: RangeChangeObserver) {
    }

    override fun getRange(): Int {
        return dNodeCount
    }
}