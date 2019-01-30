package com.dianping.shield.node.processor.legacy.section

import com.dianping.agentsdk.framework.SectionCellInterface
import com.dianping.shield.feature.SectionTitleInterface
import com.dianping.shield.node.itemcallbacks.lazy.LegacyInterfaceRowItemProvider
import com.dianping.shield.node.processor.ProcessorHolder
import com.dianping.shield.node.useritem.SectionItem

/**
 * Created by zhi.he on 2018/6/28.
 */
class NormalSectionInterfaceProcessor(private val holder: ProcessorHolder) : SectionInterfaceProcessor() {

    override fun handleSectionItem(sci: SectionCellInterface, sectionItem: SectionItem, section: Int): Boolean {
        val rowCount = sci.getRowCount(section)
        if (rowCount > 0) {
            sectionItem.isLazyLoad = true
            sectionItem.rowCount = rowCount
            sectionItem.lazyLoadRowItemProvider = LegacyInterfaceRowItemProvider(sci, holder)
        }
        if (sci is SectionTitleInterface) {
            sectionItem.sectionTitle = sci.getSectionTitle(section)
        }
        return false
    }
}
