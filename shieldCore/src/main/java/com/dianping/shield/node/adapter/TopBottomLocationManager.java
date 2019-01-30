package com.dianping.shield.node.adapter;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.dianping.agentsdk.pagecontainer.SetAutoOffsetInterface;
import com.dianping.shield.entity.ScrollDirection;
import com.dianping.shield.framework.ZFrameLayout;
import com.dianping.shield.node.cellnode.InnerBottomInfo;
import com.dianping.shield.node.cellnode.InnerTopInfo;
import com.dianping.shield.node.cellnode.ShieldDisplayNode;
import com.dianping.shield.node.useritem.BottomState;
import com.dianping.shield.node.useritem.TopState;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by runqi.wei at 2018/11/7
 */
public class TopBottomLocationManager extends ViewLocationChangeProcessor implements SetAutoOffsetInterface {

    protected static final boolean DEBUG = false;

    protected static final int TOP = 0;
    protected static final int BOTTOM = 1;
    protected int topOffset;
    protected int bottomOffset;
    protected FrameLayout topContainer;
    protected FrameLayout bottomContainer;
    protected int singlyBottomOffset = 0;
    protected RecyclerView recyclerView;
    String TAG = this.getClass().getSimpleName();
    private SparseArray<ShieldDisplayNode> topNodeList = new SparseArray<>();//设置的置顶节点
    private SparseArray<ShieldDisplayNode> currentTopNodeList = new SparseArray<>();//当前已置顶的节点
    private SparseArray<ShieldDisplayNode> bottomNodeList = new SparseArray<>();//设置的置底节点
    private SparseArray<ShieldDisplayNode> currentBottomNodeList = new SparseArray<>();//当前已置底的节点
    private HashMap<ShieldDisplayNode, TopBottomNodeInfo> nodeInfoMap = new HashMap<>();
    private HashMap<ShieldDisplayNode, Integer> nodeHeightMap = new HashMap<>();

