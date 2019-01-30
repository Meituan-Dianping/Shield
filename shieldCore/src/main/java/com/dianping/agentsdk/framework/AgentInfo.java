package com.dianping.agentsdk.framework;

import com.dianping.shield.framework.ConfigPriority;

import java.io.Serializable;
import java.util.HashMap;

public class AgentInfo implements Serializable {
    public Class<? extends AgentInterface> agentClass;
    public String agentPath;
    public String index;
    public String extraInfo;
    public Class extraClass;
    public ConfigPriority configPriority = ConfigPriority.MAIN;
    public HashMap<String, Object> arguments;

    /**
     * 带排序的Agent
     *
     * @param agentClass
     * @param index
     */
    public AgentInfo(Class<? extends AgentInterface> agentClass, String index) {
        if (index == null) {
            throw new RuntimeException("index 不许为null 可以传空字符串");
        }

        this.agentClass = agentClass;
        this.index = index;
    }

    @Override
    public String toString() {
        if (agentClass != null) {
            return agentClass.getSimpleName() + " " + index;
        } else if (extraClass != null) {
            return extraClass.getSimpleName() + " " + index;
        } else {
            return agentPath + " " + index;
        }
    }
}
