package com.dianping.shield.node.processor.impl.divider

import com.dianping.shield.node.PositionType
import com.dianping.shield.node.cellnode.ShieldDisplayNode
import com.dianping.shield.node.useritem.DividerStyle

/**
 * Created by zhi.he on 2018/7/2.
 */
class SectionDividerShowTypeProcessor : DividerInfoProcessor() {
    override fun handleDividerInfo(data: ShieldDisplayNode): Boolean {

        data.rowParent?.showCellTopLineDivider = when (data.rowParent?.sectionParent?.sectionDividerShowType) {
            DividerStyle.ShowType.MIDDLE, DividerStyle.ShowType.NO_TOP -> {
                when (data.positionType) {
                    PositionType.FIRST, PositionType.SINGLE -> false
                    else -> data.rowParent?.showCellTopLineDivider == true
                }
            }

            DividerStyle.ShowType.NONE -> false
            else -> {
                data.rowParent?.showCellTopLineDivider == true
            }
        }
        data.rowParent?.showCellBottomLineDivider = when (data.rowParent?.sectionParent?.sectionDividerShowType) {
            DividerStyle.ShowType.MIDDLE, DividerStyle.ShowType.NO_BOTTOM -> {
                when (data.positionType) {
                    PositionType.LAST, PositionType.SINGLE -> false
                    else -> data.rowParent?.showCellBottomLineDivider == true
                }
            }
            DividerStyle.ShowType.TOP_BOTTOM -> {
                when (data.positionType) {
                    PositionType.FIRST, PositionType.MIDDLE -> false
                    else -> data.rowParent?.showCellBottomLineDivider == true
                }
            }
            DividerStyle.ShowType.NONE -> false
            else -> {
                data.rowParent?.showCellBottomLineDivider == true
            }
        }

        return false
    }
}