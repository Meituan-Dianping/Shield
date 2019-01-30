package com.dianping.shield.node.cellnode

import android.graphics.drawable.Drawable
import com.dianping.agentsdk.framework.LinkType
import com.dianping.shield.entity.ScrollDirection
import com.dianping.shield.expose.EntrySetHolder
import com.dianping.shield.expose.RangeAppearStateManager
import com.dianping.shield.node.PositionType
import com.dianping.shield.node.cellnode.callback.lazyload.LazyLoadShieldRowProvider
import com.dianping.shield.node.useritem.DividerStyle
import com.dianping.shield.utils.RangeRemoveableArrayList
import kotlin.properties.Delegates

/**
 * Created by zhi.he on 2018/6/25.
 */
open class ShieldSection : RangeHolder, EntrySetHolder<ShieldRow> {
    private val objectListObservers = ArrayList<RangeChangeObserver>()
    override fun registerObserver(observer: RangeChangeObserver) {
        objectListObservers.add(observer)
    }

    override fun unregisterObserver(observer: RangeChangeObserver) {
        objectListObservers.remove(observer)
    }

//    @JvmField
//    var sectionIndex: Int = 0

    fun currentSectionIndex(): Int {
        return cellParent?.shieldSections?.indexOf(this) ?: -1
    }

    @JvmField
    var cellParent: ShieldViewCell? = null

    @JvmField
    var hasHeaderCell: Boolean = false

    @JvmField
    var hasFooterCell: Boolean = false

    @JvmField
    var isLazyLoad = false

    var rowProvider: LazyLoadShieldRowProvider? = null

    @JvmField
    var shieldRows: RangeRemoveableArrayList<ShieldRow?>? = null

    @JvmField
    var sectionTitle: String? = null //Section 标题

    @JvmField
    var previousLinkType: LinkType.Previous? = null   //sectionLinkType

    @JvmField
    var nextLinkType: LinkType.Next? = null //

    @JvmField
    var adjustedPreviousType: LinkType.Previous? = null

    @JvmField
    var adjustedNextType: LinkType.Next? = null

    @JvmField
    var sectionHeaderHeight = 10// section Gap 的高度定制，在非link状态下有效，单位dp

    @JvmField
    var sectionHeaderDrawable: Drawable? = null//section Gap的Drawable定制

    @JvmField
    var sectionFooterHeight = 10// section Gap 的高度定制，在非link状态下有效，单位dp

    @JvmField
    var sectionFooterDrawable: Drawable? = null//section Gap的Drawable定制

    @JvmField
    var sectionDividerShowType: DividerStyle.ShowType = DividerStyle.ShowType.ALL

    @JvmField
    var dividerStyle: DividerStyle? = null //划线定制 如果行内没有定制划线，则以section上的划线为准，如果section没有定制就走默认

    val rangeDispatcher: RangeDispatcher by lazy(LazyThreadSafetyMode.NONE) {
        RangeDispatcher()
    }

    //只需要改变section的PositionType，会动态调整Section的头节点或者尾节点
    var sectionPositionType: PositionType by Delegates.observable(PositionType.UNKNOWN) { property, oldValue, newValue ->
        if (newValue != oldValue && getRange() > 0) {
            when {
                (oldValue == PositionType.FIRST && newValue == PositionType.MIDDLE) ||
                        (oldValue == PositionType.MIDDLE && newValue == PositionType.FIRST) ||
                        (oldValue == PositionType.LAST && newValue == PositionType.SINGLE) ||
                        (oldValue == PositionType.SINGLE && newValue == PositionType.LAST) -> {
                    getShieldDisplayNode(0)
                }
                (oldValue == PositionType.FIRST && newValue == PositionType.SINGLE) ||
                        (oldValue == PositionType.MIDDLE && newValue == PositionType.LAST) ||
                        (oldValue == PositionType.LAST && newValue == PositionType.MIDDLE) ||
                        (oldValue == PositionType.SINGLE && newValue == PositionType.FIRST) -> {
                    getShieldDisplayNode(getRange() - 1)
                }
            }
        }
    }

    override fun getRange(): Int {
        return rangeDispatcher.totalRange
    }

    fun getShieldDisplayNode(position: Int): ShieldDisplayNode? {
        var node: ShieldDisplayNode? = null
        if ((hasHeaderCell && position == 0) || (hasFooterCell && position == getRange() - 1)) {
            node = shieldRows?.get(position)?.getDisplayNodeAtPosition(0)
        } else {
            val positionPair = rangeDispatcher.getInnerPosition(position)
            node = getShieldRow(positionPair?.index
                    ?: 0)?.getDisplayNodeAtPosition(positionPair?.innerIndex ?: 0)
        }
        node?.let { computeNodePositionType(position, it) }
        return node
    }

