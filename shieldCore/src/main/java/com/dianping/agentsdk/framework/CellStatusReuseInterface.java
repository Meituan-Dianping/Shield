package com.dianping.agentsdk.framework;

import android.view.View;

/**
 * Created by zlf on 2018/11/29.
 */

public interface CellStatusReuseInterface extends CellStatusInterface {

    void updateLoadingView(View view);

    void updateLoadingFailedView(View view);

    void updateLoadingEmptyView(View view);
}
