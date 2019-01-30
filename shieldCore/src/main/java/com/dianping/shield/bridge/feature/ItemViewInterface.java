package com.dianping.shield.bridge.feature;

import android.view.View;

/**
 * Created by xianhe.dong on 2018/12/18.
 * email xianhe.dong@dianping.com
 */

public interface ItemViewInterface extends ViewRectInterface{
    /**
     * 获取展示的container
     * @param view
     * @return
     */
    View getItemView(View view);

    int getItemViewTop(View view);

    int getItemViewBottom(View view);

    int getItemViewLeft(View view);

    int getItemViewRight(View view);

    int getItemViewWidth(View view);

    int getItemViewHeight(View view);
}
