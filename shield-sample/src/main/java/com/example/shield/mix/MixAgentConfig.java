package com.example.shield.mix;

import com.dianping.agentsdk.framework.AgentInfo;
import com.dianping.agentsdk.framework.AgentInterface;
import com.dianping.agentsdk.framework.AgentListConfig;
import com.dianping.agentsdk.utils.AgentInfoHelper;

import java.util.Map;

/**
 * Created by nihao on 2017/7/17.
 */

public class MixAgentConfig implements AgentListConfig {
    private final static String AGENT_PKG_NAME = "com.example.shield.mix.agent.";

    @Override
    public boolean shouldShow() {
        return true;
    }

    @Override
    public Map<String, AgentInfo> getAgentInfoList() {
        String[][][] agentArray = {
                {
                        {"mix0", AGENT_PKG_NAME + "MixLoadingAgent"},
                        {"mix1", AGENT_PKG_NAME + "MixCellAgent"}
                }
        };
        return AgentInfoHelper.getAgents(agentArray);
    }

    @Override
    public Map<String, Class<? extends AgentInterface>> getAgentList() {
        return null;
    }
}
