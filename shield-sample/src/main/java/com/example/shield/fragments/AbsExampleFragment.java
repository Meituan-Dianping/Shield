package com.example.shield.fragments;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.dianping.agentsdk.framework.CellManagerInterface;
import com.dianping.shield.manager.ShieldNodeCellManager;

import org.jetbrains.annotations.NotNull;

/**
 * Created by hai on 2017/5/17.
 */
public abstract class AbsExampleFragment extends AgentManagerFragment {
    public abstract String functionName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(functionName());
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @NotNull
    @Override
    public CellManagerInterface<?> initCellManager() {
        return new ShieldNodeCellManager(getContext());
    }
}