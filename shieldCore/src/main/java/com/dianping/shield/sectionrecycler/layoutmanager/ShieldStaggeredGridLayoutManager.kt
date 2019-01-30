package com.dianping.shield.sectionrecycler.layoutmanager

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.LinearSmoothScroller
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View
import com.dianping.agentsdk.sectionrecycler.layoutmanager.OnSmoothScrollListener
import com.dianping.shield.feature.IFocusChildScrollWhenBack
import com.dianping.shield.sectionrecycler.ShieldLayoutManagerInterface
import java.util.*

/**
 * Create By zhi.he at 2018/4/26
 */
open class ShieldStaggeredGridLayoutManager(
        spanCount: Int,
        orientation: Int
) : StaggeredGridLayoutManager(spanCount, orientation), ShieldLayoutManagerInterface , IFocusChildScrollWhenBack {

    protected var topOffset = 0
    var eventHelper: SmoothScrollEventHelper = SmoothScrollEventHelper()

    private var isScrollEnabled = true
    protected var setAllowFocusedChildRecOnScreen = true

    protected var holderRecyclerView: RecyclerView? = null
    override fun onAttachedToWindow(view: RecyclerView?) {
        super.onAttachedToWindow(view)
        this.holderRecyclerView = view
    }

    override fun onDetachedFromWindow(view: RecyclerView?, recycler: RecyclerView.Recycler?) {
        super.onDetachedFromWindow(view, recycler)
        this.holderRecyclerView = null
    }

    override fun scrollToPositionWithOffset(globalPosition: Int, offset: Int, isSmoothScroll: Boolean) {
        scrollToPositionWithOffset(globalPosition, offset, isSmoothScroll, null)
    }

    override fun scrollToPositionWithOffset(globalPosition: Int, offset: Int, isSmoothScroll: Boolean, listeners: ArrayList<OnSmoothScrollListener>?) {
        //only support scroll first
        if (isSmoothScroll) {
            smoothScrollToPositionWithOffset(globalPosition, offset, listeners)
        } else {
            scrollToPositionWithOffset(globalPosition, offset)
        }
    }

    fun smoothScrollToPositionWithOffset(globalPosition: Int, offset: Int) {
        smoothScrollToPositionWithOffset(globalPosition, offset, null)
    }

    fun smoothScrollToPositionWithOffset(globalPosition: Int, offset: Int, listeners: ArrayList<OnSmoothScrollListener>?) {
        this.holderRecyclerView?.let { rv ->
            rv.addOnScrollListener(eventHelper)
            eventHelper.recyclerView = rv
            eventHelper.listeners = listeners
            var smoothScroller = LinearSmoothScrollerWithOffset(rv.context, this.orientation, offset)
            smoothScroller?.targetPosition = globalPosition
            startSmoothScroll(smoothScroller)
        }
    }

    override fun findFirstVisibleItemPosition(completely: Boolean): Int {
        lateinit var firstItems: IntArray
        if (completely) {
            firstItems = findFirstCompletelyVisibleItemPositions(null)
        } else {

            firstItems = findFirstVisibleItemPositions(null)
        }

        return firstItems.min() ?: -1
    }

    override fun findLastVisibleItemPosition(completely: Boolean): Int {
        lateinit var lastItems: IntArray
        if (completely) {
            lastItems = findLastCompletelyVisibleItemPositions(null)
        } else {
            lastItems = findLastVisibleItemPositions(null)
        }

        return lastItems.max() ?: -1
    }

    fun setScrollEnabled(flag: Boolean) {
        this.isScrollEnabled = flag
    }

    override fun canScrollVertically(): Boolean {
        return isScrollEnabled && super.canScrollVertically()
    }

    override fun setFocusChildScrollOnScreenWhenBack(allow: Boolean) {
        this.setAllowFocusedChildRecOnScreen = allow
    }

    override fun requestChildRectangleOnScreen(parent: RecyclerView, child: View, rect: Rect,
                                               immediate: Boolean,
                                               focusedChildVisible: Boolean): Boolean {
        return if (focusedChildVisible && !this.setAllowFocusedChildRecOnScreen) {
            false
        } else super.requestChildRectangleOnScreen(parent, child, rect, immediate, focusedChildVisible)
    }

    inner class LinearSmoothScrollerWithOffset(context: Context, val orientation: Int, val offset: Int) : LinearSmoothScroller(context) {

        override fun onTargetFound(targetView: View?, state: RecyclerView.State?, action: Action?) {
            var xOffset = 0
            var yOffset = 0
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                yOffset = offset
            } else if (orientation == StaggeredGridLayoutManager.HORIZONTAL) {
                xOffset = offset
            }
            val dx = calculateDxToMakeVisible(targetView, horizontalSnapPreference) + xOffset
            val dy = calculateDyToMakeVisible(targetView, verticalSnapPreference) + yOffset + topOffset
            val distance = Math.sqrt((dx * dx + dy * dy).toDouble()).toInt()
            val time = calculateTimeForDeceleration(distance)
            if (time > 0) {
                action?.update(-dx, -dy, time, mDecelerateInterpolator)
            }
        }

        override fun calculateDyToMakeVisible(view: View?, snapPreference: Int): Int {
            val layoutManager = layoutManager
            if (layoutManager == null || !layoutManager.canScrollVertically()) {
                return 0
            }
            val params = view?.getLayoutParams() as RecyclerView.LayoutParams
            val top = view.top - params.topMargin
            val bottom = view.bottom + params.bottomMargin
            val start = layoutManager.paddingTop
            val end = layoutManager.height - layoutManager.paddingBottom
            return calculateDtToFit(top, bottom, start, end, snapPreference)
        }

        override fun onStart() {
            super.onStart()
            eventHelper.onStart()
        }

        override fun onSeekTargetStep(dx: Int, dy: Int, state: RecyclerView.State?, action: Action?) {
            super.onSeekTargetStep(dx, dy, state, action)
            eventHelper.onScrolling()
        }

        override fun onStop() {
            super.onStop()
            eventHelper.onStop()
        }

        override fun getVerticalSnapPreference(): Int {
            return SNAP_TO_START
        }

        override fun getHorizontalSnapPreference(): Int {
            return SNAP_TO_START
        }
    }

}