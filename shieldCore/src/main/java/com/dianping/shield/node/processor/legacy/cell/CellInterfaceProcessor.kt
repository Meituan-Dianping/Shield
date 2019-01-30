package com.dianping.shield.node.processor.legacy.cell

import com.dianping.agentsdk.framework.SectionCellInterface
import com.dianping.shield.node.processor.Processor
import com.dianping.shield.node.useritem.ShieldSectionCellItem

/**
 * Created by zhi.he on 2018/6/26.
 */
abstract class CellInterfaceProcessor : Processor() {
    override fun handleData(vararg obj: Any?): Boolean {
        if (obj.size == 2 && obj[0] is SectionCellInterface && obj[1] is ShieldSectionCellItem) {
            return handleSectionCellInterface(obj[0] as SectionCellInterface, obj[1] as ShieldSectionCellItem)
        }
        return false
    }

    abstract fun handleSectionCellInterface(sci: SectionCellInterface, sectionCellItem: ShieldSectionCellItem): Boolean

}