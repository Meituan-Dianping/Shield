package com.dianping.agentsdk.framework;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

/**
 * Created by hezhi on 16/3/3.
 */
public interface AgentManagerInterface {

    void setupAgents(Bundle savedInstanceState, ArrayList<AgentListConfig> defaultConfig);

    void startAgents();

    void resumeAgents();

    void pauseAgents();

    void stopAgents();

    void destroyAgents();

    void onSaveInstanceState(Bundle outState);

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void resetAgents(Bundle savedInstanceState, ArrayList<AgentListConfig> defaultConfig);

    AgentInterface findAgent(String name);

}
