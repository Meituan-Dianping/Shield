package com.dianping.agentsdk.framework;

import android.view.View;

/**
 * Created by hezhi on 16/6/22.
 */
public interface CellStatusInterface {

    CellStatus.LoadingStatus loadingStatus();

    View loadingView();

    View loadingFailedView();

    View emptyView();

    View.OnClickListener loadingRetryListener();

}
