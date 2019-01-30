package com.dianping.shield.node.cellnode.callback.lazyload

import com.dianping.shield.node.cellnode.ShieldRow
import com.dianping.shield.node.cellnode.ShieldSection

/**
 * Created by zhi.he on 2018/7/13.
 */
interface LazyLoadShieldRowProvider {
    fun getRowNodeCount(row: Int, sectionParent: ShieldSection): Int

    fun isPreLoad(row: Int, sectionParent: ShieldSection): Boolean

    fun getShieldRow(row: Int, sectionParent: ShieldSection): ShieldRow
}