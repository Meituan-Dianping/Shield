package com.dianping.agentsdk.framework;

import java.io.Serializable;

public class AgentInfo implements Serializable {
    public Class<? extends AgentInterface> agentClass;
    public String agentPath;
    public String index;
    public String extraInfo;
    public Class extraClass;

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
