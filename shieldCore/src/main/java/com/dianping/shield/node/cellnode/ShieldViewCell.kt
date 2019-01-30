package com.dianping.shield.node.cellnode

import com.dianping.agentsdk.framework.AgentInterface
import com.dianping.agentsdk.framework.CellStatus
import com.dianping.shield.entity.ScrollDirection
import com.dianping.shield.expose.EntrySetHolder
import com.dianping.shield.expose.RangeAppearStateManager
import com.dianping.shield.node.itemcallbacks.MoveStatusCallback
import com.dianping.shield.node.useritem.ExposeInfo
import com.dianping.shield.node.useritem.MGEInfo
import com.dianping.shield.utils.RangeRemoveableArrayList

/**
 * Created by zhi.he on 2018/6/25.
 */
class ShieldViewCell : EntrySetHolder<ShieldSection> {
    @JvmField
    var owner: AgentInterface? = null

    @JvmField
    var key: String? = null

    @JvmField
    var name: String? = null

    @JvmField
    var groupParent: ShieldCellGroup? = null

    @JvmField
    var viewCellIndex: Int = 0

    @JvmField
    var shouldShow: Boolean = true

    @JvmField
    var shieldSections: RangeRemoveableArrayList<ShieldSection>? = null

    @JvmField
    var loadingStatus: CellStatus.LoadingStatus? = null    //模块整体LoadingStatus

    @JvmField
    var loadingMoreStatus: CellStatus.LoadingMoreStatus? = null   //模块整体LoadingMoreStatus

//    @JvmField
//    var previousLinkType: LinkType.Previous? = null //模块默认LinkType.Previous
//
//    @JvmField
//    var nextLinkType: LinkType.Next? = null //模块默认LinkType.Next

    @JvmField
    var exposeInfo: ExposeInfo? = null //模块曝光配置包括回调

    @JvmField
    var mgeInfo: MGEInfo? = null    // 模块曝光打点

    @JvmField
    var moveStatusCallback: MoveStatusCallback? = null

    @JvmField
    var needScrollToTop: Boolean = false

    @JvmField
    var recyclerViewTypeSizeMap: Map<String, Int>? = null

    private fun getShieldSection(position: Int): ShieldSection? {
        return shieldSections?.get(position)
    }

    fun indexOfShieldSection(shieldSection: ShieldSection): Int {
        return shieldSections?.indexOf(shieldSection) ?: -1
    }

    @JvmField
    var attachStatusChangeListenerList: ArrayList<AttachStatusChangeListener<ShieldViewCell>>? = null

    private fun dispatchAttachStatusChanged(oldAttachStatus: AttachStatus?, newAttachStatus: AttachStatus?, direction: ScrollDirection?) {
        attachStatusChangeListenerList?.forEach {
            it.onAttachStatusChanged(viewCellIndex, this@ShieldViewCell, oldAttachStatus, newAttachStatus, direction)
        }
    }

    @JvmField
    var moveStatusEventListenerList: ArrayList<MoveStatusEventListener<ShieldViewCell>>? = null

    private fun dispatchAppearanceEvent(appearanceEvent: AppearanceEvent?, direction: ScrollDirection?) {
        moveStatusEventListenerList?.forEach {
            when (appearanceEvent) {
                AppearanceEvent.FULLY_APPEAR, AppearanceEvent.PARTLY_APPEAR -> {
                    it.onAppeared(viewCellIndex, this@ShieldViewCell, appearanceEvent, direction)
                }
                AppearanceEvent.FULLY_DISAPPEAR, AppearanceEvent.PARTLY_DISAPPEAR -> {
                    it.onDisappeared(viewCellIndex, this@ShieldViewCell, appearanceEvent, direction)
                }
            }

        }
    }

    @JvmField
    var rangeAppearStateManager: RangeAppearStateManager<ShieldSection> = RangeAppearStateManager(this)

    override fun getEntryCount(): Int {
        return shieldSections?.size ?: 0
    }

    override fun getEntry(position: Int): ShieldSection? {
        return getShieldSection(position)
    }

    override fun onAttachStateChanged(oldAttachStatus: AttachStatus?, newAttachStatus: AttachStatus?, direction: ScrollDirection?) {
        dispatchAttachStatusChanged(oldAttachStatus, newAttachStatus, direction)
    }

    override fun onAppearanceEvent(appearanceEvent: AppearanceEvent?, direction: ScrollDirection?) {
        dispatchAppearanceEvent(appearanceEvent, direction)
    }

    fun getViewCellTotalRange(): Int {
        return shieldSections?.let {
            var totalCount = 0
            it.forEach {
                totalCount += it.getRange()
            }
            totalCount
        } ?: 0
    }
}