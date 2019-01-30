package com.dianping.shield.framework

import android.os.Bundle
import com.dianping.agentsdk.framework.AgentListConfig
import com.dianping.agentsdk.framework.AgentManagerInterface
import com.dianping.agentsdk.framework.CellManagerInterface
import java.util.*

/**
 * Created by zhi.he on 2018/5/9.
 */
interface ShieldContainerInterface {
    fun generaterConfigs(): ArrayList<AgentListConfig>?

    fun resetAgents(savedInstanceState: Bundle?)

    fun getHostCellManager(): CellManagerInterface<*>?

    fun getHostAgentManager(): AgentManagerInterface?
}