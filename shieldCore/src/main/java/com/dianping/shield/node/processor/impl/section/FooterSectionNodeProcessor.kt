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
 * 处理FooterCell
 */
class FooterSectionNodeProcessor(private val processorHolder: ProcessorHolder) : SectionNodeProcessor() {
//    private var rowDividerStyleProcessor = BaseRowNodeProcessor(context, defaultTheme)

    override fun handleShieldSection(sectionItem: SectionItem, shieldSection: ShieldSection): Boolean {
        sectionItem.footerRowItem?.let {
            shieldSection.shieldRows
                    ?: let { shieldSection.shieldRows = RangeRemoveableArrayList() }
            //读取Footer Cell
            shieldSection.shieldRows?.add(ShieldRow().apply {
                //                rowIndex = shieldSection.shieldRows?.size ?: -1
                sectionParent = shieldSection
                shieldDisplayNodes = ArrayList(arrayOfNulls<ShieldDisplayNode>(1).asList())
                cellType = CellType.FOOTER
                typePrefix = sectionParent?.cellParent?.name
                processorHolder.rowProcessorChain.startProcessor(it, this)
            })
        }
        return false
    }
}