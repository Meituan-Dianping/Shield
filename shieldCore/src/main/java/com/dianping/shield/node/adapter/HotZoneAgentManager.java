package com.dianping.shield.node.adapter;

import com.dianping.agentsdk.framework.AgentInterface;
import com.dianping.shield.entity.HotZoneYRange;
import com.dianping.shield.entity.ScrollDirection;
import com.dianping.shield.feature.HotZoneStatusInterface;
import com.dianping.shield.manager.LightAgentManager;
import com.dianping.shield.node.adapter.hotzone.AgentHotZoneInfo;
import com.dianping.shield.node.adapter.hotzone.HotZoneInfo;
import com.dianping.shield.node.adapter.hotzone.HotZoneLocation;
import com.dianping.shield.node.cellnode.ShieldDisplayNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by bingwei.zhou at 2018/11/16
 */
public class HotZoneAgentManager extends HotZoneManager {

    private ArrayList<AgentHotZoneInfo> agentLocationList = new ArrayList<>();
    private ArrayList<AgentHotZoneInfo> observerAgentsLocList = new ArrayList<>();
    private HotZoneStatusInterface hotZoneStatusInterface;
    private Set<String> observerAgents;

    public HotZoneAgentManager(int bottom, int top) {
        super(bottom, top);
    }

    @Override
    public HotZoneYRange getHotZoneYRange() {
        if(hotZoneStatusInterface!=null) {
            return hotZoneStatusInterface.defineStatusHotZone();
        }else {
            return null;
        }
    }


    void setHotZoneStatusInterface(HotZoneStatusInterface hotZoneStatusInterface, String prefix) {
        this.hotZoneStatusInterface = hotZoneStatusInterface;
        if (prefix != null) {
            Set<String> stringSet = new HashSet();
            for (String agentName : hotZoneStatusInterface.observerAgents()) {
                stringSet.add(prefix + LightAgentManager.AGENT_SEPARATE + agentName);
            }
            observerAgents = stringSet;
        } else {
            observerAgents = hotZoneStatusInterface.observerAgents();
        }
    }

    @Override
    public void updateHotZoneLocation(ArrayList<HotZoneInfo> hotZoneInfoList, ScrollDirection scrollDirection) {
        if (hotZoneInfoList.size() == 0 || hotZoneStatusInterface == null) {
            return;
        }
        agentLocationList.clear();
        observerAgentsLocList.clear();
        for (int i = 0; i < hotZoneInfoList.size(); i++) {
            HotZoneInfo hotZoneInfo = hotZoneInfoList.get(i);
            HotZoneLocation hotZoneLocation = hotZoneInfo.hotZoneLocation;
            ShieldDisplayNode shieldDisplayNode = hotZoneInfo.shieldDisplayNode;
            if (shieldDisplayNode == null) {
                break;
            }
            AgentInterface agentInterface = shieldDisplayNode.rowParent.sectionParent.cellParent.owner;
            AgentHotZoneInfo agentHotZoneInfo = new AgentHotZoneInfo(agentInterface.getHostName(), hotZoneLocation);
            for(String observerAgent: observerAgents){
                //为了兼容tab下配置的模块是多重嵌套结构，使用startwith是为了将拿到的叶子节点与observerAgent中的父容器的agent名进行匹配（叶子节点名称格式：父容器名@叶子节点名）
                if(agentInterface.getHostName().startsWith(observerAgent)){
                    observerAgentsLocList.add(agentHotZoneInfo);
                }
            }
            agentLocationList.add(agentHotZoneInfo);
        }

        if (observerAgents.size() > 0) {
            hotZoneStatusInterface.onHotZoneLocationChanged(observerAgentsLocList, scrollDirection);
        } else {
            hotZoneStatusInterface.onHotZoneLocationChanged(agentLocationList, scrollDirection);
        }

    }
}
