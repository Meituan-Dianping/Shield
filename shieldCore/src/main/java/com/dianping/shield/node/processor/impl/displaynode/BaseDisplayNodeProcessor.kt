package com.dianping.shield.node.processor.impl.displaynode

import android.content.Context
import com.dianping.shield.node.cellnode.ShieldDisplayNode
import com.dianping.shield.node.cellnode.StaggeredGridSection
import com.dianping.shield.node.cellnode.callback.DefaultViewPaintingCallback
import com.dianping.shield.node.processor.NodeCreator
import com.dianping.shield.node.useritem.ViewItem

/**
 * Created by zhi.he on 2018/7/4.
 */
class BaseDisplayNodeProcessor(private val context: Context) : DisplayNodeProcessor() {
    override fun handleViewItem(viewItem: ViewItem, dNode: ShieldDisplayNode): Boolean {
        viewItem.viewType?.let {
            dNode.viewType = "${dNode.rowParent?.typePrefix?.let { "$it${NodeCreator.viewTypeSepreator}" }
                    ?: let { "" }}${viewItem.viewType}"
        }
        viewItem.stableid?.let {
            dNode.stableid = "${dNode.rowParent?.typePrefix?.let { "$it${NodeCreator.viewTypeSepreator}" }
                    ?: let { "" }}${viewItem.stableid}"
        }
        dNode.data = viewItem.data
//        dNode.cellType = dNode.rowParent?.cellType

        dNode.context = context
//        dNode.path = NodePath().apply {
//            row = dNode.rowParent?.rowIndex ?: -1
//            section = dNode.rowParent?.sectionParent?.sectionIndex ?: -1
//            cell = dNode.rowParent?.sectionParent?.cellParent?.viewCellIndex ?: -1
//            group = dNode.rowParent?.sectionParent?.cellParent?.groupParent?.groupIndex ?: -1
//        }
        dNode.viewPaintingCallback = DefaultViewPaintingCallback(viewItem.viewPaintingCallback)
        val sectionParent = dNode.rowParent?.sectionParent
        if (sectionParent is StaggeredGridSection) {
            dNode.staggeredGridXGap = sectionParent.xStaggeredGridGap
            dNode.staggeredGridYGap = sectionParent.yStaggeredGridGap
            dNode.staggeredGridLeftMargin = sectionParent.staggeredGridLeftMargin
            dNode.staggeredGridRightMargin = sectionParent.staggeredGridRightMargin
        }

        return false
    }

}