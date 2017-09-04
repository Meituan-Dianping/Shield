package com.dianping.agentsdk.debugtools;

/**
 * Created by xianhe.dong on 2017/7/18.
 * email xianhe.dong@dianping.com
 */

public class AgentMapListItemModel implements Comparable<AgentMapListItemModel> {
    public String key;
    public String agentClassName;

    @Override
    public int compareTo(AgentMapListItemModel agentMapListItemModel) {
        return agentMapListItemModel.key.compareTo(this.key);
    }
}
