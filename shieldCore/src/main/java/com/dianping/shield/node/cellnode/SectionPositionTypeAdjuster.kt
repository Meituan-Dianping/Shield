package com.dianping.shield.node.cellnode

import com.dianping.shield.node.processor.NodeCreator
import com.dianping.shield.utils.ObservableArrayList
import com.dianping.shield.utils.ObservableList

/**
 * Created by zhi.he on 2018/7/24.
 */
class SectionPositionTypeAdjuster : ObservableList.OnListChangedCallback<ObservableArrayList<ShieldSection>>() {


    override fun onChanged(sender: ObservableArrayList<ShieldSection>) {

    }

    override fun onItemRangeChanged(sender: ObservableArrayList<ShieldSection>, positionStart: Int, itemCount: Int, oldItems: MutableList<Any?>?) {
        adjustListAndNeighbor(sender, positionStart, itemCount)
    }

    override fun onItemRangeInserted(sender: ObservableArrayList<ShieldSection>, positionStart: Int, itemCount: Int) {
        adjustListAndNeighbor(sender, positionStart, itemCount)
    }

    override fun onItemRangeMoved(sender: ObservableArrayList<ShieldSection>, fromPosition: Int, toPosition: Int, itemCount: Int) {
        //not support
    }

    override fun onItemRangeRemoved(sender: ObservableArrayList<ShieldSection>, positionStart: Int, itemCount: Int, oldItems: MutableList<Any?>?) {
        //调整删除范围的上一个和下一个
        var lastSection: ShieldSection? = if (positionStart > 0) {
            sender[positionStart - 1]
        } else null

        //调整下一个 和 最后一个
        if (positionStart < sender.size) {
            val nextSection = sender[positionStart]
            NodeCreator.adjustSectionLinkType(lastSection, nextSection)
        }
    }

    override fun onItemRangeReplaced(sender: ObservableArrayList<ShieldSection>, fromPosition: Int, newItemCount: Int, oldItemCount: Int, oldItems: MutableList<Any?>?) {
        adjustListAndNeighbor(sender, fromPosition, newItemCount)
    }

    private fun adjustListAndNeighbor(sender: ObservableArrayList<ShieldSection>, positionStart: Int, itemCount: Int) {
        //针对插入进行调整 过滤空section
        var lastSection: ShieldSection? = null
        if (positionStart > 0) {
            for (i in 1..positionStart) {
                if (sender[positionStart - i].getRange() > 0) {
                    lastSection = sender[positionStart - i]
                    break
                }
            }
        }

        //调整上一个 和 insert范围
        for (i in positionStart until positionStart + itemCount) {
            if (sender[i].getRange() > 0) {
                val insertedSection = sender[i]
                NodeCreator.adjustSectionLinkType(lastSection, insertedSection)
                lastSection = insertedSection
            }
        }

        //调整下一个 和 insert最后一个
        if (positionStart + itemCount < sender.size) {
            val nextSection = sender[positionStart + itemCount]
            if (nextSection.getRange() > 0) {
                NodeCreator.adjustSectionLinkType(lastSection, nextSection)
            }
        }
    }
}