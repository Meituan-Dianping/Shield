package com.dianping.shield.framework;

import java.util.HashMap;

/**
 * Created by zhi.he on 2017/10/23.
 */

public class ShieldConfigInfo implements Cloneable {
    public String hostName;
    public Class agentClass;
    public String agentPath;
    public ConfigPriority priority = ConfigPriority.MAIN;
    public HashMap<String, Object> arguments;

    public ShieldConfigInfo(String hostName, Class agentClass) {
        this.hostName = hostName;
        this.agentClass = agentClass;
    }

    public ShieldConfigInfo(String hostName, String agentPath) {
        this.hostName = hostName;
        this.agentPath = agentPath;
    }

    public ShieldConfigInfo(String hostName, Class agentClass, ConfigPriority priority) {
        this.hostName = hostName;
        this.agentClass = agentClass;
        this.priority = priority;
    }

    public ShieldConfigInfo(String hostName, String agentPath, ConfigPriority priority) {
        this.hostName = hostName;
        this.agentPath = agentPath;
        this.priority = priority;
    }

    @Override
    public Object clone() {
        ShieldConfigInfo result;
        try {
            result = (ShieldConfigInfo) super.clone();
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            result = new ShieldConfigInfo(hostName, agentClass, priority);
            result.agentPath = agentPath;
            result.arguments = arguments;
            return result;
        }
        return result;
    }
}
