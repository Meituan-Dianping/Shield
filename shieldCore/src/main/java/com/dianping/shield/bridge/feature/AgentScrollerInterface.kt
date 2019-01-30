package com.dianping.shield.bridge.feature

import com.dianping.shield.entity.AgentScrollerParams

/**
 * Created by zhi.he on 2018/7/31.
 * AgentScrollerParams类只能通过提供的静态方法构造出特定的几种实例
 * 例如:
 * AgentScrollerParams.toPage()
 * AgentScrollerParams.toAgent(xxAgent)
 * 可以通过Builder的方式设置其他参数，
 * AgentScrollerParams.toAgent(agentInterface).setOffset(offset).setNeedAutoOffset(true)
 *
 * 请通过getFeature()方法获取该接口的实现
 *
 * 完整调用示例：
 *  getFeature().scrollToNode(AgentScrollerParams.toRow(xxAgent.this, section, row)
 *              .setNeedAutoOffset(true).setOffset(offset));
 */
interface AgentScrollerInterface {
    fun scrollToNode(params: AgentScrollerParams)
}