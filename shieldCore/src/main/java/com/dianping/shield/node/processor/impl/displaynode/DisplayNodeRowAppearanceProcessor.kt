package com.dianping.shield.node.processor.impl.displaynode

import com.dianping.shield.node.cellnode.AttachStatusChangeListener
import com.dianping.shield.node.cellnode.ShieldDisplayNode
import com.dianping.shield.node.useritem.ViewItem

/**
 * Created by runqi.wei at 2018/7/31
 */
class DisplayNodeRowAppearanceProcessor : DisplayNodeProcessor() {
    override fun handleViewItem(viewItem: ViewItem, dNode: ShieldDisplayNode): Boolean {
        dNode.rowParent?.rangeAppearStateManager?.let {
            if (dNode.attachStatusChangeListenerList == null) {
                dNode.attachStatusChangeListenerList = ArrayList()
            }
            dNode.attachStatusChangeListenerList?.add(AttachStatusChangeListener { position, data, oldStatus, attachStatus, direction ->
                dNode.rowParent?.rangeAppearStateManager?.onEntryAttachStatusChanged(dNode, attachStatus, direction)
            })
        }

        return false
    }
}