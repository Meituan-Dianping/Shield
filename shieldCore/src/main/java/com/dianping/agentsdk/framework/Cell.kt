package com.dianping.agentsdk.framework

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ListAdapter
import com.dianping.shield.node.cellnode.ShieldSection
import com.dianping.shield.node.cellnode.ShieldViewCell

/**
 * Created by zhi.he on 2018/7/1.
 */
class Cell {
    @JvmField
    var owner: AgentInterface? = null
    @JvmField
    var key: String? = null
    @JvmField
    var name: String? = null
    @JvmField
    var view: View? = null
    @JvmField
    var adpater: ListAdapter? = null
    @JvmField
    var recyclerViewAdapter: RecyclerView.Adapter<*>? = null
    @JvmField
    var shieldViewCell: ShieldViewCell? = null
    @JvmField
    var groupIndex: String? = null
    @JvmField
    var innerIndex: String? = null
    @JvmField
    var lastCell: Cell? = null

    @JvmField
    var nextCell: Cell? = null

    override fun toString(): String {
        return if (view == null && adpater == null && recyclerViewAdapter == null && shieldViewCell == null) {
            "(Empty Cell)"
        } else {
            "owner:${owner?.javaClass?.simpleName
                    ?: "null"}|key:$key|view:${view?.toString()}|listAdapterCount:${adpater?.count}|" +
                    "itemCount:${recyclerViewAdapter?.itemCount}"
        }
    }

//    fun getLastCellTail(): ShieldDisplayNode? {
//        return lastCell?.getMyCellTail()
//    }
//
//    fun getMyCellTail(): ShieldDisplayNode? {
//        return shieldViewCell?.let {
//            it.shieldSections?.lastOrNull()?.shieldRows?.lastOrNull()?.shieldDisplayNodes?.lastOrNull()
//        } ?: let { getLastCellTail() }
//    }
//
//    fun getMyCellHead(): ShieldDisplayNode? {
//        return shieldViewCell?.shieldSections?.firstOrNull()?.shieldRows?.firstOrNull()?.shieldDisplayNodes?.firstOrNull()
//                ?: let { getNextCellHead() }
//    }
//
//    fun getNextCellHead(): ShieldDisplayNode? {
//        return nextCell?.getMyCellHead()
//    }

    fun getLastCellTailSection(): ShieldSection? {
        return lastCell?.getMyCellTailSection()
    }

    fun getMyCellHeadSection(): ShieldSection? {
        return shieldViewCell?.shieldSections?.firstOrNull()?.let {
//            if (it.getRange() > 0) {
                it
//            } else {
//                getNextCellHeadSection()
//            }
        } ?: let { getNextCellHeadSection() }

    }

    fun getMyCellTailSection(): ShieldSection? {
        return shieldViewCell?.shieldSections?.lastOrNull()?.let {
//            if (it.getRange() > 0) {
                it
//            } else {
//                getLastCellTailSection()
//            }
        } ?: let { getLastCellTailSection() }
    }

    fun getNextCellHeadSection(): ShieldSection? {
        return nextCell?.getMyCellHeadSection()
    }
}