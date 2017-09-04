package com.dianping.agentsdk.pagecontainer;

import android.view.View;

/**
 * Created by zdh on 16/11/10.
 */

public interface CommonPageFunctionInterface{
    void setTopView(View topView,View originView);
    void setBottomView(View bottomView,View originView);
    int getScrollY();
}
