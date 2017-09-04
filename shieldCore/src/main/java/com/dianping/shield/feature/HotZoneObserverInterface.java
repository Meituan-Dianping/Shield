package com.dianping.shield.feature;

import com.dianping.shield.entity.HotZoneYRange;
import com.dianping.shield.entity.ScrollDirection;

import java.util.Set;

/**
 * Created by zhi.he on 2017/8/11.
 */

public interface HotZoneObserverInterface {

    HotZoneYRange defineHotZone();

    Set<String> observerAgents();

    void scrollReach(String agentName, ScrollDirection scrollDirection);

    void scrollOut(String agentName, ScrollDirection scrollDirection);
}
