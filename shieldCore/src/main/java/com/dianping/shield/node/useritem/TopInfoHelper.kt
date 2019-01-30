package com.dianping.shield.node.useritem

import com.dianping.shield.feature.TopPositionInterface
import com.dianping.shield.layoutmanager.TopLinearLayoutManager

/**
 * Created by runqi.wei at 2018/8/17
 */
class TopInfoHelper {
    companion object {
        @JvmStatic
        fun createTopInfo(): TopInfo {
            var topInfo = TopInfo()
            topInfo.startType = TopInfo.StartType.SELF
            topInfo.endType = TopInfo.EndType.NONE
            return topInfo
        }

        @JvmStatic
        fun createTopInfo(topPositionInfo: TopPositionInterface.TopPositionInfo): TopInfo {
            var topInfo = TopInfo()
            topInfo.let {
                topInfo.needAutoOffset = topPositionInfo.needAutoOffset
                topInfo.offset = topPositionInfo.offset
                topInfo.startType = TopInfoHelper.createStartType(topPositionInfo.startTop)
                topInfo.endType = TopInfoHelper.createEndType(topPositionInfo.stopTop)
                topInfo.zPosition = topPositionInfo.zPosition
                topInfo.onTopStateChangeListener = TopInfo.OnTopStateChangeListener { cellType, section, row, state ->
                    topPositionInfo.onTopStateChangeListener?.onTopStageChanged(cellType, section, row, parseState(state))
                }
            }

            return topInfo
        }

        fun parseState(topState: TopState) : TopLinearLayoutManager.TopState{
            return when(topState){
                TopState.TOP -> TopLinearLayoutManager.TopState.TOP
                TopState.NORMAL -> TopLinearLayoutManager.TopState.NORMAL
                TopState.ENDING -> TopLinearLayoutManager.TopState.ENDING_TOP
            }
        }

        @JvmStatic
        fun createStartType(startTop: TopPositionInterface.AutoStartTop?): TopInfo.StartType {
            return when (startTop) {
                TopPositionInterface.AutoStartTop.SELF -> TopInfo.StartType.SELF
                TopPositionInterface.AutoStartTop.ALWAYS -> TopInfo.StartType.ALWAYS
                else -> TopInfo.StartType.SELF
            }
        }

        @JvmStatic
        fun createEndType(stopTop: TopPositionInterface.AutoStopTop?): TopInfo.EndType {
            return when (stopTop) {
                TopPositionInterface.AutoStopTop.NONE -> TopInfo.EndType.NONE
                TopPositionInterface.AutoStopTop.MODULE -> TopInfo.EndType.MODULE
                TopPositionInterface.AutoStopTop.SECTION -> TopInfo.EndType.SECTION
                TopPositionInterface.AutoStopTop.CELL -> TopInfo.EndType.CELL
                else -> TopInfo.EndType.NONE
            }
        }
    }
}