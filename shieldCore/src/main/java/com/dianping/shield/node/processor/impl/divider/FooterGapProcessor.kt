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
class FooterGapProcessor(private val context: Context, private val dividerThemePackage: DividerThemePackage) : DividerInfoProcessor() {
    override fun handleDividerInfo(data: ShieldDisplayNode): Boolean {
        data.dividerInfo ?: let { data.dividerInfo = DividerConfigInfo() }
        when (data.positionType) {
            PositionType.LAST, PositionType.SINGLE -> {
                val footerHeight = data.rowParent?.sectionParent?.sectionFooterHeight ?: -1
                if (footerHeight >= 0) {
                    data.dividerInfo?.footerGapHeight = ViewUtils.dip2px(context, footerHeight.toFloat())
                } else {
                    data.dividerInfo?.footerGapHeight = ViewUtils.dip2px(context, dividerThemePackage.defaultFooterHeight.toFloat())
                }
                data.rowParent?.sectionParent?.sectionFooterDrawable?.let {
                    data.dividerInfo?.footerGapDrawable = it
                } ?: let {
                    data.dividerInfo?.footerGapDrawable = dividerThemePackage.defaultSpaceDrawable
                }
            }
            PositionType.FIRST, PositionType.MIDDLE -> {
                data.dividerInfo?.footerGapHeight = 0
                data.dividerInfo?.footerGapDrawable = null
            }
            else -> {

            }
        }
        return false
    }
}