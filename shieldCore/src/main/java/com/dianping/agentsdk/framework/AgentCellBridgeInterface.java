package com.dianping.agentsdk.framework;

import java.util.ArrayList;

/**
 * Created by hezhi on 16/3/14.
 */
public interface AgentCellBridgeInterface {
    void updateCells(ArrayList<AgentInterface> addList, ArrayList<AgentInterface> updateList, ArrayList<AgentInterface> deleteList);
}
