package com.dianping.shield.feature;

import com.dianping.agentsdk.pagecontainer.SetTopParams;

/**
 * Created by runqi.wei on 2017/9/26.
 */

public interface ExtraCellTopParamsInterface extends ExtraCellTopInterface{
    SetTopParams getHeaderSetTopParams(int viewType);
    SetTopParams getFooterSetTopParams(int viewType);
}