    internal fun getShieldRow(position: Int): ShieldRow? {
        return shieldRows?.getOrNull(position) ?: let {
            //rowProvider不包含Header和Footer
            val providerPosition = if (hasHeaderCell) {
                position - 1
            } else {
                position
            }
            val shieldRow = rowProvider?.getShieldRow(providerPosition, this)
            shieldRows?.set(position, shieldRow)
            shieldRow
        }
    }

    fun indexOfShieldRow(shieldRow: ShieldRow): Int {
        return shieldRows?.indexOf(shieldRow) ?: -1
    }

    /*
    * position 这个节点在section中的当前位置
    * **/
    private fun computeNodePositionType(position: Int, node: ShieldDisplayNode) {
        when (sectionPositionType) {
            PositionType.FIRST -> when (position) {
                0 -> node.positionType = PositionType.FIRST
                else -> node.positionType = PositionType.MIDDLE
            }

            PositionType.MIDDLE -> node.positionType = PositionType.MIDDLE

            PositionType.LAST -> when (position) {
                getRange() - 1 -> node.positionType = PositionType.LAST
                else -> node.positionType = PositionType.MIDDLE
            }

            PositionType.SINGLE -> when (position) {
                0 -> if (getRange() == 1) node.positionType = PositionType.SINGLE else node.positionType = PositionType.FIRST
                in 1 until getRange() - 1 -> node.positionType = PositionType.MIDDLE
                getRange() - 1 -> if (getRange() == 1) node.positionType = PositionType.SINGLE else node.positionType = PositionType.LAST
            }

            else -> {
                node.positionType = PositionType.UNKNOWN
            }
        }
    }

    //这个startPosition不包含HeaderCell
    fun notifyRowInsert(startPosition: Int, count: Int) {
        //累加新增Row的Range之和
        var rangeCount = 0
        //生成新的RowRangeHolder
        val rowRangeHolderList = ArrayList<RowRangeHolder>()
        var sectionIndex = currentSectionIndex()
        for (i in startPosition until startPosition + count) {
            rowRangeHolderList.add(RowRangeHolder().apply {
                dNodeCount = rowProvider?.getRowNodeCount(i, this@ShieldSection) ?: 0
                rangeCount += dNodeCount
            })
        }
        //插入到Section内的HolderDispatcher中以增加totalRange
        val rowStartPosition = if (hasHeaderCell) startPosition + 1 else startPosition
        rangeDispatcher.addAll(rowStartPosition, rowRangeHolderList)

        //确定插入前的Row的最后一个节点的全局位置，确定range之和作为count，
        val startRangePosition = rangeDispatcher.getStartPosition(rowStartPosition)

        //扩大缓冲区
        shieldRows?.addAll(rowStartPosition, arrayOfNulls<ShieldRow>(count).asList())

        resetInsertNeighborNode(rowStartPosition, count)

        markNodePathOutDate(rowStartPosition)

        objectListObservers.forEach {
            it.onItemRangeInserted(this, startRangePosition, rangeCount)
        }
    }

    //这个startPosition不包含HeaderCell
    fun notifyRowRemove(startPosition: Int, count: Int) {
        //累加删除的Row的Range之和，从旧的range里面加
        var rangeCount = 0

        //获取rowPosition
        val rowStartPosition = if (hasHeaderCell) startPosition + 1 else startPosition

        for (i in rowStartPosition until rowStartPosition + count) {
            rangeCount += rangeDispatcher[i].getRange()
        }

        //确定插入前的Row的最后一个节点的全局位置，确定range之和作为count，
        val startRangePosition = rangeDispatcher.getStartPosition(rowStartPosition)

        rangeDispatcher.removeRange(rowStartPosition, rowStartPosition + count)

        //缩小缓冲区
        shieldRows?.removeRange(rowStartPosition, rowStartPosition + count)

        //重新计算相邻节点PositionType
        resetRemovetNeighborNode(rowStartPosition, count)

        markNodePathOutDate(rowStartPosition)

        //mark 后面所有的row path试下

        objectListObservers.forEach {
            it.onItemRangeRemoved(this, startRangePosition, rangeCount)
        }
    }

