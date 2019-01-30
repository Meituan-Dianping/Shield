package com.dianping.shield.entity;

import com.dianping.agentsdk.framework.AgentInterface;
import com.dianping.agentsdk.sectionrecycler.layoutmanager.OnSmoothScrollListener;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by zhi.he on 2018/7/31.
 * 只能通过提供的静态方法构造出特定的几种实例
 * 例如:
 * AgentScrollerParams.toPage()
 * AgentScrollerParams.toAgent(xxAgent)
 * 可以通过Builder的方式设置其他参数，
 * AgentScrollerParams.toAgent(agentInterface).setOffset(offset).setNeedAutoOffset(true).addOnSmoothScrollListener(scrollListener)
 */

public class AgentScrollerParams implements Serializable {
    public int offset = 0;
    public boolean isSmooth = true;
    public boolean needAutoOffset = false;
    public boolean needPauseExpose = false;
    public ArrayList<OnSmoothScrollListener> listenerArrayList;

    @NotNull
    private ScrollScope scope;

    @Nullable
    private NodeInfo nodeInfo;

    private AgentScrollerParams(ScrollScope scope, NodeInfo nodeInfo) {
        this.scope = scope;
        this.nodeInfo = nodeInfo;
    }

    public static AgentScrollerParams toPage() {
        return new AgentScrollerParams(ScrollScope.PAGE, null);
    }

    public static AgentScrollerParams toAgent(AgentInterface agent) {
        return new AgentScrollerParams(ScrollScope.AGENT, NodeInfo.agent(agent));
    }

    public static AgentScrollerParams toSection(AgentInterface agent, int section) {
        return new AgentScrollerParams(ScrollScope.SECTION, NodeInfo.section(agent, section));
    }

    public static AgentScrollerParams toRow(AgentInterface agent, int section, int row) {
        return new AgentScrollerParams(ScrollScope.ROW, NodeInfo.row(agent, section, row));
    }

    public static AgentScrollerParams toHeader(AgentInterface agent, int section) {
        return new AgentScrollerParams(ScrollScope.HEADER, NodeInfo.header(agent, section));
    }

    public static AgentScrollerParams toFooter(AgentInterface agent, int section) {
        return new AgentScrollerParams(ScrollScope.FOOTER, NodeInfo.footer(agent, section));
    }

    public AgentScrollerParams setOffset(int offset) {
        this.offset = offset;
        return this;
    }

    public AgentScrollerParams setSmooth(boolean smooth) {
        this.isSmooth = smooth;
        return this;
    }

    public AgentScrollerParams setNeedAutoOffset(boolean needAutoOffset) {
        this.needAutoOffset = needAutoOffset;
        return this;
    }

    public AgentScrollerParams setNeedPauseExpose(boolean needPauseExpose) {
        this.needPauseExpose = needPauseExpose;
        return this;
    }

    public AgentScrollerParams addOnSmoothScrollListener(OnSmoothScrollListener smoothScrollListener) {
        if (listenerArrayList == null) {
            listenerArrayList = new ArrayList<>();
        }
        listenerArrayList.add(smoothScrollListener);
        return this;
    }

    public ScrollScope getScope() {
        return scope;
    }

    @Nullable
    public NodeInfo getNodeInfo() {
        return nodeInfo;
    }
}
