package com.dianping.agentsdk.framework;

import android.view.View;

/**
 * Created by hezhi on 16/6/24.
 */
public interface CellStatusMoreInterface {

    CellStatus.LoadingMoreStatus loadingMoreStatus();

    void onBindView(CellStatus.LoadingMoreStatus status);

    View loadingMoreView();

    View loadingMoreFailedView();

    View.OnClickListener loadingMoreRetryListener();
}
