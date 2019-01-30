package com.dianping.shield.sectionrecycler

import com.dianping.agentsdk.sectionrecycler.layoutmanager.OnSmoothScrollListener

/**
 *
 * CellManager可使用的LayoutManager能力接口
 * Created by hezhi on 18/04/26.
 */
interface ShieldLayoutManagerInterface {

    /** 滚动到某个item，支持offset和smooth scroll */
    fun scrollToPositionWithOffset(globalPosition: Int, offset: Int, isSmoothScroll: Boolean)

    /** 滚动到某个item，支持offset和smooth scroll, 支持 smooth scroll 的 OnSmoothScrollListener 回调*/
    fun scrollToPositionWithOffset(globalPosition: Int, offset: Int, isSmoothScroll: Boolean, listeners: ArrayList<OnSmoothScrollListener>?)

    /** 获取第一个可见的Item Positon，支持完全可见和不完全可见 */

    fun findFirstVisibleItemPosition(completely: Boolean): Int

    /** 获取最后一个可见的Item Positon，支持完全可见和不完全可见 */
    fun findLastVisibleItemPosition(completely: Boolean): Int
}