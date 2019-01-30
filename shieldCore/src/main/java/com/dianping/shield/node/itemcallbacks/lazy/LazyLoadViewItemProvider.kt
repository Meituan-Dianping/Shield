package com.dianping.shield.node.itemcallbacks.lazy

import com.dianping.shield.node.useritem.ViewItem

/**
 * Created by zhi.he on 2018/7/13.
 */
interface LazyLoadViewItemProvider {
    fun getViewItem(viewPosition: Int): ViewItem
}