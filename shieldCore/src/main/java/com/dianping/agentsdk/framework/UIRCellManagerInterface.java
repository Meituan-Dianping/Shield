package com.dianping.agentsdk.framework;

import android.view.ViewGroup;

/**
 * Created by huajiawei on 2018/3/6.
 *
 * 支持局部刷新(U)、插入(I)、删除(R)的CellManagerInterface
 */

public interface UIRCellManagerInterface<T extends ViewGroup> extends CellManagerInterface<T> {

    /**
     * See {@link UIRDriverInterface#updateAgentCell(AgentInterface, UpdateAgentType, int, int, int)}
     */
    void updateAgentCell(AgentInterface agent, UpdateAgentType updateAgentType, int section, int row, int count);
}
