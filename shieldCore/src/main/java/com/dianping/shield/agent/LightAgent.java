package com.dianping.shield.agent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.View;

import com.dianping.agentsdk.framework.AgentInterface;
import com.dianping.agentsdk.framework.AgentListConfig;
import com.dianping.agentsdk.framework.AgentManagerInterface;
import com.dianping.agentsdk.framework.CellManagerInterface;
import com.dianping.agentsdk.framework.DriverInterface;
import com.dianping.agentsdk.framework.PageContainerInterface;
import com.dianping.agentsdk.framework.SectionCellInterface;
import com.dianping.agentsdk.framework.UIRDriverInterface;
import com.dianping.agentsdk.framework.UpdateAgentType;
import com.dianping.agentsdk.framework.WhiteBoard;
import com.dianping.shield.bridge.feature.ShieldGlobalFeatureInterface;
import com.dianping.shield.framework.ShieldContainerInterface;
import com.dianping.shield.node.useritem.ShieldSectionCellItem;

import java.util.ArrayList;
import java.util.HashMap;

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
    protected ArrayList<String> messageRegistrationList;
    protected HashMap<String, Object> arguments;

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
        if (getWhiteBoard() != null && messageRegistrationList != null && !messageRegistrationList.isEmpty()) {
            for (String id : messageRegistrationList) {
                getWhiteBoard().removeMessageHandler(id);
            }
        }
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

    public void updateAgentCell(UpdateAgentType updateAgentType, int sectionPosition, int rowPosition, int count) {
        if (bridge instanceof UIRDriverInterface) {
            ((UIRDriverInterface) bridge).updateAgentCell(this, updateAgentType, sectionPosition, rowPosition, count);
        } else {
            updateAgentCell();
        }
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

    public void registerMessageHandler(@NonNull String key, @NonNull WhiteBoard.MessageHandler messageHandler) {
        if (getWhiteBoard() == null) {
            return;
        }

        if (messageRegistrationList == null) {
            messageRegistrationList = new ArrayList<>();
        }

        messageRegistrationList.add(getWhiteBoard().registerMessageHandlerWithId(key, messageHandler));
    }

    public String registerMessageHandlerWithId(@NonNull String key, @NonNull WhiteBoard.MessageHandler messageHandler) {
        if (getWhiteBoard() == null) {
            return null;
        }

        if (messageRegistrationList == null) {
            messageRegistrationList = new ArrayList<>();
        }

        String id = getWhiteBoard().registerMessageHandlerWithId(key, messageHandler);
        messageRegistrationList.add(id);
        return id;
    }

    public void removeMessageHandler(@NonNull String key, @NonNull WhiteBoard.MessageHandler handler) {
        if (getWhiteBoard() == null) {
            return;
        }

        getWhiteBoard().removeMessageHandler(key, handler);
    }

    public void removeMessageHandler(@NonNull WhiteBoard.MessageHandler handler) {
        if (getWhiteBoard() == null) {
            return;
        }

        getWhiteBoard().removeMessageHandler(handler);
    }

    public void removeMessageHandler(String id) {
        if (getWhiteBoard() == null) {
            return;
        }

        getWhiteBoard().removeMessageHandler(id);
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

    @Override
    public HashMap<String, Object> getArguments() {
        return arguments;
    }

    @Override
    public void setArguments(HashMap<String, Object> arguments) {
        this.arguments = arguments;
    }

    @Override
    public ShieldSectionCellItem getSectionCellItem() {
        return null;
    }

    /**
     * ShieldNodeCellManager will add rootView to a DisplayNodeContainer for each row.
     * It makes rootView.getTop() method return 0 as a wrong value.
     * try to find the DisplayNodeContainer of the view and return DisplayNodeContainer's getTop value to fix this bug
     *
     * @param rootView the rootView of row
     */
    public Rect getViewParentRect(View rootView) {
        return getFeature().getViewParentRect(rootView);
    }

    public ShieldGlobalFeatureInterface getFeature() {
        if (bridge instanceof ShieldGlobalFeatureInterface) {
            return (ShieldGlobalFeatureInterface) bridge;
        } else return null;
    }

}
