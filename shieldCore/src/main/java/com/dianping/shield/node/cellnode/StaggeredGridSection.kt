package com.dianping.shield.node.cellnode

import android.graphics.Rect

/**
 * Created by zhi.he on 2018/8/27.
 */
class StaggeredGridSection : ShieldSection() {
    var spanCount = 2 //列数
    var xStaggeredGridGap = 0 //x轴间隔，单位dp
    var yStaggeredGridGap = 0 //y轴间隔，单位dp
    var staggeredGridLeftMargin = 0;
    var staggeredGridRightMargin = 0;

}