package com.dianping.shield.node.processor.legacy.section

import com.dianping.agentsdk.framework.SectionCellInterface
import com.dianping.shield.node.processor.Processor
import com.dianping.shield.node.useritem.SectionItem

/**
 * Created by zhi.he on 2018/6/28.
 */
abstract class SectionInterfaceProcessor : Processor() {
    override fun handleData(vararg obj: Any?): Boolean {
        if (obj.size == 3 && obj[0] is SectionCellInterface && obj[1] is SectionItem && obj[2] is Int) {
            return handleSectionItem(obj[0] as SectionCellInterface, obj[1] as SectionItem, obj[2] as Int)
        }
        return false
    }

    abstract fun handleSectionItem(sci: SectionCellInterface, sectionItem: SectionItem, section: Int): Boolean
}