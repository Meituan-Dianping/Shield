package com.dianping.shield.node.cellnode

import android.content.Context
import android.view.View
import com.dianping.shield.node.PositionType
import com.dianping.shield.node.adapter.DisplayNodeContainer
import com.dianping.shield.node.adapter.hotzone.HotZone
import com.dianping.shield.node.itemcallbacks.ViewClickCallbackWithData
import com.dianping.shield.node.itemcallbacks.ViewLongClickCallbackWithData
import com.dianping.shield.node.itemcallbacks.ViewPaintingCallback
import com.dianping.shield.node.processor.NodeCreator
import com.dianping.shield.node.processor.ProcessorHolder
import com.dianping.shield.utils.ShieldObjectsUtils
import java.util.*
import kotlin.properties.Delegates

/**
 * Created by zhi.he on 2018/7/18.
 */
class ShieldDisplayNode {
    @JvmField
    var rowParent: ShieldRow? = null

    //String viewType
    @JvmField
    var viewType: String? = null

    @JvmField
    //String stableid
    var stableid: String? = null

    @JvmField
    //模块数据
    var data: Any? = null

    @JvmField
    var staggeredGridXGap: Int? = null

    @JvmField
    var staggeredGridYGap: Int? = null

    @JvmField
    var staggeredGridLeftMargin: Int? = null

    @JvmField
    var staggeredGridRightMargin: Int? = null

    @JvmField
    //Context
    var context: Context? = null

    @JvmField
    var viewPaintingCallback: ViewPaintingCallback? = null

    @JvmField
    var clickCallback: ViewClickCallbackWithData? = null

    @JvmField
    var longClickCallback: ViewLongClickCallbackWithData? = null

    @JvmField
    var dividerInfo: DividerConfigInfo? = null

    @JvmField
    var innerTopInfo: InnerTopInfo? = null

    @JvmField
    var innerBottomInfo: InnerBottomInfo? = null

    @JvmField
    var hotZoneList: ArrayList<HotZone>? = null

    @JvmField
    var attachDetachInterfaceArrayList: ArrayList<ViewAttachDetachInterface>? = null

    @JvmField
    var attachStatusChangeListenerList: ArrayList<AttachStatusChangeListener<ShieldDisplayNode>>? = null

    @JvmField
    var moveStatusEventListenerList: ArrayList<MoveStatusEventListener<ShieldDisplayNode>>? = null

//    @JvmField
    //CellType
//    var cellType: CellType? = null

    //    @JvmField
    @JvmField
    var isUpdate: Boolean = false

    fun currentNodeIndex(): Int {
        return rowParent?.shieldDisplayNodes?.indexOf(this) ?: -1
    }

    var positionType: PositionType by Delegates.observable(PositionType.UNKNOWN) { property, oldValue, newValue ->
        if (newValue != oldValue) {
            pHolder?.let {
                NodeCreator.repackDisplayNodeWithPositionType(this, it)
                isUpdate = true
            }
        }
    }

    //    @JvmField
    var path: NodePath? = null
        get() {
            field ?: let {
                field = NodePath().apply {
                    group = it.rowParent?.sectionParent?.cellParent?.groupParent?.groupIndex ?: -1
                    cell = it.rowParent?.sectionParent?.cellParent?.viewCellIndex ?: -1
                    section = it.rowParent?.sectionParent?.currentSectionIndex() ?: -1
                    row = it.rowParent?.currentRowIndex() ?: -1
                    node = currentNodeIndex()
                    cellType = it.rowParent?.cellType
                }
            }
            return field
        }

    @JvmField
    var containerView: DisplayNodeContainer? = null

    @JvmField
    var view: View? = null

    @JvmField
    var pHolder: ProcessorHolder? = null

    fun isUnique() : Boolean {
        return (innerTopInfo != null || innerBottomInfo != null)
    }

    fun getViewTypeWithTopInfo(): String? {
        return if (isUnique()) null else viewType
    }

    fun equals(o1: ShieldDisplayNode?, o2: ShieldDisplayNode?): Boolean {
        return o1 === o2 || o1 != null && o2 != null && o1 == o2
    }

    fun same(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as ShieldDisplayNode?
        return ShieldObjectsUtils.equals(stableid, that!!.stableid) || (viewType != null
                && ShieldObjectsUtils.equals(viewType, that.viewType)
                && ShieldObjectsUtils.equals(viewPaintingCallback, that.viewPaintingCallback))
    }

    fun contentsEquals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val node = o as ShieldDisplayNode?
        return ((viewType != null
                && ShieldObjectsUtils.equals(viewType, node!!.viewType)
                && ShieldObjectsUtils.equals(viewPaintingCallback, node.viewPaintingCallback))
                && ShieldObjectsUtils.equals(stableid, node.stableid)
                && (data != null
                && ShieldObjectsUtils.equals(data, node.data))
                && ShieldObjectsUtils.equals(dividerInfo, node.dividerInfo)
                && ShieldObjectsUtils.equals(innerTopInfo, node.innerTopInfo)
                && path == node.path)
    }

    override fun hashCode(): Int {
        var result = viewType?.hashCode() ?: 0
        result = 31 * result + (stableid?.hashCode() ?: 0)
        result = 31 * result + (data?.hashCode() ?: 0)
        result = 31 * result + (viewPaintingCallback?.hashCode() ?: 0)
        result = 31 * result + (innerTopInfo?.hashCode() ?: 0)
        result = 31 * result + (path?.hashCode() ?: 0)
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ShieldDisplayNode

        if (!ShieldObjectsUtils.equals(viewType, other.viewType)) return false
        if (!ShieldObjectsUtils.equals(stableid, other.stableid)) return false
        if (!ShieldObjectsUtils.equals(data, other.data)) return false
        if (!ShieldObjectsUtils.equals(viewPaintingCallback, other.viewPaintingCallback)) return false
        if (!ShieldObjectsUtils.equals(innerTopInfo, other.innerTopInfo)) return false
        if (!ShieldObjectsUtils.equals(path, other.path)) return false

        return true
    }


    companion object {
        @JvmStatic
        fun same(o1: ShieldDisplayNode?, o2: ShieldDisplayNode?): Boolean {
            return o1 === o2 || o1 != null && o2 != null && o1.same(o2)
        }

        @JvmStatic
        fun contentsEquals(o1: ShieldDisplayNode?, o2: ShieldDisplayNode?): Boolean {
            return o1 === o2 || o1 != null && o2 != null && o1.contentsEquals(o2)
        }
    }
}