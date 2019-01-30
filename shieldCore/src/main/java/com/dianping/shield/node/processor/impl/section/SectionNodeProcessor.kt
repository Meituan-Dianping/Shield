package com.dianping.shield.node.processor.impl.section

import com.dianping.shield.node.cellnode.ShieldSection
import com.dianping.shield.node.processor.Processor
import com.dianping.shield.node.useritem.SectionItem

/**
 * Created by zhi.he on 2018/6/25.
 */
abstract class SectionNodeProcessor : Processor() {

    override fun handleData(vararg obj: Any?): Boolean {
        if (obj.size == 2 && obj[0] is SectionItem && obj[1] is ShieldSection) {
            return handleShieldSection(obj[0] as SectionItem, obj[1] as ShieldSection)
        }
        return false
    }

    protected abstract fun handleShieldSection(sectionItem: SectionItem, shieldSection: ShieldSection): Boolean
}