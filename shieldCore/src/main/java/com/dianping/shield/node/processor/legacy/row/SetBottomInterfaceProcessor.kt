package com.dianping.shield.node.processor.legacy.row

import com.dianping.agentsdk.framework.SectionCellInterface
import com.dianping.shield.feature.SetBottomInterface
import com.dianping.shield.node.useritem.BottomInfo
import com.dianping.shield.node.useritem.RowItem

/**
 * Created by runqi.wei at 2018/10/29
 */
class SetBottomInterfaceProcessor : RowInterfaceProcessor() {
    override fun handleRowItem(sci: SectionCellInterface, rowItem: RowItem, section: Int, row: Int): Boolean {
        when (sci) {
            is SetBottomInterface -> {
                val viewType = sci.getViewType(section, row)
                if (sci.isBottomView(viewType)) {
                    rowItem.bottomInfo = BottomInfo()
                }
            }
        }
        return false
    }
}