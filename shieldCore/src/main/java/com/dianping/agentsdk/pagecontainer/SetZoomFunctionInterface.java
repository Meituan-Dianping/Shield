package com.dianping.agentsdk.pagecontainer;

import android.view.View;

/**
 * Created by xianhe.dong on 2017/7/21.
 * email xianhe.dong@dianping.com
 * 下拉放大 容器实现
 */

public interface SetZoomFunctionInterface {
    /**
     * 获取到模块的zoomView 透传到PullZoomRecylerView
     * @param zoomView
     */
    void setZoomView(View zoomView);
}
