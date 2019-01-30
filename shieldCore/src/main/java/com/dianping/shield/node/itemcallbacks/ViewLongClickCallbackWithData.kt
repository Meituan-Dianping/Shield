package com.dianping.shield.node.itemcallbacks

import android.view.View
import com.dianping.shield.node.cellnode.NodePath

/**
 * Created by zhi.he on 2018/6/21.
 */
interface ViewLongClickCallbackWithData {
    fun onViewLongClicked(view: View, data: Any?, path: NodePath?): Boolean
}