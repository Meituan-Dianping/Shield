package com.dianping.shield.node.adapter.hotzone;

import com.dianping.shield.node.cellnode.ShieldDisplayNode;

public class HotZoneInfo  {
    public ShieldDisplayNode shieldDisplayNode;
    public HotZoneLocation hotZoneLocation;

    public HotZoneInfo(ShieldDisplayNode shieldDisplayNode, HotZoneLocation hotZoneLocation) {
        this.shieldDisplayNode = shieldDisplayNode;
        this.hotZoneLocation = hotZoneLocation;
    }
}
