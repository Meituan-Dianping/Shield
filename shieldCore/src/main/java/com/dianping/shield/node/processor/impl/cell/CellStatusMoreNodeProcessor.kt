package com.dianping.shield.node.processor.impl.cell

import android.content.Context
import com.dianping.agentsdk.framework.CellStatus
import com.dianping.agentsdk.framework.LinkType
import com.dianping.shield.entity.CellType
import com.dianping.shield.feature.LoadingAndLoadingMoreCreator
import com.dianping.shield.node.cellnode.*
import com.dianping.shield.node.cellnode.callback.LoadingMorePaintingCallback
import com.dianping.shield.node.itemcallbacks.LoadingMoreViewPaintingListener
import com.dianping.shield.node.itemcallbacks.ViewClickCallbackWithData
import com.dianping.shield.node.processor.NodeCreator
import com.dianping.shield.node.processor.ProcessorHolder
import com.dianping.shield.node.useritem.RowItem
import com.dianping.shield.node.useritem.ShieldSectionCellItem
import com.dianping.shield.node.useritem.ViewItem
import com.dianping.shield.utils.RangeRemoveableArrayList

/**
 * Created by zhi.he on 2018/6/25.
 * 处理status more
 */
class CellStatusMoreNodeProcessor(mContext: Context, private val creator: LoadingAndLoadingMoreCreator?, private val processorHolder: ProcessorHolder) : CellNodeProcessor(mContext) {

//    var rowProcessor = BaseRowNodeProcessor(context, defaultTheme)

    override fun handleShieldViewCell(cellItem: ShieldSectionCellItem, shieldViewCell: ShieldViewCell, addList: ArrayList<ShieldSection>): Boolean {
        when (cellItem.loadingMoreStatus) {
            CellStatus.LoadingMoreStatus.LOADING,
            CellStatus.LoadingMoreStatus.FAILED -> {
                //创建LoadingMore节点
                shieldViewCell.shieldSections
                        ?: let { shieldViewCell.shieldSections = RangeRemoveableArrayList() }
                var loadingMoreSection = ShieldSection().apply sc@{
                    this.cellParent = shieldViewCell
//                    sectionIndex = shieldViewCell.shieldSections?.size ?: -1

                    previousLinkType = LinkType.Previous.DISABLE_LINK_TO_PREVIOUS
                    nextLinkType = LinkType.Next.DISABLE_LINK_TO_NEXT
                    sectionHeaderHeight = 0
                    sectionFooterHeight = 0
                    shieldRows = RangeRemoveableArrayList()
                    var row = ShieldRow().apply {
                        this.sectionParent = this@sc
//                        rowIndex = shieldRows?.size ?: -1

                        shieldDisplayNodes = ArrayList(arrayOfNulls<ShieldDisplayNode>(1).asList())

                        var viewItem: ViewItem? = null
                        var typePrefix: String? = null

                        when (cellItem.loadingMoreStatus) {
                            CellStatus.LoadingMoreStatus.LOADING -> {
                                cellItem.loadingMoreViewItem?.viewPaintingCallback?.let {
                                    viewItem = cellItem.loadingMoreViewItem
                                    typePrefix = shieldViewCell.name
                                } ?: let {
                                    viewItem = createDefaultLoadingMoreItem(NodeCreator.LOADING_MORE_TYPE,
                                            cellItem.loadingMoreViewItem?.data, cellItem.loadingMoreViewItem?.clickCallback,
                                            cellItem.loadingMoreViewPaintingListener)
                                }
                            }
                            CellStatus.LoadingMoreStatus.FAILED -> {
                                cellItem.loadingMoreFailedViewItem?.viewPaintingCallback?.let {
                                    viewItem = cellItem.loadingMoreFailedViewItem
                                    typePrefix = shieldViewCell.name
                                } ?: let {
                                    viewItem = createDefaultLoadingMoreItem(NodeCreator.LOADING_MORE_FAILED_TYPE,
                                            cellItem.loadingMoreFailedViewItem?.data, cellItem.loadingMoreFailedViewItem?.clickCallback,
                                            cellItem.loadingMoreViewPaintingListener)
                                }
                            }
                            else -> {
                            }
                        }

                        viewItem?.let {
                            this.cellType = CellType.LOADING_MORE
                            this.typePrefix = typePrefix
                            processorHolder.rowProcessorChain.startProcessor(RowItem().apply {
                                //设置LoadingMore的默认Link属性
                                showCellTopDivider = false
                                showCellBottomDivider = false
                                viewItems = ArrayList()
                                viewItems.add(viewItem)
                            }, this)

                        }
                    }
                    rangeDispatcher.add(RowRangeHolder(1))
                    shieldRows?.add(row)
                }
                shieldViewCell.shieldSections?.add(loadingMoreSection)
                addList.add(loadingMoreSection)
                return true
            }
            else -> {
                return false
            }
        }
    }

    private fun createDefaultLoadingMoreItem(viewType: String, data: Any?, clickCallback: ViewClickCallbackWithData?,
                                             loadingMoreViewPaintingListener: LoadingMoreViewPaintingListener): ViewItem {
        return ViewItem().apply {
            this.viewType = viewType
            viewPaintingCallback = LoadingMorePaintingCallback(creator, loadingMoreViewPaintingListener)
            clickCallback?.let {
                this.data = data
                this.clickCallback = it
            }
        }
    }

}