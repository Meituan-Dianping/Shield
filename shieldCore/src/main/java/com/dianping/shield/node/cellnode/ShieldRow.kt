package com.dianping.shield.node.cellnode

import com.dianping.shield.entity.CellType
import com.dianping.shield.entity.ScrollDirection
import com.dianping.shield.expose.EntrySetHolder
import com.dianping.shield.expose.RangeAppearStateManager
import com.dianping.shield.node.adapter.hotzone.HotZone
import com.dianping.shield.node.cellnode.callback.lazyload.LazyLoadDisplayNodeProvider
import com.dianping.shield.node.useritem.BottomInfo
import com.dianping.shield.node.useritem.DividerStyle
import com.dianping.shield.node.useritem.ExposeInfo
import com.dianping.shield.node.useritem.TopInfo

/**
 * Created by zhi.he on 2018/7/2.
 */
class ShieldRow : EntrySetHolder<ShieldDisplayNode> {

    //    @JvmField
//    var rowIndex: Int = 0
    @JvmField
    var sectionParent: ShieldSection? = null
    @JvmField
    var shieldDisplayNodes: ArrayList<ShieldDisplayNode?>? = null
    /**
     * 划线显隐控制
     */
    @JvmField
    var showCellTopLineDivider = true
    @JvmField
    var showCellBottomLineDivider = true
    @JvmField
    var dividerStyle: DividerStyle? = null

    @JvmField
    var cellType: CellType = CellType.NORMAL

    @JvmField
    var typePrefix: String? = null

    //7.13日新增row缓冲
    var viewCount: Int = 1

    var isLazyLoad = false

    var nodeProvider: LazyLoadDisplayNodeProvider? = null

    var topInfo: TopInfo? = null

    var bottomInfo: BottomInfo? = null

    //row的内部相对位置，normal>0 header=-1 footer=-2
    fun currentRowIndex(): Int {
        if (cellType == CellType.HEADER) return -1
        if (cellType == CellType.FOOTER) return -2
        val row = sectionParent?.shieldRows?.indexOf(this) ?: -1
        return if (sectionParent?.hasHeaderCell == true) {
            row - 1
        } else row
    }

    var hotZoneArray: ArrayList<HotZone>? = null

    var exposeInfoArr: ArrayList<ExposeInfo>? = null

    fun getDisplayNodeAtPosition(position: Int): ShieldDisplayNode? {
        var node: ShieldDisplayNode? = null
        if (!isLazyLoad || position < shieldDisplayNodes?.size ?: -1) {
            node = shieldDisplayNodes?.get(position)
        }
        node ?: let {
            node = nodeProvider?.getShieldDisplayNodeAtPosition(position, this)
            node?.let {
                shieldDisplayNodes?.set(position, it)
            }
        }
        return node
    }

    @JvmField
    var attachStatusChangeListenerList: ArrayList<AttachStatusChangeListener<ShieldRow>>? = null

    private fun dispatchAttachStatusChanged(oldAttachStatus: AttachStatus?, newAttachStatus: AttachStatus?, direction: ScrollDirection?) {
        attachStatusChangeListenerList?.forEach {
            it.onAttachStatusChanged(this@ShieldRow.sectionParent?.indexOfShieldRow(this@ShieldRow)
                    ?: -1, this@ShieldRow, oldAttachStatus, newAttachStatus, direction)
        }
    }

    @JvmField
    var moveStatusEventListenerList: ArrayList<MoveStatusEventListener<ShieldRow>>? = null

    private fun dispatchAppearanceEvent(appearanceEvent: AppearanceEvent?, direction: ScrollDirection?) {
        moveStatusEventListenerList?.forEach {
            when (appearanceEvent) {
                AppearanceEvent.FULLY_APPEAR, AppearanceEvent.PARTLY_APPEAR -> {
                    it.onAppeared(this@ShieldRow.sectionParent?.indexOfShieldRow(this@ShieldRow)
                            ?: -1,
                            this@ShieldRow, appearanceEvent, direction)
                }
                AppearanceEvent.FULLY_DISAPPEAR, AppearanceEvent.PARTLY_DISAPPEAR -> {
                    it.onDisappeared(this@ShieldRow.sectionParent?.indexOfShieldRow(this@ShieldRow)
                            ?: -1,
                            this@ShieldRow, appearanceEvent, direction)
                }
            }

        }
    }

    @JvmField
    var rangeAppearStateManager: RangeAppearStateManager<ShieldDisplayNode> = RangeAppearStateManager(this)

    override fun getEntryCount(): Int {
        return shieldDisplayNodes?.size ?: 0
    }

    override fun getEntry(position: Int): ShieldDisplayNode? {
        return shieldDisplayNodes?.get(position)
    }

    override fun onAttachStateChanged(oldAttachStatus: AttachStatus?, newAttachStatus: AttachStatus?, direction: ScrollDirection?) {
        dispatchAttachStatusChanged(oldAttachStatus, newAttachStatus, direction)
    }

    override fun onAppearanceEvent(appearanceEvent: AppearanceEvent?, direction: ScrollDirection?) {
        dispatchAppearanceEvent(appearanceEvent, direction)
    }
}