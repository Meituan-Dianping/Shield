package com.dianping.shield.node.processor.legacy.row

import com.dianping.agentsdk.framework.SectionCellInterface
import com.dianping.shield.entity.CellType
import com.dianping.shield.feature.ExtraCellExposedInterface
import com.dianping.shield.node.cellnode.NodePath
import com.dianping.shield.node.itemcallbacks.ExposeCallback
import com.dianping.shield.node.useritem.ExposeInfo
import com.dianping.shield.node.useritem.RowItem

/**
 * Created by runqi.wei at 2018/7/27
 */

class HeaderRowExposeProcessor : RowInterfaceProcessor() {
    override fun handleRowItem(sci: SectionCellInterface, rowItem: RowItem, section: Int, row: Int): Boolean {
        if (sci is ExtraCellExposedInterface) {

            if (rowItem.exposeInfoArray == null) {
                rowItem.exposeInfoArray = ArrayList()
            }

            var exposeInfo = ExposeInfo()
            exposeInfo.exposeScope = sci.getExtraCellExposeScope(section, CellType.HEADER)
            exposeInfo.exposeDuration = sci.extraCellExposeDuration(section, CellType.HEADER)
            exposeInfo.stayDuration = sci.extraCellStayDuration(section, CellType.HEADER)
            exposeInfo.maxExposeCount = sci.maxExtraExposeCount(section, CellType.HEADER)
            exposeInfo.agentExposeCallback = object : ExposeCallback {
                override fun onExpose(data: Any?, count: Int, path: NodePath?) {
                    path?.apply {
                        sci.onExtraCellExposed(path.section, CellType.HEADER, count)
                    }
                }
            }
            rowItem.exposeInfoArray.add(exposeInfo)
        }
        return false
    }

}