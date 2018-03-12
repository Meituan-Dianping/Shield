package com.dianping.shield.feature;

import com.dianping.agentsdk.pagecontainer.SetTopFunctionInterface;

/**
 * Created by runqi.wei on 2017/9/25.
 */

public interface ExtraCellTopInterface {

    boolean isHeaderTopView(int viewType);

    SetTopFunctionInterface getSetHeaderTopFunctionInterface();

    boolean isFooterTopView(int viewType);

    SetTopFunctionInterface getSetFooterTopFunctionInterface();
}
