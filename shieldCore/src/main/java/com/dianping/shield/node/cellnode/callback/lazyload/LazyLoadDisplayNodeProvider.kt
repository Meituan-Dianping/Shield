package com.dianping.shield.node.cellnode.callback.lazyload

import com.dianping.shield.node.cellnode.ShieldDisplayNode
import com.dianping.shield.node.cellnode.ShieldRow

/**
 * Created by zhi.he on 2018/7/13.
 */
interface LazyLoadDisplayNodeProvider {
    fun getShieldDisplayNodeAtPosition(viewPosition: Int, rowParent: ShieldRow): ShieldDisplayNode
}