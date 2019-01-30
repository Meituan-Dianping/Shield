package com.dianping.shield.manager.feature

import com.dianping.agentsdk.framework.WhiteBoard
import com.dianping.shield.consts.ShieldConst
import com.dianping.shield.node.cellnode.ShieldCellGroup
import com.dianping.shield.node.cellnode.ShieldViewCell
import kotlin.properties.Delegates

/**
 * Created by zhi.he on 2018/8/10.
 */
class AgentVisibiltyCollector(private val whiteBoard: WhiteBoard?, private val loopCellGroupsCollector: LoopCellGroupsCollector) : CellManagerFeatureInterface {
    var oldAgentList = ArrayList<String>()
    override fun onAdapterNotify(cellGroups: ArrayList<ShieldCellGroup?>) {
        var visibleAgentList = ArrayList<String>()
        cellGroups.forEach { cellGroup ->
            cellGroup?.shieldViewCells?.forEach { shieldViewCell ->
                var hostName = shieldViewCell.owner?.hostName ?: "";
                if (whiteBoard != null) {
                    if (shieldViewCell.getViewCellTotalRange() > 0) {
                        visibleAgentList.add(hostName)
                        if (!whiteBoard.getBoolean(ShieldConst.AGENT_VISIBILITY_KEY + hostName)) {
                            whiteBoard.putBoolean(ShieldConst.AGENT_VISIBILITY_KEY + hostName, true)
                        }
                    } else {
                        if (whiteBoard.getBoolean(ShieldConst.AGENT_VISIBILITY_KEY + hostName)) {
                            whiteBoard.putBoolean(ShieldConst.AGENT_VISIBILITY_KEY + hostName, false)
                        }
                    }
                }
            }
        }
        if (visibleAgentList.size!=oldAgentList.size||!visibleAgentList.equals(oldAgentList)) {
            oldAgentList = visibleAgentList
            whiteBoard?.putSerializable(ShieldConst.AGENT_VISIBILITY_LIST_KEY, visibleAgentList)
        }
    }

    override fun onCellNodeRefresh(shieldViewCell: ShieldViewCell) {

    }
}