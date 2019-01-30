package com.dianping.shield.node.processor.impl.divider

import com.dianping.shield.node.cellnode.ShieldDisplayNode
import com.dianping.shield.node.processor.Processor

/**
 * Created by zhi.he on 2018/7/2.
 */
abstract class DividerInfoProcessor : Processor() {
    override fun handleData(vararg obj: Any?): Boolean {
        if (obj.size == 1 && obj[0] is ShieldDisplayNode) {
            return handleDividerInfo(obj[0] as ShieldDisplayNode)
        }
        return false
    }

    abstract fun handleDividerInfo(dNode: ShieldDisplayNode): Boolean
}