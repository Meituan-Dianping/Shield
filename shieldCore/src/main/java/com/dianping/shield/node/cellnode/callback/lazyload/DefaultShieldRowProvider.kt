package com.dianping.shield.node.cellnode.callback.lazyload

import com.dianping.shield.entity.CellType
import com.dianping.shield.node.cellnode.ShieldDisplayNode
import com.dianping.shield.node.cellnode.ShieldRow
import com.dianping.shield.node.cellnode.ShieldSection
import com.dianping.shield.node.itemcallbacks.lazy.LazyLoadRowItemProvider
import com.dianping.shield.node.processor.ProcessorHolder
import com.dianping.shield.node.useritem.LayoutType

/**
 * Created by zhi.he on 2018/7/13.
 */
class DefaultShieldRowProvider(private val itemProvider: LazyLoadRowItemProvider, private val processorHolder: ProcessorHolder) : LazyLoadShieldRowProvider {
    override fun isPreLoad(row: Int, sectionParent: ShieldSection): Boolean {
        return itemProvider.isPreLoad(sectionParent.currentSectionIndex(), row)
    }

    override fun getRowNodeCount(row: Int, sectionParent: ShieldSection): Int {
        return when (itemProvider.getRowLayoutType(sectionParent.currentSectionIndex(), row)) {
            LayoutType.LINEAR_FULL_FILL -> 1
            else -> itemProvider.getRowViewCount(sectionParent.currentSectionIndex(), row)
        }
    }

    override fun getShieldRow(row: Int, sectionParent: ShieldSection): ShieldRow {
        return ShieldRow().apply {
            //rowIndex = sectionParent.shieldRows?.size ?: -1
//            rowIndex = row
            this.sectionParent = sectionParent
//            itemProvider.getRowItem(row)

            shieldDisplayNodes = ArrayList(arrayOfNulls<ShieldDisplayNode>(getRowNodeCount(row, sectionParent)).asList())

            cellType = CellType.NORMAL
            typePrefix = this.sectionParent?.cellParent?.name
            val rowItem = itemProvider.getRowItem(sectionParent.currentSectionIndex(), row)
            processorHolder.rowProcessorChain.startProcessor(rowItem, this)
        }
    }


}