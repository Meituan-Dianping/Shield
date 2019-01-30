package com.dianping.shield.node.useritem;

/**
 * Created by runqi.wei at 2018/10/29
 */
public class BottomInfo {
    public int zPosition;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BottomInfo that = (BottomInfo) o;

        return zPosition == that.zPosition;
    }

    @Override
    public int hashCode() {
        return zPosition;
    }
}
