package com.dianping.shield.node.itemcallbacks.lazy

import com.dianping.agentsdk.framework.SectionCellInterface
import com.dianping.shield.entity.CellType
import com.dianping.shield.feature.SetBottomInterface
import com.dianping.shield.feature.SetTopInterface
import com.dianping.shield.feature.SetTopParamsInterface
import com.dianping.shield.feature.TopPositionInterface
import com.dianping.shield.node.processor.ProcessorHolder
import com.dianping.shield.node.useritem.LayoutType
import com.dianping.shield.node.useritem.RowItem

/**
 * Created by zhi.he on 2018/7/15.
 */
open class LegacyInterfaceRowItemProvider(private val sci: SectionCellInterface, private val processorHolder: ProcessorHolder) : LazyLoadRowItemProvider {
    override fun isPreLoad(section: Int, row: Int): Boolean {
        return when (sci) {
            is TopPositionInterface -> {
                sci.getTopPositionInfo(CellType.NORMAL, section, row) != null
            }
            is SetTopParamsInterface -> {
                val viewType = sci.getViewType(section, row)
                sci.isTopView(viewType)
            }
            is SetTopInterface -> {
                val viewType = sci.getViewType(section, row)
                sci.isTopView(viewType)
            }
            else -> {
                false
            }
        } || when (sci) {
            is SetBottomInterface -> {
                val viewType = sci.getViewType(section, row)
                sci.isBottomView(viewType)
            }
            else -> {
                false
            }
        }
    }

    override fun getRowLayoutType(section: Int, row: Int): LayoutType {
        return LayoutType.LINEAR_FULL_FILL
    }

    override fun getRowViewCount(section: Int, row: Int): Int {
        return 1
    }

    override fun getRowItem(section: Int, row: Int): RowItem {
        return RowItem().apply {
            processorHolder.rowInterfaceProcessorChain.startProcessor(sci, this, section, row)
        }
    }

}