package com.dianping.shield.node.processor.impl.section

import com.dianping.shield.entity.CellType
import com.dianping.shield.node.cellnode.ShieldDisplayNode
import com.dianping.shield.node.cellnode.ShieldRow
import com.dianping.shield.node.cellnode.ShieldSection
import com.dianping.shield.node.processor.ProcessorHolder
import com.dianping.shield.node.useritem.SectionItem
import com.dianping.shield.utils.RangeRemoveableArrayList

/**
 * Created by zhi.he on 2018/6/25.
 * 处理HeaderCell
 */
class HeaderSectionNodeProcessor(private val processorHolder: ProcessorHolder) : SectionNodeProcessor() {

//    private val rowDividerStyleProcessor = BaseRowNodeProcessor(context, defaultTheme)

    override fun handleShieldSection(sectionItem: SectionItem, shieldSection: ShieldSection): Boolean {
        sectionItem.headerRowItem?.let {
            shieldSection.shieldRows
                    ?: let { shieldSection.shieldRows = RangeRemoveableArrayList() }
            //读取Header Cell
            shieldSection.shieldRows?.add(ShieldRow().apply {
                //                rowIndex = shieldSection.shieldRows?.size ?: -1
                sectionParent = shieldSection
                shieldDisplayNodes = ArrayList(arrayOfNulls<ShieldDisplayNode>(1).asList())
                cellType = CellType.HEADER
                typePrefix = sectionParent?.cellParent?.name
                processorHolder.rowProcessorChain.startProcessor(it, this)
            })
        }
        return false
    }
}