package com.example.shield.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dianping.agentsdk.framework.AgentCellBridgeInterface;
import com.dianping.agentsdk.framework.AgentInterface;
import com.dianping.agentsdk.framework.AgentListConfig;
import com.dianping.agentsdk.framework.AgentManagerInterface;
import com.dianping.agentsdk.framework.CellManagerInterface;
import com.dianping.agentsdk.framework.DriverInterface;
import com.dianping.agentsdk.framework.PageContainerInterface;
import com.dianping.agentsdk.framework.WhiteBoard;
import com.dianping.agentsdk.manager.SectionRecyclerCellManager;
import com.dianping.shield.feature.ExposeScreenLoadedInterface;
import com.dianping.shield.feature.LoadingAndLoadingMoreCreator;
import com.dianping.shield.framework.ShieldContainerInterface;
import com.example.shield.manager.CommonAgentManager;

import java.util.ArrayList;

/**
 * Created by nihao on 2017/9/4.
 */

public abstract class AgentManagerFragment extends Fragment implements AgentCellBridgeInterface, DriverInterface, ShieldContainerInterface {
    static final String TAG = AgentManagerFragment.class.getSimpleName();

    protected CellManagerInterface cellManager;

    protected AgentManagerInterface agentManager;

    protected WhiteBoard whiteBoard;

    protected PageContainerInterface pageContainer;

    public AgentManagerFragment() {
        this.whiteBoard = new WhiteBoard();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        pageContainer = getPageContainer();
        if (pageContainer != null) {
            View rootView = pageContainer.onCreateView(inflater, container, savedInstanceState);
            return rootView;
        }
        return null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        cellManager = getCellManager();
        if (cellManager instanceof SectionRecyclerCellManager) {
            ((SectionRecyclerCellManager) cellManager).setWhiteBoard(whiteBoard);
        }
        agentManager = getAgentManager();
        agentManager.setupAgents(savedInstanceState, generaterDefaultConfigAgentList());
        if (pageContainer != null) {
            pageContainer.onActivityCreated(savedInstanceState);
            setAgentContainerView(pageContainer.getAgentContainerView());
        }
    }

    public CellManagerInterface getCellManager() {
        if (cellManager == null) {
            cellManager = new SectionRecyclerCellManager(getContext());
            if (getActivity() instanceof LoadingAndLoadingMoreCreator && cellManager instanceof SectionRecyclerCellManager) {
                ((SectionRecyclerCellManager) cellManager).setDefaultLoadingAndLoadingMoreCreator((LoadingAndLoadingMoreCreator) getActivity());
            }
        }
        return cellManager;
    }

    public AgentManagerInterface getAgentManager() {
        if (agentManager == null) {
            agentManager = new CommonAgentManager(this, this, this, pageContainer);
        }
        return agentManager;
    }

    public PageContainerInterface getPageContainer() {
        return null;
    }

    /**
     * 在运行过程中,agentlist有变化的时候调用,更新agentlist列表本身的值,区别于dispatchCellChanged,
     * 单纯只更新现有的agentlist中的agent内容
     *
     * @param savedInstanceState
     */
    @Override
    public void resetAgents(Bundle savedInstanceState) {
        agentManager.resetAgents(savedInstanceState, generaterConfigs());
        if (cellManager instanceof SectionRecyclerCellManager) {
            ((SectionRecyclerCellManager) cellManager).resetHotZone();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        whiteBoard.onCreate(savedInstanceState);
        if (pageContainer != null) {
            pageContainer.onCreate(savedInstanceState);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        agentManager.startAgents();
    }

    @Override
    public void onResume() {
        super.onResume();
        agentManager.resumeAgents();
        if (pageContainer != null) {
            pageContainer.onResume();
        }
    }

    @Override
    public void onPause() {
        // agents
        super.onPause();
        agentManager.pauseAgents();
        if (pageContainer != null) {
            pageContainer.onPause();
        }
    }

    @Override
    public void onStop() {
        // agents
        super.onStop();
        agentManager.stopAgents();
        if (pageContainer != null) {
            pageContainer.onStop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cellManager instanceof ExposeScreenLoadedInterface) {
            ((ExposeScreenLoadedInterface) cellManager).finishExpose();
        }
        if (agentManager != null) {
            agentManager.destroyAgents();
        }
        whiteBoard.onDestory();
        if (pageContainer != null) {
            pageContainer.onDestroy();
        }
        cellManager = null;
        agentManager = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        agentManager.onSaveInstanceState(outState);
        whiteBoard.onSaveInstanceState(outState);
        if (pageContainer != null) {
            pageContainer.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // agents
        agentManager.onActivityResult(requestCode, resultCode, data);
        if (pageContainer != null) {
            pageContainer.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void setAgentContainerView(ViewGroup containerView) {
        if (cellManager != null) {
            cellManager.setAgentContainerView(containerView);
            notifyCellChanged();
        } else {
            throw new NullPointerException("setAgentContainerView method should be called after super.onActivityCreated method");
        }
    }

    protected void updateAgentContainer() {
        cellManager.notifyCellChanged();
    }

    public AgentInterface findAgent(String name) {
        return agentManager.findAgent(name);
    }

    protected void notifyCellChanged() {
        cellManager.notifyCellChanged();
    }

    @Override
    public void updateCells(ArrayList<AgentInterface> addList, ArrayList<AgentInterface> updateList, ArrayList<AgentInterface> deleteList) {
        cellManager.updateCells(addList, updateList, deleteList);
    }

    /**
     * 得到AgentListConfig列表,放在前面的列表会被优先支持
     *
     * @return
     */

    abstract protected ArrayList<AgentListConfig> generaterDefaultConfigAgentList();

    @Override
    public void updateAgentCell(AgentInterface agent) {
        if (cellManager != null) {
            cellManager.updateAgentCell(agent);
        }
    }

    @Override
    public WhiteBoard getWhiteBoard() {
        return whiteBoard;
    }

    @Override
    public CellManagerInterface getHostCellManager() {
        return cellManager;
    }

    @Override
    public AgentManagerInterface getHostAgentManager() {
        return agentManager;
    }

    @Override
    public ArrayList<AgentListConfig> generaterConfigs() {
        return generaterDefaultConfigAgentList();
    }
}
