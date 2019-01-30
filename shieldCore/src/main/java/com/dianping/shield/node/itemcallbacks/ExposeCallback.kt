package com.dianping.shield.node.itemcallbacks

import com.dianping.shield.node.cellnode.NodePath

/**
 * Created by zhi.he on 2018/6/20.
 */
interface ExposeCallback {
    fun onExpose(data: Any?, count: Int, path:NodePath?)
}