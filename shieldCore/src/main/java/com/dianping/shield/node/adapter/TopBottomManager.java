package com.dianping.shield.node.adapter;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.dianping.shield.node.cellnode.InnerTopInfo;
import com.dianping.shield.node.cellnode.ShieldDisplayNode;
import com.dianping.shield.node.useritem.TopState;
import com.dianping.shield.utils.ShieldObjectsUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by runqi.wei at 2018/6/21
 */
public class TopBottomManager {

    private static final boolean DEBUG = false;

    protected static final int TOP = 0;
    protected static final int BOTTOM = 1;
    private FrameLayout topContainer;
    private LinearLayout bottomContainer;
    private RecyclerView recyclerView;
    private SparseArray<ShieldDisplayNode> topNodeList = new SparseArray<>();
    private SparseArray<ShieldDisplayNode> bottomNodeList = new SparseArray<>();
    private SparseArray<TopBottomNodeInfo> currentTopNodeList = new SparseArray<>();
    private SparseArray<TopBottomNodeInfo> currentBottomNodeList = new SparseArray<>();

    private HashMap<ShieldDisplayNode, TopBottomNodeInfo> nodeInfoMap = new HashMap<>();

    private Context mContext;

    public TopBottomManager(Context mContext) {
        this.mContext = mContext;
    }

