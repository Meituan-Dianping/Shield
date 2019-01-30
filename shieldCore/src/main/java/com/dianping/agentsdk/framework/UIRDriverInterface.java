package com.dianping.agentsdk.framework;

/**
 * Created by huajiawei on 2018/3/7.
 */

public interface UIRDriverInterface extends DriverInterface {

    /**
     * 此接口用于支持局部刷新
     *
     * @param agent
     * @param updateAgentType 决定这里是刷新、插入section、插入row、删除section还是删除row。
     * @param section
     * @param row
     * @param count 如果这里是插入/删除section，count表示section count；如果这里是插入/删除row，count表示row count
     */
    void updateAgentCell(AgentInterface agent, UpdateAgentType updateAgentType, int section, int row, int count);
}
