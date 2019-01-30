package com.dianping.shield.manager.feature

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import com.dianping.shield.node.adapter.ShieldDisplayNodeAdapter
import com.dianping.shield.node.cellnode.ShieldCellGroup
import com.dianping.shield.node.cellnode.ShieldViewCell
import com.dianping.shield.node.cellnode.StaggeredGridSection

/**
 * Created by zhi.he on 2018/8/27.
 */
class StaggeredGridCollector(private val layoutManager: RecyclerView.LayoutManager?,
                             private val shieldDisplayNodeAdapter: ShieldDisplayNodeAdapter,
                             private val looper: LoopCellGroupsCollector) : CellManagerFeatureInterface, CellManagerScrollListenerInterface {


    var findStaggered = false
    var startTime: Long = 0;
    override fun onCellNodeRefresh(shieldViewCell: ShieldViewCell) {

    }

    override fun onAdapterNotify(cellGroups: ArrayList<ShieldCellGroup?>) {
        //瀑布流布局下动态改变spanCount
        if (layoutManager is StaggeredGridLayoutManager) {
            looper.addBeforeLoopAction { findStaggered = false }
            looper.addIndexedSectionAction { _, shieldSection ->
                if (!findStaggered && shieldSection is StaggeredGridSection
                        && shieldSection.spanCount > 1 && layoutManager.spanCount != shieldSection.spanCount) {
                    layoutManager.spanCount = shieldSection.spanCount
                    shieldDisplayNodeAdapter.staggeredGridSpaceDecoration.xGap = shieldSection.xStaggeredGridGap
                    shieldDisplayNodeAdapter.staggeredGridSpaceDecoration.yGap = shieldSection.yStaggeredGridGap
                    shieldDisplayNodeAdapter.staggeredGridSpaceDecoration.leftMargin = shieldSection.staggeredGridLeftMargin
                    shieldDisplayNodeAdapter.staggeredGridSpaceDecoration.rightMargin = shieldSection.staggeredGridRightMargin
                    findStaggered = true
                }
            }
        }
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {

        if (recyclerView.layoutManager is StaggeredGridLayoutManager) {
            var staggeredGridLayoutManager = recyclerView.layoutManager as StaggeredGridLayoutManager;
            val first = staggeredGridLayoutManager.findFirstCompletelyVisibleItemPositions(null);
            //监听RecyclerView是否滑到顶部，若滑到顶部，则调用StaggeredLayoutManager的invalidateSpanAssignments方法刷新间隔
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                //当recycleview的item的第一行完全漏出的时候刷新decoration
                for (i in first) {
                    if (i <= staggeredGridLayoutManager.spanCount * 3) {
                        staggeredGridLayoutManager.invalidateSpanAssignments();
                        //Protect: java.lang.IllegalStateException: Cannot invalidate item decorations during a scroll or layout
                        if (!recyclerView.isComputingLayout){
                            recyclerView.invalidateItemDecorations();
                        }
                    }
                }
            }
        }
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
    }
}