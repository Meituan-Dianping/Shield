package com.example.shield.linktype;

import com.dianping.agentsdk.framework.AgentInfo;
import com.dianping.agentsdk.framework.AgentInterface;
import com.dianping.agentsdk.framework.AgentListConfig;
import com.dianping.agentsdk.utils.AgentInfoHelper;

import java.util.Map;

/**
 * Created by nihao on 2017/7/14.
 */

public class LinkTypeAgentConfig implements AgentListConfig {
    private final static String AGENT_PKG_NAME = "com.example.shield.linktype.agent.";

    @Override
    public boolean shouldShow() {
        return true;
    }

    @Override
    public Map<String, AgentInfo> getAgentInfoList() {
        String[][][] agentArray = {
                {
                        {"linktype_custom", AGENT_PKG_NAME + "LinkTypeCustomAgent"}
                },
                {
                        {"linktype0", AGENT_PKG_NAME + "LinkTypeFirstAgent"}
                },
                {
                        {"linktype1", AGENT_PKG_NAME + "LinkTypeSecondAgent"}
                },
                {
                        {"linktype2", AGENT_PKG_NAME + "LinkTypeThirdAgent"}
                },
                {
                        {"linktype3", AGENT_PKG_NAME + "LinkTypeFourthAgent"}
                }

        };
        return AgentInfoHelper.getAgents(agentArray);
    }

    @Override
    public Map<String, Class<? extends AgentInterface>> getAgentList() {
        return null;
    }
}
