package com.dianping.agentsdk.pagecontainer;

import android.support.v7.widget.RecyclerView;

/**
 * Created by zdh on 16/11/11.
 */

public interface RecyclerViewPageContainerInterface {
    void addScrollListener(RecyclerView.OnScrollListener onScrollListener);
    void removeScrollListener(RecyclerView.OnScrollListener onScrollListener);
}
