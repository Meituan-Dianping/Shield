package com.dianping.shield.node.processor.legacy.row

import com.dianping.agentsdk.framework.SectionCellInterface
import com.dianping.shield.node.processor.Processor
import com.dianping.shield.node.useritem.RowItem

/**
 * Created by zhi.he on 2018/6/28.
 */
abstract class RowInterfaceProcessor : Processor() {
    override fun handleData(vararg obj: Any?): Boolean {
        if (obj.size == 4 && obj[0] is SectionCellInterface && obj[1] is RowItem && obj[2] is Int && obj[3] is Int) {
            return handleRowItem(obj[0] as SectionCellInterface, obj[1] as RowItem, obj[2] as Int, obj[3] as Int)
        }
        return false
    }

    abstract fun handleRowItem(sci: SectionCellInterface, rowItem: RowItem, section: Int, row: Int): Boolean
}