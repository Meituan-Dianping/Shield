package com.dianping.agentsdk.framework;

import android.view.ViewGroup;

import java.util.ArrayList;


/**
 * Created by hezhi on 15/12/11.
 */
public interface CellManagerInterface<T extends ViewGroup> {

    void setAgentContainerView(T containerView);

    //手动刷新接口
    void notifyCellChanged();

    void updateAgentCell(AgentInterface agent);

    void updateCells(ArrayList<AgentInterface> addList, ArrayList<AgentInterface> updateList, ArrayList<AgentInterface> deleteList);

}
