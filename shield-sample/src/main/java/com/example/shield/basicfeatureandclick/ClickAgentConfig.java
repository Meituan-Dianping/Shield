package com.example.shield.basicfeatureandclick;

import com.dianping.agentsdk.framework.AgentInfo;
import com.dianping.agentsdk.framework.AgentInterface;
import com.dianping.agentsdk.framework.AgentListConfig;
import com.dianping.agentsdk.utils.AgentInfoHelper;

import java.util.Map;

/**
 * Created by bingwei on 17/7/12..
 */

public class ClickAgentConfig implements AgentListConfig {
    private static final String AGENT_PKG_NAME = "com.example.shield.basicfeatureandclick.agent.";

    @Override
    public boolean shouldShow() {
        return true;
    }

    @Override
    public Map<String, AgentInfo> getAgentInfoList() {
        String[][][] agentArray = {
                {
                        {"module_a", AGENT_PKG_NAME + "ClickSectionThreeAgent"}
                },
                {
                        {"module_b", AGENT_PKG_NAME + "ClickSectionTwoAgent"}
                }
        };
        return AgentInfoHelper.getAgents(agentArray);
    }

    @Override
    public Map<String, Class<? extends AgentInterface>> getAgentList() {
        return null;
    }
}
