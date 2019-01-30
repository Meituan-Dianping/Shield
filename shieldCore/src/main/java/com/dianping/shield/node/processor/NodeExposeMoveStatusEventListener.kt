package com.dianping.shield.node.processor

import android.os.Handler
import com.dianping.shield.expose.MoveStatusExposeEngine
import com.dianping.shield.node.cellnode.NodePath
import com.dianping.shield.node.cellnode.ShieldDisplayNode
import com.dianping.shield.node.useritem.ExposeInfo

/**
 * Created by runqi.wei at 2018/8/10
 */
class NodeExposeMoveStatusEventListener(private val infoHolder: ExposeMoveStatusEventInfoHolder,
                                        private val exposeInfo: ExposeInfo,
                                        private val handler: Handler)
    : ExposeMoveStatusEventListener<ShieldDisplayNode>(infoHolder, exposeInfo, handler) {

    init {
        moveStatusExposeEngine = object : MoveStatusExposeEngine<ShieldDisplayNode>(infoHolder, handler, exposeInfo) {
            override fun getPath(item: ShieldDisplayNode?): NodePath? {
                return item?.path
            }
        }
    }
}