    public TopBottomLocationManager(int top, int bottom) {
        super(bottom, top);
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public void clearTop() {
        this.topNodeList.clear();
        requestUpdate();
    }

    public void setTopNodeList(SparseArray<ShieldDisplayNode> topNodeList) {

        if (DEBUG) {
            Log.d(TAG, "setTopNodeList: topNodeList.size = " + topNodeList.size());
        }

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

    @Override
    public void onViewLocationChanged(ScrollDirection scrollDirection) {
        if(DEBUG) {
            Log.d("<<<<<<", "Millis: " + System.currentTimeMillis());
        }
        if (topNodeList != null) {
            SparseArray<ShieldDisplayNode> newTopArr = new SparseArray<>();
            SparseArray<ShieldDisplayNode> newEndingArr = new SparseArray<>();
            SparseArray<ShieldDisplayNode> lastTopNodeList = currentTopNodeList;
            currentTopNodeList = new SparseArray<>();

            processTopNodes(lastTopNodeList, newTopArr, newEndingArr);
            if (DEBUG) {
                Log.d(TAG, currentTopNodeList != null ? currentTopNodeList.toString() : "");
            }
            layoutTopNodes();
            dispatchTopNodeStatusChanged(lastTopNodeList, newTopArr, newEndingArr);
        }

        if (bottomNodeList != null) {

            SparseArray<ShieldDisplayNode> newBottomArr = new SparseArray<>();
            SparseArray<ShieldDisplayNode> newBottomEndingArr = new SparseArray<>();
            SparseArray<ShieldDisplayNode> lastBottomNodeList = currentBottomNodeList;
            currentBottomNodeList = new SparseArray<>();
            processBottomNodes(lastBottomNodeList, newBottomArr, newBottomEndingArr);
            layoutBottomNodes();
//            dispatchBottomNodeStatusChanged();
        }

    }

    @Override
    public void clear() {
        super.clear();
        clearTop();
        clearBottom();
    }

    private void processTopNodes(SparseArray<ShieldDisplayNode> lastTopNodeList,
                                 SparseArray<ShieldDisplayNode> newTopArr,
                                 SparseArray<ShieldDisplayNode> newEndingArr) {

        if (firstLastPositionInfo != null && !firstLastPositionInfo.isEmpty()) {
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
                if (innerTopInfo.needAutoOffset) {
                    line += getAutoOffset();
                }
                int startTop = Integer.MIN_VALUE;
                if (innerTopInfo.startPos >= 0) {
                    startTop = getTopOrBottomPosition(TOP, innerTopInfo.startPos);
                }
                int endBottom = Integer.MAX_VALUE;
                if (innerTopInfo.endPos >= 0) {
                    endBottom = getTopOrBottomPosition(BOTTOM, innerTopInfo.endPos);
                }
                if (DEBUG) {
                    Log.d(TAG, "processTopNodes: pos = " + pos + " startTop = " + startTop + " endPositionViewBottom = " + endBottom);
                    Log.d(TAG, String.format((isTop(line, startTop, endBottom, node) ? "TOP" : "NO") + " pos = %d, line = %d, startTop = %d, endBottom = %d, node = %s", pos, line, startTop, endBottom, node.getPath().toString()));
                }
                if (isTop(line, startTop, endBottom, node)) {

                    //当前的节点有可能view为null但是需要和之前的node重用
                    ShieldDisplayNode lastNode = lastTopNodeList.get(pos);
                    if (node.equals(lastNode)) {
                        if (node.view == null) {
                            node.view = lastNode.view;
                        }
                        lastTopNodeList.remove(pos);
                    }

                    if( node.view == null ){//创建view
                        invalidateView(pos, node);
                        if( node.view != null ) {
                            measureViewWithMargin(node);
                            setNodeHeight(node, node.view.getMeasuredHeight());
                        }
                    }

                    TopBottomNodeInfo info = nodeInfoMap.get(node);
                    if (info == null ) {
                        info = new TopBottomNodeInfo(innerTopInfo.zPosition, line,
                                startTop, endBottom);
                        nodeInfoMap.put(node, info);
                    }

                    Integer height = getNodeHeight(node, null);
                    if (height == null && node.view != null) {
                        invalidateView(pos, node);
                        measureViewWithMargin(node);
                        setNodeHeight(node, node.view.getMeasuredHeight());
                        height = node.view.getMeasuredHeight();
                    }

                    info.startHoverLine = line;
                    info.endPositionViewBottom = endBottom;
                    info.zPosition = innerTopInfo.zPosition;

                    if (isTopEnding(line, endBottom, height)) {
                        if (info.topState != TopState.ENDING) {
                            info.topState = TopState.ENDING;
                            newEndingArr.put(pos, node);
                        }
                    } else {
                        if (info.topState != TopState.TOP) {
                            info.topState = TopState.TOP;
                            newTopArr.put(pos, node);
                        }
                    }
                    //加入当前置顶列表，注意node的view有可能为null
                    currentTopNodeList.put(pos, node);
                    if (DEBUG) {
                        Log.d(TAG, "processTopNodes: add top node at pos : " + pos + "  " + node.getPath());
                    }
                }
            }
        }

        for (int j = 0; j < lastTopNodeList.size(); j++) {
            if (DEBUG) {
                Log.d(TAG, "processTopNodes: remove last node at pos : " + lastTopNodeList.keyAt(j) + "  " + lastTopNodeList.valueAt(j).getPath());
            }

            ShieldDisplayNode node = lastTopNodeList.valueAt(j);
            removeFromTop(node);
            TopBottomNodeInfo info = nodeInfoMap.get(node);
            if (info != null) {
                info.topState = TopState.NORMAL;
            }
        }
    }

    private void setNodeHeight(ShieldDisplayNode node, int height) {
        nodeHeightMap.put(node, height);
    }

    private Integer getNodeHeight(ShieldDisplayNode node, Integer defaultValue) {
        Integer integer = nodeHeightMap.get(node);
        if (integer == null) {
            return defaultValue;
        }

        return integer;
    }

    private void layoutTopNodes() {

        if (topContainer == null) {
            return;
        }
        ArrayList<View> inScreenTopViews = new ArrayList<>();
        for (int i = 0; i < topContainer.getChildCount(); i++) {
            inScreenTopViews.add(topContainer.getChildAt(i));
        }

        if (currentTopNodeList != null && currentTopNodeList.size() > 0) {
            SparseArray<ArrayList<ShieldDisplayNode>> topBucketLst = sortNodes(currentTopNodeList);

            for (int j = 0; j < topBucketLst.size(); j++) {
                ArrayList<ShieldDisplayNode> bucket = topBucketLst.valueAt(j);
                for (int k = bucket.size() - 1; k >= 0; k--) {
                    ShieldDisplayNode node = bucket.get(k);
                    if (node == null || node.view == null) {
                        continue;
                    }
                    TopBottomNodeInfo info = nodeInfoMap.get(node);
                    inScreenTopViews.remove(node.view);
                    layoutToTopContainer(node, info ,getNodeHeight(node, 0));
                }
            }
        }

        if (!inScreenTopViews.isEmpty()) {
            for (int i = 0; i < inScreenTopViews.size(); i++) {
                topContainer.removeView(inScreenTopViews.get(i));
            }
        }

        boolean topFocus = false;
        for (int i = 0; i < topContainer.getChildCount(); i++) {
            View child = topContainer.getChildAt(i);
            if (child.hasWindowFocus() || child.hasFocus()) {
                topFocus = true;
                break;
            }
        }
        if (!topFocus) {
            topContainer.clearFocus();
        }
    }

    protected void dispatchTopNodeStatusChanged
            (SparseArray<ShieldDisplayNode> lastTopNodeList,
             SparseArray<ShieldDisplayNode> newTopArr,
             SparseArray<ShieldDisplayNode> newEndingArr) {
        if (DEBUG) {
            Log.d(TAG, "notify Top nodes");
        }
        for (int nt = 0; nt < newTopArr.size(); nt++) {
            ShieldDisplayNode node = newTopArr.valueAt(nt);
            notifyTopStateChanged(node);
        }

        for (int ne = 0; ne < newEndingArr.size(); ne++) {
            ShieldDisplayNode node = newEndingArr.valueAt(ne);
            notifyTopStateChanged(node);
        }

        for (int nd = 0; nd < lastTopNodeList.size(); nd++) {
            ShieldDisplayNode node = lastTopNodeList.valueAt(nd);
            notifyTopStateChanged(node);
        }
    }

    private void notifyTopStateChanged(ShieldDisplayNode node) {

        if (node != null && node.innerTopInfo != null && node.innerTopInfo.listener != null) {
            TopState state = TopState.NORMAL;
            TopBottomNodeInfo info = nodeInfoMap.get(node);
            if (info != null) {
                state = info.topState;
            }
            if (DEBUG) {
                Log.d(TAG, "node " + node + " >>>> " + state);
            }
            node.innerTopInfo.listener.onTopStateChanged(node, state);
        }
    }

    protected boolean isTop(int line, int startTop, int endBottom, ShieldDisplayNode node) {
        return startTop <= line && endBottom > 0;
    }

    protected boolean isTopEnding(int line, int endBottom, int height) {
        return endBottom - height < line;
    }

    //预处理置底
    private void processBottomNodes(SparseArray<ShieldDisplayNode> lastBottomNodeList,
                                    SparseArray<ShieldDisplayNode> newBottomArr,
                                    SparseArray<ShieldDisplayNode> newBottomEndingArr) {

        singlyBottomOffset = 0;
        if (firstLastPositionInfo != null && !firstLastPositionInfo.isEmpty()) {

            //循环当前设置列表
            for (int i = bottomNodeList.size() - 1; i >= 0; i--) {

                int pos = bottomNodeList.keyAt(i);

                ShieldDisplayNode node = bottomNodeList.valueAt(i);
                if (node == null) {
                    continue;
                }

                InnerBottomInfo innerBottomInfo = node.innerBottomInfo;
                if (innerBottomInfo == null) {
                    continue;
                }

                //算开始结束位置
                int line = getActualBottom(recyclerView) - innerBottomInfo.offset;
                int startTop = getTopOrBottomPosition(TOP, innerBottomInfo.startPos);
                int endBottom = getTopOrBottomPosition(BOTTOM, innerBottomInfo.endPos);
                if (DEBUG) {
                    Log.d(TAG, "processTopNodes: pos = " + pos + " startTop = " + startTop + " endPositionViewBottom = " + endBottom);
                }
                if (isBottom(line, startTop, endBottom, node)) {

                    //当前的节点有可能view为null但是需要和之前的node重用
                    ShieldDisplayNode lastNode = lastBottomNodeList.get(pos);
                    if (node.equals(lastNode)) {
                        if (node.view == null) {
                            node.view = lastNode.view;
                        }
                        lastBottomNodeList.remove(pos);
                    } else {
                        for (int index = 0, size = lastBottomNodeList.size(); index < size; index++) {
                            int lastKey = lastBottomNodeList.keyAt(index);
                            lastNode = lastBottomNodeList.get(lastKey);
                            if (node.equals(lastNode)) {
                                if (node.view == null) {
                                    node.view = lastNode.view;
                                }
                                lastBottomNodeList.remove(lastKey);
                                break;
                            }
                        }
                    }

                    TopBottomNodeInfo info = nodeInfoMap.get(node);
                    //不在屏幕内但是要置顶置底的情况
                    if (info == null) {
                        //预先获取view
                        invalidateView(pos, node);
                        info = new TopBottomNodeInfo(innerBottomInfo.zPosition,
                                line, startTop, endBottom);
                        nodeInfoMap.put(node, info);
                        if( node.view != null ) {
                            measureViewWithMargin(node);
                            setNodeHeight(node, node.view.getMeasuredHeight());
                        }
                    }

                    Integer height = getNodeHeight(node, null);
                    if (height == null && node.view != null) {
                        invalidateView(pos, node);
                        measureViewWithMargin(node);
                        setNodeHeight(node, node.view.getMeasuredHeight());
                    }
                    height = getNodeHeight(node, 0);

                    if (node.innerBottomInfo.mode == InnerBottomInfo.Mode.SINGLY) {
                        info.startHoverLine = line - singlyBottomOffset;
                    }
                    info.endPositionViewBottom = endBottom;
                    info.zPosition = innerBottomInfo.zPosition;
                    singlyBottomOffset += height;

                    if (isBottomEnding(line, startTop)) {
                        if (info.bottomState != BottomState.ENDING) {
                            info.bottomState = BottomState.ENDING;
                            newBottomEndingArr.put(pos, node);
                        }
                    } else {
                        if (info.bottomState != BottomState.BOTTOM) {
                            info.bottomState = BottomState.BOTTOM;
                            newBottomArr.put(pos, node);
                        }
                    }
                    //当前置底的列已经清空，加上新的置底节点，注意：节点的view可能null
                    currentBottomNodeList.put(pos, node);
                    if (DEBUG) {
                        Log.d(TAG, "processTopNodes: add top node at pos : " + pos + "  " + node.getPath());
                    }
                }
            }
        }

        //把当前置底但是不在新设置的节点列表的 remove掉
        for (int j = 0; j < lastBottomNodeList.size(); j++) {
            if (DEBUG) {
                Log.d(TAG, "processBottomNodes: remove last node at pos : " + lastBottomNodeList.keyAt(j) + "  " + lastBottomNodeList.valueAt(j).getPath());
            }

            ShieldDisplayNode node = lastBottomNodeList.valueAt(j);
            removeFromBottom(node);
//            nodeInfoMap.remove(node);
//            TopBottomNodeInfo info = nodeInfoMap.get(node);
//            if (info != null) {
//                info.bottomState = BottomState.NORMAL;
//            }
        }
    }

    private void layoutBottomNodes() {

        if (bottomContainer == null) {
            return;
        }
        ArrayList<View> inScreenBottomViews = new ArrayList<>();
        for (int i = 0; i < bottomContainer.getChildCount(); i++) {
            inScreenBottomViews.add(bottomContainer.getChildAt(i));
        }
        if (currentBottomNodeList != null && currentBottomNodeList.size() > 0) {
            SparseArray<ArrayList<ShieldDisplayNode>> bottomBucketLst = sortNodes(currentBottomNodeList);

            for (int j = 0; j < bottomBucketLst.size(); j++) {
                ArrayList<ShieldDisplayNode> bucket = bottomBucketLst.valueAt(j);
                for (int k = 0; k < bucket.size(); k++) {
                    ShieldDisplayNode node = bucket.get(k);
                    if (node == null || node.view == null) {
                        continue;
                    }
                    inScreenBottomViews.remove(node.view);
                    TopBottomNodeInfo info = nodeInfoMap.get(node);
                    layoutToBottomContainer(node, info, getNodeHeight(node, 0));
                }
            }
        }

        if (!inScreenBottomViews.isEmpty()) {
            for (int i = 0; i < inScreenBottomViews.size(); i++) {
                bottomContainer.removeView(inScreenBottomViews.get(i));
            }
        }

    }


    protected boolean isBottom(int line, int startTop, int endBottom, ShieldDisplayNode node) {
        return startTop < recyclerView.getHeight() && endBottom >= line;
    }

    protected boolean isBottomEnding(int line, int startTop) {
        return startTop > line;
    }

    protected int getTopOrBottomPosition(int side, int position) {

        if (firstLastPositionInfo == null) {
            return -1;
        }

        for (int i = 0; i < firstLastPositionInfo.firstVisibleItemPositions.size(); i++) {
            Integer first = firstLastPositionInfo.firstVisibleItemPositions.get(i);
            if (first != null && first >= 0 && position < first) {
                return Integer.MIN_VALUE;
            }
        }

        for (int i = 0; i < firstLastPositionInfo.lastVisibleItemPositions.size(); i++) {
            Integer last = firstLastPositionInfo.lastVisibleItemPositions.get(i);
            if (last != null && last >= 0 && position > last) {
                return Integer.MAX_VALUE;
            }
        }

        int pos = position;
        if (ShieldDisplayNodeAdapter.needOffset(recyclerView)) {
            pos += ShieldDisplayNodeAdapter.getOffset(recyclerView);
        }
        RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(pos);
        View view = null;
        if (holder != null) {
            view = holder.itemView;
        }

        if (view != null) {
            if (side == TOP) {
                return view.getTop();
            } else if (side == BOTTOM) {
                return view.getBottom();
            }
        }

        return 0;
    }

    private void invalidateView(int pos, ShieldDisplayNode node) {
        if (node.view == null) {
            node.view = createNodeView(pos, node);
        }

        updateNodeView(pos, node);
    }

    private void measureViewWithMargin(ShieldDisplayNode node) {
        if (topContainer == null) {
            return;
        }

        ViewGroup.LayoutParams lp = node.view.getLayoutParams();
        if (lp == null) {
            lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            node.view.setLayoutParams(lp);
        }

        int parentWidth = topContainer.getMeasuredWidth();
        int parentWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(parentWidth, View.MeasureSpec.EXACTLY);//width默认match parent
        int parentHeight = topContainer.getMeasuredHeight();
        int parentHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(parentHeight, View.MeasureSpec.UNSPECIFIED);
        final int childWidthMeasureSpec = ViewGroup.getChildMeasureSpec(parentWidthMeasureSpec,
                0, lp.width);
        final int childHeightMeasureSpec = ViewGroup.getChildMeasureSpec(parentHeightMeasureSpec,
                0, lp.height);
        node.view.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    private View createNodeView(int pos, ShieldDisplayNode node) {
        View view = node.viewPaintingCallback.onCreateView(recyclerView.getContext(), null, node.viewType);
        node.view = view;
        node.viewPaintingCallback.updateView(view, node, node.getPath());
        return view;
    }

    private void updateNodeView(int pos, ShieldDisplayNode node) {
        node.viewPaintingCallback.updateView(node.view, node, node.getPath());
    }

    @NonNull
    private SparseArray<ArrayList<ShieldDisplayNode>> sortNodes
            (SparseArray<ShieldDisplayNode> nodeList) {
        SparseArray<ArrayList<ShieldDisplayNode>> topBucketLst = new SparseArray<>();

        for (int i = 0; i < nodeList.size(); i++) {
            ShieldDisplayNode node = nodeList.valueAt(i);
            if (node == null) {
                continue;
            }
            TopBottomNodeInfo info = nodeInfoMap.get(node);
            if (info == null) {
                continue;
            }
            ArrayList<ShieldDisplayNode> bucket = topBucketLst.get(info.zPosition);
            if (bucket == null) {
                bucket = new ArrayList<>();
                topBucketLst.put(info.zPosition, bucket);
            }
            bucket.add(node);
        }
        return topBucketLst;
    }

    protected void layoutToTopContainer(ShieldDisplayNode node, TopBottomNodeInfo info , int height) {

        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            Log.w("TopBottomManager", "layout must be in Main Thread!!!", new Exception());
            return;
        }

        if (topContainer != null && node != null && node.view != null) {

            boolean viewHasFocus = node.view.hasFocus() ;//|| node.view.hasWindowFocus();

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

            if (node.view.getParent() instanceof ViewGroup && node.view.getParent() != topContainer) {
                // 这里的 removeView 会导致递归调用 layoutToTopContainer 方法
                // removeView -> requestFocus -> RecyclerView#SmoothScrollTo
                // -> onScrollStateChanged -> layoutToTopContainer
                ((ViewGroup) node.view.getParent()).removeView(node.view);
            }
            if (node.view.getParent() == null) {
                ((ZFrameLayout)topContainer).addView(node.view, lp, info.zPosition );
                if( node.view.getHeight() == 0 ) {
                    topContainer.post(new Runnable() {
                        @Override
                        public void run() {
                            topContainer.requestLayout();
                        }
                    });
                }
            }
            if (viewHasFocus) {
                node.view.requestFocus();
            }
            node.view.setTranslationY(Math.min(info.startHoverLine, info.endPositionViewBottom - height));

        }
    }

    protected void removeFromTop(ShieldDisplayNode node) {
        if (topContainer != null && node != null && node.view != null && node.view.getParent() == topContainer) {
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

    protected void layoutToBottomContainer(ShieldDisplayNode node, TopBottomNodeInfo info, int height) {

        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            Log.w("TopBottomManager", "layout must be in Main Thread!!!", new Exception());
            return;
        }

        if (bottomContainer != null && node != null && node.view != null) {

            if (node.view.getParent() instanceof ViewGroup && node.view.getParent() != bottomContainer) {
                ((ViewGroup) node.view.getParent()).removeView(node.view);
            }
            if (node.view.getParent() == null) {
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
                ((ZFrameLayout)bottomContainer).addView(node.view, lp, info.zPosition );
                if( node.view.getHeight() == 0 ) {
                    bottomContainer.post(new Runnable() {
                        @Override
                        public void run() {
                            bottomContainer.requestLayout();
                        }
                    });
                }
            }
            if (node.view.hasFocus() ){// || node.view.hasWindowFocus()) {
                node.view.requestFocus();
            }
            node.view.setTranslationY(Math.max( info.startPositionViewTop , info.startHoverLine - height));
        }
    }

    protected void removeFromBottom(ShieldDisplayNode node) {
        if (bottomContainer != null && node != null && node.view != null && node.view.getParent() == bottomContainer) {
            bottomContainer.removeView(node.view);
        }
        if (node.containerView != null
                && ShieldDisplayNode.Companion.contentsEquals(node.containerView.getNode(), node)) {
            node.containerView.setSubView(node.view);
            if (node.view != null) {
                node.view.setTranslationY(0);
            }
        }
    }


    public void requestUpdate() {
        nodeHeightMap.clear();
    }

    public boolean currentTopNode(ShieldDisplayNode node) {
        if (node == null) {
            return false;
        }
        for (int i = 0; i < currentTopNodeList.size(); i++) {
            if (node.equals(currentTopNodeList.valueAt(i))) {
                return true;
            }
        }

        return false;
    }

    public boolean currentBottomNode(ShieldDisplayNode node) {
        if (node == null) {
            return false;
        }
        for (int i = 0; i < currentBottomNodeList.size(); i++) {
            if (node.equals(currentBottomNodeList.valueAt(i))) {
                return true;
            }
        }

        return false;
    }

    public void setTopContainer(FrameLayout topContainer) {
        this.topContainer = topContainer;
    }

    private Handler handler = new Handler( Looper.getMainLooper() );
    public void setBottomContainer(FrameLayout bottomContainer) {
        this.bottomContainer = bottomContainer;
        /*if (bottomContainer != null) { //删除进行测试
            bottomContainer.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            layoutBottomNodes();
                        }
                    });
                }
            });
        }*/
    }

    @Override
    public int getAutoOffset() {
        return this.topOffset;
    }

    @Override
    public void setAutoOffset(int offset) {
        this.topOffset = offset;
    }

    private static class TopBottomNodeInfo {

        int zPosition = 0;
        int startHoverLine = 0;
        int startPositionViewTop = 0;
        int endPositionViewBottom = 0;
        TopState topState = TopState.NORMAL;
        BottomState bottomState = BottomState.NORMAL;

        public TopBottomNodeInfo(int zPosition, int startTopLineInScreen,
                                 int startPositionViewTop, int endPositionViewBottom) {
            this.zPosition = zPosition;
            this.startHoverLine = startTopLineInScreen;
            this.startPositionViewTop = startPositionViewTop;
            this.endPositionViewBottom = endPositionViewBottom;
        }
    }
}
