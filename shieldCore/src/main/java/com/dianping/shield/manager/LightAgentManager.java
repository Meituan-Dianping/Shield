package com.dianping.shield.manager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.dianping.agentsdk.framework.AgentCellBridgeInterface;
import com.dianping.agentsdk.framework.AgentInfo;
import com.dianping.agentsdk.framework.AgentInterface;
import com.dianping.agentsdk.framework.AgentListConfig;
import com.dianping.agentsdk.framework.AgentManagerInterface;
import com.dianping.agentsdk.framework.DriverInterface;
import com.dianping.agentsdk.framework.PageContainerInterface;
import com.dianping.shield.bridge.ShieldLogger;
import com.dianping.shield.env.ShieldEnvironment;
import com.dianping.shield.framework.FullOptionMenuLifecycle;
import com.dianping.shield.framework.OptionMenuLifecycle;
import com.dianping.shield.framework.ShieldContainerInterface;
import com.dianping.shield.framework.ShieldLifeCycler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by hezhi on 16/3/3.
 */
public class LightAgentManager implements AgentManagerInterface {
    public final static String AGENT_SEPARATE = "@";
    protected final ArrayList<String> agentList = new ArrayList<String>(); // 排好序的模块key
    protected final HashMap<String, AgentInterface> agents = new HashMap<String, AgentInterface>();//方便快速查找模块
    protected Fragment fragment;
    protected AgentCellBridgeInterface agentCellBridgeInterface;
    protected DriverInterface driverInterface;
    protected PageContainerInterface pageContainer;

    public LightAgentManager(ShieldLifeCycler shieldLifeCycler) {
        this.fragment = shieldLifeCycler.hostFragment;
        this.agentCellBridgeInterface = shieldLifeCycler;
        this.driverInterface = shieldLifeCycler;
        this.pageContainer = shieldLifeCycler.getPageContainer();
    }

    public LightAgentManager(Fragment fragment, AgentCellBridgeInterface agentCellBridgeInterface,
                             DriverInterface driverInterface, PageContainerInterface pageContainerInterface) {
        this.fragment = fragment;
        this.agentCellBridgeInterface = agentCellBridgeInterface;
        this.driverInterface = driverInterface;
        this.pageContainer = pageContainerInterface;
    }

    @Override
    public void setupAgents(Bundle savedInstanceState, ArrayList<AgentListConfig> defaultConfig) {
        ShieldEnvironment.INSTANCE.getShieldLogger().setLevel(ShieldLogger.getVERBOSE());
        setupAgents(defaultConfig);
        ArrayList<AgentInterface> addList = new ArrayList<AgentInterface>();
        for (String name : agentList) {
            AgentInterface agent = agents.get(name);
            if (agent != null) {
                Bundle b = savedInstanceState == null ? null : savedInstanceState.getBundle("agent/"
                        + name);
                agent.onCreate(b);
                addList.add(agent);
            }
        }
        if (ShieldEnvironment.INSTANCE.isDebug()) {
            ShieldEnvironment.INSTANCE.getShieldLogger().d("@onCreateAgents@Agents:" + agents.toString());
        }
    }

    private void setupAgents(ArrayList<AgentListConfig> defaultConfig) {
        agentList.clear();
//        agents.clear();

        setDefaultAgent(defaultConfig);
    }

    //初始化agents
    protected void setDefaultAgent(ArrayList<AgentListConfig> defaultConfig) {
        setDefaultAgent(defaultConfig, "", "");
    }

