package com.dianping.shield.entity;

import com.dianping.agentsdk.framework.AgentInterface;

/**
 * Created by zhi.he on 2018/12/13.
 * 只能通过提供的静态方法构造出特定的几种实例
 * 例如:
 * NodeInfo.agent(xxAgent)
 * NodeInfo.section(xxAgent,0)
 * NodeInfo.row(xxAgent,0,0)
 * NodeInfo.header(xxAgent,0)
 * NodeInfo.footer(xxAgent,0)
 */

public class NodeInfo {

    public static final int ROW_HEADR = -1;
    public static final int ROW_FOOTER = -2;

    private AgentInterface agent;
    private Scope scope;
    private CellInfo cellInfo;

    private NodeInfo(AgentInterface agent, Scope scope, CellInfo cellInfo) {
        this.agent = agent;
        this.scope = scope;
        this.cellInfo = cellInfo;
    }

    public static NodeInfo agent(AgentInterface agent) {
        return new NodeInfo(agent, Scope.AGENT, new CellInfo(0, 0, CellType.NORMAL));
    }

    public static NodeInfo section(AgentInterface agent, int section) {
        return new NodeInfo(agent, Scope.SECTION, new CellInfo(section, 0, CellType.NORMAL));
    }

    public static NodeInfo row(AgentInterface agent, int section, int row) {
        return new NodeInfo(agent, Scope.ROW, new CellInfo(section, row, CellType.NORMAL));
    }

    public static NodeInfo header(AgentInterface agent, int section) {
        return new NodeInfo(agent, Scope.HEADER, new CellInfo(section, ROW_HEADR, CellType.HEADER));
    }

    public static NodeInfo footer(AgentInterface agent, int section) {
        return new NodeInfo(agent, Scope.FOOTER, new CellInfo(section, ROW_FOOTER, CellType.FOOTER));
    }

    public AgentInterface getAgent() {
        return agent;
    }

    public int getSection() {
        return cellInfo.section;
    }

    public int getRow() {
        return cellInfo.row;
    }

    public Scope getScope() {
        return scope;
    }

    public CellInfo getCellInfo() {
        return cellInfo;
    }

    public enum Scope {
        //模块
        AGENT,
        //Section
        SECTION,
        //普通Cell
        ROW,
        //HeaderCell
        HEADER,
        //FooterCell
        FOOTER
    }

}
