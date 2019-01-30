package com.dianping.shield.node.processor.impl.displaynode

import com.dianping.shield.node.cellnode.ShieldDisplayNode
import com.dianping.shield.node.useritem.ViewItem

/**
 * Created by runqi.wei at 2018/9/11
 */
class DisplayNodeHotZoneProcessor : DisplayNodeProcessor() {
    override fun handleViewItem(viewItem: ViewItem, dNode: ShieldDisplayNode): Boolean {
        dNode.apply {
            this.hotZoneList = this.rowParent?.hotZoneArray
        }
        return false
    }
}