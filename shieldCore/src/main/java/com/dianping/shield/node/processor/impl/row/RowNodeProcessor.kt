package com.dianping.shield.node.processor.impl.row

import com.dianping.shield.node.cellnode.ShieldRow
import com.dianping.shield.node.processor.Processor
import com.dianping.shield.node.useritem.RowItem

/**
 * Created by zhi.he on 2018/6/25.
 */
abstract class RowNodeProcessor : Processor() {

    override fun handleData(vararg obj: Any?): Boolean {
        if (obj.size == 2 && obj[0] is RowItem && obj[1] is ShieldRow) {
            return handleRowItem(obj[0] as RowItem, obj[1] as ShieldRow)
        }
        return false
    }

    protected abstract fun handleRowItem(rowItem: RowItem, shieldRow: ShieldRow): Boolean
}