package com.dianping.shield.node.cellnode;

import com.dianping.shield.node.useritem.BottomInfo;
import com.dianping.shield.node.useritem.TopState;

/**
 * Created by runqi.wei at 2018/10/29
 */
public class InnerBottomInfo {
    public BottomInfo bottomInfo;
    public Mode mode = Mode.SINGLY;
    public int startPos = -1;
    public int endPos = Integer.MAX_VALUE;

    public int offset;

    public int zPosition;

    public InnerTopInfo.TopStateChangeListener listener;

    /**
     * {@link #SINGLY} 模式 View 会一个接一个连续排列，
     * {@link #OVERLAY} 模式 View 会互相重叠在一起
     */
    public enum Mode {
        SINGLY,
        OVERLAY
    }

    public interface BottomStateChangeListener{
        void onBottomStateChanged(ShieldDisplayNode node, TopState state);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InnerBottomInfo that = (InnerBottomInfo) o;

        return bottomInfo != null ? bottomInfo.equals(that.bottomInfo) : that.bottomInfo == null;
    }

    @Override
    public int hashCode() {
        return bottomInfo != null ? bottomInfo.hashCode() : 0;
    }
}
