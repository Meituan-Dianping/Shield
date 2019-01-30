package com.dianping.shield.manager.feature

import android.support.v7.widget.RecyclerView
import com.dianping.shield.entity.AgentScrollerParams
import com.dianping.shield.entity.ScrollScope
import com.dianping.shield.bridge.feature.AgentScrollerInterface
import com.dianping.shield.node.cellnode.ShieldCellGroup
import com.dianping.shield.node.cellnode.ShieldViewCell

/**
 * Created by bingwei on 2018/8/12.
 */

class AgentScrollTop(private val scroller: AgentScrollerInterface) : CellManagerFeatureInterface, CellManagerScrollListenerInterface {
    private var idNeedScroll: Boolean = true
    private var scrollToTopCell: ShieldViewCell? = null

    @JvmField
    var scrollToTopByFirstMarkedAgent: Boolean = false

    override fun onCellNodeRefresh(shieldViewCell: ShieldViewCell) {
        if(shieldViewCell.needScrollToTop && idNeedScroll) {
            if (scrollToTopByFirstMarkedAgent) {
                if (scrollToTopCell == null) {
                    scrollToTopCell = shieldViewCell
                }
            } else {
                scrollToTopCell = shieldViewCell
            }
        }
    }

    override fun onAdapterNotify(cellGroups: ArrayList<ShieldCellGroup?>) {
        if (idNeedScroll) {
            scrollToTopCell?.owner?.let {
                scroller.scrollToNode(AgentScrollerParams.toAgent(it).setNeedAutoOffset(true).setSmooth(false))
            }
        }
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (scrollToTopCell != null && idNeedScroll && newState == RecyclerView.SCROLL_STATE_DRAGGING) {
            idNeedScroll = false
        }
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
    }
}