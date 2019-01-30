package com.dianping.shield.node.adapter.hotzone;

import java.util.ArrayList;

/**
 * Created by runqi.wei at 2018/7/11
 */
public class HotZone {

    public static final int PARENT_TOP = Integer.MIN_VALUE;
    public static final int PARENT_BOTTOM = Integer.MAX_VALUE;

    public int start;

    public int end;

    public ArrayList<OnHotZoneStateChangeListener> listenerArrayList;

    public HotZone() {
    }

    public HotZone(int start, int end) {
        this.start = start;
        this.end = end;
    }
}
