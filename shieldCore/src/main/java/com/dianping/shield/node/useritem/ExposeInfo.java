package com.dianping.shield.node.useritem;

import com.dianping.shield.entity.ExposeScope;
import com.dianping.shield.node.itemcallbacks.ExposeCallback;

/**
 * Created by zhi.he on 2018/6/21.
 */

public class ExposeInfo {
    public Object data;
    public ExposeScope exposeScope = ExposeScope.PX;
    public int maxExposeCount = Integer.MAX_VALUE;
    public long exposeDuration = 0;
    public long stayDuration = 0;
    public ExposeCallback agentExposeCallback;
}
