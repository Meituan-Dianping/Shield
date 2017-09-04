package com.example.shield.headerfootercell;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dianping.agentsdk.framework.AgentListConfig;
import com.example.shield.fragments.AbsExampleFragment;

import java.util.ArrayList;

/**
 * Created by nihao on 2017/7/13.
 */
public class HeaderFooterCellFragment extends AbsExampleFragment {

    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        recyclerView = new RecyclerView(getContext());
        LinearLayout rootView = new LinearLayout(getContext());
        rootView.addView(recyclerView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setAgentContainerView(recyclerView);
    }

    @Override
    public String functionName() {
        return "headerfootercellfragment";
    }

    @Override
    protected ArrayList<AgentListConfig> generaterDefaultConfigAgentList() {
        ArrayList<AgentListConfig> configs = new ArrayList<>();
        configs.add(new HeaderFooterCellAgentConfig());
        return configs;
    }
}
