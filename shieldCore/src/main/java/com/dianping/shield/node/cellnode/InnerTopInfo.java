package com.dianping.shield.node.cellnode;

import com.dianping.shield.node.useritem.TopInfo;
import com.dianping.shield.node.useritem.TopState;

/**
 * Created by runqi.wei at 2018/6/21
 */
public class InnerTopInfo {

    public TopInfo topInfo;
    public int startPos = -1;
    public int endPos = Integer.MAX_VALUE;

    public boolean needAutoOffset = false;
    public int offset;

    public int zPosition;

    public TopStateChangeListener listener;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InnerTopInfo that = (InnerTopInfo) o;

        return topInfo != null ? topInfo.equals(that.topInfo) : that.topInfo == null;
    }

    @Override
    public int hashCode() {
        return topInfo != null ? topInfo.hashCode() : 0;
    }

    public interface TopStateChangeListener{
        void onTopStateChanged(ShieldDisplayNode node, TopState state);
    }
}
