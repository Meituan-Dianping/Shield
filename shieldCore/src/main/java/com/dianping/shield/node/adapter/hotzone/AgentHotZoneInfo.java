package com.dianping.shield.node.adapter.hotzone;

public class AgentHotZoneInfo  {
    public String agentHostName;
    public HotZoneLocation hotZoneLocation;

    public AgentHotZoneInfo(String agentHostName, HotZoneLocation hotZoneLocation) {
        this.agentHostName = agentHostName;
        this.hotZoneLocation = hotZoneLocation;
    }
}
