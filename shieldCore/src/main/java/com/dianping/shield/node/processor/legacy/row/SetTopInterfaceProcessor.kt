package com.dianping.shield.node.processor.legacy.row

import com.dianping.agentsdk.framework.SectionCellInterface
import com.dianping.shield.entity.CellType
import com.dianping.shield.feature.SetTopInterface
import com.dianping.shield.feature.SetTopParamsInterface
import com.dianping.shield.feature.TopPositionInterface
import com.dianping.shield.node.useritem.RowItem
import com.dianping.shield.node.useritem.TopInfoHelper

/**
 * Created by runqi.wei at 2018/8/16
 */
class SetTopInterfaceProcessor : RowInterfaceProcessor() {
    override fun handleRowItem(sci: SectionCellInterface, rowItem: RowItem, section: Int, row: Int): Boolean {
        when (sci) {
            is TopPositionInterface -> {
                sci.getTopPositionInfo(CellType.NORMAL, section, row)?.let {
                    rowItem.topInfo = TopInfoHelper.createTopInfo(it)
                }
            }
            is SetTopParamsInterface -> {
                val viewType = sci.getViewType(section, row)
                if (sci.isTopView(viewType)) {
                    rowItem.topInfo = TopInfoHelper.createTopInfo()
                    rowItem.topInfo?.let {
                        sci.getSetTopParams(viewType)?.apply {
                            it.offset = this.marginTopHeight
                        }
                    }
                }
            }
            is SetTopInterface -> {
                val viewType = sci.getViewType(section, row)
                if (sci.isTopView(viewType)) {
                    rowItem.topInfo = TopInfoHelper.createTopInfo()
                }
            }
        }
        return false
    }
}