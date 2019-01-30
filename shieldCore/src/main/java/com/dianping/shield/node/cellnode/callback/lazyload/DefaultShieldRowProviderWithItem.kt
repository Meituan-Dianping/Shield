package com.dianping.shield.node.cellnode.callback.lazyload

import com.dianping.shield.entity.CellType
import com.dianping.shield.node.cellnode.ShieldDisplayNode
import com.dianping.shield.node.cellnode.ShieldRow
import com.dianping.shield.node.cellnode.ShieldSection
import com.dianping.shield.node.processor.ProcessorHolder
import com.dianping.shield.node.useritem.LayoutType
import com.dianping.shield.node.useritem.RowItem

/**
 * Created by zhi.he on 2018/7/16.
 */
class DefaultShieldRowProviderWithItem(private val rowItems: ArrayList<RowItem>, private val processorHolder: ProcessorHolder) : LazyLoadShieldRowProvider {
    override fun isPreLoad(row: Int, sectionParent: ShieldSection): Boolean {
        return rowItems[row].topInfo != null || rowItems[row].bottomInfo != null
    }

    override fun getRowNodeCount(row: Int, sectionParent: ShieldSection): Int {
        return when (rowItems[row].layoutType) {
            LayoutType.LINEAR_FULL_FILL -> 1
            else -> rowItems[row].viewItems?.size ?: 0
        }
    }

    override fun getShieldRow(row: Int, sectionParent: ShieldSection): ShieldRow {
        return ShieldRow().apply {
            //rowIndex = sectionParent.shieldRows?.size ?: -1
//            rowIndex = row
            this.sectionParent = sectionParent
//            itemProvider.getRowItem(row)

            shieldDisplayNodes = ArrayList(arrayOfNulls<ShieldDisplayNode>(getRowNodeCount(row, sectionParent)).asList())
//            shieldDisplayNodes = ArrayList()
            cellType = CellType.NORMAL
            typePrefix = this.sectionParent?.cellParent?.name
            var rowItem = rowItems[row]
            processorHolder.rowProcessorChain.startProcessor(rowItem, this)
        }
    }
}