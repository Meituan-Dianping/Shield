package com.dianping.shield.node.processor.legacy.section

import android.graphics.Rect
import com.dianping.agentsdk.framework.SectionCellInterface
import com.dianping.agentsdk.framework.SectionExtraCellInterface
import com.dianping.shield.node.cellnode.callback.legacy.LegacyFooterPaintingCallback
import com.dianping.shield.node.cellnode.callback.legacy.LegacyHeaderPaintingCallback
import com.dianping.shield.node.processor.NodeCreator
import com.dianping.shield.node.processor.ProcessorHolder
import com.dianping.shield.node.useritem.DividerStyle
import com.dianping.shield.node.useritem.RowItem
import com.dianping.shield.node.useritem.SectionItem
import com.dianping.shield.node.useritem.ViewItem

/**
 * Created by zhi.he on 2018/6/28.
 */
class ExtraCellInterfaceProcessor(private val processorHolder: ProcessorHolder) : SectionInterfaceProcessor() {
    override fun handleSectionItem(sci: SectionCellInterface, sectionItem: SectionItem, section: Int): Boolean {
        if (sci is SectionExtraCellInterface) {
            if (sci.hasHeaderForSection(section)) {
                var rowItem = RowItem().apply {
                    viewItems = ArrayList()
                    viewItems.add(ViewItem().apply {
                        viewType = "headercell${NodeCreator.viewTypeSepreator}${sci.getHeaderViewType(section)}"
                        viewPaintingCallback = LegacyHeaderPaintingCallback(sci)
                    })
                    showCellTopDivider = sci.hasTopDividerForHeader(section)
                    showCellBottomDivider = sci.hasBottomDividerForHeader(section)
                    dividerStyle ?: let { dividerStyle = DividerStyle() }
                    val offset = sci.getHeaderDividerOffset(section)
                    if (offset >= 0) {
                        dividerStyle.cellBottomLineOffset = Rect(offset.toInt(), 0, 0, 0)
                    }
                }
                processorHolder.headerInterfaceProcessorChain.startProcessor(sci, rowItem, section, -1)
                sectionItem.headerRowItem = rowItem
            }
            if (sci.hasFooterForSection(section)) {
                var rowItem = RowItem().apply {
                    viewItems = ArrayList()
                    viewItems.add(ViewItem().apply {
                        viewType = "footercell${NodeCreator.viewTypeSepreator}${sci.getFooterViewType(section)}"
                        viewPaintingCallback = LegacyFooterPaintingCallback(sci)
                    })
                    showCellBottomDivider = sci.hasBottomDividerForFooter(section)
                    dividerStyle ?: let { dividerStyle = DividerStyle() }
                    val offset = sci.getFooterDividerOffset(section)
                    if (offset >= 0) {
                        dividerStyle.bottomStyleLineOffset = Rect(offset.toInt(), 0, 0, 0)
                    }
                }

                processorHolder.footerInterfaceProcessorChain.startProcessor(sci, rowItem, section, -2)
                sectionItem.footerRowItem = rowItem
            }
        }
        return false
    }
}