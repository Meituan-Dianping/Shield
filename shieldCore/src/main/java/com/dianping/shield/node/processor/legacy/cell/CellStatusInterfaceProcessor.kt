package com.dianping.shield.node.processor.legacy.cell

import com.dianping.agentsdk.framework.CellStatusInterface
import com.dianping.agentsdk.framework.SectionCellInterface
import com.dianping.shield.feature.LoadingAndLoadingMoreCreator
import com.dianping.shield.node.cellnode.callback.legacy.LegacyLoadingPaintingCallback
import com.dianping.shield.node.processor.NodeCreator
import com.dianping.shield.node.processor.legacy.LegacyRetryClickListener
import com.dianping.shield.node.useritem.ShieldSectionCellItem
import com.dianping.shield.node.useritem.ViewItem

/**
 * Created by zhi.he on 2018/6/26.
 */
class CellStatusInterfaceProcessor(private val creator: LoadingAndLoadingMoreCreator?) : CellInterfaceProcessor() {
    override fun handleSectionCellInterface(sci: SectionCellInterface, sectionCellItem: ShieldSectionCellItem): Boolean {
        if (sci is CellStatusInterface) {
            sectionCellItem.loadingStatus = sci.loadingStatus()
//            sci.loadingView()?.let {
//                sectionCellItem.loadingViewItem = ViewItem().apply {
//                    viewType = NodeCreator.LOADING_TYPE_CUSTOM
//                    viewPaintingCallback = LegacyLoadingPaintingCallback(sci)
//                }
//            }
            sectionCellItem.loadingViewItem = ViewItem().apply {
                viewType = NodeCreator.LOADING_TYPE_CUSTOM
                viewPaintingCallback = LegacyLoadingPaintingCallback(sci, creator)
            }

//            sci.loadingFailedView()?.let {
            sectionCellItem.failedViewItem = ViewItem().apply {
                viewType = NodeCreator.FAILED_TYPE_CUSTOM
                viewPaintingCallback = LegacyLoadingPaintingCallback(sci, creator)
                sci.loadingRetryListener()?.let {
                    clickCallback = LegacyRetryClickListener(it)
                }
            }
//            }
//            sci.emptyView()?.let {
            sectionCellItem.emptyViewItem = ViewItem().apply {
                viewType = NodeCreator.EMPTY_TYPE_CUSTOM
                viewPaintingCallback = LegacyLoadingPaintingCallback(sci, creator)
            }
//            }
        }
        return false
    }
}