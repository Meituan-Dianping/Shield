package com.dianping.shield.node.processor.legacy.cell

import com.dianping.agentsdk.framework.CellStatusMoreInterface
import com.dianping.agentsdk.framework.SectionCellInterface
import com.dianping.shield.feature.LoadingAndLoadingMoreCreator
import com.dianping.shield.node.cellnode.callback.legacy.LegacyLoadingMorePaintingCallback
import com.dianping.shield.node.processor.NodeCreator
import com.dianping.shield.node.processor.legacy.LegacyLoadingMoreListener
import com.dianping.shield.node.processor.legacy.LegacyRetryClickListener
import com.dianping.shield.node.useritem.ShieldSectionCellItem
import com.dianping.shield.node.useritem.ViewItem

/**
 * Created by zhi.he on 2018/6/28.
 */
class CellStatusMoreInterfaceProcessor(private val creator: LoadingAndLoadingMoreCreator?) : CellInterfaceProcessor() {
    override fun handleSectionCellInterface(sci: SectionCellInterface, sectionCellItem: ShieldSectionCellItem): Boolean {
        if (sci is CellStatusMoreInterface) {
            sectionCellItem.loadingMoreStatus = sci.loadingMoreStatus()
//            sci.loadingMoreView()?.let {
            sectionCellItem.loadingMoreViewItem = ViewItem().apply {
                viewType = NodeCreator.LOADING_MORE_TYPE_CUSTOM
                data = viewType
                viewPaintingCallback = LegacyLoadingMorePaintingCallback(sci, creator)
            }
//            }
//            sci.loadingMoreFailedView()?.let {
            sectionCellItem.loadingMoreFailedViewItem = ViewItem().apply {
                viewType = NodeCreator.LOADING_MORE_FAILED_TYPE_CUSTOM
                data = viewType
                viewPaintingCallback = LegacyLoadingMorePaintingCallback(sci, creator)
                sci.loadingMoreRetryListener()?.let {
                    clickCallback = LegacyRetryClickListener(it)
                }
            }
//            }
            sectionCellItem.loadingMoreViewPaintingListener = LegacyLoadingMoreListener(sci)
        }
        return false
    }
}