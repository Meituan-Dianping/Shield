package com.dianping.shield.expose

import com.dianping.shield.entity.ScrollDirection
import com.dianping.shield.node.cellnode.AppearanceEvent
import com.dianping.shield.node.cellnode.AttachStatus

interface EntrySetHolder<T> {

    fun getEntryCount(): Int

    fun getEntry(position: Int): T?

    fun onAttachStateChanged(oldAttachStatus: AttachStatus?, newAttachStatus: AttachStatus?, direction: ScrollDirection?)

    fun onAppearanceEvent(appearanceEvent: AppearanceEvent?, direction: ScrollDirection?)

}