    //这个startPosition不包含HeaderCell
    fun notifyRowUpdate(startPosition: Int, count: Int) {
        //这里先不再考虑Row下层node的局部刷新，统一认为Row的Update不会
        // 导致range变化，仅仅只是转化成相应的全局change

        var rangeCount = 0

        //获取rowPosition
        val rowStartPosition = if (hasHeaderCell) startPosition + 1 else startPosition

        for (i in rowStartPosition until rowStartPosition + count) {
            rangeCount += rangeDispatcher[i].getRange()
            //清空对应位置的缓冲，以便重新生成节点
            shieldRows?.set(i, null)
        }

        //确定插入前的Row的最后一个节点的全局位置，确定range之和作为count，
        val startRangePosition = rangeDispatcher.getStartPosition(rowStartPosition)

        objectListObservers.forEach {
            it.onItemRangeChanged(this, startRangePosition, rangeCount)
        }
    }

    private fun resetInsertNeighborNode(rowStartPosition: Int, count: Int) {
        //调整rowInsert导致的前一个和后一个的节点(已经生成的ShieldDisplayNode)的PositionType，还没有生成的不管
        shieldRows?.getOrNull(rowStartPosition - 1)?.apply {
            shieldDisplayNodes?.getOrNull(shieldDisplayNodes?.lastIndex ?: -1)?.let {
                it.isUpdate = false
            }
        }

        shieldRows?.getOrNull(rowStartPosition + count)?.apply {
            shieldDisplayNodes?.getOrNull(0)?.let {
                it.isUpdate = false
            }
        }
    }

    private fun resetRemovetNeighborNode(rowStartPosition: Int, count: Int) {
        //调整rowInsert导致的前一个和后一个的节点(已经生成的ShieldDisplayNode)的PositionType，还没有生成的不管
        shieldRows?.getOrNull(rowStartPosition - 1)?.apply {
            shieldDisplayNodes?.getOrNull(shieldDisplayNodes?.lastIndex ?: -1)?.let {
                it.isUpdate = false
            }
        }

        shieldRows?.getOrNull(rowStartPosition)?.apply {
            shieldDisplayNodes?.getOrNull(0)?.let {
                it.isUpdate = false
            }
        }
    }

    private fun markNodePathOutDate(rowStartPosition: Int) {
        for (i in rowStartPosition..(shieldRows?.lastIndex ?: -1)) {
            shieldRows?.get(i)?.shieldDisplayNodes?.forEach { node ->
                node?.path = null
            }
        }
    }


    @JvmField
    var attachStatusChangeListenerList: ArrayList<AttachStatusChangeListener<ShieldSection>>? = null

    private fun dispatchAttachStatusChanged(oldAttachStatus: AttachStatus?, newAttachStatus: AttachStatus?, direction: ScrollDirection?) {
        attachStatusChangeListenerList?.forEach {
            it.onAttachStatusChanged(this@ShieldSection.cellParent?.indexOfShieldSection(this@ShieldSection)
                    ?: -1, this@ShieldSection, oldAttachStatus, newAttachStatus, direction)
        }
    }

    @JvmField
    var moveStatusEventListenerList: ArrayList<MoveStatusEventListener<ShieldSection>>? = null

    private fun dispatchAppearanceEvent(appearanceEvent: AppearanceEvent?, direction: ScrollDirection?) {
        moveStatusEventListenerList?.forEach {
            when (appearanceEvent) {
                AppearanceEvent.FULLY_APPEAR, AppearanceEvent.PARTLY_APPEAR -> {
                    it.onAppeared(this@ShieldSection.cellParent?.indexOfShieldSection(this@ShieldSection)
                            ?: -1,
                            this@ShieldSection, appearanceEvent, direction)
                }
                AppearanceEvent.FULLY_DISAPPEAR, AppearanceEvent.PARTLY_DISAPPEAR -> {
                    it.onDisappeared(this@ShieldSection.cellParent?.indexOfShieldSection(this@ShieldSection)
                            ?: -1,
                            this@ShieldSection, appearanceEvent, direction)
                }
            }

        }
    }

    @JvmField
    var rangeAppearStateManager: RangeAppearStateManager<ShieldRow> = RangeAppearStateManager(this)

    override fun getEntryCount(): Int {
        return shieldRows?.size ?: 0
    }

    override fun getEntry(position: Int): ShieldRow? {
        return getShieldRow(position)
    }

    override fun onAttachStateChanged(oldAttachStatus: AttachStatus?, newAttachStatus: AttachStatus?, direction: ScrollDirection?) {
        dispatchAttachStatusChanged(oldAttachStatus, newAttachStatus, direction)
    }

    override fun onAppearanceEvent(appearanceEvent: AppearanceEvent?, direction: ScrollDirection?) {
        dispatchAppearanceEvent(appearanceEvent, direction)
    }
}