package com.dianping.shield.node.processor.legacy.section

import android.graphics.Rect
import com.dianping.agentsdk.framework.SectionCellInterface
import com.dianping.agentsdk.framework.SectionDividerInfoInterface
import com.dianping.shield.node.useritem.DividerStyle
import com.dianping.shield.node.useritem.SectionItem

/**
 * Created by zhi.he on 2018/6/28.
 */
class SectionDividerInfoInterfaceProcessor : SectionInterfaceProcessor() {
    override fun handleSectionItem(sci: SectionCellInterface, sectionItem: SectionItem, section: Int): Boolean {
        if (sci is SectionDividerInfoInterface) {
            sci.getDividerInfo(section)?.let {
                sectionItem.dividerStyle = DividerStyle().apply {
                    if (it.leftOffset >= 0 || it.rightOffset >= 0) {
                        //之前接口设计只对middle有效，并且单位为px，勉强适配
                        middleStyleLineOffset = Rect(it.leftOffset, 0, it.rightOffset, 0)
                    }
                    topStyleLineDrawable = it.topDividerDrawable
                    middleStyleLineDrawable = it.middleDividerDrawable
                    bottomStyleLineDrawable = it.bottomDividerDrawable
                }
            }
        }
        return false
    }
}