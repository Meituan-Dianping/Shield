package com.dianping.shield.node.processor.impl.section

import com.dianping.shield.entity.CellType
import com.dianping.shield.node.cellnode.RowRangeHolder
import com.dianping.shield.node.cellnode.ShieldDisplayNode
import com.dianping.shield.node.cellnode.ShieldRow
import com.dianping.shield.node.cellnode.ShieldSection
import com.dianping.shield.node.cellnode.callback.lazyload.DefaultShieldRowProvider
import com.dianping.shield.node.cellnode.callback.lazyload.DefaultShieldRowProviderWithItem
import com.dianping.shield.node.processor.ProcessorHolder
import com.dianping.shield.node.useritem.SectionItem
import com.dianping.shield.utils.RangeRemoveableArrayList
import java.util.*

/**
 * Created by zhi.he on 2018/6/25.
 * 处理Normal Row
 */
class NormalSectionNodeProcessor(private val processorHolder: ProcessorHolder) : SectionNodeProcessor() {

    //    private var rowDividerStyleProcessor = BaseRowNodeProcessor(context, defaultTheme)
//    var processorChain = ProcessorChain(processorHolder)
//            .addProcessor(BaseRowNodeProcessor::class.java)

    override fun handleShieldSection(sectionItem: SectionItem, shieldSection: ShieldSection): Boolean {

        //ShieldSection层面上默认延迟加载ShieldRow
        shieldSection.rangeDispatcher.clear()

        var rowSize = if (sectionItem.isLazyLoad) {
            sectionItem.rowCount
        } else {
            sectionItem.rowItems?.size ?: 0
        }

        shieldSection.isLazyLoad = true

        if (sectionItem.isLazyLoad) {
            sectionItem.lazyLoadRowItemProvider?.let {
                shieldSection.rowProvider = DefaultShieldRowProvider(it, processorHolder)
            }
        } else {
            sectionItem.rowItems?.let {
                shieldSection.rowProvider = DefaultShieldRowProviderWithItem(it, processorHolder)
            }
        }

        sectionItem.headerRowItem?.let {
            shieldSection.hasHeaderCell = true
            rowSize++
        }
        sectionItem.footerRowItem?.let {
            shieldSection.hasFooterCell = true
            rowSize++
        }

        //rowsize是包含header和footer的总行数
        shieldSection.shieldRows ?: let {
            shieldSection.shieldRows = RangeRemoveableArrayList(arrayOfNulls<ShieldRow>(rowSize).asList())
        }

        //HeaderCell直接生成
        sectionItem.headerRowItem?.let { headerRowItem ->
            //读取Header Cell
            shieldSection.shieldRows?.set(0, ShieldRow().apply {
                //                rowIndex = 0
                sectionParent = shieldSection
                cellType = CellType.HEADER
                typePrefix = sectionParent?.cellParent?.name
                shieldDisplayNodes = ArrayList(arrayOfNulls<ShieldDisplayNode>(headerRowItem.viewItems?.size
                        ?: 0).asList())
                processorHolder.rowProcessorChain.startProcessor(headerRowItem, this)
            })
        }

        //FooterCell直接生成
        sectionItem.footerRowItem?.let { footerRowItem ->
            //读取Footer Cell
            shieldSection.shieldRows?.set(rowSize - 1, ShieldRow().apply {
                //                rowIndex = rowSize - 1
                sectionParent = shieldSection
                cellType = CellType.FOOTER
                typePrefix = sectionParent?.cellParent?.name
                shieldDisplayNodes = ArrayList(arrayOfNulls<ShieldDisplayNode>(footerRowItem.viewItems?.size
                        ?: 0).asList())
                processorHolder.rowProcessorChain.startProcessor(footerRowItem, this)
            })
        }

        //所有的行都作为RangeHolder加入RangeDispatcher
        val rowRangeList = ArrayList<RowRangeHolder>()
        for (i in 0 until rowSize) {
            rowRangeList.add(RowRangeHolder().apply {
                if (i == 0 && shieldSection.hasHeaderCell) {
                    dNodeCount = shieldSection.shieldRows?.get(0)?.shieldDisplayNodes?.size ?: 0
                } else if (i == rowSize - 1 && shieldSection.hasFooterCell) {
                    dNodeCount = shieldSection.shieldRows?.get(rowSize - 1)?.shieldDisplayNodes?.size ?: 0
                } else {
                    //这个row index 为 inner index
                    val rowIndex = if (shieldSection.hasHeaderCell) i - 1 else i
                    dNodeCount = shieldSection.rowProvider?.getRowNodeCount(rowIndex, shieldSection) ?: 0
                }
            })
        }

        shieldSection.rangeDispatcher.addAll(rowRangeList)

        shieldSection.sectionTitle = sectionItem.sectionTitle

        return false
    }
}