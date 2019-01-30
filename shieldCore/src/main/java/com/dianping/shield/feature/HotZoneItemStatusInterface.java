package com.dianping.shield.feature;

import com.dianping.shield.entity.CellInfo;
import com.dianping.shield.entity.HotZoneYRange;
import com.dianping.shield.entity.ScrollDirection;
import com.dianping.shield.node.adapter.hotzone.AgentHotZoneInfo;
import com.dianping.shield.node.adapter.hotzone.CellHotZoneInfo;

import java.util.ArrayList;
import java.util.Set;

public interface HotZoneItemStatusInterface {
    HotZoneYRange defineHotZone();

    ArrayList<CellInfo> targetCells();

    void onHotZoneLocationChanged(ArrayList<CellHotZoneInfo> locationList, ScrollDirection scrollDirection);
}
