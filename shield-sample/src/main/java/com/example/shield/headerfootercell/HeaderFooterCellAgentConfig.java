package com.example.shield.headerfootercell;

import com.dianping.agentsdk.framework.AgentInfo;
import com.dianping.agentsdk.framework.AgentInterface;
import com.dianping.agentsdk.framework.AgentListConfig;
import com.dianping.agentsdk.utils.AgentInfoHelper;

import java.util.Map;

/**
 * headerfootercell config file
 * Created by nihao on 2017/7/13.
 */
public class HeaderFooterCellAgentConfig implements AgentListConfig {
    private static final String AGENT_PKG_NAME = "com.example.shield.headerfootercell.agent.";

    @Override
    public boolean shouldShow() {
        return true;
    }

    @Override
    public Map<String, AgentInfo> getAgentInfoList() {
        String[][][] agentArray = {
                {
                        {"headerfootercellagent1", AGENT_PKG_NAME + "HeaderFooterCellFirstAgent"}
                },
                {
                        {"headerfootercellagent2", AGENT_PKG_NAME + "HeaderFooterCellSecondAgent"}
                }
        };
        return AgentInfoHelper.getAgents(agentArray);
    }

    @Override
    public Map<String, Class<? extends AgentInterface>> getAgentList() {
        return null;
    }
}
