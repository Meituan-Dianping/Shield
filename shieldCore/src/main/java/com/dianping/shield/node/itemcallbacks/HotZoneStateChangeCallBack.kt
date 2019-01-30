package com.dianping.shield.node.itemcallbacks

import com.dianping.shield.entity.ScrollDirection

/**
 * Created by runqi.wei at 2018/9/11
 */
interface HotZoneStateChangeCallBack {

    fun scrollReach(sectionPosition: Int?, rowPosition: Int?, scrollDirection: ScrollDirection?)

    fun scrollOut(sectionPosition: Int?, rowPosition: Int?, scrollDirection: ScrollDirection?)

}