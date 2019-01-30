package com.dianping.shield.node.processor.legacy.cell

import com.dianping.agentsdk.framework.SectionCellInterface
import com.dianping.shield.feature.RecyclerPoolSizeInterface
import com.dianping.shield.feature.ScrollToTopOffsetInterface
import com.dianping.shield.feature.StaggeredGridCellInfoInterface
import com.dianping.shield.node.processor.ProcessorHolder
import com.dianping.shield.node.useritem.DividerStyle
import com.dianping.shield.node.useritem.SectionItem
import com.dianping.shield.node.useritem.ShieldSectionCellItem
import com.dianping.shield.node.useritem.StaggeredGridSectionItem

/**
 * Created by zhi.he on 2018/6/28.
 */
class NormalCellInterfaceProcessor(private val holder: ProcessorHolder) : CellInterfaceProcessor() {

    override fun handleSectionCellInterface(sci: SectionCellInterface, sectionCellItem: ShieldSectionCellItem): Boolean {
        val sectionCount = sci.sectionCount
        if (sectionCount > 0) {
            sectionCellItem.sectionItems ?: let { sectionCellItem.sectionItems = ArrayList() }

            for (i in 0 until sectionCount) {

                if (sci is StaggeredGridCellInfoInterface && sci.spanCount(i) > 1) {
                    sectionCellItem.sectionItems?.add(StaggeredGridSectionItem().apply {
                        spanCount = sci.spanCount(i)
                        xStaggeredGridGap = sci.xStaggeredGridGap(i)
                        yStaggeredGridGap = sci.yStaggeredGridGap(i)
                        staggeredLeftMargin = sci.staggeredGridLeftMargin(i);
                        staggeredRightMargin = sci.staggeredGridRightMargin(i);
                        holder.sectionInterfaceProcessorChain.startProcessor(sci, this, i)
                        if(spanCount>1){
                            dividerShowType = DividerStyle.ShowType.NONE
                        }
                    })
                } else {
                    sectionCellItem.sectionItems?.add(SectionItem().apply {
                        holder.sectionInterfaceProcessorChain.startProcessor(sci, this, i)
                    })
                }

            }
            if (sci is ScrollToTopOffsetInterface) {
                sectionCellItem.needScrollToTop = sci.needScrollToTop();
            }
        }
        if (sci is RecyclerPoolSizeInterface) {
            sci.recyclerableViewSizeMap()?.forEach {
                sectionCellItem.recyclerViewTypeSizeMap
                        ?: let { sectionCellItem.recyclerViewTypeSizeMap = HashMap() }
                sectionCellItem.recyclerViewTypeSizeMap[it.key.toString()] = it.value
            }
        }

        return false
    }
}