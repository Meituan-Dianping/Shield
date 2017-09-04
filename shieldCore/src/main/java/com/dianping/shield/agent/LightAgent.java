package com.dianping.shield.agent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.dianping.agentsdk.framework.AgentInterface;
import com.dianping.agentsdk.framework.AgentListConfig;
import com.dianping.agentsdk.framework.AgentManagerInterface;
import com.dianping.agentsdk.framework.CellManagerInterface;
import com.dianping.agentsdk.framework.DriverInterface;
import com.dianping.agentsdk.framework.PageContainerInterface;
import com.dianping.agentsdk.framework.SectionCellInterface;
import com.dianping.agentsdk.framework.WhiteBoard;
import com.dianping.shield.framework.ShieldContainerInterface;

import java.util.ArrayList;

/**
 * Created by hezhi on 16/12/9.
 * Agent基类
 */
public abstract class LightAgent implements AgentInterface, ShieldContainerInterface {
    public String index = "";
    public String hostName = "";
    protected Fragment fragment;
    protected DriverInterface bridge;
    protected PageContainerInterface pageContainer;

    public LightAgent(Fragment fragment, DriverInterface bridge, PageContainerInterface pageContainer) {
        this.fragment = fragment;
        this.bridge = bridge;
        this.pageContainer = pageContainer;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public Bundle saveInstanceState() {
        return new Bundle();
    }

    @Deprecated
    @Override
    public void onAgentChanged(Bundle data) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public String getIndex() {
        return index;
    }

    @Override
    public void setIndex(String index) {
        this.index = index;
    }

    @Override
    public String getHostName() {
        return hostName;
    }

    @Override
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    @Override
    public SectionCellInterface getSectionCellInterface() {
        return null;
    }

    @Override
    public String getAgentCellName() {
        return hashCode() + "-" + this.getClass().getCanonicalName();
    }

    public void updateAgentCell() {
        bridge.updateAgentCell(this);
    }

    public WhiteBoard getWhiteBoard() {
        return bridge.getWhiteBoard();
    }

    public Fragment getHostFragment() {
        return fragment;
    }

    public void startActivity(Intent intent) {
        fragment.startActivity(intent);
    }

    public void startActivityForResult(Intent intent, int requestCode) {
        fragment.startActivityForResult(intent, requestCode);
    }

    public Context getContext() {
        return fragment.getContext();
    }

    //模块嵌套开始
    @Override
    public ArrayList<AgentListConfig> generaterConfigs() {
        return null;
    }

    @Override
    public void resetAgents(Bundle savedInstanceState) {
        if (fragment instanceof ShieldContainerInterface) {
            ((ShieldContainerInterface) fragment).resetAgents(savedInstanceState);
        }
    }

    @Override
    public CellManagerInterface getHostCellManager() {
        if (fragment instanceof ShieldContainerInterface) {
            return ((ShieldContainerInterface) fragment).getHostCellManager();
        }
        return null;
    }

    @Override
    public AgentManagerInterface getHostAgentManager() {
        if (fragment instanceof ShieldContainerInterface) {
            return ((ShieldContainerInterface) fragment).getHostAgentManager();
        }
        return null;
    }
}
