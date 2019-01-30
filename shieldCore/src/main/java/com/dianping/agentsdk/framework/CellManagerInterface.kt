package com.dianping.agentsdk.framework

import android.view.ViewGroup

/**
 * Create By zhi.he at 2018/4/28
 */
interface CellManagerInterface<out T : @JvmWildcard ViewGroup> {
    fun setAgentContainerView(containerView: @UnsafeVariance T)

    //手动刷新接口
    fun notifyCellChanged()

    fun updateAgentCell(agent: AgentInterface)

    fun updateCells(
            addList: ArrayList<AgentInterface>?,
            updateList: ArrayList<AgentInterface>?,
            deleteList: ArrayList<AgentInterface>?
    )
}