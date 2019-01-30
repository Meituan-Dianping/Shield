package com.dianping.shield.node.cellnode.callback

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.dianping.shield.node.cellnode.NodePath
import com.dianping.shield.node.cellnode.ShieldDisplayNode
import com.dianping.shield.node.itemcallbacks.ViewPaintingCallback
import com.dianping.shield.node.processor.NodeCreator

/**
 * Created by zhi.he on 2018/6/26.
 * originCallback Item上的原始callback
 */
class DefaultViewPaintingCallback(var originCallback: ViewPaintingCallback) : ViewPaintingCallback {
    override fun updateView(view: View, data: Any?, path: NodePath?) {
        if (data is ShieldDisplayNode) {
            originCallback.updateView(view, data.data, path)
        } else {
            originCallback.updateView(view, data, path)
        }
    }

    override fun onCreateView(context: Context, parent: ViewGroup?, viewType: String?): View {
        val originViewType = NodeCreator.revertViewType(viewType)
        return originCallback.onCreateView(context, parent, originViewType)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DefaultViewPaintingCallback

        if (originCallback != other.originCallback) return false

        return true
    }

    override fun hashCode(): Int {
        return originCallback.hashCode()
    }


}