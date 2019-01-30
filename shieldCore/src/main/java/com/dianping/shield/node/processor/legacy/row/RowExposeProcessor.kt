package com.dianping.shield.node.processor.legacy.row

import com.dianping.agentsdk.framework.SectionCellInterface
import com.dianping.shield.feature.CellExposedInterface
import com.dianping.shield.node.cellnode.NodePath
import com.dianping.shield.node.itemcallbacks.ExposeCallback
import com.dianping.shield.node.useritem.ExposeInfo
import com.dianping.shield.node.useritem.RowItem

/**
 * Created by runqi.wei at 2018/7/27
 */

class RowExposeProcessor : RowInterfaceProcessor() {
    override fun handleRowItem(sci: SectionCellInterface, rowItem: RowItem, section: Int, row: Int): Boolean {

        if (sci is CellExposedInterface) {

            if (rowItem.exposeInfoArray == null) {
                rowItem.exposeInfoArray = ArrayList()
            }

            var exposeInfo = ExposeInfo()
            exposeInfo.exposeScope = sci.getExposeScope(section, row)
            exposeInfo.exposeDuration = sci.exposeDuration(section, row)
            exposeInfo.stayDuration = sci.stayDuration(section, row)
            exposeInfo.maxExposeCount = sci.maxExposeCount(section, row)
            exposeInfo.agentExposeCallback = object : ExposeCallback {
                override fun onExpose(data: Any?, count: Int, path: NodePath?) {
                    path?.apply {
                        sci.onExposed(path.section, path.row, count)
                    }
                }
            }
            rowItem.exposeInfoArray.add(exposeInfo)
        }

        return false
    }

}