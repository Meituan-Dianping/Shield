package com.dianping.shield.node.processor.legacy.row

import android.graphics.Rect
import com.dianping.agentsdk.framework.DividerInterface
import com.dianping.agentsdk.framework.DividerOffsetInterface
import com.dianping.agentsdk.framework.SectionCellInterface
import com.dianping.agentsdk.framework.TopDividerInterface
import com.dianping.shield.node.useritem.DividerStyle
import com.dianping.shield.node.useritem.RowItem

/**
 * Created by zhi.he on 2018/6/28.
 */
class DividerInterfaceProcessor : RowInterfaceProcessor() {
    override fun handleRowItem(sci: SectionCellInterface, rowItem: RowItem, section: Int, row: Int): Boolean {
        rowItem.dividerStyle ?: let { rowItem.dividerStyle = DividerStyle() }
        //上线定制
        if (sci is TopDividerInterface) {
            rowItem.dividerStyle.cellTopLineDrawable = sci.getTopDivider(section, row)
            val cellTopLineLeftOffset = sci.topDividerLeftOffset(section, row)
            val cellTopLineRightOffset = sci.topDividerRightOffset(section, row)
            if (cellTopLineLeftOffset >= 0 || cellTopLineRightOffset >= 0) {
                rowItem.dividerStyle.cellTopLineOffset = Rect(cellTopLineLeftOffset, 0, cellTopLineRightOffset, 0)
            }
        }
        var cellBottomLineLeftOffset = -1
        var cellBottomLineRightOffset = -1

        if (sci is DividerInterface) {
            //旧的接口同时会设置上分割线的显隐
            rowItem.showCellTopDivider = sci.showDivider(section, row)

            rowItem.showCellBottomDivider = sci.showDivider(section, row)
            cellBottomLineLeftOffset = sci.dividerOffset(section, row)
            rowItem.dividerStyle.cellBottomLineDrawable = sci.getDivider(section, row)
        }

        //这个接口优先级更高，但是-1不会覆盖上面接口的非-1值
        if (sci is DividerOffsetInterface) {
            var leftOffset = sci.getDividerLeftOffset(section, row)
            if (leftOffset >= 0) cellBottomLineLeftOffset = leftOffset
            cellBottomLineRightOffset = sci.getDividerRightOffset(section, row)
        }
        if (cellBottomLineLeftOffset >= 0 || cellBottomLineRightOffset >= 0) {
            rowItem.dividerStyle.cellBottomLineOffset = Rect(cellBottomLineLeftOffset, 0, cellBottomLineRightOffset, 0)
        }
        return false
//        nextProcessor?.handleRowItem(sci, rowItem, section, row)
    }
}