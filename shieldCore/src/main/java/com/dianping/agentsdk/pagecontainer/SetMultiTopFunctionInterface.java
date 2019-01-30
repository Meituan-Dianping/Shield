package com.dianping.agentsdk.pagecontainer;

import android.view.View;

import com.dianping.shield.feature.ExtraCellTopInterface;
import com.dianping.shield.feature.SetTopInterface;

/**
 * Created by runqi.wei on 2018/1/25.
 */

public interface SetMultiTopFunctionInterface {

    public boolean needMultiStickTop();

    /**
     * 置顶场景：置顶的View处于可视范围内，当上推页面时，如果超出页面范围，置顶显示，
     * 其他情况跟随页面滑动
     *
     * @return 在容器内用于替换topView的空白View
     */
    View setMultiTopView(ExtraCellTopInterface setTopInterface, int viewType, View topView, SetTopParams topParams);

    View setMultiTopView(SetTopInterface setTopInterface, int viewType, View topView, SetTopParams topParams);

    /**
     *  在updateView时更新置顶参数
     * @param topParams 新的置顶参数
     */
    void updateSetTopParams(View topView, SetTopParams topParams);

    boolean isTop(View topView);
}
