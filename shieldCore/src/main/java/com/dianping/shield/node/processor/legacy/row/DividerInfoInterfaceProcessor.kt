package com.dianping.shield.node.processor.legacy.row

import android.graphics.Rect
import com.dianping.agentsdk.framework.DividerInfo
import com.dianping.agentsdk.framework.SectionCellInterface
import com.dianping.agentsdk.sectionrecycler.divider.DividerInfoInterface
import com.dianping.shield.entity.CellType
import com.dianping.shield.node.useritem.DividerStyle
import com.dianping.shield.node.useritem.RowItem

/**
 * Created by zhi.he on 2018/6/28.
 */
class DividerInfoInterfaceProcessor : RowInterfaceProcessor() {
    override fun handleRowItem(sci: SectionCellInterface, rowItem: RowItem, section: Int, row: Int): Boolean {
        if (sci is DividerInfoInterface) {
            //兼容Header
            when (row) {
                -1 -> sci.getDividerInfo(CellType.HEADER, section, 0)
                -2 -> sci.getDividerInfo(CellType.FOOTER, section, row)
                else -> sci.getDividerInfo(CellType.NORMAL, section, row)
            }?.let {
                rowItem.dividerStyle ?: let { rowItem.dividerStyle = DividerStyle() }
                rowItem.dividerStyle.styleType = when (it.style) {
                    DividerInfo.DividerStyle.AUTO -> DividerStyle.StyleType.AUTO
                    DividerInfo.DividerStyle.NONE -> DividerStyle.StyleType.NONE
                    DividerInfo.DividerStyle.TOP -> DividerStyle.StyleType.TOP
                    DividerInfo.DividerStyle.MIDDLE -> DividerStyle.StyleType.MIDDLE
                    DividerInfo.DividerStyle.BOTTOM -> DividerStyle.StyleType.BOTTOM
                    DividerInfo.DividerStyle.SINGLE -> DividerStyle.StyleType.SINGLE
                    else -> DividerStyle.StyleType.AUTO
                }
                if (it.leftOffset >= 0 || it.rightOffset >= 0) {
                    //之前接口设计只对middle有效，并且单位为px，勉强适配
                    rowItem.dividerStyle.middleStyleLineOffset = Rect(it.leftOffset, 0, it.rightOffset, 0)
                }

                rowItem.dividerStyle.topStyleLineDrawable = it.topDividerDrawable
                rowItem.dividerStyle.middleStyleLineDrawable = it.middleDividerDrawable
                rowItem.dividerStyle.bottomStyleLineDrawable = it.bottomDividerDrawable
            }
        }
        return false
//        nextProcessor?.handleRowItem(sci, rowItem, section, row)
    }
}