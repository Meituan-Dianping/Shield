package com.dianping.shield.node.processor.impl.displaynode

import com.dianping.shield.node.cellnode.ShieldDisplayNode
import com.dianping.shield.node.processor.Processor
import com.dianping.shield.node.useritem.ViewItem

/**
 * Created by zhi.he on 2018/7/4.
 */
abstract class DisplayNodeProcessor : Processor() {

    override fun handleData(vararg obj: Any?): Boolean {
        if (obj.size == 2 && obj[0] is ViewItem && obj[1] is ShieldDisplayNode) {
            return handleViewItem(obj[0] as ViewItem, obj[1] as ShieldDisplayNode)
        }
        return false
    }

    protected abstract fun handleViewItem(viewItem: ViewItem, dNode: ShieldDisplayNode): Boolean
}