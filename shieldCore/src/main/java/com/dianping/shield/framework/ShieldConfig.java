package com.dianping.shield.framework;

import com.dianping.agentsdk.framework.AgentInfo;
import com.dianping.agentsdk.framework.AgentInterface;
import com.dianping.agentsdk.framework.AgentListConfig;
import com.dianping.agentsdk.utils.AgentInfoHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by zhi.he on 2017/10/23.
 */

public abstract class ShieldConfig implements AgentListConfig {

    public abstract ArrayList<ArrayList<ShieldConfigInfo>> getAgentGroupConfig();

    @Override
    public Map<String, AgentInfo> getAgentInfoList() {
        ArrayList<ArrayList<ShieldConfigInfo>> shieldConfig = getAgentGroupConfig();
        if (shieldConfig == null || shieldConfig.isEmpty()) return null;

        HashMap<String, AgentInfo> agents = new LinkedHashMap<>();
        for (int i = 0; i < shieldConfig.size(); i++) {
            ArrayList<ShieldConfigInfo> groupList = shieldConfig.get(i);
            if (groupList == null || groupList.isEmpty()) continue;

            for (int j = 0; j < groupList.size(); j++) {
                ShieldConfigInfo shieldConfigInfo = groupList.get(j);
                if (shieldConfigInfo.agentClass != null) {
                    try {
                        agents.put(shieldConfigInfo.hostName,
                                AgentInfoHelper.createAgentInfo(shieldConfigInfo.agentClass, i, j,
                                        shieldConfig.size(), groupList.size()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                } else if (shieldConfigInfo.agentPath != null && !"".equals(shieldConfigInfo.agentPath)) {
                    try {
                        Class agentClass = Class.forName(shieldConfigInfo.agentPath);
                        agents.put(shieldConfigInfo.hostName,
                                AgentInfoHelper.createAgentInfo(agentClass, i, j,
                                        shieldConfig.size(), groupList.size()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                }
            }
        }
        return agents;

    }

    @Override
    public Map<String, Class<? extends AgentInterface>> getAgentList() {
        return null;
    }
}
