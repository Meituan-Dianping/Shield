package com.dianping.shield.node.itemcallbacks

import com.dianping.shield.entity.ExposeScope
import com.dianping.shield.entity.ScrollDirection

/**
 * Created by zhi.he on 2018/6/21.
 */
interface MoveStatusCallback {
    fun onAppear(scope: ExposeScope, direction: ScrollDirection, data: Any?)
    fun onDisappear(scope: ExposeScope, direction: ScrollDirection, data: Any?)
}