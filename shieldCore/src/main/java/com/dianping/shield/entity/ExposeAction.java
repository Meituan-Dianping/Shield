package com.dianping.shield.entity;

import com.dianping.agentsdk.framework.AgentInterface;

/**
 * Created by zhi.he on 2018/12/12.
 * 只能通过提供的静态方法构造出特定的几种实例
 * 例如:
 * ExposeAction.startExpose()
 * ExposeAction.startExpose(1000L)
 * ExposeAction.resetAgentExpose(xxAgent)
 */

public class ExposeAction {
    private ExposeControlActionType actionType;
    private long startDelay = 0L;
    private AgentInterface agent = null;
    private CellInfo cellInfo = null;

    private ExposeAction(ExposeControlActionType actionType, long startDelay, AgentInterface agent, CellInfo cellInfo) {
        this.actionType = actionType;
        this.startDelay = startDelay;
        this.agent = agent;
        this.cellInfo = cellInfo;
    }

    public static ExposeAction startExpose() {
        return new ExposeAction(ExposeControlActionType.ACTION_START_EXPOSE, 0L, null, null);
    }

    public static ExposeAction startExpose(long startDelay) {
        return new ExposeAction(ExposeControlActionType.ACTION_START_EXPOSE, startDelay, null, null);
    }

    public static ExposeAction finishExpose() {
        return new ExposeAction(ExposeControlActionType.ACTION_FINISH_EXPOSE, 0L, null, null);
    }

    public static ExposeAction pauseExpose() {
        return new ExposeAction(ExposeControlActionType.ACTION_PAUSE_EXPOSE, 0L, null, null);
    }

    public static ExposeAction resumeExpose() {
        return new ExposeAction(ExposeControlActionType.ACTION_RESUME_EXPOSE, 0L, null, null);
    }

    public static ExposeAction resetAgentExpose(AgentInterface agent) {
        return new ExposeAction(ExposeControlActionType.ACTION_RESET_AGENT_EXPOSE_HISTORY, 0L, agent, null);
    }

    public static ExposeAction resetRowExpose(AgentInterface agent, int section, int row) {
        return new ExposeAction(ExposeControlActionType.ACTION_RESET_AGENT_EXPOSE_HISTORY, 0L, agent, new CellInfo(section, row, CellType.NORMAL));
    }

    public static ExposeAction resetHeaderExpose(AgentInterface agent, int section) {
        return new ExposeAction(ExposeControlActionType.ACTION_RESET_AGENT_EXPOSE_HISTORY, 0L, agent, new CellInfo(section, -1, CellType.HEADER));
    }

    public static ExposeAction resetFooterExpose(AgentInterface agent, int section) {
        return new ExposeAction(ExposeControlActionType.ACTION_RESET_AGENT_EXPOSE_HISTORY, 0L, agent, new CellInfo(section, -2, CellType.FOOTER));
    }

    public ExposeControlActionType getActionType() {
        return actionType;
    }

    public long getStartDelay() {
        return startDelay;
    }

    public AgentInterface getAgent() {
        return agent;
    }

    public CellInfo getCellInfo() {
        return cellInfo;
    }
}
