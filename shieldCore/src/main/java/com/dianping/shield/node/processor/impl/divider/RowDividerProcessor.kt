package com.dianping.shield.node.processor.impl.divider

import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.support.annotation.ColorInt
import com.dianping.shield.node.DividerThemePackage
import com.dianping.shield.node.PositionType
import com.dianping.shield.node.cellnode.DividerConfigInfo
import com.dianping.shield.node.cellnode.ShieldDisplayNode
import com.dianping.shield.node.useritem.DividerStyle

/**
 * Created by zhi.he on 2018/7/2.
 */
class RowDividerProcessor(private val dividerThemePackage: DividerThemePackage) : DividerInfoProcessor() {
    override fun handleDividerInfo(data: ShieldDisplayNode): Boolean {
        data.dividerInfo ?: let { data.dividerInfo = DividerConfigInfo() }
        var styleType: DividerStyle.StyleType = data.rowParent?.dividerStyle?.styleType
                ?: DividerStyle.StyleType.AUTO;
        if (!dividerThemePackage.enableDivider) {
            styleType = DividerStyle.StyleType.NONE;
        }
        //支持自定义DividerStyle
        if (styleType == DividerStyle.StyleType.AUTO) {
            styleType = when (data.positionType) {
                PositionType.FIRST -> DividerStyle.StyleType.TOP
                PositionType.MIDDLE -> DividerStyle.StyleType.MIDDLE
                PositionType.LAST -> DividerStyle.StyleType.BOTTOM
                PositionType.SINGLE -> DividerStyle.StyleType.SINGLE
                else -> DividerStyle.StyleType.AUTO
            }
        }

        //根据不同的styleType定义划线
        when (styleType) {
            DividerStyle.StyleType.TOP -> {
                addDividerInfo(data, DividerType.TOP)
                addDividerInfo(data, DividerType.MIDDLE)
            }
            DividerStyle.StyleType.MIDDLE -> {
                addTopLineCustom(data)//custom or null no default
                addDividerInfo(data, DividerType.MIDDLE)//custom or default
            }

            DividerStyle.StyleType.BOTTOM -> {
                addTopLineCustom(data) //custom or null no default
                addDividerInfo(data, DividerType.BOTTOM) //custom or default
            }

            DividerStyle.StyleType.SINGLE -> {
                addDividerInfo(data, DividerType.TOP)
                addDividerInfo(data, DividerType.BOTTOM)
            }
            DividerStyle.StyleType.NONE -> {
                addTopLineCustom(data)//custom or null no default
                addBottomLineCustom(data)//custom or null no default
            }

            else -> {

            }
        }
        return false
    }

    //定制了就画，没定制不画
    private fun addTopLineCustom(dNode: ShieldDisplayNode) {
        if (dNode.rowParent?.showCellTopLineDivider == true) {//只看优先级高的TopLine定制
            dNode.dividerInfo?.cellTopLineOffset = dNode.rowParent?.dividerStyle?.cellTopLineOffset
            dNode.dividerInfo?.cellTopLineDrawable = dNode.rowParent?.dividerStyle?.cellTopLineDrawable
                    ?: dNode.rowParent?.dividerStyle?.cellTopLineColor?.let { createDrawable(it) }
        }
    }

    //定制了就画，没定制不画
    private fun addBottomLineCustom(dNode: ShieldDisplayNode) {
        if (dNode.rowParent?.showCellBottomLineDivider == true) {//只看优先级高的BottomLine定制
            dNode.dividerInfo?.cellBottomLineOffset = dNode.rowParent?.dividerStyle?.cellBottomLineOffset
            dNode.dividerInfo?.cellBottomLineDrawable = dNode.rowParent?.dividerStyle?.cellBottomLineDrawable
                    ?: dNode.rowParent?.dividerStyle?.cellBottomLineColor?.let { createDrawable(it) }
        }
    }

    private enum class DividerType {
        TOP, MIDDLE, BOTTOM
    }

