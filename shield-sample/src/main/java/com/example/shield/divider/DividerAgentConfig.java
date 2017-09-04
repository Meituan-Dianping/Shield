package com.example.shield.divider;

import com.dianping.agentsdk.framework.AgentInfo;
import com.dianping.agentsdk.framework.AgentInterface;
import com.dianping.agentsdk.framework.AgentListConfig;
import com.dianping.agentsdk.utils.AgentInfoHelper;

import java.util.Map;

/**
 * Created by bingwei on 17/7/12.
 */

public class DividerAgentConfig implements AgentListConfig {
    private static final String AGENT_PKG_NAME = "com.example.shield.divider.agent.";

    @Override
    public boolean shouldShow() {
        return true;
    }

    @Override
    public Map<String, AgentInfo> getAgentInfoList() {
        String[][][] agentArray = {
                {
                        {"default_divider", AGENT_PKG_NAME + "DefaultDividerAgent"}
                },
                {
                        {"section_divider", AGENT_PKG_NAME + "SectionDividerAgent"}
                },
                {
                        {"row_divider", AGENT_PKG_NAME + "RowDividerAgent"}
                }
        };
        return AgentInfoHelper.getAgents(agentArray);
    }

    @Override
    public Map<String, Class<? extends AgentInterface>> getAgentList() {
        return null;
    }
}
