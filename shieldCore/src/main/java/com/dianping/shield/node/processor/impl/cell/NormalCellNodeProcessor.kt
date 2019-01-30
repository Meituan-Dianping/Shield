package com.dianping.shield.node.processor.impl.cell

import android.content.Context
import com.dianping.shield.node.cellnode.ShieldSection
import com.dianping.shield.node.cellnode.ShieldViewCell
import com.dianping.shield.node.cellnode.StaggeredGridSection
import com.dianping.shield.node.processor.ProcessorHolder
import com.dianping.shield.node.useritem.ShieldSectionCellItem
import com.dianping.shield.node.useritem.StaggeredGridSectionItem
import com.dianping.shield.utils.RangeRemoveableArrayList

/**
 * Created by zhi.he on 2018/6/25.
 * 处理正常section
 */
class NormalCellNodeProcessor(context: Context, private val holder: ProcessorHolder) : CellNodeProcessor(context) {

    override fun handleShieldViewCell(cellItem: ShieldSectionCellItem, shieldViewCell: ShieldViewCell, addList: ArrayList<ShieldSection>): Boolean {
        cellItem.sectionItems?.let {
            shieldViewCell.shieldSections
                    ?: let { shieldViewCell.shieldSections = RangeRemoveableArrayList() }

            for (sValue in it) {
                val section = if (sValue is StaggeredGridSectionItem) {
                    StaggeredGridSection().apply {
                        spanCount = sValue.spanCount
                        xStaggeredGridGap = sValue.xStaggeredGridGap
                        yStaggeredGridGap = sValue.yStaggeredGridGap
                        staggeredGridLeftMargin = sValue.staggeredLeftMargin
                        staggeredGridRightMargin = sValue.staggeredRightMargin
                        sectionDividerShowType = sValue.dividerShowType
                    }
                } else {
                    ShieldSection()
                }.apply sc@{
                    //                    sectionIndex = shieldViewCell.shieldSections?.size ?: -1
                    cellParent = shieldViewCell
                }
                shieldViewCell.shieldSections?.add(section)
                holder.sectionProcessorChain.startProcessor(sValue, section)
                addList.add(section)
            }
        }
        shieldViewCell.needScrollToTop = cellItem.needScrollToTop
        shieldViewCell.recyclerViewTypeSizeMap = cellItem.recyclerViewTypeSizeMap
        return false
    }
}