    private fun addDividerInfo(dNode: ShieldDisplayNode, dividerType: DividerType) {
        when (dividerType) {
            DividerType.TOP -> {
                //看是否隐藏TopDivider
                if (dNode.rowParent?.showCellTopLineDivider == true) {
                    dNode.dividerInfo?.cellTopLineOffset = pickOffsetRect(dNode.rowParent?.dividerStyle?.cellTopLineOffset,
                            dividerThemePackage.defaultSectionDividerOffset)
                            ?: pickOffsetRect(dNode.rowParent?.dividerStyle?.topStyleLineOffset,
                            dividerThemePackage.defaultSectionDividerOffset)
                            ?: dividerThemePackage.defaultSectionDividerOffset

                    dNode.dividerInfo?.cellTopLineDrawable = dNode.rowParent?.dividerStyle?.cellTopLineDrawable
                            ?: dNode.rowParent?.dividerStyle?.cellTopLineColor?.let { createDrawable(it) }
                            ?: dNode.rowParent?.dividerStyle?.topStyleLineDrawable
                            ?: dNode.rowParent?.dividerStyle?.topStyleLineColor?.let { createDrawable(it) }
                            ?: dividerThemePackage.defaultSectionTopDivider
                            ?: dividerThemePackage.defaultSectionDivider
                }
            }
            DividerType.MIDDLE -> {
                if (dNode.rowParent?.showCellBottomLineDivider == true) {
                    dNode.dividerInfo?.cellBottomLineOffset = pickOffsetRect(dNode.rowParent?.dividerStyle?.cellBottomLineOffset,
                            dividerThemePackage.defaultDividerOffset)
                            ?: pickOffsetRect(dNode.rowParent?.dividerStyle?.middleStyleLineOffset,
                            dividerThemePackage.defaultDividerOffset)
                            ?: dividerThemePackage.defaultDividerOffset

                    dNode.dividerInfo?.cellBottomLineDrawable = dNode.rowParent?.dividerStyle?.cellBottomLineDrawable
                            ?: dNode.rowParent?.dividerStyle?.cellBottomLineColor?.let { createDrawable(it) }
                            ?: dNode.rowParent?.dividerStyle?.middleStyleLineDrawable
                            ?: dNode.rowParent?.dividerStyle?.middleStyleLineColor?.let { createDrawable(it) }
                            ?: dividerThemePackage.defaultDivider
                }
            }
            DividerType.BOTTOM -> {
                if (dNode.rowParent?.showCellBottomLineDivider == true) {
                    dNode.dividerInfo?.cellBottomLineOffset = pickOffsetRect(dNode.rowParent?.dividerStyle?.cellBottomLineOffset,
                            dividerThemePackage.defaultSectionDividerOffset)
                            ?: pickOffsetRect(dNode.rowParent?.dividerStyle?.bottomStyleLineOffset,
                            dividerThemePackage.defaultSectionDividerOffset)
                            ?: dividerThemePackage.defaultSectionDividerOffset

                    dNode.dividerInfo?.cellBottomLineDrawable = dNode.rowParent?.dividerStyle?.cellBottomLineDrawable
                            ?: dNode.rowParent?.dividerStyle?.cellBottomLineColor?.let { createDrawable(it) }
                            ?: dNode.rowParent?.dividerStyle?.bottomStyleLineDrawable
                            ?: dNode.rowParent?.dividerStyle?.bottomStyleLineColor?.let { createDrawable(it) }
                            ?: dividerThemePackage.defaultSectionBottomDivider
                            ?: dividerThemePackage.defaultSectionDivider
                }
            }
        }

    }


    private fun createDrawable(@ColorInt color: Int): Drawable {
        val gradientDrawable = GradientDrawable()
        gradientDrawable.setColor(color)
        gradientDrawable.setSize(gradientDrawable.intrinsicWidth, 1)
        return gradientDrawable
    }

    //修改Rect中的-1值为默认值
    private fun pickOffsetRect(rect: Rect?, defaultRect: Rect?): Rect? {
        rect?.let {
            if (it.left < 0) it.left = defaultRect?.left ?: 0
            if (it.right < 0) it.right = defaultRect?.right ?: 0
            if (it.top < 0) it.top = defaultRect?.top ?: 0
            if (it.bottom < 0) it.bottom = defaultRect?.bottom ?: 0
        }
        return rect
    }
}