package com.dianping.shield.feature;

import com.dianping.shield.entity.CellInfo;
import com.dianping.shield.entity.HotZoneYRange;
import com.dianping.shield.entity.ScrollDirection;

import java.util.ArrayList;

/**
 * Created by zhi.he on 2018/1/3.
 */

public interface HotZoneItemListener {

    HotZoneYRange defineHotZone();

    ArrayList<CellInfo> targetCells();

    void scrollReach(CellInfo cellInfo, ScrollDirection scrollDirection);

    void scrollOut(CellInfo cellInfo, ScrollDirection scrollDirection);
}
