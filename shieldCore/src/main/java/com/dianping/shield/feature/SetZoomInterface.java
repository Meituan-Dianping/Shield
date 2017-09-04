package com.dianping.shield.feature;

import com.dianping.agentsdk.pagecontainer.SetZoomFunctionInterface;

/**
 * Created by xianhe.dong on 2017/7/21.
 * email xianhe.dong@dianping.com
 * 下拉放大接口 模块实现
 */

public interface SetZoomInterface {

    /**
     * 根据viewType返回是否是zoomView
     * @param viewType
     * @return
     */
    boolean isZoomView(int viewType);

    /**
     * 从构造方法中获取pageContainer 强转返回
     * @return
     */
    SetZoomFunctionInterface getSetZoomFunctionInterface();
}
