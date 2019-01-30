package com.dianping.agentsdk.sectionrecycler.layoutmanager;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.dianping.shield.feature.IFocusChildScrollWhenBack;
import com.dianping.shield.sectionrecycler.ShieldLayoutManagerInterface;
import com.dianping.shield.sectionrecycler.WrapContentLinearLayoutManager;
import com.dianping.shield.sectionrecycler.layoutmanager.SmoothScrollEventHelper;

import java.util.ArrayList;


/**
 * Created by runqi.wei
 * 11:31
 * 16.11.2016.
 */

public class LinearLayoutManagerWithSmoothOffset extends WrapContentLinearLayoutManager implements ShieldLayoutManagerInterface ,IFocusChildScrollWhenBack {

    protected Context context;
    private boolean isScrollEnabled = true;

    protected RecyclerView mRecyclerView;

    //滚动事件标志位
    protected SmoothScrollEventHelper scrollEventHelper = new SmoothScrollEventHelper();
    protected boolean setAllowFocusedChildRecOnScreen = true ;//默认走recycleview的系统行为


    public LinearLayoutManagerWithSmoothOffset(Context context) {
        super(context, VERTICAL, false);
        init(context);
    }

    public LinearLayoutManagerWithSmoothOffset(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        init(context);
    }

    protected void init(Context context) {
        this.context = context;
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        this.mRecyclerView = view;
    }

    @Override
    public void onDetachedFromWindow(RecyclerView view, RecyclerView.Recycler recycler) {
        this.mRecyclerView = null;
        super.onDetachedFromWindow(view, recycler);
    }

    public Context getContext() {
        return context;
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
        return isScrollEnabled && super.canScrollVertically();
    }

    public void smoothScrollToPosition(int position, int offset) {
        smoothScrollToPosition(position, offset, null);
    }

    public void smoothScrollToPosition(int position, int offset, ArrayList<OnSmoothScrollListener> listeners) {
        if (mRecyclerView != null) {
            mRecyclerView.addOnScrollListener(scrollEventHelper);
        }
        scrollEventHelper.setRecyclerView(mRecyclerView);
        scrollEventHelper.setListeners(listeners);
        LinearSmoothScrollerWithOffset linearSmoothScroller = new LinearSmoothScrollerWithOffset(getContext(), this, offset, scrollEventHelper);
        linearSmoothScroller.setTargetPosition(position);
        startSmoothScroll(linearSmoothScroller);
    }

    @Override
    public void scrollToPositionWithOffset(int globalPosition, int offset, boolean isSmoothScroll) {
        scrollToPositionWithOffset(globalPosition, offset, isSmoothScroll, null);
    }

    public void scrollToPositionWithOffset(int globalPosition, int offset, boolean isSmoothScroll, ArrayList<OnSmoothScrollListener> listeners) {
        if (isSmoothScroll) {
            smoothScrollToPosition(globalPosition, offset, listeners);
        } else {
            scrollToPositionWithOffset(globalPosition, offset);
        }
    }

    @Override
    public int findFirstVisibleItemPosition(boolean completely) {
        if (completely) {
            return findFirstCompletelyVisibleItemPosition();
        } else {
            return findFirstVisibleItemPosition();
        }
    }

    @Override
    public int findLastVisibleItemPosition(boolean completely) {
        if (completely) {
            return findLastCompletelyVisibleItemPosition();
        } else {
            return findLastVisibleItemPosition();
        }
    }

    @Override
    public boolean requestChildRectangleOnScreen(RecyclerView parent, View child, Rect rect,
                                                 boolean immediate,
                                                 boolean focusedChildVisible) {
        if( focusedChildVisible && !setAllowFocusedChildRecOnScreen ){
            return false ;
        }
        return super.requestChildRectangleOnScreen( parent, child, rect, immediate, focusedChildVisible );
    }

    @Override
    public void setFocusChildScrollOnScreenWhenBack(boolean allow) {
        setAllowFocusedChildRecOnScreen = allow ;
    }

    protected static class LinearSmoothScrollerWithOffset extends TopLinearSmoothScroller {

        protected int offset;
        protected SmoothScrollEventHelper eventHelper;

        public LinearSmoothScrollerWithOffset(Context context, @NonNull LinearLayoutManager llm) {
            super(context, llm);
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }

        public LinearSmoothScrollerWithOffset(Context context, @NonNull LinearLayoutManager llm, int offset, SmoothScrollEventHelper eventHelper) {
            super(context, llm);
            this.offset = offset;
            this.eventHelper = eventHelper;
        }

        @Override
        protected void onTargetFound(View targetView, RecyclerView.State state, Action action) {
            int xOffset = 0;
            int yOffset = 0;
            if (llm.getOrientation() == LinearLayoutManager.VERTICAL) {
                yOffset = offset;
            } else if (llm.getOrientation() == LinearLayoutManager.HORIZONTAL) {
                xOffset = offset;
            }
            final int dx = calculateDxToMakeVisible(targetView, getHorizontalSnapPreference()) + xOffset;
            final int dy = calculateDyToMakeVisible(targetView, getVerticalSnapPreference()) + yOffset + getTopOffset();
            final int distance = (int) Math.sqrt(dx * dx + dy * dy);
            final int time = calculateTimeForDeceleration(distance);
            if (time > 0) {
                action.update(-dx, -dy, time, mDecelerateInterpolator);
            }
        }
        @Override
        public int calculateDyToMakeVisible(View view, int snapPreference) {
            final RecyclerView.LayoutManager layoutManager = getLayoutManager();
            if (layoutManager == null || !layoutManager.canScrollVertically()) {
                return 0;
            }
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                    view.getLayoutParams();
            final int top = view.getTop() - params.topMargin;
            final int bottom = view.getBottom() + params.bottomMargin;
            final int start = layoutManager.getPaddingTop();
            final int end = layoutManager.getHeight() - layoutManager.getPaddingBottom();
            return calculateDtToFit(top, bottom, start, end, snapPreference);
        }
        @Override
        protected void onStart() {
            super.onStart();
            if (eventHelper != null) {
                eventHelper.onStart();
            }
        }

        @Override
        protected void onSeekTargetStep(int dx, int dy, RecyclerView.State state, Action action) {
            super.onSeekTargetStep(dx, dy, state, action);
            if (eventHelper != null) {
                eventHelper.onScrolling();
            }
        }

        @Override
        protected void onStop() {
            super.onStop();
            if (eventHelper != null) {
                eventHelper.onStop();
            }
        }

        @Override
        protected int getVerticalSnapPreference() {
            return SNAP_TO_START;
        }

        @Override
        public PointF computeScrollVectorForPosition(int targetPosition) {
            return llm.computeScrollVectorForPosition(targetPosition);
        }

        @Override
        protected int getHorizontalSnapPreference() {
            return SNAP_TO_START;
        }
    }
}
