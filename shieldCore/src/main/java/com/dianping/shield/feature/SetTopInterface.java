package com.dianping.shield.feature;

import com.dianping.agentsdk.pagecontainer.SetTopFunctionInterface;

/**
 * Created by zdh on 17/4/5.
 */

public interface SetTopInterface {

    boolean isTopView(int viewType);

    SetTopFunctionInterface getSetTopFunctionInterface();
}
