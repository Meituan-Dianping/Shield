package com.dianping.agentsdk.framework;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

/**
 * Created by hezhi on 16/3/3.
 */
public interface AgentManagerInterface {
    /**
     *  配置模块并分发onCreate 生命周期
     * */
    void setupAgents(Bundle savedInstanceState, ArrayList<AgentListConfig> defaultConfig);

    /**
     *  分发onStart生命周期
     * */
    void startAgents();

    /**
     *  分发onResume生命周期
     * */
    void resumeAgents();

    /**
     *  分发onPause生命周期
     * */
    void pauseAgents();

    /**
     *  分发onStop生命周期
     * */
    void stopAgents();

    /**
     *  分发onDestory生命周期
     * */
    void destroyAgents();

    /**
     *  分发onSaveInstanceState生命周期
     * */
    void onSaveInstanceState(Bundle outState);

    /**
     *  分发onActivityResult生命周期
     * */
    void onActivityResult(int requestCode, int resultCode, Intent data);

    /**
     *  重新加载模块配置，并更新模块
     * */
    void resetAgents(Bundle savedInstanceState, ArrayList<AgentListConfig> defaultConfig);

    /**
     *  初始化视图虚拟节点
     * */
    void initViewCell();

    /**
     *  通过配置名获取一个模块
     * */
    AgentInterface findAgent(String name);

}
