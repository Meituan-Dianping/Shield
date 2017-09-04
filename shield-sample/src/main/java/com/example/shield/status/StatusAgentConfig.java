package com.example.shield.status;

import com.dianping.agentsdk.framework.AgentInfo;
import com.dianping.agentsdk.framework.AgentInterface;
import com.dianping.agentsdk.framework.AgentListConfig;
import com.dianping.agentsdk.utils.AgentInfoHelper;

import java.util.Map;

/**
 * Created by bingwei on 17/7/12..
 */

public class StatusAgentConfig implements AgentListConfig {
    private static final String AGENT_PKG_NAME = "com.example.shield.status.agent.";

    @Override
    public boolean shouldShow() {
        return true;
    }

    @Override
    public Map<String, AgentInfo> getAgentInfoList() {
        String[][][] agentArray = {
                {
                        {"loading_button", AGENT_PKG_NAME + "ControlButtonAgent"},
                        {"loading", AGENT_PKG_NAME + "LoadingStatusAgent"}
                },
                {
                        {"loading_more", AGENT_PKG_NAME + "LoadingStatusMoreAgent"}
                },
                {
                        {"loading_more_again", AGENT_PKG_NAME + "LoadingStatusMoreAgent"}
                }
        };
        return AgentInfoHelper.getAgents(agentArray);
    }

    @Override
    public Map<String, Class<? extends AgentInterface>> getAgentList() {
        return null;
    }
}
