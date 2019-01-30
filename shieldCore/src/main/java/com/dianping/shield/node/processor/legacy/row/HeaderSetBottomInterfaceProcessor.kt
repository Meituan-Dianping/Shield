package com.dianping.shield.node.processor.legacy.row

import com.dianping.agentsdk.framework.SectionCellInterface
import com.dianping.agentsdk.framework.SectionExtraCellInterface
import com.dianping.shield.feature.ExtraCellBottomInterface
import com.dianping.shield.node.useritem.BottomInfo
import com.dianping.shield.node.useritem.RowItem

/**
 * Created by runqi.wei at 2018/10/29
 */
class HeaderSetBottomInterfaceProcessor : RowInterfaceProcessor() {
    override fun handleRowItem(sci: SectionCellInterface, rowItem: RowItem, section: Int, row: Int): Boolean {
        if (sci is SectionExtraCellInterface) {
            when (sci) {
                is ExtraCellBottomInterface -> {
                    val viewType = sci.getHeaderViewType(section)
                    if (sci.isHeaderBottomView(viewType)) {
                        rowItem.bottomInfo = BottomInfo()
                    }
                }
            }
        }
        return false
    }
}