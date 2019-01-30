package com.dianping.shield.node.processor.impl.row

import com.dianping.shield.node.cellnode.ShieldRow
import com.dianping.shield.node.useritem.RowItem

/**
 * Created by runqi.wei at 2018/8/17
 */
class RowTopInfoProcessor : RowNodeProcessor() {
    override fun handleRowItem(rowItem: RowItem, shieldRow: ShieldRow): Boolean {
        shieldRow.topInfo = rowItem.topInfo
        return false
    }
}