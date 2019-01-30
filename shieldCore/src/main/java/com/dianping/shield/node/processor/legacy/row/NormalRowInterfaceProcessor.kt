package com.dianping.shield.node.processor.legacy.row

import android.view.View
import com.dianping.agentsdk.framework.ItemClickInterface
import com.dianping.agentsdk.framework.ItemLongClickInterface
import com.dianping.agentsdk.framework.RowDataInterface
import com.dianping.agentsdk.framework.SectionCellInterface
import com.dianping.shield.node.cellnode.NodePath
import com.dianping.shield.node.cellnode.callback.legacy.LegacyViewPaintingCallback
import com.dianping.shield.node.itemcallbacks.ViewClickCallbackWithData
import com.dianping.shield.node.itemcallbacks.ViewLongClickCallbackWithData
import com.dianping.shield.node.useritem.RowItem
import com.dianping.shield.node.useritem.ViewItem

/**
 * Created by zhi.he on 2018/6/28.
 */
class NormalRowInterfaceProcessor : RowInterfaceProcessor() {
    override fun handleRowItem(sci: SectionCellInterface, rowItem: RowItem, section: Int, row: Int): Boolean {
        rowItem.viewItems ?: let { rowItem.viewItems = ArrayList() }
        rowItem.viewItems?.add(ViewItem().apply {
            viewType = sci.getViewType(section, row).toString()
            if (sci is RowDataInterface) {
                data = sci.getData(section, row)
            }
            viewPaintingCallback = LegacyViewPaintingCallback(sci)
//            data = CellInfo(section, row, CellType.NORMAL)
            if (sci is ItemClickInterface && sci.onItemClickListener != null) {
                clickCallback = object : ViewClickCallbackWithData {
                    override fun onViewClicked(view: View, data: Any?, path: NodePath?) {
                        sci.onItemClickListener.onItemClick(view,
                                path?.section ?: -1, path?.row ?: -3)
                    }
                }
            }
            if (sci is ItemLongClickInterface && sci.onItemLongClickListener != null) {
                longClickCallback = object : ViewLongClickCallbackWithData {
                    override fun onViewLongClicked(view: View, data: Any?, path: NodePath?): Boolean {
                        sci.onItemLongClickListener.onItemLongClick(view,
                                path?.section ?: -1, path?.row ?: -3)
                        return false
                    }
                }
            }
        })
        return false
//        nextProcessor?.handleRowItem(sci, rowItem, section, row)
    }
}