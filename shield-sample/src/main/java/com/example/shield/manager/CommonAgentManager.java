package com.example.shield.manager;

import android.support.v4.app.Fragment;

import com.dianping.agentsdk.framework.AgentCellBridgeInterface;
import com.dianping.agentsdk.framework.AgentInterface;
import com.dianping.agentsdk.framework.DriverInterface;
import com.dianping.agentsdk.framework.PageContainerInterface;
import com.dianping.shield.manager.LightAgentManager;

/**
 * Created by nihao on 2017/9/4.
 */

public class CommonAgentManager extends LightAgentManager {

    protected DriverInterface featureBridge;

    public CommonAgentManager(Fragment fragment, AgentCellBridgeInterface agentCellBridgeInterface,
                              DriverInterface featureBridgeInterface, PageContainerInterface pageContainerInterface) {
        super(fragment, agentCellBridgeInterface, featureBridgeInterface, pageContainerInterface);
        this.featureBridge = featureBridgeInterface;
    }

    protected AgentInterface constructAgents(Class<? extends AgentInterface> agentClass) {
        AgentInterface cellAgent = null;
        try {
            cellAgent = agentClass.getConstructor(Object.class).newInstance(
                    fragment);
        } catch (Exception e) {
//                        e.printStackTrace();
        }
        if (cellAgent == null) {
            try {
                Class<?>[] params = {Fragment.class,
                        DriverInterface.class, PageContainerInterface.class};
                Object[] values = {fragment, featureBridge, pageContainer};
                cellAgent = agentClass.getConstructor(params).newInstance(
                        values);
            } catch (Exception e) {
//                            e.printStackTrace();
            }
        }

        if (cellAgent == null) {
            try {
                cellAgent = agentClass.newInstance();
            } catch (Exception e) {
//                            e.printStackTrace();
            }
        }

        return cellAgent;
    }
}
