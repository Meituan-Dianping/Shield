package com.dianping.shield.node.adapter;

import android.util.SparseArray;

import com.dianping.shield.node.cellnode.ShieldDisplayNode;

/**
 * Created by runqi.wei at 2018/7/16
 */
public interface NodeList {

    int size();

    ShieldDisplayNode getShieldDisplayNode(int position);

    void registerObserver(ListObserver observer);

    void unregisterObserver(ListObserver observer);

    SparseArray<ShieldDisplayNode> getTopNodeList();
    
    SparseArray<ShieldDisplayNode> getHotZoneNodeList();

}
