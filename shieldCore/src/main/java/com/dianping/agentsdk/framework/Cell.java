package com.dianping.agentsdk.framework;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ListAdapter;

public class Cell {
    public AgentInterface owner;
    public String name;
    public View view;
    public ListAdapter adpater;
    public RecyclerView.Adapter recyclerViewAdapter;
    public String groupIndex;
    public String innerIndex;
}