    public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        updateTopBottomViews();
    }

    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        updateTopBottomViews();
    }

    public void updateTopBottomViews() {

        SparseArray<TopBottomNodeInfo> lastTopNodeList = currentTopNodeList;
        currentTopNodeList = new SparseArray<>();

        SparseArray<TopBottomNodeInfo> newTopArr = new SparseArray<>();
        SparseArray<TopBottomNodeInfo> newEndingArr = new SparseArray<>();

        SparseArray<TopBottomNodeInfo> lastBottomNodeList = currentBottomNodeList;
        currentBottomNodeList = new SparseArray<>();
        SparseArray<TopBottomNodeInfo> newBottomArr = new SparseArray<>();

        if (recyclerView != null) {
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            Pair<Integer, Integer> firstAndLast = findFirstAndLast(layoutManager);
            int first = firstAndLast.first;
            int last = firstAndLast.second;

            processTopNode(lastTopNodeList, newTopArr, newEndingArr, layoutManager, first, last);
//            processBottomNode(lastBottomNodeList, newBottomArr, layoutManager, first, last);
        }
        updateTopViews();
//        updateBottomViews();
        for (int j = 0; j < lastTopNodeList.size(); j++) {
            TopBottomNodeInfo nodeInfo = lastTopNodeList.valueAt(j);
            nodeInfo.state = TopState.NORMAL;
            removeFromTop(nodeInfo.node);
        }

        for (int nt = 0; nt < newTopArr.size(); nt++) {
            TopBottomNodeInfo info = newTopArr.valueAt(nt);
            notifyTopStateChanged(info);
        }

        for (int ne = 0; ne < newEndingArr.size(); ne++) {
            TopBottomNodeInfo info = newEndingArr.valueAt(ne);
            notifyTopStateChanged(info);
        }

        for (int nd = 0; nd < lastTopNodeList.size(); nd++) {
            TopBottomNodeInfo info = lastTopNodeList.valueAt(nd);
            notifyTopStateChanged(info);
        }

    }

    private void processTopNode(SparseArray<TopBottomNodeInfo> lastTopNodeList, SparseArray<TopBottomNodeInfo> newTopArr, SparseArray<TopBottomNodeInfo> newEndingArr, RecyclerView.LayoutManager layoutManager, int first, int last) {
        for (int i = 0; i < topNodeList.size(); i++) {

            int pos = topNodeList.keyAt(i);

            ShieldDisplayNode node = topNodeList.valueAt(i);
            if (node == null) {
                continue;
            }

            InnerTopInfo innerTopInfo = node.innerTopInfo;
            if (innerTopInfo == null) {
                continue;
            }

            int line = innerTopInfo.offset;
            int startTop = getTopOrBottomPosition(TOP, layoutManager, innerTopInfo.startPos, first, last);
            int endBottom = getTopOrBottomPosition(BOTTOM, layoutManager, innerTopInfo.endPos, first, last);

            invalidateView(pos, node);

            TopBottomNodeInfo info = getTopNodeInfo(pos, node, line, endBottom);

            if (isTop(line, startTop, endBottom, node)) {
                if (isEnding(line, endBottom, info.height)) {
                    if (info.state != TopState.ENDING) {
                        info.state = TopState.ENDING;
                        newEndingArr.put(pos, info);
                    }
                } else {
                    if (info.state != TopState.TOP) {
                        info.state = TopState.TOP;
                        newTopArr.put(pos, info);
                    }
                }
                currentTopNodeList.put(pos, info);
                lastTopNodeList.remove(pos);
            }
        }
    }

    private void processBottomNode(SparseArray<TopBottomNodeInfo> lastBottomNodeList, SparseArray<TopBottomNodeInfo> newBottomArr, RecyclerView.LayoutManager layoutManager, int first, int last) {
        for (int i = 0; i < bottomNodeList.size(); i++) {
            ShieldDisplayNode node = bottomNodeList.valueAt(i);

            int pos = bottomNodeList.keyAt(i);

            if (node == null) {
                continue;
            }

            invalidateView(pos, node);

            if (node.view != null) {
                ViewGroup.LayoutParams lp = node.view.getLayoutParams();
                if (!(lp instanceof LinearLayout.LayoutParams)) {
                    node.view.setLayoutParams(new LinearLayout.LayoutParams(lp.width, lp.height));
                }
            }
            TopBottomNodeInfo info = new TopBottomNodeInfo();
            info.position = pos;
            info.node = node;
            info.height = node.view.getMeasuredHeight();

            currentBottomNodeList.put(pos, info);
            lastBottomNodeList.remove(pos);
        }

    }

    @NonNull
    private TopBottomNodeInfo getTopNodeInfo(int pos, ShieldDisplayNode node, int line, int endBottom) {
        TopBottomNodeInfo info = nodeInfoMap.get(node);
        if (info == null) {
            info = new TopBottomNodeInfo();
            nodeInfoMap.put(node, info);
        }
        info.position = pos;
        info.node = node;
        info.height = node.view.getMeasuredHeight();
        info.zPosition = node.innerTopInfo.zPosition;
        info.startLine = line;
        info.endLine = endBottom;
        return info;
    }

    public void requestUpdate() {
        updatedNode.clear();
    }

    private ArrayList<ShieldDisplayNode> updatedNode = new ArrayList<>();

    private void invalidateView(int pos, ShieldDisplayNode node) {
        if (node.view == null) {
            node.view = createTopView(pos, node);
        }

        if (!updatedNode.contains(node)) {
            updateNodeView(pos, node);
            ViewGroup.LayoutParams lp = node.view.getLayoutParams();
            if (lp == null) {
                lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                node.view.setLayoutParams(lp);
            }
            measureTopView(node, lp);

            updatedNode.add(node);
        }
    }

    private void notifyTopStateChanged(TopBottomNodeInfo info) {
        if (info.node != null && info.node.innerTopInfo != null && info.node.innerTopInfo.listener != null) {
            info.node.innerTopInfo.listener.onTopStateChanged(info.node, info.state);
        }
    }

    private View createTopView(int pos, ShieldDisplayNode node) {
        View view = node.viewPaintingCallback.onCreateView(mContext, null, node.viewType);
        node.view = view;
        node.viewPaintingCallback.updateView(view, node, node.getPath());
        return view;
    }

    private void updateNodeView(int pos, ShieldDisplayNode node) {
        node.viewPaintingCallback.updateView(node.view, node, node.getPath());
    }

    private void measureTopView(ShieldDisplayNode node, ViewGroup.LayoutParams lp) {
        if (topContainer == null) {
            return;
        }
        int parentWidth = topContainer.getMeasuredWidth();
        int parentWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(parentWidth, View.MeasureSpec.AT_MOST);
        int parentHeight = topContainer.getMeasuredHeight();
        int parentHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(parentHeight, View.MeasureSpec.UNSPECIFIED);
        final int childWidthMeasureSpec = ViewGroup.getChildMeasureSpec(parentWidthMeasureSpec,
                0, lp.width);
        final int childHeightMeasureSpec = ViewGroup.getChildMeasureSpec(parentHeightMeasureSpec,
                0, lp.height);
        node.view.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    protected boolean isTop(int line, int startTop, int endBottom, ShieldDisplayNode node) {
        return startTop <= line && endBottom > 0;
    }

    protected boolean isEnding(int line, int endBottom, int height) {
        return endBottom - height < line;
    }

    protected int getTopOrBottomPosition(int side, RecyclerView.LayoutManager layoutManager, int position, int first, int last) {
        if (position < first) {
            return Integer.MIN_VALUE;
        } else if (position > last) {
            return Integer.MAX_VALUE;
        } else {
            View view = layoutManager.findViewByPosition(position);
            if (side == TOP) {
                return view.getTop();
            } else if (side == BOTTOM) {
                return view.getBottom();
            } else {
                return 0;
            }
        }
    }

    protected Pair<Integer, Integer> findFirstAndLast(RecyclerView.LayoutManager layoutManager) {
        int first = -1;
        int last = -1;
        if (layoutManager instanceof LinearLayoutManager) {
            first = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
            last = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int[] firstItemArr = ((StaggeredGridLayoutManager) layoutManager).findFirstVisibleItemPositions(null);
            if (firstItemArr != null && firstItemArr.length > 0) {
                first = firstItemArr[0];
            }
            int[] lastItemArr = ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(null);
            if (lastItemArr != null && lastItemArr.length > 0) {
                last = lastItemArr[lastItemArr.length - 1];
            }
        }

        return new Pair<>(first, last);
    }

    public void setTopContainer(FrameLayout topContainer) {
        this.topContainer = topContainer;
        requestUpdate();
    }

    public void setBottomContainer(LinearLayout bottomContainer) {
        this.bottomContainer = bottomContainer;
        requestUpdate();
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        if (this.recyclerView == null) {
            clearTop();
            clearBottom();
        }
    }

    public void clearTop() {
        this.topNodeList.clear();
        requestUpdate();
    }

    public void setTopNodeList(SparseArray<ShieldDisplayNode> topNodeList) {
        this.topNodeList.clear();
        for (int i = 0; i < topNodeList.size(); i++) {
            if (topNodeList.valueAt(i) != null) {
                this.topNodeList.put(topNodeList.keyAt(i), topNodeList.valueAt(i));
            }
        }
        requestUpdate();
    }

    public void clearBottom() {
        this.bottomNodeList.clear();
        requestUpdate();
    }

    public void setBottomNodeList(SparseArray<ShieldDisplayNode> bottomNodeList) {
        this.bottomNodeList.clear();
        for (int i = 0; i < bottomNodeList.size(); i++) {
            if (bottomNodeList.valueAt(i) != null) {
                this.bottomNodeList.put(bottomNodeList.keyAt(i), bottomNodeList.valueAt(i));
            }
        }

        requestUpdate();
    }

    public int findPositionOf(ShieldDisplayNode displayNode) {
        int index = this.topNodeList.indexOfValue(displayNode);
        if (index >= 0) {
            return this.topNodeList.keyAt(index);
        }

        return -1;
    }

    private void updateTopViews() {

        if (topContainer == null) {
            return;
        }
        ArrayList<View> inScreenTopViews = new ArrayList<>();
        for (int i = 0; i < topContainer.getChildCount(); i++) {
            inScreenTopViews.add(topContainer.getChildAt(i));
        }

        if (currentTopNodeList != null && currentTopNodeList.size() > 0) {
            SparseArray<ArrayList<TopBottomNodeInfo>> topBucketLst = sortTopNodes();

            for (int j = 0; j < topBucketLst.size(); j++) {
                int pos = topNodeList.keyAt(j);
                ArrayList<TopBottomNodeInfo> bucket = topBucketLst.valueAt(j);
                for (int k = bucket.size() - 1; k >= 0; k--) {
                    TopBottomNodeInfo info = bucket.get(k);
                    inScreenTopViews.remove(info.node.view);
                    layoutToTopContainer(pos, info.node, info.startLine, info.endLine, info.height);
                }
            }
        }

        if (!inScreenTopViews.isEmpty()) {
            for (int i = 0; i < inScreenTopViews.size(); i++) {
                topContainer.removeView(inScreenTopViews.get(i));
            }
        }
    }

    private void updateBottomViews() {

        if(Thread.currentThread() != Looper.getMainLooper().getThread()) {
            Log.w("TopBottomManager", "layout must be in Main Thread!!!", new Exception());
            return;
        }
        
        if (bottomContainer == null) {
            return;
        }

        ArrayList<View> inScreenBottomViews = new ArrayList<>();
        for (int i = 0; i < bottomContainer.getChildCount(); i++) {
            inScreenBottomViews.add(bottomContainer.getChildAt(i));
        }

        if (currentBottomNodeList != null && currentBottomNodeList.size() > 0) {
            for (int j = 0; j < currentBottomNodeList.size(); j++) {
                TopBottomNodeInfo info = currentBottomNodeList.valueAt(j);
                inScreenBottomViews.remove(info.node.view);
                if (info.node.view.getParent() instanceof ViewGroup) {
                    ((ViewGroup) info.node.view.getParent()).removeView(info.node.view);
                }

                bottomContainer.addView(info.node.view);
            }
        }

        if (!inScreenBottomViews.isEmpty()) {
            for (int i = 0; i < inScreenBottomViews.size(); i++) {
                bottomContainer.removeView(inScreenBottomViews.get(i));
            }
        }
    }

    @NonNull
    private SparseArray<ArrayList<TopBottomNodeInfo>> sortTopNodes() {
        SparseArray<ArrayList<TopBottomNodeInfo>> topBucketLst = new SparseArray<>();

        for (int i = 0; i < currentTopNodeList.size(); i++) {
            TopBottomNodeInfo topNodeInfo = currentTopNodeList.valueAt(i);
            ArrayList<TopBottomNodeInfo> bucket = topBucketLst.get(topNodeInfo.zPosition);
            if (bucket == null) {
                bucket = new ArrayList<>();
            }
            topBucketLst.put(topNodeInfo.zPosition, bucket);
            bucket.add(topNodeInfo);
        }
        return topBucketLst;
    }

    protected void layoutToTopContainer(int pos, ShieldDisplayNode node, int top, int bottom, int height) {
        if(Thread.currentThread() != Looper.getMainLooper().getThread()) {
            Log.w("TopBottomManager", "layout must be in Main Thread!!!", new Exception());
            return;
        }

        if (topContainer != null && node != null) {

            if (node.view.getParent() instanceof ViewGroup) {
                ((ViewGroup) node.view.getParent()).removeView(node.view);
            }

            ViewGroup.LayoutParams vlp = node.view.getLayoutParams();
            FrameLayout.LayoutParams lp;
            if (vlp == null) {
                lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            } else {
                lp = new FrameLayout.LayoutParams(vlp.width, vlp.height);
                if (vlp instanceof ViewGroup.MarginLayoutParams) {
                    lp.leftMargin = ((ViewGroup.MarginLayoutParams) vlp).leftMargin;
                    lp.rightMargin = ((ViewGroup.MarginLayoutParams) vlp).rightMargin;
                    lp.topMargin = ((ViewGroup.MarginLayoutParams) vlp).topMargin;
                    lp.bottomMargin = ((ViewGroup.MarginLayoutParams) vlp).bottomMargin;
                }
            }
            topContainer.addView(node.view, lp);
            node.view.setTranslationY(Math.min(top, bottom - height));

            if (DEBUG) {
                Log.d("top_view", String.format("layout to top of node %s, path %s, view %s", node, node.getPath(), node.view));
            }
        }
    }

    protected void removeFromTop(ShieldDisplayNode node) {
        if (topContainer != null && node != null && node.view != null && node.view.getParent() == topContainer) {
            if (DEBUG) {
                Log.d("top_view", String.format("remove from top of node %s, path %s, view %s", node, node.getPath(), node.view));
            }
            topContainer.removeView(node.view);
        }
        if (node.containerView != null
                && ShieldDisplayNode.Companion.contentsEquals(node.containerView.getNode(), node)) {
            node.containerView.setSubView(node.view);
            if (node.view != null) {
                node.view.setTranslationY(0);
            }
        }
    }

    private static class TopBottomNodeInfo {

        ShieldDisplayNode node;
        int position = 0;
        int zPosition = 0;
        int startLine = 0;
        int endLine = 0;
        int height = 0;
        TopState state = TopState.NORMAL;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TopBottomNodeInfo that = (TopBottomNodeInfo) o;
            return ShieldObjectsUtils.equals(node, that.node);
        }

        @Override
        public int hashCode() {
            return ShieldObjectsUtils.hash(node);
        }
    }
}
