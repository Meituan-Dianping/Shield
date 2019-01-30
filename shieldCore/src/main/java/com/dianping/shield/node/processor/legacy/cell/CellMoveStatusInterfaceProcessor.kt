package com.dianping.shield.node.processor.legacy.cell

import com.dianping.agentsdk.framework.SectionCellInterface
import com.dianping.shield.entity.ExposeScope
import com.dianping.shield.entity.ScrollDirection
import com.dianping.shield.feature.MoveStatusInterface
import com.dianping.shield.node.itemcallbacks.MoveStatusCallback
import com.dianping.shield.node.useritem.ShieldSectionCellItem

/**
 * Created by runqi.wei at 2018/11/14
 */
class CellMoveStatusInterfaceProcessor : CellInterfaceProcessor() {
    override fun handleSectionCellInterface(sci: SectionCellInterface, sectionCellItem: ShieldSectionCellItem): Boolean {
        if (sci is MoveStatusInterface) {
            sectionCellItem.moveStatusCallback = object : MoveStatusCallback {
                override fun onAppear(scope: ExposeScope, direction: ScrollDirection, data: Any?) {
                    sci.onAppear(scope, direction)
                }

                override fun onDisappear(scope: ExposeScope, direction: ScrollDirection, data: Any?) {
                    sci.onDisappear(scope, direction)
                }
            }
        }
        return false
    }
}