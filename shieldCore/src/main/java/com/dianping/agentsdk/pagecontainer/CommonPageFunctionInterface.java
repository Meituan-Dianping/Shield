package com.dianping.agentsdk.pagecontainer;

import android.view.View;
import android.widget.FrameLayout;

import android.support.v7.widget.RecyclerView;

/**
 * Created by zdh on 16/11/10.
 */

public interface CommonPageFunctionInterface{
    void setTopView(View topView,View originView);
    void setBottomView(View bottomView,View originView);
    int getScrollY();
}
