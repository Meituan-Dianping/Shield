package com.dianping.shield.node.itemcallbacks

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.dianping.shield.node.cellnode.NodePath

/**
 * Created by zhi.he on 2018/6/21.
 */
interface ViewPaintingCallback {
    fun onCreateView(context: Context, parent: ViewGroup?, viewType: String?): View

    fun updateView(view: View, data: Any?, path: NodePath?)
}