package com.dianping.agentsdk.utils;


import android.util.Pair;

import com.dianping.agentsdk.framework.AgentInfo;
import com.dianping.agentsdk.framework.AgentInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by hezhi on 16/6/14.
 */
public class AgentInfoHelper {
    private HashMap<String, AgentInfo> DEFAULTAGENTS = new HashMap<String, AgentInfo>();
    private HashMap<String, AgentClassBean> classMap = new HashMap<String, AgentClassBean>();

    /**
     * 从agent多维数组中生成config map
     *
     * @return config map.
     */
    public static Map<String, AgentInfo> getAgents(String[][][] configArray) {
        if (configArray == null) return null;

        HashMap<String, AgentInfo> agents = new LinkedHashMap<String, AgentInfo>();
        for (int i = 0; i < configArray.length; i++) {
            if (configArray[i] == null) continue;

            for (int j = 0; j < configArray[i].length; j++) {
                try {
                    Class agentClass = Class.forName(configArray[i][j][1]);
                    agents.put(configArray[i][j][0], createAgentInfo(agentClass, i, j, configArray.length, configArray[i].length));
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }
        }
        return agents;
    }

    /**
     * 组合多个config List
     *
     * @param configList 多个config map
     * @return 组合后的config map.
     */
    public static Map<String, AgentInfo> combineConfigs(ArrayList<String[][][]> configList) {
        Map<String, AgentInfo> combineMap = new HashMap<>();
        String groupPrefix = "";
        int grouplength = 0;
        ArrayList<Pair<String, AgentInfo>> subList = new ArrayList<>();
        //双层遍历合并成多维数组
        for (int i = 0; i < configList.size(); i++) {
            if (configList.get(i) == null) continue;
            //遍历获得总长度
            grouplength += configList.get(i).length;
        }
        int groupIndex = 0;
        for (int i = 0; i < configList.size(); i++) {
            if (configList.get(i) == null) continue;
            for (int j = 0; j < configList.get(i).length; j++) {
                if (configList.get(i)[j] == null) continue;
                for (int k = 0; k < configList.get(i)[j].length; k++) {
                    try {
                        combineMap.put(configList.get(i)[j][k][0],
                                createAgentInfo(Class.forName(configList.get(i)[j][k][1]),
                                        j + groupIndex, k, grouplength, configList.get(i)[j].length));
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                }
            }
            groupIndex += configList.get(i).length;

        }

        return combineMap;
    }

    public static AgentInfo createAgentInfo(Class agentClass, int groupindex, int index, int groupLength, int length) {
        if (AgentInterface.class.isAssignableFrom(agentClass)) {
            AgentInfo agentInfo = new AgentInfo((Class<? extends AgentInterface>) agentClass,
                    addZeroPrefix(groupindex, getIntStrLength(groupLength))
                            + "." + addZeroPrefix(index, getIntStrLength(length)));
            return agentInfo;
        } else {
            AgentInfo agentInfo = new AgentInfo(null,
                    addZeroPrefix(groupindex, getIntStrLength(groupLength))
                            + "." + addZeroPrefix(index, getIntStrLength(length)));
            agentInfo.extraClass = agentClass;
            return agentInfo;
        }
    }

    public static String addZeroPrefix(int num, int zerolength) {
        int j = getIntStrLength(num);
        String zeroStr = "";
        for (int k = 0; k < zerolength - j; k++) {
            zeroStr = zeroStr + "0";
        }
        return zeroStr + num;
    }

    public static int getIntStrLength(int num) {
        if (num == 0) return 1;
        return ((int) Math.log10(num) + 1);
    }

    public Map<String, AgentInfo> getAgents() {
        Iterator iter = classMap.entrySet().iterator();
        while (iter.hasNext()) {
            try {
                Map.Entry entry = (Map.Entry) iter.next();
                String key = (String) entry.getKey();
                AgentClassBean val = (AgentClassBean) entry.getValue();
                DEFAULTAGENTS.put(key, new AgentInfo((Class<? extends AgentInterface>) Class.forName(val.name), val.index));
            } catch (Exception e) {
                continue;
            }
        }
        return DEFAULTAGENTS;
    }

    public void addToAgents(String key, String className, String index) {
        classMap.put(key, new AgentClassBean(className, index));
    }

    public class AgentClassBean {
        public String name;
        public String index;

        public AgentClassBean(String name, String index) {
            this.name = name;
            this.index = index;
        }
    }

}