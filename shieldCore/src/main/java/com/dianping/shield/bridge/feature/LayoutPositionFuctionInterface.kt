package com.dianping.shield.bridge.feature

import android.view.View

/**
 * Created by zhi.he on 2018/12/10.
 */
interface LayoutPositionFuctionInterface {
    fun getChildAtIndex(index: Int, isBizView: Boolean = true): View?
    fun findViewAtPosition(position: Int, isBizView: Boolean = true): View?
    fun getChildCount(): Int
    fun getChildAdapterPosition(child: View): Int
}