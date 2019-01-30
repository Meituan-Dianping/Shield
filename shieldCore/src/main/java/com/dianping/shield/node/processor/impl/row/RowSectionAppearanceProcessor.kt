package com.dianping.shield.node.processor.impl.row

import com.dianping.shield.node.cellnode.AttachStatusChangeListener
import com.dianping.shield.node.cellnode.ShieldRow
import com.dianping.shield.node.useritem.RowItem

/**
 * Created by runqi.wei at 2018/8/1
 */
class RowSectionAppearanceProcessor : RowNodeProcessor(){
    override fun handleRowItem(rowItem: RowItem, shieldRow: ShieldRow): Boolean {
        shieldRow.sectionParent?.rangeAppearStateManager?.let {
            if (shieldRow.attachStatusChangeListenerList == null) {
                shieldRow.attachStatusChangeListenerList = ArrayList()
            }
            shieldRow.attachStatusChangeListenerList?.add(AttachStatusChangeListener { position, data, oldStatus, attachStatus, direction ->
                shieldRow.sectionParent?.rangeAppearStateManager?.onEntryAttachStatusChanged(shieldRow, attachStatus, direction)
            })
        }
        return false
    }
}