package com.dianping.shield.bridge.feature

import com.dianping.agentsdk.framework.AgentInterface

/**
 * Created by zhi.he on 2018/12/13.
 */
interface AgentFinderInterface {
    fun findAgent(name: String): AgentInterface?
}