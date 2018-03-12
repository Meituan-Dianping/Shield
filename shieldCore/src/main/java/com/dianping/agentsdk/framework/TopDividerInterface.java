package com.dianping.agentsdk.framework;

import android.graphics.drawable.Drawable;

/**
 * Created by xianhe.dong on 16/6/22.
 */
public interface TopDividerInterface {

    //获得顶部分割线
    Drawable getTopDivider(int sectionPosition, int rowPosition);

    //分割线左边距
    int topDividerLeftOffset(int sectionPosition, int rowPosition);

    //分割线右边距
    int topDividerRightOffset(int sectionPosition, int rowPosition);
}

