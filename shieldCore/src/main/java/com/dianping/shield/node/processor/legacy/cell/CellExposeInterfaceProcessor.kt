package com.dianping.shield.node.processor.legacy.cell

import com.dianping.agentsdk.framework.SectionCellInterface
import com.dianping.shield.feature.ExposedInterface
import com.dianping.shield.node.cellnode.NodePath
import com.dianping.shield.node.itemcallbacks.ExposeCallback
import com.dianping.shield.node.useritem.ExposeInfo
import com.dianping.shield.node.useritem.ShieldSectionCellItem

/**
 * Created by runqi.wei at 2018/8/1
 */
class CellExposeInterfaceProcessor : CellInterfaceProcessor() {
    override fun handleSectionCellInterface(sci: SectionCellInterface, sectionCellItem: ShieldSectionCellItem): Boolean {
        if (sci is ExposedInterface) {
            if (sectionCellItem.exposeInfo == null) {
                sectionCellItem.exposeInfo = ArrayList()
            }
            var exposeInfo = ExposeInfo()
            exposeInfo.exposeScope = sci.exposeScope
            exposeInfo.exposeDuration = sci.exposeDuration()
            exposeInfo.stayDuration = sci.stayDuration()
            exposeInfo.maxExposeCount = sci.maxExposeCount()
            exposeInfo.agentExposeCallback = object : ExposeCallback {
                override fun onExpose(data: Any?, count: Int, path: NodePath?) {
                    sci.onExposed(count)
                }
            }
            sectionCellItem.exposeInfo.add(exposeInfo)
        }
        return false
    }
}