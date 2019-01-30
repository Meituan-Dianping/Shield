package com.dianping.shield.node.processor.legacy.section

import com.dianping.agentsdk.framework.DividerInterface
import com.dianping.agentsdk.framework.SectionCellInterface
import com.dianping.shield.node.useritem.DividerStyle
import com.dianping.shield.node.useritem.SectionItem

/**
 * Created by zhi.he on 2018/6/28.
 */
class DividerShowTypeInterfaceProcessor : SectionInterfaceProcessor() {
    override fun handleSectionItem(sci: SectionCellInterface, sectionItem: SectionItem, section: Int): Boolean {
        if (sci is DividerInterface) {
            sectionItem.dividerShowType = when (sci.dividerShowType(section)) {
                DividerInterface.ShowType.TOP_END -> DividerStyle.ShowType.TOP_BOTTOM
                DividerInterface.ShowType.ALL -> DividerStyle.ShowType.ALL
                DividerInterface.ShowType.NONE -> DividerStyle.ShowType.NONE
                DividerInterface.ShowType.MIDDLE -> DividerStyle.ShowType.MIDDLE
                DividerInterface.ShowType.NO_TOP -> DividerStyle.ShowType.NO_TOP
                DividerInterface.ShowType.DEFAULT -> DividerStyle.ShowType.DEFAULT
                else -> DividerStyle.ShowType.DEFAULT
            }
        }
        return false
    }
}
