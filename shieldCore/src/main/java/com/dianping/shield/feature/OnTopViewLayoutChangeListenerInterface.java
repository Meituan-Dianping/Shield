package com.dianping.shield.feature;

import com.dianping.agentsdk.pagecontainer.OnTopViewLayoutChangeListener;
import com.dianping.agentsdk.pagecontainer.SetTopViewListenerInterface;
import com.dianping.shield.entity.CellType;

/**
 * Created by runqi.wei on 2018/3/26.
 */

public interface OnTopViewLayoutChangeListenerInterface {

    SetTopViewListenerInterface getSetTopViewListenerInterface();

    OnTopViewLayoutChangeListener getOnTopViewLayoutChangeListener(CellType cellType, int viewType);

}