    protected void setDefaultAgent(ArrayList<AgentListConfig> defaultConfig, String keyPath, String index) {
        Map<String, AgentInfo> defaultAgent = getDefaultAgentList(defaultConfig);
        if (defaultAgent == null) {
            ShieldEnvironment.INSTANCE.getShieldLogger().e(
                    "@ReadShieldConfigError!!@FragmentName:" + fragment.toString() + "&Failed to find a useful config");
            return;
        }

        for (Map.Entry<String, AgentInfo> entry : defaultAgent.entrySet()) {
            try {
                String key;
                if (!"".equals(keyPath)) {
                    key = keyPath + AGENT_SEPARATE + entry.getKey();
                } else {
                    key = entry.getKey();
                }
                if (!agents.containsKey(key)) {
                    agentList.add(key);
                    AgentInterface cellAgent = constructAgents(entry.getValue());
                    if (cellAgent != null && entry.getValue() != null && entry.getValue().arguments != null) {
                        cellAgent.setArguments(entry.getValue().arguments);
                    }
                    updateIndexInfo(cellAgent, keyPath, index, key, entry);

                } else {
                    agentList.add(key);
                    AgentInterface cellAgent = agents.get(key);
                    updateIndexInfo(cellAgent, keyPath, index, key, entry);

                }
            } catch (Exception e) {
                ShieldEnvironment.INSTANCE.getShieldLogger().e("@ReadShieldConfigError!!@FragmentName:"
                        + fragment.toString() + "&" + e.toString());
                Log.e("Shield", e.toString());
                continue;
            }
        }

    }

    private void updateIndexInfo(AgentInterface cellAgent, String keyPath, String index, String key, Map.Entry<String, AgentInfo> entry) {
        if (cellAgent != null) {
            String childIndex;
            if (!"".equals(keyPath)) {
                childIndex = index + "." + entry.getValue().index;
            } else {
                childIndex = entry.getValue().index;
            }

            cellAgent.setIndex(childIndex);
            cellAgent.setHostName(key);
            agents.put(key, cellAgent);
            if (cellAgent instanceof ShieldContainerInterface && ((ShieldContainerInterface) cellAgent).generaterConfigs() != null) {
                setDefaultAgent(((ShieldContainerInterface) cellAgent).generaterConfigs(), key, childIndex);
            }
        }
    }

    //筛出可用配置
    protected Map<String, AgentInfo> getDefaultAgentList(ArrayList<AgentListConfig> defaultConfig) {

        if (defaultConfig == null) {
            return null;
        }

        for (AgentListConfig agentListConfig : defaultConfig) {
            try {
                if (agentListConfig.shouldShow()) {
                    Map<String, AgentInfo> result = agentListConfig.getAgentInfoList();
                    if (result == null) {
                        result = new LinkedHashMap<String, AgentInfo>();
                        Map<String, Class<? extends AgentInterface>> tmp = agentListConfig.getAgentList();
                        for (Map.Entry<String, Class<? extends AgentInterface>> entry : tmp.entrySet()) {
                            result.put(entry.getKey(), new AgentInfo(entry.getValue(), ""));
                        }
                    }
                    return result;
                }
            } catch (Exception e) {
                ShieldEnvironment.INSTANCE.getShieldLogger().e("@ReadShieldConfigException!!@FragmentName:"
                        + fragment.toString() + "&Exception:" + e.toString());
                return null;
            }
        }

//        throw new RuntimeException("getDefaultAgentList() agentListConfig no one should be shown?");
        ShieldEnvironment.INSTANCE.getShieldLogger().e("@ReadShieldConfigError!!@FragmentName:"
                + fragment.toString() + "& no one config should be shown?");
        return null;
    }

    //反射构造Agent
    protected AgentInterface constructAgents(AgentInfo agentInfo) {
        if (agentInfo == null) {
            ShieldEnvironment.INSTANCE.getShieldLogger().e("Failed to construct a null agent@FragmentName:"
                    + fragment.toString());
            return null;
        }
        Class<? extends AgentInterface> agentClass = agentInfo.agentClass;
        AgentInterface cellAgent = null;
        try {
            cellAgent = agentClass.getConstructor(Object.class).newInstance(
                    fragment);
        } catch (Exception e) {
            ShieldEnvironment.INSTANCE.getShieldLogger().v("@ConstructAgents@Agent:"
                    + agentInfo.toString() + "@(Object.class) constructor not found:" + e.toString());
//              e.printStackTrace();
        }
        if (cellAgent == null) {
            try {
                Class<?>[] params = {Fragment.class,
                        DriverInterface.class, PageContainerInterface.class};
                Object[] values = {fragment, driverInterface, pageContainer};
                cellAgent = agentClass.getConstructor(params).newInstance(
                        values);
            } catch (Exception e) {
                ShieldEnvironment.INSTANCE.getShieldLogger().v("@ConstructAgents@Agent:"
                        + agentInfo.toString() + "@{fragment, driverInterface, pageContainer} constructor not found:" + e.toString());
            }
        }

        if (cellAgent == null) {
            try {
                cellAgent = agentClass.newInstance();
            } catch (Exception e) {
                ShieldEnvironment.INSTANCE.getShieldLogger().e("@ConstructAgentsException@Agent:"
                        + agentInfo.toString() + "Exception:" + e.toString());
            }
        }

        if (cellAgent == null) {
            ShieldEnvironment.INSTANCE.getShieldLogger().e("@ConstructAgentsException@" + agentClass.getCanonicalName() + "@:Failed to construct Agent");
        }
        return cellAgent;
    }


