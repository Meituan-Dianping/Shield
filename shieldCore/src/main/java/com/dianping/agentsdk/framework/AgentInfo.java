package com.dianping.agentsdk.framework;

import java.io.Serializable;

public class AgentInfo implements Serializable {
    public Class<? extends AgentInterface> agentClass;
    public String index;

    /**
     * 带排序的Agent
     *
     * @param agentClass
     * @param index      可以写类似"0200Basic.10Address" 不允许传null
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
        return agentClass.getSimpleName() + " " + index;
    }
}
