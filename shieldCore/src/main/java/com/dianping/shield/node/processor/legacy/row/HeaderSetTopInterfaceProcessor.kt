package com.dianping.shield.node.processor.legacy.row

import com.dianping.agentsdk.framework.SectionCellInterface
import com.dianping.agentsdk.framework.SectionExtraCellInterface
import com.dianping.shield.entity.CellType
import com.dianping.shield.feature.ExtraCellTopInterface
import com.dianping.shield.feature.ExtraCellTopParamsInterface
import com.dianping.shield.feature.TopPositionInterface
import com.dianping.shield.node.useritem.RowItem
import com.dianping.shield.node.useritem.TopInfoHelper

/**
 * Created by runqi.wei at 2018/8/17
 */
class HeaderSetTopInterfaceProcessor : RowInterfaceProcessor() {
    override fun handleRowItem(sci: SectionCellInterface, rowItem: RowItem, section: Int, row: Int): Boolean {
        if (sci is SectionExtraCellInterface) {
            when (sci) {
                is TopPositionInterface -> {
                    sci.getTopPositionInfo(CellType.HEADER, section, row)?.let {
                        rowItem.topInfo = TopInfoHelper.createTopInfo(it)
                    }
                }
                is ExtraCellTopParamsInterface -> {
                    val viewType = sci.getHeaderViewType(section)
                    if (sci.isHeaderTopView(viewType)) {
                        rowItem.topInfo = TopInfoHelper.createTopInfo()
                        rowItem.topInfo?.let {
                            sci.getHeaderSetTopParams(viewType)?.apply {
                                it.offset = this.marginTopHeight
                            }
                        }
                    }
                }
                is ExtraCellTopInterface -> {
                    val viewType = sci.getHeaderViewType(section)
                    if (sci.isHeaderTopView(viewType)) {
                        rowItem.topInfo = TopInfoHelper.createTopInfo()
                    }
                }

            }
        }
        return false
    }
}