    @Override
    public void startAgents() {
        for (String name : agentList) {
            AgentInterface agent = agents.get(name);
            if (agent != null) {
                agent.onStart();
            }
        }
    }

    @Override
    public void resumeAgents() {
        for (String name : agentList) {
            AgentInterface agent = agents.get(name);
            if (agent != null) {
                agent.onResume();
            }
        }
    }

    @Override
    public void pauseAgents() {
        for (String name : agentList) {
            AgentInterface agent = agents.get(name);
            if (agent != null) {
                agent.onPause();
            }
        }
    }

    @Override
    public void stopAgents() {
        for (String name : agentList) {
            AgentInterface agent = agents.get(name);
            if (agent != null) {
                agent.onStop();
            }
        }
    }

    @Override
    public void destroyAgents() {
        for (String name : agentList) {
            AgentInterface agent = agents.get(name);
            if (agent != null) {
                agent.onDestroy();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        for (String name : agentList) {
            AgentInterface agent = agents.get(name);
            if (agent != null) {
                Bundle b = agent.saveInstanceState();
                if (b != null) {
                    outState.putBundle("agent/" + name, b);
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        for (String name : agentList) {
            AgentInterface agent = agents.get(name);
            if (agent != null) {
                agent.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void resetAgents(Bundle savedInstanceState, ArrayList<AgentListConfig> defaultConfig) {
        // 三种可能
        // 1.以前没有的agent,新增的,要重新onCreat,onResume
        // 2.以前有的agent,现在还有,那什么都不做,还原这个agent,等待之后统一dispatchCellChanged
        // 3.以前有的agent,后来没有了,那要调用stop,destory
        // resetAgent的时候,对agent列表做一个整理
        ShieldEnvironment.INSTANCE.getShieldLogger().v("@ResetStart@");
        ArrayList<String> copyOfAgentList = (ArrayList<String>) agentList.clone();
        HashMap<String, AgentInterface> copyOfAgents = (HashMap<String, AgentInterface>) agents.clone();

        // 因为现在需求同一个Cell根据AgentList的不同,放在不同的位置,所以也要更新Cell的index

        ArrayList<AgentInterface> addList = new ArrayList<AgentInterface>();
        ArrayList<AgentInterface> updateList = new ArrayList<AgentInterface>();
        ArrayList<AgentInterface> deleteList = new ArrayList<AgentInterface>();

        setupAgents(defaultConfig);

        Bundle b;

        for (String name : agentList) {

            // 情况1,后续新增的
            if (!copyOfAgentList.contains(name)) {
                AgentInterface agent = agents.get(name);
                b = savedInstanceState == null ? null : savedInstanceState.getBundle("agent/"
                        + name);
                if (agent != null) {
                    agent.onCreate(b);
                    agent.onStart();
                    agent.onResume();
                    addList.add(agent);
                }
            } else {
                // 情况2,为了筛选出情况3
                copyOfAgentList.remove(name);
                // 得到复用的agent
                AgentInterface cellAgent = copyOfAgents.get(name);
                if (cellAgent != null && agents.get(name) != null) {
                    // 更新复用agent的index
                    cellAgent.setIndex(agents.get(name).getIndex());
                    // 更新对应的cell
                    updateList.add(cellAgent);
                    // 把原来已经生成好的agent替换空的agent,避免两次onCreate
                    agents.put(name, cellAgent);
                    cellAgent.setHostName(name);
                }
            }
        }

        // 情况3,之前有的,现在又没有了
        for (String name : copyOfAgentList) {
            AgentInterface agent = copyOfAgents.get(name);
            if (agent != null) {
                if (fragment.isResumed()) {
                    agent.onPause();
                }
                agent.onStop();
                agent.onDestroy();
                agents.remove(name);
                //删除不再存在的Agent对应的Cell
                deleteList.add(agent);
            }
        }

        copyOfAgentList.clear();
        copyOfAgents.clear();

        if (ShieldEnvironment.INSTANCE.isDebug()) {
            ShieldEnvironment.INSTANCE.getShieldLogger().d("@ResetAgents@Agents:" + agents.toString());
            ShieldEnvironment.INSTANCE.getShieldLogger().v("@ResetNotifyCell@");
        }
        agentCellBridgeInterface.updateCells(addList, updateList, deleteList);

        // only for old version
        dispatchCellChanged(fragment.getActivity(), null, null);

    }

    @Override
    public void initViewCell() {
        ArrayList<AgentInterface> addList = new ArrayList<AgentInterface>();
        for (String name : agentList) {
            AgentInterface agent = agents.get(name);
            if (agent != null) {
                addList.add(agent);
            }
        }
        agentCellBridgeInterface.updateCells(addList, null, null);
        dispatchCellChanged(fragment.getActivity(), null, null);
        if (ShieldEnvironment.INSTANCE.isDebug()) {
            ShieldEnvironment.INSTANCE.getShieldLogger().v("@InitNotifyCell@");
        }
    }

    //已经不推荐通过name来寻找其他模块
    @Override
    public AgentInterface findAgent(String name) {
        AgentInterface result = agents.get(name);
        //兼容agent在tab嵌套中的情形，考虑在tab中，name会添加前缀的情形
        if (result == null) {
            for (Map.Entry<String, AgentInterface> entry : agents.entrySet()) {
                if (entry.getKey().endsWith(AGENT_SEPARATE + name)) {
                    return entry.getValue();
                }
            }
        }
        return result;
    }

    //onAgentChanged callback is Deprecated,so don't call dispatchCellChanged.
    @Deprecated
    public void dispatchCellChanged(FragmentActivity activity, AgentInterface caller, Bundle data) {
        if (activity == null) {
            return;
        }
        for (String a : agentList) {
            AgentInterface ca = agents.get(a);

            if (caller != null && caller != ca)
                continue;

            if (ca != null) {
                try {
                    ca.onAgentChanged(data);
                } finally {
                }
            }
        }
    }


    //add option menu lifecycle (optional)
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        for (String name : agentList) {
            AgentInterface agent = agents.get(name);
            if (agent != null && agent instanceof OptionMenuLifecycle) {
                ((OptionMenuLifecycle) agent).onCreateOptionsMenu(menu, inflater);
            }
        }
    }

    public void onPrepareOptionsMenu(Menu menu) {
        for (String name : agentList) {
            AgentInterface agent = agents.get(name);
            if (agent != null && agent instanceof FullOptionMenuLifecycle) {
                ((FullOptionMenuLifecycle) agent).onPrepareOptionsMenu(menu);
            }
        }
    }

    public void onDestroyOptionsMenu() {
        for (String name : agentList) {
            AgentInterface agent = agents.get(name);
            if (agent != null && agent instanceof FullOptionMenuLifecycle) {
                ((FullOptionMenuLifecycle) agent).onDestroyOptionsMenu();
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        for (String name : agentList) {
            AgentInterface agent = agents.get(name);
            if (agent != null && agent instanceof OptionMenuLifecycle) {
                boolean needBreak = ((OptionMenuLifecycle) agent).onOptionsItemSelected(item);
                if (needBreak) return true;
            }
        }

        return false;
    }

    public void onOptionsMenuClosed(Menu menu) {
        for (String name : agentList) {
            AgentInterface agent = agents.get(name);
            if (agent != null && agent instanceof FullOptionMenuLifecycle) {
                ((FullOptionMenuLifecycle) agent).onOptionsMenuClosed(menu);
            }
        }
    }
}
