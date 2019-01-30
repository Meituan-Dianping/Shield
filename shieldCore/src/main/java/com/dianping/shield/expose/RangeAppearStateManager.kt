package com.dianping.shield.expose

import com.dianping.shield.entity.ScrollDirection
import com.dianping.shield.node.cellnode.AppearanceEvent
import com.dianping.shield.node.cellnode.AttachStatus

/**
 * Created by runqi.wei at 2018/7/30
 */

class RangeAppearStateManager<E>(private val entrySetHolder: EntrySetHolder<E>) {

    var entryEventMap = HashMap<E, AttachStatus>()
    var oldAttachStatus: AttachStatus = AttachStatus.DETACHED

    fun onEntryAttachStatusChanged(entry: E, attachStatus: AttachStatus?, direction: ScrollDirection?) {
        if (attachStatus == AttachStatus.DETACHED || attachStatus == null) {
            entryEventMap.remove(entry)
        } else {
            entryEventMap[entry] = attachStatus
        }

        checkSetState(direction)
    }

    private fun checkSetState(direction: ScrollDirection?) {

        var newStatus = getNewStatus()
        if (newStatus != oldAttachStatus) {
            var oldS = oldAttachStatus
            oldAttachStatus = newStatus
            entrySetHolder.onAttachStateChanged(oldS, newStatus, direction)
            var appearanceEventArr = AppearanceEvent.parseFromAttachStatus(oldS, newStatus)
            appearanceEventArr?.apply {
                for (event in appearanceEventArr) {
                    entrySetHolder.onAppearanceEvent(event, direction)
                }
            }
        }
    }

    private fun getNewStatus(): AttachStatus {
        when {
            entryEventMap.isEmpty() -> return AttachStatus.DETACHED
            entrySetHolder.getEntryCount() > entryEventMap.size -> return AttachStatus.PARTLY_ATTACHED
            else -> {
                var wholeStatus: AttachStatus? = null
                for (i in 0 until entrySetHolder.getEntryCount()) {
                    entrySetHolder.getEntry(i).apply {
                        var status = entryEventMap[this] ?: AttachStatus.DETACHED
                        if (status == AttachStatus.PARTLY_ATTACHED) {
                            return AttachStatus.PARTLY_ATTACHED
                        }
                        if (wholeStatus == null) {
                            wholeStatus = status
                        }

                        if (wholeStatus != status) {
                            return AttachStatus.PARTLY_ATTACHED
                        }
                    }
                }

                return wholeStatus ?: AttachStatus.DETACHED
            }
        }

    }

    fun findCurrentStatus(entry: E): AttachStatus {
        return entryEventMap[entry] ?: AttachStatus.DETACHED
    }

}