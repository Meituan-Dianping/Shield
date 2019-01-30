package com.dianping.shield.node.adapter.hotzone;

import com.dianping.shield.entity.CellInfo;

public class CellHotZoneInfo {
    public CellInfo cellInfo;
    public HotZoneLocation hotZoneLocation;

    public CellHotZoneInfo(CellInfo cellInfo, HotZoneLocation hotZoneLocation) {
        this.cellInfo = cellInfo;
        this.hotZoneLocation = hotZoneLocation;
    }
}
