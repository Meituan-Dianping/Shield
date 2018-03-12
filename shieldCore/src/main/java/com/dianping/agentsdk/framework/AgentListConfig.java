package com.dianping.agentsdk.framework;

import java.util.Map;

/**
 * use ShieldConfig instead;
 *
 */
@Deprecated
public interface AgentListConfig {

    /**
     * 此函数返回此AgentList是否该被展示
     * 需非常谨慎,因为不管是否展示,此方法都会被调用,绝对不许在此方法中crash
     *
     * @return true 会被展示,false 不被展示
     */
    boolean shouldShow();

    /**
     * 得到在带排序的AgentList
     *
     * @return
     */
    @Deprecated
    Map<String, AgentInfo> getAgentInfoList();

    /**
     * 得到一个agentList
     *
     * @return
     */
    @Deprecated
    Map<String, Class<? extends AgentInterface>> getAgentList();
}
