package com.dianping.shield.node.useritem;

import com.dianping.shield.node.itemcallbacks.HotZoneStateChangeCallBack;

import java.util.ArrayList;

/**
 * Created by runqi.wei at 2018/9/11
 */
public class HotZoneInfo {

    public static final int PARENT_TOP = Integer.MIN_VALUE;
    public static final int PARENT_BOTTOM = Integer.MAX_VALUE;

    public int start;

    public int end;

    public ArrayList<HotZoneStateChangeCallBack> callBackList;

}
