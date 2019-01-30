package com.dianping.shield.node.processor.impl.divider

import android.content.Context
import com.dianping.agentsdk.framework.ViewUtils
import com.dianping.shield.node.DividerThemePackage
import com.dianping.shield.node.PositionType
import com.dianping.shield.node.cellnode.DividerConfigInfo
import com.dianping.shield.node.cellnode.ShieldDisplayNode

/**
 * Created by zhi.he on 2018/7/2.
 */
class HeaderGapProcessor(private val context: Context, private val dividerThemePackage: DividerThemePackage) : DividerInfoProcessor() {
    override fun handleDividerInfo(data: ShieldDisplayNode): Boolean {
        data.dividerInfo ?: let { data.dividerInfo = DividerConfigInfo() }
        when (data.positionType) {
            PositionType.FIRST, PositionType.SINGLE -> {
                var headerHeight = data.rowParent?.sectionParent?.sectionHeaderHeight ?: -1
                if (headerHeight >= 0) {
                    data.dividerInfo?.headerGapHeight = ViewUtils.dip2px(context, headerHeight.toFloat())
                } else {
                    data.dividerInfo?.headerGapHeight = ViewUtils.dip2px(context, dividerThemePackage.defaultHeaderHeight.toFloat())
                }
                data.rowParent?.sectionParent?.sectionHeaderDrawable?.let {
                    data.dividerInfo?.headerGapDrawable = it
                } ?: let {
                    data.dividerInfo?.headerGapDrawable = dividerThemePackage.defaultSpaceDrawable
                }

            }
            PositionType.LAST, PositionType.MIDDLE -> {
                data.dividerInfo?.headerGapHeight = 0
                data.dividerInfo?.headerGapDrawable = null
            }
            else -> {

            }
        }
        return false
    }
}