package com.dianping.shield.framework;

import android.os.Bundle;

import com.dianping.agentsdk.framework.AgentListConfig;
import com.dianping.agentsdk.framework.AgentManagerInterface;
import com.dianping.agentsdk.framework.CellManagerInterface;

import java.util.ArrayList;

/**
 * Created by zhi.he on 2017/8/14.
 */

public interface ShieldContainerInterface {

    ArrayList<AgentListConfig> generaterConfigs();

    void resetAgents(Bundle savedInstanceState);

    CellManagerInterface getHostCellManager();

    AgentManagerInterface getHostAgentManager();
}
