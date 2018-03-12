package com.dianping.agentsdk.pagecontainer;

import android.view.View;

/**
 * Created by zdh on 16/12/12.
 */

public interface SetTopFunctionInterface {

    /**
     * 置顶场景：置顶的View处于可视范围内，当上推页面时，如果超出页面范围，置顶显示，
     * 其他情况跟随页面滑动
     *
     * @param topView 真实置顶的view
     * @return 在容器内用于替换topView的空白View
     */
    View setTopView(View topView, SetTopParams topParams);

    /**
     *  在updateView时更新置顶参数
     * @param topParams 新的置顶参数
     */
    void updateSetTopParams(View topView, SetTopParams topParams);

    boolean isTop(View topView);
}
