package com.dianping.agentsdk.pagecontainer;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by zdh on 16/11/10.
 */

public interface CommonPageContainerFunctionInterface {
    void scrollToPosition(int position);
    void smoothScrollToPosition(int position);
    FrameLayout getRecyclerViewLayout();
    void setCanScroll(boolean canScroll);
    void addItemDecoration(RecyclerView.ItemDecoration decor);
    void setLayoutManager(RecyclerView.LayoutManager layoutManager);
    View getRecyclerViewRootView();
}
