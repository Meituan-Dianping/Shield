package com.dianping.shield.framework;

/**
 * Created by zhi.he on 2017/10/23.
 */

public class ShieldConfigInfo {
    public String hostName;
    public Class agentClass;
    public String agentPath;

    public ShieldConfigInfo(String hostName, Class agentClass) {
        this.hostName = hostName;
        this.agentClass = agentClass;
    }

    public ShieldConfigInfo(String hostName, String agentPath) {
        this.hostName = hostName;
        this.agentPath = agentPath;
    }
}
