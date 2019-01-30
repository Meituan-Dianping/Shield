package com.dianping.agentsdk.framework;

import android.view.View;

/**
 * Created by zlf on 2018/11/29.
 */

public interface CellStatusMoreReuseInterface extends CellStatusMoreInterface {

    void updateLoadingMoreView(View view);

    void updateLoadingMoreFailedView(View view);
}
