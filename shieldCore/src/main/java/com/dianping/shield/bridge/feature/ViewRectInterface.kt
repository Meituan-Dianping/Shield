package com.dianping.shield.bridge.feature

import android.graphics.Rect
import android.view.View

/**
 * Created by zhi.he on 2018/12/11.
 */
interface ViewRectInterface {
    fun getViewParentRect(rootBizView: View?): Rect?
}