package com.dianping.agentsdk.framework;

import android.graphics.drawable.Drawable;

/**
 * Created by hezhi on 16/6/22.
 */
public interface DividerInterface {
    //分割线ShowType
    enum ShowType {
        //只展示Section顶部和底部分割线
        TOP_END,
        //展示所有分割线
        ALL,
        //隐藏所有分割线
        NONE,
        //隐藏Section顶部和底部分割线,只展示Row之间分割线
        MIDDLE,
        //隐藏Section顶部分割线
        NO_TOP,

        DEFAULT
    }
    //自定义某个Row下方的分割线View，以Drawable形式提供
    Drawable getDivider(int sectionPosition, int rowPosition);

    //自定义某个Row下方的分割线距Section边缘的左边距
    int dividerOffset(int sectionPosition, int rowPosition);

    //自定义某个Row下方的分割线是否展示
    boolean showDivider(int sectionPosition, int rowPosition);

    //自定义某个Section的分割线ShowType
    ShowType dividerShowType(int sectionPosition);
}
