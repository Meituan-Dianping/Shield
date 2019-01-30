package com.dianping.shield.node.cellnode.callback.lazyload

import com.dianping.shield.node.itemcallbacks.lazy.LazyLoadViewItemProvider
import com.dianping.shield.node.cellnode.ShieldDisplayNode
import com.dianping.shield.node.cellnode.ShieldRow
import com.dianping.shield.node.processor.ProcessorHolder

/**
 * Created by zhi.he on 2018/7/13.
 */
class DefaultDisplayNodeProvider(private val viewItemProvider: LazyLoadViewItemProvider, private val processorHolder: ProcessorHolder) : LazyLoadDisplayNodeProvider {
    override fun getShieldDisplayNodeAtPosition(viewPosition: Int, rowParent: ShieldRow): ShieldDisplayNode {
        var viewItem = viewItemProvider.getViewItem(viewPosition)
        return ShieldDisplayNode().apply {
            this.rowParent = rowParent
            this.pHolder = processorHolder
            processorHolder.nodeProcessorChain.startProcessor(viewItem, this)
        }
    }
}