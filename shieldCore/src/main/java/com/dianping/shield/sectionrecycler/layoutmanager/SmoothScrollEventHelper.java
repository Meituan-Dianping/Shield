package com.dianping.shield.sectionrecycler.layoutmanager;

import android.support.v7.widget.RecyclerView;

import com.dianping.agentsdk.sectionrecycler.layoutmanager.OnSmoothScrollListener;

import java.util.ArrayList;

/**
 * Created by runqi.wei at 2018/11/20
 */
public class SmoothScrollEventHelper extends RecyclerView.OnScrollListener {

    protected RecyclerView recyclerView;
    //滚动事件标志位
    protected boolean hasScrollingRun;
    protected boolean hasScrollingStopped;
    protected boolean hasStateChanged;

    protected ArrayList<OnSmoothScrollListener> listeners;

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public void setListeners(ArrayList<OnSmoothScrollListener> listeners) {
        this.listeners = listeners;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        hasStateChanged = (newState == RecyclerView.SCROLL_STATE_SETTLING);
        if (newState != RecyclerView.SCROLL_STATE_SETTLING && hasScrollingStopped) {
            recyclerView.removeOnScrollListener(this);
            resetSignals();
            dispatchStopScrollEvent();
        }
    }

    public void resetSignals() {
        hasStateChanged = false;
        hasScrollingRun = false;
        hasScrollingStopped = false;
    }

    public void onStart() {
        dispatchStartScrollEvent();
    }

    public void onScrolling() {
        hasScrollingRun = true;
    }

    public void onStop() {
        hasScrollingStopped = true;

        // 如果没有滚动过
        // 直接分发滚动结束回调
        // 如果滚动过，要在 onScrollStateChanged 中分发滚动结束回调
        if (!hasScrollingRun && !hasStateChanged) {
            resetSignals();
            if (recyclerView != null) {
                recyclerView.removeOnScrollListener(this);
            }
            dispatchStopScrollEvent();
        }
    }

    protected void dispatchStartScrollEvent() {
        if (listeners != null && !listeners.isEmpty()) {
            for (OnSmoothScrollListener listener : listeners) {
                if (listener == null) {
                    continue;
                }

                listener.onScrollStart();
            }
        }
    }

    protected void dispatchStopScrollEvent() {
        if (listeners != null && !listeners.isEmpty()) {
            for (OnSmoothScrollListener listener : listeners) {
                if (listener == null) {
                    continue;
                }

                listener.onScrollStop();
            }
        }
    }
}
