package com.dianping.shield.node.processor.impl.row

import android.util.Log
import com.dianping.shield.node.cellnode.ShieldDisplayNode
import com.dianping.shield.node.cellnode.ShieldRow
import com.dianping.shield.node.cellnode.callback.lazyload.DefaultDisplayNodeProvider
import com.dianping.shield.node.processor.ProcessorHolder
import com.dianping.shield.node.useritem.RowItem

/**
 * Created by zhi.he on 2018/6/27.
 */
class BaseRowNodeProcessor(private val holder: ProcessorHolder) : RowNodeProcessor() {

    override fun handleRowItem(rowItem: RowItem, shieldRow: ShieldRow): Boolean {
        shieldRow.dividerStyle = rowItem.dividerStyle
        shieldRow.showCellTopLineDivider = rowItem.showCellTopDivider
        shieldRow.showCellBottomLineDivider = rowItem.showCellBottomDivider
//        shieldRow.shieldDisplayNodes ?: let { shieldRow.shieldDisplayNodes = ArrayList() }

        if (rowItem.isLazyLoad) {
//            dividerStyle = rowItem.dividerStyle
//            showCellTopLineDivider = rowItem.showCellTopDivider
//            showCellBottomLineDivider = rowItem.showCellBottomDivider
            shieldRow.viewCount = rowItem.viewCount
            shieldRow.isLazyLoad = true
            shieldRow.nodeProvider = DefaultDisplayNodeProvider(rowItem.lazyLoadViewItemProvider, holder)
        } else {
            rowItem.viewItems?.let {
                //Todo 先只处理LINEAR的LayoutType

                for ((index, vi) in it.withIndex()) {
                    vi?.let {
                        //统一在此处创建node
                        var startTime = System.nanoTime();
                        val dNode = ShieldDisplayNode().apply {
                            this.rowParent = shieldRow
                            this.pHolder = holder
                            holder.nodeProcessorChain.startProcessor(vi, this)
                        }

                        var endTime = System.nanoTime()
                        Log.d("BaseRowNodeProcessor", "Start at " + startTime + " end at " + endTime + " => cost " + (endTime - startTime))

                        shieldRow.shieldDisplayNodes?.set(index, dNode)
                    }
                }
            }
        }
        
        return false
    }
}