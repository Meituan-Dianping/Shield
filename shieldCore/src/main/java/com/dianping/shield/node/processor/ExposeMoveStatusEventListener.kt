package com.dianping.shield.node.processor

import android.os.Handler
import com.dianping.shield.entity.ScrollDirection
import com.dianping.shield.expose.MoveStatusExposeEngine
import com.dianping.shield.node.cellnode.AppearanceEvent
import com.dianping.shield.node.cellnode.MoveStatusEventListener
import com.dianping.shield.node.useritem.ExposeInfo

open class ExposeMoveStatusEventListener<T>(infoHolder: ExposeMoveStatusEventInfoHolder, exposeInfo: ExposeInfo, handler: Handler) : MoveStatusEventListener<T> {

    protected var moveStatusExposeEngine : MoveStatusExposeEngine<T> = MoveStatusExposeEngine(infoHolder, handler, exposeInfo)

    var currentDelayRunnable: Runnable? = null

    override fun reset(item: T) {
        moveStatusExposeEngine.reset(item)
    }

    override fun onAppeared(position: Int, item: T, appearEvent: AppearanceEvent?, direction: ScrollDirection?) {
        moveStatusExposeEngine.onAppeared(item, appearEvent)
    }

    override fun onDisappeared(position: Int, item: T, appearEvent: AppearanceEvent?, direction: ScrollDirection?) {
        moveStatusExposeEngine.onDisappeared(item, appearEvent)
    }

}