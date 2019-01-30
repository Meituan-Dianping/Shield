package com.dianping.shield.node.adapter.hotzone;

import com.dianping.shield.entity.ScrollDirection;
import com.dianping.shield.node.cellnode.ShieldDisplayNode;

/**
 * Created by runqi.wei at 2018/7/16
 */
public interface OnHotZoneStateChangeListener {

    void onHotZoneStateChanged(int position, int currentFirst, int currentLast,
                               ShieldDisplayNode node, HotZone hotZone,
                               HotZoneLocation oldHotZoneLocation, HotZoneLocation hotZoneLocation,
                               ScrollDirection scrollDirection);
}
