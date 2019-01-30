package com.dianping.shield.node.useritem;

import android.graphics.Rect;

/**
 * Created by zhi.he on 2018/8/27.
 */

public class StaggeredGridSectionItem extends SectionItem {
    public int spanCount = 2; //列数
    public int xStaggeredGridGap = 0; //x轴间隔，单位dp
    public int yStaggeredGridGap = 0; //y轴间隔，单位dp
    public int staggeredLeftMargin = 0;
    public int staggeredRightMargin = 0;

    public LayoutType sectionLayoutType = LayoutType.STAGGERED_GRID;
}
