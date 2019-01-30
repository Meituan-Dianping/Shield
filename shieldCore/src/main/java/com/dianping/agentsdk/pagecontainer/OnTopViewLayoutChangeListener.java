package com.dianping.agentsdk.pagecontainer;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by zhi.he on 2018/3/23.
 */

public interface OnTopViewLayoutChangeListener {
    void onLayoutLocationChangeListener(View view, int preTopViewPosition, int preTopViewStatus, ViewGroup.MarginLayoutParams lp);
}

