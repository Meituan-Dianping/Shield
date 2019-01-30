package com.dianping.shield.node.processor.impl.cell

import android.content.Context
import com.dianping.agentsdk.framework.CellStatus
import com.dianping.agentsdk.framework.LinkType
import com.dianping.shield.entity.CellType
import com.dianping.shield.feature.LoadingAndLoadingMoreCreator
import com.dianping.shield.node.itemcallbacks.ViewClickCallbackWithData
import com.dianping.shield.node.cellnode.*
import com.dianping.shield.node.cellnode.callback.LoadingPaintingCallback
import com.dianping.shield.node.processor.NodeCreator
import com.dianping.shield.node.processor.ProcessorHolder
import com.dianping.shield.node.useritem.RowItem
import com.dianping.shield.node.useritem.ShieldSectionCellItem
import com.dianping.shield.node.useritem.ViewItem
import com.dianping.shield.utils.RangeRemoveableArrayList

/**
 * Created by zhi.he on 2018/6/25.
 * 处理status
 */
class CellStatusNodeProcessor(context: Context, private val creator: LoadingAndLoadingMoreCreator?, private val processorHolder: ProcessorHolder) : CellNodeProcessor(context) {

//    var rowProcessor = BaseRowNodeProcessor(context, defaultTheme)

    override fun handleShieldViewCell(cellItem: ShieldSectionCellItem, shieldViewCell: ShieldViewCell, addList: ArrayList<ShieldSection>): Boolean {
        when (cellItem.loadingStatus) {
            CellStatus.LoadingStatus.LOADING,
            CellStatus.LoadingStatus.FAILED,
            CellStatus.LoadingStatus.EMPTY -> {
                shieldViewCell.shieldSections
                        ?: let { shieldViewCell.shieldSections = RangeRemoveableArrayList() }
                //创建Loading节点
                var loadingSection = ShieldSection().apply sc@{
                    this.cellParent = shieldViewCell
//                    sectionIndex = shieldViewCell.shieldSections?.size ?: -1
                    //设置Loading的默认Link属性
                    previousLinkType = LinkType.Previous.DISABLE_LINK_TO_PREVIOUS
                    nextLinkType = LinkType.Next.DISABLE_LINK_TO_NEXT
                    sectionHeaderHeight = 0
                    sectionFooterHeight = 0
                    isLazyLoad = false

                    shieldRows = RangeRemoveableArrayList()
                    val row = ShieldRow().apply {
                        this.sectionParent = this@sc
//                        rowIndex = shieldRows?.size ?: -1
                        showCellTopLineDivider = false
                        showCellBottomLineDivider = false

                        shieldDisplayNodes = ArrayList(arrayOfNulls<ShieldDisplayNode>(1).asList())

                        var viewItem: ViewItem? = null
                        var typePrefix: String? = null
                        when (cellItem.loadingStatus) {
                            CellStatus.LoadingStatus.LOADING -> {
                                cellItem.loadingViewItem?.viewPaintingCallback?.let {
                                    viewItem = cellItem.loadingViewItem
                                    typePrefix = shieldViewCell.name
                                } ?: let {
                                    viewItem = createDefaultLoadingItem(NodeCreator.LOADING_TYPE,
                                            cellItem.loadingViewItem?.data, cellItem.loadingViewItem?.clickCallback)
                                }
                            }
                            CellStatus.LoadingStatus.FAILED -> {
                                cellItem.failedViewItem?.viewPaintingCallback?.let {
                                    viewItem = cellItem.failedViewItem
                                    typePrefix = shieldViewCell.name
                                } ?: let {
                                    viewItem = createDefaultLoadingItem(NodeCreator.FAILED_TYPE,
                                            cellItem.failedViewItem?.data, cellItem.failedViewItem?.clickCallback)
                                }
                            }
                            CellStatus.LoadingStatus.EMPTY -> {
                                cellItem.emptyViewItem?.viewPaintingCallback?.let {
                                    viewItem = cellItem.emptyViewItem
                                    typePrefix = shieldViewCell.name
                                } ?: let {
                                    viewItem = createDefaultLoadingItem(NodeCreator.EMPTY_TYPE,
                                            cellItem.emptyViewItem?.data, cellItem.emptyViewItem?.clickCallback)
                                }
                            }
                            else -> {

                            }
                        }

                        viewItem?.let {
                            this.cellType = CellType.LOADING
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
                shieldViewCell.shieldSections?.add(loadingSection)
                addList.add(loadingSection)
                return true
            }
            else -> {
                return false
            }
        }
    }

    private fun createDefaultLoadingItem(viewType: String, data: Any?, clickCallback: ViewClickCallbackWithData?): ViewItem {
        return ViewItem().apply {
            this.viewType = viewType
            viewPaintingCallback = LoadingPaintingCallback(creator)
            clickCallback?.let {
                this.data = data
                this.clickCallback = it
            }
        }
    }

}