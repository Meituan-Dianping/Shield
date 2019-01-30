package com.dianping.shield.node.processor.impl.displaynode

import com.dianping.shield.node.cellnode.ShieldDisplayNode
import com.dianping.shield.node.cellnode.callback.ViewClickPaintingCallback
import com.dianping.shield.node.cellnode.callback.ViewLongClickPaintingCallback
import com.dianping.shield.node.useritem.ViewItem

/**
 * Created by zhi.he on 2018/7/4.
 */
class ClickDisplayNodeProcessor : DisplayNodeProcessor() {
    override fun handleViewItem(viewItem: ViewItem, dNode: ShieldDisplayNode): Boolean {
        dNode.viewPaintingCallback?.let { dNodePaintCallback ->
            viewItem.clickCallback?.let {
                dNode.clickCallback = it
                dNode.viewPaintingCallback = ViewClickPaintingCallback(dNodePaintCallback, viewItem)
            }
            viewItem.longClickCallback?.let {
                dNode.longClickCallback = it
                dNode.viewPaintingCallback = ViewLongClickPaintingCallback(dNodePaintCallback, viewItem)
            }
        }

        return false
    }
}