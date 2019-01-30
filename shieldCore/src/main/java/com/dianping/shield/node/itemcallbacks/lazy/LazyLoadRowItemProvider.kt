package com.dianping.shield.node.itemcallbacks.lazy

import com.dianping.shield.node.useritem.LayoutType
import com.dianping.shield.node.useritem.RowItem

/**
 * Created by zhi.he on 2018/7/13.
 */
interface LazyLoadRowItemProvider {
    fun getRowLayoutType(section: Int, row: Int): LayoutType

    fun isPreLoad(section: Int, row: Int): Boolean

    fun getRowViewCount(section: Int, row: Int): Int

    fun getRowItem(section: Int, row: Int): RowItem
}