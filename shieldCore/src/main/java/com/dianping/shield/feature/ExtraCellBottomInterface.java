package com.dianping.shield.feature;

import com.dianping.agentsdk.pagecontainer.SetBottomFunctionInterface;

/**
 * Created by runqi.wei on 2017/10/9.
 */

public interface ExtraCellBottomInterface {

    boolean isHeaderBottomView(int viewType);
    SetBottomFunctionInterface getHeaderSetBottomFunctionInterface();

    boolean isFooterBottomView(int viewType);
    SetBottomFunctionInterface getFooterSetBottomFunctionInterface();
}
