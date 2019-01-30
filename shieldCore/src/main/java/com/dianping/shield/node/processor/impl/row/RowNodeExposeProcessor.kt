package com.dianping.shield.node.processor.impl.row

import com.dianping.shield.node.cellnode.ShieldRow
import com.dianping.shield.node.useritem.RowItem

/**
 * Created by runqi.wei at 2018/7/27
 */
class RowNodeExposeProcessor : RowNodeProcessor(){
    override fun handleRowItem(rowItem: RowItem, shieldRow: ShieldRow): Boolean {
        shieldRow.exposeInfoArr = rowItem.exposeInfoArray
        return false
    }

}