package com.dianping.shield.layoutmanager;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.dianping.agentsdk.pagecontainer.OnTopViewLayoutChangeListener;
import com.dianping.agentsdk.pagecontainer.SetAutoOffsetInterface;
import com.dianping.shield.env.ShieldEnvironment;
import com.dianping.shield.logger.SCLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by runqi.wei at 2018/5/14
 */
public class TopLinearLayoutManager extends LinearLayoutManager implements CoveredYInterface, SetAutoOffsetInterface {

    public static final int STATUS_TOP = 0;
    public static final int STATUS_FOLLOW = 1;
    protected static final boolean DEBUG = ShieldEnvironment.INSTANCE.isDebug();
    protected static final int TOP = 0;
    protected static final int BOTTOM = 1;
    protected SCLogger logger;
    protected OrientationHelper mOrientationHelper;
    protected int autoOffset = 0;
    protected Mode topMode = Mode.OVERLAY;
    protected ArrayList<OnViewTopStateChangeListener> topStateChangedListenerArrayList = new ArrayList<>();
    protected ArrayList<OnTopViewLayoutChangeListener> topViewLayoutChangeListenerArrayList = new ArrayList<>();
    protected SparseArray<TBHolder> topPositionList = new SparseArray<>();
    protected SparseArray<TBHolder> currentTopHolderList = new SparseArray<>();
    protected ArrayList<Integer> topViewsInScreen = new ArrayList<>();
    protected SparseArray<View> currentTopViewList = new SparseArray<>();
    protected SparseArray<TBHolder> lastTopViewHolderList = new SparseArray<>();
    protected RecyclerView.Recycler tmpRecycler;
    private int coveredY = 0;
    private boolean interceptTouchEventForTopViews;

    protected boolean enableTop = true;

    public TopLinearLayoutManager(Context context) {
        super(context);
        init();
    }

