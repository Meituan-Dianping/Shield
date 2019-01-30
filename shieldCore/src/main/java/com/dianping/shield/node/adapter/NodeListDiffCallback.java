package com.dianping.shield.node.adapter;

import android.support.v7.util.DiffUtil;

import com.dianping.shield.env.ShieldEnvironment;
import com.dianping.shield.logger.SCLogger;
import com.dianping.shield.node.cellnode.ShieldDisplayNode;

import java.util.ArrayList;

/**
 * Created by runqi.wei at 2018/6/20
 */
class NodeListDiffCallback extends DiffUtil.Callback {

    private static final boolean DEBUG = ShieldEnvironment.INSTANCE.isDebug();
    private SCLogger logger = new SCLogger().setTag("DiffUtil");

    private ArrayList<ShieldDisplayNode> oldNodeList;
    private ArrayList<ShieldDisplayNode> newNodeList;

    public NodeListDiffCallback() {
    }

    public void setNodeList(ArrayList<ShieldDisplayNode> oldNodeList, ArrayList<ShieldDisplayNode> newNodeList) {
        this.oldNodeList = oldNodeList;
        this.newNodeList = newNodeList;
    }

    @Override
    public int getOldListSize() {
        return oldNodeList == null ? 0 : oldNodeList.size();
    }

    @Override
    public int getNewListSize() {
        return newNodeList == null ? 0 : newNodeList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        ShieldDisplayNode oldItem = oldNodeList.get(oldItemPosition);
        ShieldDisplayNode newItem = newNodeList.get(newItemPosition);
        if (DEBUG) {
            logger.d("Compare areItemsTheSame(%d, %d) = %s", oldItemPosition, newItemPosition, ShieldDisplayNode.Companion.same(oldItem, newItem));
        }
        return ShieldDisplayNode.Companion.same(oldItem, newItem);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {

        ShieldDisplayNode oldItem = oldNodeList.get(oldItemPosition);
        ShieldDisplayNode newItem = newNodeList.get(newItemPosition);
        if (DEBUG) {
            logger.d("Compare areContentsTheSame(%d, %d) = %s", oldItemPosition, newItemPosition, ShieldDisplayNode.Companion.contentsEquals(oldItem, newItem));
        }
        return ShieldDisplayNode.Companion.contentsEquals(oldItem, newItem);
    }
}
