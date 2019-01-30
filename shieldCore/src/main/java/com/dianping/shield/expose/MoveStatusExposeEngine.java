package com.dianping.shield.expose;

import android.os.Handler;
import android.support.v4.util.Pair;

import com.dianping.shield.entity.ExposeScope;
import com.dianping.shield.node.cellnode.AppearanceEvent;
import com.dianping.shield.node.cellnode.NodePath;
import com.dianping.shield.node.processor.ExposeMoveStatusEventInfoHolder;
import com.dianping.shield.node.useritem.ExposeInfo;

/**
 * Created by runqi.wei at 2018/12/13
 */
public class MoveStatusExposeEngine<T> {

    protected ExposeMoveStatusEventInfoHolder infoHolder;

    protected Handler handler;
    protected ExposeInfo exposeInfo;
    protected Runnable currentDelayRunnable = null;

    public MoveStatusExposeEngine(ExposeMoveStatusEventInfoHolder infoHolder, Handler handler, ExposeInfo exposeInfo) {
        this.infoHolder = infoHolder;
        this.handler = handler;
        this.exposeInfo = exposeInfo;
    }

    public void reset(T item) {
        Pair<T, ExposeScope> key = new Pair<>(item, exposeInfo.exposeScope);
        infoHolder.reset(key);
    }

    public void onAppeared(T item, AppearanceEvent appearEvent) {
        if (exposeInfo.agentExposeCallback == null)
            return;

        if (!(appearEvent == AppearanceEvent.PARTLY_APPEAR && exposeInfo.exposeScope == ExposeScope.PX)
                && !(appearEvent == AppearanceEvent.FULLY_APPEAR && exposeInfo.exposeScope == ExposeScope.COMPLETE)) {
            return;
        }

        EventRunnable runnable = new EventRunnable(item);
        if (exposeInfo.stayDuration > 0) {
            currentDelayRunnable = runnable;
        }
        handler.postDelayed(runnable, exposeInfo.stayDuration);
    }

    public void onDisappeared(T item, AppearanceEvent appearEvent) {
        if (appearEvent == AppearanceEvent.PARTLY_DISAPPEAR && exposeInfo.exposeScope == ExposeScope.COMPLETE
                || appearEvent == AppearanceEvent.FULLY_DISAPPEAR && exposeInfo.exposeScope == ExposeScope.PX) {
            if (currentDelayRunnable != null) {
                handler.removeCallbacks(currentDelayRunnable);
                currentDelayRunnable = null;
            }
        }
    }

    protected void dispatchExposeEvent(T item) {

        currentDelayRunnable = null;

        Pair<T, ExposeScope> key = new Pair<>(item, exposeInfo.exposeScope);
        int count = infoHolder.getCount(key);
        if (exposeInfo.maxExposeCount <= count || exposeInfo.exposeDuration + infoHolder.getLastTimeMillis(item) > System.currentTimeMillis()) {
            return;
        }

        if (exposeInfo.agentExposeCallback != null) {
            exposeInfo.agentExposeCallback.onExpose(exposeInfo.data, count, getPath(item));
        }
        infoHolder.setCount(key, count + 1);
        infoHolder.setLastTimeMillis(key, System.currentTimeMillis());
    }

    public NodePath getPath(T item) {
        return null;
    }

    private class EventRunnable implements Runnable {

        T item;

        public EventRunnable(T item) {
            this.item = item;
        }

        @Override
        public void run() {
            dispatchExposeEvent(item);
        }
    }
}