    public TopLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        init();
    }

    public TopLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setInterceptTouchEventForTopViews(boolean interceptTouchEventForTopViews) {
        this.interceptTouchEventForTopViews = interceptTouchEventForTopViews;
    }

    public void setEnableTop(boolean enableTop) {
        this.enableTop = enableTop;
    }

    @Override
    public int getCoveredY() {
        return coveredY;
    }

    protected void init() {
        interceptTouchEventForTopViews = true;
        logger = new SCLogger().setTag("TopLinearLayoutManager");
        mOrientationHelper = OrientationHelper.createOrientationHelper(this, getOrientation());
        setItemPrefetchEnabled(false);
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        if (!enableTop) {
            return;
        }
        view.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                if (!interceptTouchEventForTopViews) {
                    if (rv.getScrollState() == RecyclerView.SCROLL_STATE_SETTLING) {
                        rv.stopScroll();
                    }
                    View v = rv.findChildViewUnder(e.getX(), e.getY());
                    if (v != null && currentTopViewList.indexOfValue(v) >= 0) {
                       return v.dispatchTouchEvent(e);
                    }
                } else {
                    if (rv.getScrollState() == RecyclerView.SCROLL_STATE_SETTLING) {
                        rv.stopScroll();
                        View v = rv.findChildViewUnder(e.getX(), e.getY());
                        if (v != null && currentTopViewList.indexOfValue(v) >= 0) {
                            v.dispatchTouchEvent(e);
                        }
                    }
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    /**
     * 设置置顶 View 排列方式
     *
     * @param mode {@link Mode#SINGLY}  置顶的 View 会一个接一个连续排列，
     *             View 自己的置顶 offset 也会被计入
     *             {@link Mode#OVERLAY} 置顶的 View 会互相重叠在一起，
     *             每个 View 的 Top 为 #autoOffset + View 自己的置顶 offset
     */
    public void setTopViewMode(Mode mode) {
        this.topMode = mode;
    }

    @Override
    public int getAutoOffset() {
        return this.autoOffset;
    }

    public void setAutoOffset(int autoOffset) {
        this.autoOffset = autoOffset;
    }

    /**
     * 添加 Top Position
     *
     * @param position
     * @param start
     * @param end
     */
    public void addTopPosition(int position, int start, int end) {
        addTopPosition(position, start, end, 0, 0);
    }

    public void addTopPosition(int position, int start, int end, int offset, int zPosition) {
        if (position < 0 || start > end) {
            return;
        }
        topPositionList.put(position, new TBHolder(start, end, offset, zPosition));
    }

    public void removeTopPosition(int position) {
        topPositionList.remove(position);
    }

    public void clearTopPosition() {
        topPositionList.clear();
    }

    public void addOnViewTopStateChangeListener(OnViewTopStateChangeListener listener) {
        if (listener != null && !topStateChangedListenerArrayList.contains(listener)) {
            topStateChangedListenerArrayList.add(listener);
        }
    }

    public void removeOnViewTopStateChangeListener(OnViewTopStateChangeListener listener) {
        topStateChangedListenerArrayList.remove(listener);
    }

    public void removeAllOnViewTopStateChangeListener() {
        topStateChangedListenerArrayList.clear();
    }

    public void addOnTopViewLayoutChangeListener(OnTopViewLayoutChangeListener listener) {
        if (listener != null && !topViewLayoutChangeListenerArrayList.contains(listener)) {
            topViewLayoutChangeListenerArrayList.add(listener);
        }
    }

    public void removeOnTopViewLayoutChangeListener(OnTopViewLayoutChangeListener listener) {
        topViewLayoutChangeListenerArrayList.remove(listener);
    }

    public void clearOnTopViewLayoutChangeListener() {
        topViewLayoutChangeListenerArrayList.clear();
    }

    // START ====== Override methods to avoid error caused by top views
    @Override
    public int getChildCount() {
        // 这里不能复写 getChildCount() 为 super.getChildCount() - currentTopViewList.size(),
        // 会导致 GapWorker 调用的 collectAdjacentPrefetchPositions 方法中，调用 updateLayoutState 方法时计算错误
        // return Math.max(0, super.getChildCount() - currentTopViewList.size());
        return super.getChildCount();
    }

    @Override
    public View getChildAt(int index) {
        if (index >= super.getChildCount()) {
            logger.d("getChildAt: %d, this is a TOP VIEW index", index);
        }
        return super.getChildAt(index);
    }

    @Override
    public int findFirstVisibleItemPosition() {
        if (enableTop && currentTopViewList != null && currentTopViewList.size() > 0) {
            SparseArray<View> viewSparseArray = findItemPositionArray();
            for (int i = 0; i < viewSparseArray.size(); i++) {
                View view = viewSparseArray.valueAt(i);
                if (mOrientationHelper.getDecoratedMeasurement(view) <= 0) {
                    continue;
                }
                int pos = viewSparseArray.keyAt(i);
                if (currentTopViewList.indexOfKey(pos) < 0) {
                    if (pos < currentTopViewList.keyAt(0)) {
                        if (view.getBottom() > 0) { // 这里认为是view本身露出一点点视为visible，而不是是view+decoration+margin
                            return pos;
                        }
                    } else {
                        return fixFirstVisibleItem(pos, false);
                    }
                }
            }
        }
        return super.findFirstVisibleItemPosition();
    }

    @Override
    public int findFirstCompletelyVisibleItemPosition() {
        if (enableTop && currentTopViewList != null && currentTopViewList.size() > 0) {
            SparseArray<View> viewSparseArray = findItemPositionArray();
            for (int i = 0; i < viewSparseArray.size(); i++) {
                View view = viewSparseArray.valueAt(i);
                if (mOrientationHelper.getDecoratedMeasurement(view) <= 0) {
                    continue;
                }
                int pos = viewSparseArray.keyAt(i);
                if (currentTopViewList.indexOfKey(pos) < 0) {
                    if (pos < currentTopViewList.keyAt(0)) {
                        if (view.getTop() >= 0) { // 这里认为是view本身全部露出视为completelyVisible，而不是是view+decoration+margin
                            return pos;
                        }
                    } else {
                        return fixFirstVisibleItem(pos, true);
                    }
                }
            }
        }

        return super.findFirstCompletelyVisibleItemPosition();
    }

    // TODO：只处理了置顶 view 在最上面的情况 未能覆盖所有场景
    protected int fixFirstVisibleItem(int hoverPos, boolean complete) {

        int nextPos = hoverPos - 1;
        if (currentTopViewList.indexOfKey(nextPos) < 0) {
            if (complete) {
                View view = findViewByPositionTraversal(hoverPos);
                int height = mOrientationHelper.getEndAfterPadding();
                if (view.getTop() >= 0) {
                    return view.getTop() + view.getMeasuredHeight() <= height ? hoverPos : RecyclerView.NO_POSITION;
                } else {
                    view = findViewByPositionTraversal(hoverPos + 1);
                    if (view != null) {
                        return view.getTop() + view.getMeasuredHeight() <= height ? hoverPos + 1 : RecyclerView.NO_POSITION;
                    } else {
                        return RecyclerView.NO_POSITION;
                    }
                }
            } else {
                return hoverPos;
            }
        }

        View view = findViewByPositionTraversal(hoverPos);
        int hoverTop = mOrientationHelper.getDecoratedStart(view);
        int pos = hoverPos;

        // 向上找
        int startIndex = currentTopViewList.indexOfKey(nextPos);
        for (int i = startIndex; i >= 0 && hoverTop > 0; i--) {
            if (currentTopViewList.keyAt(i) != pos - 1) {
                break;
            }
            view = currentTopViewList.valueAt(i);
            hoverTop -= mOrientationHelper.getDecoratedMeasurement(view);
            pos--;
        }

        int height = mOrientationHelper.getEndAfterPadding();
        if (hoverTop >= 0) {
            if (complete) {
                return hoverTop + view.getMeasuredHeight() <= height ? pos : RecyclerView.NO_POSITION;
            } else {
                return pos;
            }
        } else {
            if (complete) {
                hoverTop += view.getMeasuredHeight();
                View v = findViewByPositionTraversal(pos + 1);
                if (v == null) {
                    return RecyclerView.NO_POSITION;
                }
                return hoverTop + v.getMeasuredHeight() <= height ? pos + 1 : RecyclerView.NO_POSITION;
            } else {
                return pos;
            }
        }
    }

    @Override
    public int findLastVisibleItemPosition() {
        if (enableTop && currentTopViewList != null && currentTopViewList.size() > 0) {
            SparseArray<View> viewSparseArray = findItemPositionArray();
            for (int i = viewSparseArray.size() - 1; i >= 0; i--) {
                View view = viewSparseArray.valueAt(i);
                if (mOrientationHelper.getDecoratedMeasurement(view) <= 0) {
                    continue;
                }
                int pos = viewSparseArray.keyAt(i);
                if (currentTopViewList.indexOfKey(pos) < 0) {
                    return pos;
                }
            }
        }
        return super.findLastVisibleItemPosition();
    }

    @Override
    public int findLastCompletelyVisibleItemPosition() {
        if (enableTop && currentTopViewList != null && currentTopViewList.size() > 0) {
            SparseArray<View> viewSparseArray = findItemPositionArray();
            for (int i = viewSparseArray.size() - 1; i >= 0; i--) {
                View view = viewSparseArray.valueAt(i);
                if (mOrientationHelper.getDecoratedMeasurement(view) <= 0) {
                    continue;
                }
                int pos = viewSparseArray.keyAt(i);
                if (currentTopViewList.indexOfKey(pos) < 0
                        && mOrientationHelper.getDecoratedStart(view) >= 0
                        && mOrientationHelper.getDecoratedEnd(view) <= getHeight()) {
                    return pos;
                }
            }

            return RecyclerView.NO_POSITION;
        }
        return super.findLastCompletelyVisibleItemPosition();
    }

    public View findViewByPositionWithTop(int position) {
        return findViewByPositionTraversal(position);
    }

    @Override
    public View findViewByPosition(int position) {
        if (!enableTop) {
            return super.findViewByPosition(position);
        }

        View view = findViewByPositionTraversal(position);
        if (currentTopViewList.indexOfValue(view) >= 0) {
            return null;
        }
        return view;
    }

    public View findViewByPositionTraversal(int position) {
        final int childCount = super.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (getPosition(child) == position) {
                return child;
            }
        }
        return null;
    }

    public SparseArray<View> findItemPositionArray() {
        SparseArray<View> viewIndexArray = new SparseArray<>();
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            viewIndexArray.put(getPosition(view), view);
        }
        return viewIndexArray;
    }

    @Override
    public void setOrientation(int orientation) {
        super.setOrientation(orientation);
        mOrientationHelper = OrientationHelper.createOrientationHelper(this, orientation);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        if (!enableTop) {
            super.smoothScrollToPosition(recyclerView, state, position);
            return;
        }
        TopLinearSmoothScroller linearSmoothScroller =
                new TopLinearSmoothScroller(recyclerView.getContext(), this);
        linearSmoothScroller.setTargetPosition(position);
        startSmoothScroll(linearSmoothScroller);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (!enableTop) {
            super.onLayoutChildren(recycler, state);
            return;
        }
        if (DEBUG) {
            logger.d("onLayoutChildren");
        }
        putTopViewsBack();
        detachAndScrapCurrentTopViewList(recycler);
        super.onLayoutChildren(recycler, state);
        processTopViews(recycler, state, false);
    }

    private void putTopViewsBack() {

        Log.d("top", "put top views back");
        if (currentTopHolderList == null || currentTopHolderList.size() == 0) {
            return;
        }

        View firstView = getChildAt(0);

        if (firstView == null) {
            return;
        }

        int gap = mOrientationHelper.getDecoratedStart(firstView);

        if (gap <= 0) {
            return;
        }

        for (int pos = getPosition(firstView) - 1; pos >= 0 && gap > 0; pos--) {
            if (currentTopHolderList.indexOfKey(pos) < 0) {
                break;
            }

            TBHolder holder = currentTopHolderList.get(pos);

            if (holder.view == null) {
                continue;
            }

            removeView(holder.view);
            addView(holder.view, 0);
            layoutDecoratedWithMargins(holder.view,
                    0, gap - holder.height,
                    holder.view.getMeasuredWidth(), gap);
            gap -= holder.height;
            currentTopViewList.remove(pos);
        }

    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (!enableTop) {
            return super.scrollHorizontallyBy(dx, recycler, state);
        }

        if (DEBUG) {
            logger.d("scrollHorizontallyBy %d", dx);
        }
        detachAndScrapCurrentTopViewList(recycler);
        int res = super.scrollHorizontallyBy(dx, recycler, state);
        processTopViews(recycler, state, true);
        return res;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (!enableTop) {
            return super.scrollVerticallyBy(dy, recycler, state);
        }

        if (DEBUG) {
            logger.d("scrollVerticallyBy %d", dy);
        }
        detachAndScrapCurrentTopViewList(recycler);
        int res = super.scrollVerticallyBy(dy, recycler, state);
        processTopViews(recycler, state, true);
        return res;
    }

    /**
     * 获取所有当前处于置顶状态的 Position
     *
     * @return 所有当前处于置顶状态的 Position
     */
    public ArrayList<Integer> getCurrentTopPositionList() {
        ArrayList<Integer> topPositionList = new ArrayList<>(currentTopHolderList.size());
        for (int i = 0; i < currentTopHolderList.size(); i++) {
            topPositionList.add(currentTopHolderList.keyAt(i));
        }
        return topPositionList;
    }

    /**
     * 判断 position 当前是否在置顶状态）
     *
     * @param position
     * @return {@code true} 如果这个位置当前在置顶状态
     */
    public boolean isCurrentAtTop(int position) {
        return currentTopViewList.indexOfKey(position) >= 0;
    }

    /**
     * 判断 position 是否需要置顶（当前不一定在置顶状态）
     *
     * @param position
     * @return {@code true} 如果这个位置可以置顶（当前不一定在置顶状态）
     */
    public boolean isTopPosition(int position) {
        return topPositionList.indexOfKey(position) >= 0;
    }

    private HashMap<TBHolder, TopState> lastStateMap = new HashMap<>();
    protected void processTopViews(RecyclerView.Recycler recycler, RecyclerView.State state, boolean needPushBack) {

        tmpRecycler = recycler;
        if (DEBUG) {
            logger.d("Start processTopViews =======================");
        }

        lastTopViewHolderList.clear();
        lastStateMap.clear();
        for (int i = 0; i < currentTopHolderList.size(); i++) {
            TBHolder tbHolder = currentTopHolderList.valueAt(i);
            lastTopViewHolderList.put(currentTopHolderList.keyAt(i), tbHolder);
            lastStateMap.put(tbHolder, tbHolder.state);
        }

        topViewsInScreen.clear();
        currentTopHolderList.clear();
        int topLine = autoOffset;

        int first = findFirstVisibleItemPosition();
        int last = findLastVisibleItemPosition();
        int count = last - first + 1;

        if (DEBUG) {
            logger.d("processTopViews %s", topPositionList);
        }
        for (int i = 0; i < topPositionList.size(); i++) {
            int currentPos = topPositionList.keyAt(i);

            if (DEBUG) {
                logger.d("handleData top view %d", currentPos);
            }

            TBHolder holder = topPositionList.valueAt(i);
            int holderStart = holder.startPos;
            int holderEnd = holder.endPos;

            View firstView = findViewByPosition(first);
            if (holderEnd < first && firstView != null && firstView.getTop() <= 0) {
                continue;
            }

            View lastView = findViewByPosition(last);
            if (holderStart > last && lastView != null && lastView.getBottom() >= getHeight()) {
                continue;
            }

            holder.view = getViewByPosition(currentPos, recycler);
            if( holder.view == null ){
                continue;
            }
            // fix 防止子View RequestLayout事件被吃掉;
            holder.view.requestLayout();
            measureChildWithMargins(holder.view, 0, 0);
            int height = mOrientationHelper.getDecoratedMeasurement(holder.view);
            holder.height = height;

            // 计算 Start Position 的 View 的位置，
            int startTopPos = findInlineViewTopOrBottom(TOP, holder.startPos, currentPos, holder, first, last, recycler);

            if (DEBUG) {
                logger.d("find startTopPos %d for top view %d", startTopPos, currentPos);
            }

            // 计算 End Position 的 View 的位置，
            // 如果 End Position 已经置顶了，就向下继续找
            int endBottomPos = findInlineViewTopOrBottom(BOTTOM, holder.endPos, currentPos, holder, first, last, recycler);

            if (DEBUG) {
                logger.d("find endBottomPos %d for top view %d", endBottomPos, currentPos);
            }

            // 计算 View 置顶的标志线
            // 当使用 OVERLAY 模式的时候, 每个 View 的标志线都是 autoOffset
            // 当使用 SINGLY 模式的时候，每个 View 的标志线会依次累加
            if (topMode == Mode.OVERLAY) {
                topLine = autoOffset;
            }
            topLine += holder.offset;

            // 计算 view 是否需要置顶，
            // 如果需要置顶，同时计算 view 置顶的位置
            if (DEBUG) {
                logger.d("Calculate whether to set top : startTopPos = %d, topLine = %d and endBottomPos = %d ", startTopPos, topLine, endBottomPos);
            }

            holder.state = TopState.NORMAL;
            if (startTopPos <= topLine && endBottomPos > 0) {
                if (currentPos >= first && currentPos <= last) {
                    if (topViewsInScreen.size() >= count - 1) {

                        if (DEBUG) {
                            logger.d("NOT Add position %d to TOP with startTopPosition = %d, topLine = %d, endBottomPos = %d \n \t\t with holder %s", currentPos, startTopPos, topLine, endBottomPos, holder);
                        }
                        break;
                    }
                    topViewsInScreen.add(currentPos);
                }

                holder.layoutPosition = topLine;
                holder.state = TopState.TOP;
                if (holder.layoutPosition > endBottomPos - holder.height) {
                    holder.layoutPosition = endBottomPos - holder.height;
                    holder.state = TopState.ENDING_TOP;
                }
                // topLine 的值向下偏移 View 的高度， 以备 SINGLY 模式中使用
                topLine = holder.layoutPosition + holder.height;
                currentTopHolderList.put(currentPos, holder);

                if (DEBUG) {
                    logger.d("Add position %d to TOP with startTopPosition = %d, topLine = %d, endBottomPos = %d \n \t\t with holder %s", currentPos, startTopPos, topLine, endBottomPos, holder);
                }
            }
        }

        currentTopViewList.clear();
        for (int i = 0; i < currentTopHolderList.size(); i++) {
            currentTopViewList.put(currentTopHolderList.keyAt(i), currentTopHolderList.valueAt(i).view);
        }
        DiffResult diffResult = diffViews(lastTopViewHolderList, currentTopHolderList);

        if (diffResult != null) {
            dispatchTopStateChangeEvent(diffResult);
            if (needPushBack) {
                putViewsBack(diffResult.deleteArr, first, last, recycler);
            }
        }

        // 布局 Top View
        layoutTopViews(currentTopHolderList);

    }

    protected int findInlineViewTopOrBottom(int topOrBottom, int position, int currentPos, TBHolder currentHolder, int first, int last, RecyclerView.Recycler recycler) {
        if (position >= getItemCount()) {
            return Integer.MAX_VALUE;
        }

        if (position < 0) {
            return 0;
        }
        int topBound = autoOffset;
        int bottomBound = mOrientationHelper.getEndAfterPadding();

        int space = 0;
        if (position < first) {
            View v = findViewByPositionWithTop(first);
            if (v != null) {
                space = mOrientationHelper.getDecoratedStart(v);
                int i = first - 1;
                while (i >= position && space > topBound) {

                    if (i != position || topOrBottom == TOP) {
                        if (currentTopHolderList.indexOfKey(i) >= 0) {
                            space -= currentTopHolderList.get(i).height;
                        } else if (i == currentPos) {
                            space -= currentHolder.height;
                        } else {
                            space -= mOrientationHelper.getDecoratedMeasurement(getViewByPosition(i, recycler));
                        }
                    }
                    i--;
                }
            }
        } else if (position > last) {
            View v = findViewByPositionWithTop(last);
            if (v != null) {
                space = mOrientationHelper.getDecoratedEnd(v);
                int i = last + 1;
                while (i <= position && space < bottomBound) {
                    if (i != position || topOrBottom == BOTTOM) {
                        if (currentTopHolderList.indexOfKey(i) >= 0) {
                            space += currentTopHolderList.get(i).height;
                        } else if (i == currentPos) {
                            space += currentHolder.height;
                        } else {
                            space += mOrientationHelper.getDecoratedMeasurement(getViewByPosition(i, recycler));
                        }
                    }
                    i++;
                }
            }
        } else {
            if (position != currentPos && currentTopHolderList.indexOfKey(position) < 0) {
                if (topOrBottom == TOP) {
                    space = mOrientationHelper.getDecoratedStart(getViewByPosition(position, recycler));
                } else {
                    space = mOrientationHelper.getDecoratedEnd(getViewByPosition(position, recycler));
                }
            } else {
                // 向上找
                int hover = position;
                while (hover >= first && (currentTopHolderList.indexOfKey(hover) >= 0 || hover == currentPos)) {
                    hover--;
                }
                if (hover >= first) {
                    space = mOrientationHelper.getDecoratedEnd(getViewByPosition(hover, recycler));
                    for (int p = hover + 1; p < position; p++) {
                        if (hover == currentPos) {
                            space += currentHolder.height;
                        } else if (currentTopHolderList.indexOfKey(p) >= 0) {
                            space += currentTopHolderList.get(p).height;
                        }
                    }
                    if (topOrBottom == BOTTOM) {
                        if (position == currentPos) {
                            space += currentHolder.height;
                        } else if (currentTopHolderList.indexOfKey(position) >= 0) {
                            space += currentTopHolderList.get(position).height;
                        }
                    }
                } else {
                    // 向下找
                    hover = position;
                    while (hover <= last && (currentTopHolderList.indexOfKey(hover) >= 0 || hover == currentPos)) {
                        hover++;
                    }
                    if (hover >= getItemCount()) {
                        return 0;
                    }

                    space = mOrientationHelper.getDecoratedStart(getViewByPosition(hover, recycler));
                    for (int k = hover - 1; k > position; k--) {
                        if (k == currentPos) {
                            space -= currentHolder.height;
                        } else if (currentTopViewList.indexOfKey(k) >= 0) {
                            space -= currentTopHolderList.get(k).height;
                        }
                    }
                    if (topOrBottom == TOP) {
                        if (position == currentPos) {
                            space -= currentHolder.height;
                        } else if (currentTopViewList.indexOfKey(position) >= 0) {
                            space -= currentTopHolderList.get(position).height;
                        }
                    }
                }
            }
        }
        return space;
    }

    protected void layoutTopViews(SparseArray<TBHolder> holderArr) {
        coveredY = 0;
        if (holderArr != null) {
            SparseArray<SparseArray<TBHolder>> holderBucketArr = new SparseArray<>();
            for (int j = 0; j < holderArr.size(); j++) {
                int pos = holderArr.keyAt(j);
                TBHolder holder = holderArr.valueAt(j);
                SparseArray<TBHolder> bucket = holderBucketArr.get(holder.zPosition);
                if (bucket == null) {
                    bucket = new SparseArray<>();
                    holderBucketArr.put(holder.zPosition, bucket);
                }
                bucket.put(pos, holder);
            }

            if (holderBucketArr.size() > 0) {
                for (int k = 0; k < holderBucketArr.size(); k++) {
                    SparseArray<TBHolder> bucket = holderBucketArr.valueAt(k);
                    for (int t = bucket.size() - 1; t >= 0; t--) {
                        int pos = bucket.keyAt(t);
                        TBHolder holder = bucket.valueAt(t);
                        layoutTopView(pos, holder);
                        if (holder.layoutPosition + holder.height > coveredY) {
                            coveredY = holder.layoutPosition + holder.height;
                        }
                    }
                }
            }
        }
    }

    protected void dispatchTopStateChangeEvent(DiffResult diffResult) {

        if (diffResult == null) {
            return;
        }

        dispatchTopStageChangeEvent(diffResult.changedArr);
    }

    protected void dispatchTopStageChangeEvent(SparseArray<TBHolder> array) {
        if (array == null || array.size() == 0) {
            return;
        }

        int count = array.size();
        for (int i = 0; i < count; i++) {
            int pos = array.keyAt(i);
            TBHolder holder = array.valueAt(i);
            logger.i("Dispatch top state change event for position %d with top stage = %s", pos, holder.state);
            callOnViewTopStateChanged(holder.state, pos, holder.view);
        }
    }

    protected void putViewsBack(SparseArray<TBHolder> backArr, int first, int last, RecyclerView.Recycler recycler) {

        if (backArr == null || backArr.size() == 0) {
            return;
        }

        int hoverPos = first;
        int index = 0;
        while ((backArr.indexOfKey(hoverPos) >= 0 || currentTopViewList.indexOfKey(hoverPos) >= 0) && hoverPos <= last) {
            hoverPos++;
            index++;
        }

        // 从 hover 向下
        View hoverView = findViewByPositionWithTop(hoverPos);
        int startLine = 0;
        int pos = hoverPos + 1;
        if (hoverView != null) {
            startLine = mOrientationHelper.getDecoratedEnd(hoverView);
        }
        int end = mOrientationHelper.getEndAfterPadding();
        while (startLine < end && pos < getItemCount() && backArr.size() > 0) {
            View view = null;
            boolean isInBackArr = false;
            int height = 0;
            if (backArr.indexOfKey(pos) >= 0) {
                isInBackArr = true;
                TBHolder holder = backArr.get(pos);
                height = holder.height;
                view = holder.view;
            } else {
                view = getViewByPosition(pos, recycler);
                height = mOrientationHelper.getDecoratedMeasurement(view);
            }

            if (currentTopViewList.indexOfKey(pos) < 0 && (isInBackArr || pos > last)) {
                addView(view, index);
                layoutDecoratedWithMargins(view, 0, startLine, view.getMeasuredWidth(), startLine + height);
            }
            backArr.remove(pos);
            startLine += height;
            pos++;
            index++;
        }

        // 从 hover 向上
        pos = hoverPos - 1;
        startLine = mOrientationHelper.getEndAfterPadding();
        if (hoverView != null) {
            startLine = mOrientationHelper.getDecoratedStart(hoverView);
        }
        int top = mOrientationHelper.getStartAfterPadding();
        while (startLine > top && pos >= 0 && backArr.size() > 0) {
            View view = null;
            boolean isInBackArr = false;
            int height = 0;
            if (backArr.indexOfKey(pos) >= 0) {
                isInBackArr = true;
                TBHolder holder = backArr.get(pos);
                height = holder.height;
                view = holder.view;
            } else {
                view = getViewByPosition(pos, recycler);
                height = mOrientationHelper.getDecoratedMeasurement(view);
            }

            if (currentTopViewList.indexOfKey(pos) < 0 && (isInBackArr || pos > last)) {
                addView(view, 0);
                layoutDecoratedWithMargins(view, 0, startLine - height, view.getMeasuredWidth(), startLine);
            }

            backArr.remove(pos);
            startLine -= height;
            pos--;
        }

    }

    protected void callOnViewTopStateChanged(TopState topState, int position, View view) {
        if (topStateChangedListenerArrayList != null && !topStateChangedListenerArrayList.isEmpty()) {
            for (OnViewTopStateChangeListener listener : topStateChangedListenerArrayList) {
                listener.onViewTopStateChanged(topState, position, view);
            }
        }

        if (topViewLayoutChangeListenerArrayList != null && !topViewLayoutChangeListenerArrayList.isEmpty()) {
            for (OnTopViewLayoutChangeListener listener : topViewLayoutChangeListenerArrayList) {
                listener.onLayoutLocationChangeListener(view, position,
                        topState == TopState.NORMAL ? STATUS_FOLLOW : STATUS_TOP,
                        (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) ? (ViewGroup.MarginLayoutParams) view.getLayoutParams() : null);
            }
        }
    }

    protected DiffResult diffViews(SparseArray<TBHolder> lastViewHolderArr, SparseArray<TBHolder> currentViewHolderArr) {
        DiffResult result = new DiffResult();
        if (lastViewHolderArr == null || lastViewHolderArr.size() == 0) {
            result.topArr = currentViewHolderArr;
            result.changedArr = currentViewHolderArr;
            int count = currentViewHolderArr.size();
            for (int i = 0; i < count; i++) {
                result.topArr.valueAt(i).state = TopState.TOP;
            }
            return result;
        }

        if (currentViewHolderArr == null || currentViewHolderArr.size() == 0) {
            result.deleteArr = lastViewHolderArr;
            result.changedArr = lastViewHolderArr;
            int count = lastViewHolderArr.size();
            for (int j = 0; j < count; j++) {
                result.deleteArr.valueAt(j).state = TopState.NORMAL;
            }
            return result;
        }

        result.changedArr = new SparseArray<>();
        result.topArr = new SparseArray<>();
        result.deleteArr = new SparseArray<>();
        result.endingArr = new SparseArray<>();

        // Traverse both arrays simultaneously.
        int i = 0, j = 0;
        int n = lastViewHolderArr.size();
        int m = currentViewHolderArr.size();
        while (i < n && j < m) {
            // Print smaller element and move
            // ahead in array with smaller element
            int lastCursor = lastViewHolderArr.keyAt(i);
            int currentCursor = currentViewHolderArr.keyAt(j);
            if (lastCursor < currentCursor) {
                TBHolder holder = lastViewHolderArr.valueAt(i);
                holder.state = TopState.NORMAL;
                result.changedArr.put(lastCursor, holder);
                result.deleteArr.put(lastCursor, holder);
                i++;
            } else if (lastCursor > currentCursor) {
                TBHolder holder = currentViewHolderArr.valueAt(j);
                if (holder.state == TopState.TOP) {
                    result.topArr.put(currentCursor, holder);
                } else if (holder.state == TopState.ENDING_TOP) {
                    result.endingArr.put(currentCursor, holder);
                }

                result.changedArr.put(currentCursor, holder);
                j++;
            } else {
                // If both elements same, move ahead
                // in both arrays.
                TBHolder lastHolder = lastViewHolderArr.valueAt(i);
                TopState lastState = lastStateMap.get(lastHolder);
                TBHolder holder = currentViewHolderArr.valueAt(j);
                if (holder.state != lastState) {
                    if (holder.state == TopState.TOP) {
                        result.topArr.put(currentCursor, holder);
                    } else if (holder.state == TopState.ENDING_TOP) {
                        result.endingArr.put(currentCursor, holder);
                    }
                    result.changedArr.put(currentCursor, holder);
                }
                i++;
                j++;
            }
        }

        while (i < n) {
            int iKey = lastViewHolderArr.keyAt(i);
            TBHolder iValue = lastViewHolderArr.valueAt(i);
            result.deleteArr.put(iKey, iValue);
            result.changedArr.put(iKey, iValue);
            i++;
        }
        while (j < m) {
            int jKey = currentViewHolderArr.keyAt(j);
            TBHolder jValue = currentViewHolderArr.valueAt(j);
            result.topArr.put(jKey, jValue);
            result.changedArr.put(jKey, jValue);
            j++;
        }
        return result;
    }

    protected void layoutTopView(int position, TBHolder holder) {
//        currentTopViewList.put(position, holder.view);
        addView(holder.view);
        if (DEBUG) {
            logger.d("layout %d top view at [%d, %d - %d, %d]", position, 0, holder.layoutPosition, getWidth(), holder.layoutPosition + holder.height);
        }
        layoutDecoratedWithMargins(holder.view, 0, holder.layoutPosition, getWidth(), holder.layoutPosition + holder.height);
    }

    protected View getViewByPosition(int position, RecyclerView.Recycler recycler) {
        View v = findViewByPositionWithTop(position);
        if (v == null) {
            v = getViewFromRecycler(position, recycler);
        }

        return v;
    }

    protected View getViewFromRecycler(int position, RecyclerView.Recycler recycler) {
        if (recycler == null) {
            return null;
        }

        View view = getViewFromScrapList(position, recycler);
        if (view == null && position < getItemCount()) {
            view = recycler.getViewForPosition(position);
        }

        return view;
    }

    protected View getViewFromScrapList(int position, RecyclerView.Recycler recycler) {
        if (recycler == null) {
            return null;
        }
        List<RecyclerView.ViewHolder> scrapList = recycler.getScrapList();

        final int size = scrapList.size();
        for (int i = 0; i < size; i++) {
            RecyclerView.ViewHolder holder = scrapList.get(i);
            if (holder == null) {
                continue;
            }
            final View view = scrapList.get(i).itemView;

            ViewGroup.LayoutParams vglp = view.getLayoutParams();
            if (!(vglp instanceof RecyclerView.LayoutParams)) {
                continue;
            }

            final RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) vglp;
            if (lp.isItemRemoved()) {
                continue;
            }
            if (position == lp.getViewLayoutPosition()) {
                return view;
            }
        }
        return null;
    }

    protected void detachAndScrapCurrentTopViewList(RecyclerView.Recycler recycler) {
        if (currentTopViewList == null || currentTopViewList.size() <= 0) {
            return;
        }
        for (int i = 0; i < currentTopViewList.size(); i++) {
            View view = currentTopViewList.valueAt(i);
            detachAndScrapView(view, recycler);
        }

        currentTopViewList.clear();
    }

    /**
     * {@link Mode#SINGLY} View 会一个接一个连续排列，
     * {@link Mode#OVERLAY} 置顶的 View 会互相重叠在一起
     */
    public enum Mode {
        SINGLY,
        OVERLAY
    }

    /**
     * * 结束置顶的模式
     * <ul>
     * <li>{@link TopState#NORMAL}  表示处于正常非置顶状态</li>
     * <li>{@link TopState#TOP}  表示处于置顶状态</li>
     * <li>{@link TopState#ENDING_TOP}  表示 View 正在跟随着结束位置移出屏幕</li>
     * </ul>
     */
    public enum TopState {
        /**
         * 表示处于正常非置顶状态
         */
        NORMAL,

        /**
         * 表示处于置顶状态
         */
        TOP,

        /**
         * 表示 View 正在跟随着结束位置移出屏幕
         */
        ENDING_TOP
    }

    public interface OnViewTopStateChangeListener {

        void onViewTopStateChanged(TopState topState, int position, View view);
    }

    protected static class DiffResult {

        SparseArray<TBHolder> changedArr;
        SparseArray<TBHolder> topArr;
        SparseArray<TBHolder> deleteArr;
        SparseArray<TBHolder> endingArr;
    }

    protected static class TBHolder {

        int offset = 0;
        int zPosition = 0;

        int startPos = 0;
        int endPos = Integer.MAX_VALUE;
        View view;

        int layoutPosition = 0;
        int height;
        TopState state;

        /**
         * @param startPos  不能是置顶的 Pos
         * @param endPos    不能是置顶的 Pos
         * @param offset
         * @param zPosition
         */
        public TBHolder(int startPos, int endPos, int offset, int zPosition) {
            this.offset = offset;
            this.zPosition = zPosition;
            this.startPos = startPos;
            this.endPos = endPos;
        }

        @Override
        public String toString() {
            return "TBHolder{" +
                    "offset=" + offset +
                    ", zPosition=" + zPosition +
                    ", startPos=" + startPos +
                    ", endPos=" + endPos +
                    ", view=" + view +
                    ", layoutPosition=" + layoutPosition +
                    ", height=" + height +
                    '}';
        }
    }

    public static class TopLinearSmoothScroller extends LinearSmoothScroller {

        protected int actualPosition;
        protected int targetPos;
        protected int topOffset;
        protected LinearLayoutManager llm;
        protected TopLinearLayoutManager tllm;
        protected RecyclerView.Recycler recycler;

        public TopLinearSmoothScroller(Context context, @NonNull LinearLayoutManager llm) {
            super(context);
            this.llm = llm;
            if (this.llm instanceof TopLinearLayoutManager) {
                tllm = (TopLinearLayoutManager) this.llm;
                recycler = tllm.tmpRecycler;
            }
        }

        @Override
        public void setTargetPosition(int targetPosition) {
            targetPos = targetPosition;
            actualPosition = targetPosition;
            if (tllm != null && tllm.getOrientation() == VERTICAL) {
                while (tllm.topPositionList.indexOfKey(actualPosition) >= 0 && actualPosition < tllm.getItemCount()) {
                    View v = tllm.findViewByPositionWithTop(actualPosition);
                    if (v == null) {
                        v = tllm.getViewFromRecycler(actualPosition, recycler);
                        tllm.measureChild(v, 0, 0);
                    }
                    topOffset += tllm.mOrientationHelper.getDecoratedMeasurement(v);
                    actualPosition++;
                }
                if (tllm.topPositionList.indexOfKey(actualPosition) >= 0) {
                    actualPosition = targetPosition;
                    while (tllm.currentTopViewList.indexOfKey(actualPosition) >= 0 && actualPosition >= 0) {
                        View v = tllm.findViewByPositionWithTop(actualPosition);
                        if (v == null) {
                            v = tllm.getViewFromRecycler(actualPosition, recycler);
                            tllm.measureChild(v, 0, 0);
                        }
                        topOffset -= tllm.mOrientationHelper.getDecoratedMeasurement(v);
                        actualPosition--;
                    }
                }
            }

            super.setTargetPosition(actualPosition);
        }

        public int getTopOffset() {
            return topOffset;
        }

        @Override
        protected void onTargetFound(View targetView, RecyclerView.State state, Action action) {

            final int dx = calculateDxToMakeVisible(targetView, getHorizontalSnapPreference());
            final int dy = calculateDyToMakeVisible(targetView, getVerticalSnapPreference()) + getTopOffset();
            final int distance = (int) Math.sqrt(dx * dx + dy * dy);
            final int time = calculateTimeForDeceleration(distance);
            if (time > 0) {
                action.update(-dx, -dy, time, mDecelerateInterpolator);
            }
        }
    }
}
