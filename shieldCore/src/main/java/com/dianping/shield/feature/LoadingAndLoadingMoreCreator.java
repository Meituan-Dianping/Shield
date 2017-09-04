package com.dianping.shield.feature;

import android.view.View;

/**
 * Created by zhi.he on 2017/7/6.
 */

public interface LoadingAndLoadingMoreCreator {

    //定制app 整体的loading view
    View loadingView();

    //定制app 整体的loading failed view
    View loadingFailedView();

    //定制app 整体的empty view
    View emptyView();

    //定制app 整体的loading more view
    View loadingMoreView();

    //定制app 整体的loading more failed view
    View loadingMoreFailedView();
}
