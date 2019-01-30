package com.dianping.shield.bridge.feature

import com.dianping.shield.entity.NodeInfo

/**
 * Created by zhi.he on 2018/12/10.
 * NodeInfo类只能通过提供的静态方法构造出特定的几种实例
 * 例如:
 * NodeInfo.agent(xxAgent)
 * NodeInfo.section(xxAgent,0)
 * NodeInfo.row(xxAgent,0,0)
 * NodeInfo.header(xxAgent,0)
 * NodeInfo.footer(xxAgent,0)
 *
 * 请通过getFeature()方法获取该接口的实现
 *
 * 完整调用示例：
 *  getFeature().getNodeGlobalPosition(NodeInfo.row(xxAgent,0,0));
 *
 */
interface AgentGlobalPositionInterface {
    fun getNodeGlobalPosition(nodeInfo: NodeInfo): Int
    fun getAgentInfoByGlobalPosition(globalPosition: Int): NodeInfo?
}