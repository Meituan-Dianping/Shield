package com.dianping.shield.feature;

import com.dianping.shield.entity.CellType;
import com.dianping.shield.layoutmanager.TopLinearLayoutManager;

/**
 * Created by runqi.wei at 2018/5/23
 */
public interface TopPositionInterface {

    TopPositionInfo getTopPositionInfo(CellType cellType, int section, int row);

    /**
     * 开始置顶的模式
     * <ul>
     * <li>{@link AutoStartTop#SELF}  表示当自身滚动到顶部之后开始置顶 </li>
     * <li>{@link AutoStartTop#ALWAYS}  表示直接进入置顶状态 </li>
     * </ul>
     */
    enum AutoStartTop {
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
     * <li>{@link AutoStopTop#NONE}  表示不会结束置顶</li>
     * <li>{@link AutoStopTop#MODULE}  表示跟随本模块结束置顶</li>
     * <li>{@link AutoStopTop#SECTION}  表示跟随本 Section 结束置顶</li>
     * <li>{@link AutoStopTop#CELL}  表示跟随本 Cell/Row 结束置顶</li>
     * </ul>
     */
    enum AutoStopTop {
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

    interface OnTopStateChangeListener {

        void onTopStageChanged(CellType cellType, int section, int row, TopLinearLayoutManager.TopState state);
    }

    class TopPositionInfo {

        public boolean needAutoOffset = false;

        /**
         * 置顶的开始模式，什么时候开始置顶 <br/>
         * 默认 {@link AutoStartTop#SELF}，表示当自身滚动到顶部之后开始置顶
         */
        public AutoStartTop startTop;

        /**
         * 置顶的结束模式，什么时候结束置顶 <br/>
         * 默认 {@link AutoStopTop#NONE}， 表示不会结束置顶
         */
        public AutoStopTop stopTop;

        public OnTopStateChangeListener onTopStateChangeListener;

        public int offset;

        /**
         * 置顶的布局分层，{@code zPosition} 更大的 View 会覆盖在上面
         */
        public int zPosition;
    }
}
