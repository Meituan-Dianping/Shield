package com.dianping.shield.node.adapter;

/**
 * Created by runqi.wei at 2018/7/16
 */
public interface ListObserver {

    void onChanged();

    void onItemRangeChanged(int positionStart, int itemCount);

    void onItemRangeInserted(int positionStart, int itemCount);

    void onItemRangeRemoved(int positionStart, int itemCount);

    void onItemRangeMoved(int fromPosition, int toPosition);

}
