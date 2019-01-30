package com.dianping.shield.node.useritem;

import com.dianping.shield.entity.CellType;

/**
 * Created by runqi.wei at 2018/8/17
 */
public class TopInfo {

    /**
     * 置顶的开始模式，什么时候开始置顶 <br/>
     * 默认 {@link StartType#SELF}，表示当自身滚动到顶部之后开始置顶
     */
    public StartType startType = StartType.SELF;

    /**
     * 置顶的结束模式，什么时候结束置顶 <br/>
     * 默认 {@link EndType#NONE}， 表示不会结束置顶
     */
    public EndType endType = EndType.NONE;

    public OnTopStateChangeListener onTopStateChangeListener;

    public boolean needAutoOffset = false;

    public int offset;

    /**
     * 置顶的布局分层，{@code zPosition} 更大的 View 会覆盖在上面
     */
    public int zPosition;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TopInfo topInfo = (TopInfo) o;

        if (needAutoOffset != topInfo.needAutoOffset) return false;
        if (offset != topInfo.offset) return false;
        if (zPosition != topInfo.zPosition) return false;
        if (startType != topInfo.startType) return false;
        return endType == topInfo.endType;
    }

    @Override
    public int hashCode() {
        int result = startType != null ? startType.hashCode() : 0;
        result = 31 * result + (endType != null ? endType.hashCode() : 0);
        result = 31 * result + (needAutoOffset ? 1 : 0);
        result = 31 * result + offset;
        result = 31 * result + zPosition;
        return result;
    }

    /**
     * 开始置顶的模式
     * <ul>
     * <li>{@link StartType#SELF}  表示当自身滚动到顶部之后开始置顶 </li>
     * <li>{@link StartType#ALWAYS}  表示直接进入置顶状态 </li>
     * </ul>
     */
    public enum StartType {
        /**
         * 表示当自身滚动到顶部之后开始置顶
         */
        SELF,

        /**
         * 表示直接进入置顶状态
         */
        ALWAYS
    }

    /**
     * 结束置顶的模式
     * <ul>
     * <li>{@link EndType#NONE}  表示不会结束置顶</li>
     * <li>{@link EndType#MODULE}  表示跟随本模块结束置顶</li>
     * <li>{@link EndType#SECTION}  表示跟随本 Section 结束置顶</li>
     * <li>{@link EndType#CELL}  表示跟随本 Cell/Row 结束置顶</li>
     * </ul>
     */
    public enum EndType {
        /**
         * 表示不会结束置顶
         */
        NONE,

        /**
         * 表示跟随本模块结束置顶
         */
        MODULE,

        /**
         * 表示跟随本 Section 结束置顶
         */
        SECTION,

        /**
         * 表示跟随本 Cell/Row 结束置顶
         */
        CELL
    }

    public interface OnTopStateChangeListener {

        void onTopStageChanged(CellType cellType, int section, int row, TopState state);
    }
}
