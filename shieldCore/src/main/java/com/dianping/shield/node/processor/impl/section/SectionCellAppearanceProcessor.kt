package com.dianping.shield.node.processor.impl.section

import com.dianping.shield.node.cellnode.AttachStatusChangeListener
import com.dianping.shield.node.cellnode.ShieldSection
import com.dianping.shield.node.useritem.SectionItem

/**
 * Created by runqi.wei at 2018/8/1
 */
class SectionCellAppearanceProcessor : SectionNodeProcessor(){
    override fun handleShieldSection(sectionItem: SectionItem, shieldSection: ShieldSection): Boolean {
        shieldSection.cellParent?.rangeAppearStateManager?.let {
            if (shieldSection.attachStatusChangeListenerList == null) {
                shieldSection.attachStatusChangeListenerList = ArrayList()
            }
            shieldSection.attachStatusChangeListenerList?.add(AttachStatusChangeListener { position, data, oldStatus, attachStatus, direction ->
                shieldSection.cellParent?.rangeAppearStateManager?.onEntryAttachStatusChanged(shieldSection, attachStatus, direction)
            })
        }
        return false
    }
}