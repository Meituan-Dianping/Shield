package com.dianping.agentsdk.pagecontainer;

/**
 * Created by xianhe.dong on 2017/11/3.
 */

public interface FirstItemScrollListener {
	/**
	* recyclerView 除了headerview第一个item距离顶部的值
	*/
    void onScrollChanged(int topDistance, int topViewHeight, boolean isFirstItemShow);
}
