package com.dianping.shield.node

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import com.dianping.agentsdk.framework.ViewUtils
import com.dianping.shield.core.R

/**
 * Created by zhi.he on 2018/7/3.
 */
class DividerThemePackage(val context: Context) {
    @JvmField
    // default divider middle line
    var defaultDivider: Drawable? = ContextCompat.getDrawable(context, R.drawable.section_recycler_view_divider)

    @JvmField
    // default left offset is 15dp
    var defaultDividerOffset: Rect? = Rect(ViewUtils.dip2px(context, 15f), 0, 0, 0)

    @JvmField
    // default section divider
    var defaultSectionDivider: Drawable? = ContextCompat.getDrawable(context, R.drawable.section_recycler_view_section_divider)

    @JvmField
    // default section top divider
    var defaultSectionTopDivider: Drawable? = null

    @JvmField
    // default section bottom divider
    var defaultSectionBottomDivider: Drawable? = null

    @JvmField
    // default section offset is (0,0,0,0)
    var defaultSectionDividerOffset: Rect? = Rect(0, 0, 0, 0)

    @JvmField
    // default height for spaces is 10dp
    var defaultHeaderHeight: Int = 10 //单位dp

    @JvmField
    var defaultFooterHeight: Int = 0  //单位dp

    @JvmField
    var firstHeaderExtraHeight: Int = 0  //单位dp

    @JvmField
    var lastFooterExtraHeight: Int = 10  //单位dp

    @JvmField
    var needAddFirstHeader: Boolean = false//native 默认不加最头上的header

    @JvmField
    var needAddLastFooter: Boolean = true

    @JvmField
    var defaultSpaceDrawable: Drawable? = null

    @JvmField
    var enableDivider: Boolean = true
}