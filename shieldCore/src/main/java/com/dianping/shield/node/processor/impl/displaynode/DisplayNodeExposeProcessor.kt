package com.dianping.shield.node.processor.impl.displaynode

import android.os.Handler
import android.os.Looper
import com.dianping.shield.node.cellnode.ShieldDisplayNode
import com.dianping.shield.node.processor.ExposeMoveStatusEventInfoHolder
import com.dianping.shield.node.processor.NodeExposeMoveStatusEventListener
import com.dianping.shield.node.useritem.ViewItem

/**
 * Created by runqi.wei at 2018/7/25
 */

class DisplayNodeExposeProcessor(private val infoHolder: ExposeMoveStatusEventInfoHolder) : DisplayNodeProcessor() {
    val handler: Handler by lazy(LazyThreadSafetyMode.NONE) {
        Handler(Looper.getMainLooper())
    }

    override fun handleViewItem(viewItem: ViewItem, dNode: ShieldDisplayNode): Boolean {

        dNode.apply {
            if (this.moveStatusEventListenerList == null) {
                this.moveStatusEventListenerList = ArrayList()
            }
            this.rowParent?.exposeInfoArr?.forEach { exposeInfo ->
                this.moveStatusEventListenerList?.add(NodeExposeMoveStatusEventListener(infoHolder, exposeInfo, handler))
            }
        }
        return false
    }

}