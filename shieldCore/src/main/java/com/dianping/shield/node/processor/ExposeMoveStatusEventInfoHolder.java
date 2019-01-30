package com.dianping.shield.node.processor;

import java.util.HashMap;

/**
 * Created by runqi.wei at 2018/8/16
 */
public class ExposeMoveStatusEventInfoHolder {

    public static final int NO_COUNT = 0;
    public static final long NO_TIME = 0;
    private HashMap<Object, MoveStatusEventInfo> map = new HashMap<>();

    public void reset(Object object) {
        map.remove(object);
    }

    public void setCount(Object object, int count) {
        if (object == null) {
            return;
        }
        MoveStatusEventInfo info = map.get(object);
        if (info == null) {
            info = new MoveStatusEventInfo();
        }

        info.count = count;
        map.put(object, info);
    }

    public int getCount(Object object) {
        if (object == null) {
            return NO_COUNT;
        }
        MoveStatusEventInfo info = map.get(object);
        if (info == null) {
            return NO_COUNT;
        }

        return info.count;
    }

    public void setLastTimeMillis(Object object, long lastTimeMillis) {
        if (object == null) {
            return;
        }
        MoveStatusEventInfo info = map.get(object);
        if (info == null) {
            info = new MoveStatusEventInfo();
        }

        info.lastTimeMillis = lastTimeMillis;
        map.put(object, info);
    }

    public long getLastTimeMillis(Object object) {
        if (object == null) {
            return NO_TIME;
        }
        MoveStatusEventInfo info = map.get(object);
        if (info == null) {
            return NO_TIME;
        }

        return info.lastTimeMillis;
    }

    public static class MoveStatusEventInfo{
        public int count = 0;
        public long lastTimeMillis = 0;
    }
}
