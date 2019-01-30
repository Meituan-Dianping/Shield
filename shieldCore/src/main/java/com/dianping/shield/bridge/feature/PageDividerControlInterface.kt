package com.dianping.shield.bridge.feature

import com.dianping.shield.entity.PageDividerThemeParams

/**
 * Created by zhi.he on 2018/12/10.
 *
 * PageDividerThemeParams类只能通过提供的静态方法构造出特定的几种实例
 * 例如:
 * PageDividerThemeParams.dividerLeftOffset(15)
 * PageDividerThemeParams.needLastFooter(false)
 *
 * 请通过getFeature()方法获取该接口的实现
 *
 * 完整调用示例：
 * getFeature().setPageDividerTheme(PageDividerThemeParams.dividerLeftOffset(15));
 */
interface PageDividerControlInterface {
    fun setPageDividerTheme(params: PageDividerThemeParams)
}