package com.dianping.shield.feature;

import com.dianping.agentsdk.pagecontainer.SetTopParams;

/**
 * Created by hai on 2017/6/21.
 */

public interface SetTopParamsInterface extends SetTopInterface{
    SetTopParams getSetTopParams(int viewType);
}
