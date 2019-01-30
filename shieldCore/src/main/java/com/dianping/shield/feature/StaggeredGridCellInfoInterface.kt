package com.dianping.shield.feature

import android.graphics.Rect

/**
 *
 * 瀑布流模块接口，实现该接口的模块Cell会展示成瀑布流
 * Created by hezhi on 18/04/26.
 */

interface StaggeredGridCellInfoInterface {

    //瀑布流列数
    fun spanCount(section: Int): Int {
        return 1
    }

    //x轴间隔
    fun xStaggeredGridGap(section: Int): Int {
        return 0
    }

    //x轴间隔
    fun yStaggeredGridGap(section: Int): Int {
        return 0
    }

    fun staggeredGridLeftMargin(section: Int): Int{
        return 0
    }

    fun staggeredGridRightMargin(section: Int): Int{
        return 0
    }
}