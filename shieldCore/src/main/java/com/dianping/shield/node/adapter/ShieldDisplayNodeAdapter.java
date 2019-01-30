package com.dianping.shield.node.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.dianping.agentsdk.framework.AgentInterface;
import com.dianping.agentsdk.framework.Cell;
import com.dianping.agentsdk.framework.ViewUtils;
import com.dianping.agentsdk.pagecontainer.SetAutoOffsetInterface;
import com.dianping.agentsdk.sectionrecycler.GroupBorderDecoration;
import com.dianping.agentsdk.sectionrecycler.divider.HorDividerCreator;
import com.dianping.agentsdk.sectionrecycler.divider.HorDividerDecoration;
import com.dianping.shield.adapter.MergeAdapterTypeRefreshListener;
import com.dianping.shield.debug.PerformanceManager;
import com.dianping.shield.entity.CellType;
import com.dianping.shield.entity.HotZoneYRange;
import com.dianping.shield.entity.ScrollDirection;
import com.dianping.shield.env.ShieldEnvironment;
import com.dianping.shield.feature.HotZoneItemStatusInterface;
import com.dianping.shield.feature.HotZoneStatusInterface;
import com.dianping.shield.logger.SCLogger;
import com.dianping.shield.node.StaggeredGridThemePackage;
import com.dianping.shield.node.cellnode.DividerConfigInfo;
import com.dianping.shield.node.cellnode.ShieldDisplayNode;
import com.dianping.shield.node.cellnode.StaggeredGridSection;
import com.dianping.shield.node.cellnode.ViewAttachDetachInterface;
import com.dianping.shield.node.itemcallbacks.ViewPaintingCallback;
import com.dianping.shield.node.processor.ProcessorHolder;
import com.dianping.shield.sectionrecycler.ShieldRecyclerViewInterface;
import com.dianping.shield.sectionrecycler.itemdecoration.StaggeredGridSpaceDecoration;
import com.dianping.shield.utils.IndexMap;

import java.util.Date;
import java.util.HashMap;


/**
 * Created by runqi.wei at 2018/6/20
 */
public class ShieldDisplayNodeAdapter extends RecyclerView.Adapter<ShieldDisplayNodeAdapter.DNViewHolder>
        implements HorDividerCreator, StaggeredGridSpaceDecoration.GapProvider,
        ListObserver, SetAutoOffsetInterface {


    public static final String FILE_NAME = "MergeSharedPerferance";
    public static final String NEED_BOUNDS_KEY = "NeedBounds";
    public static final String NEED_PERFORMANCE_KEY = "NeedPerformance";
    protected static final int DEFAULT_REUSE_POOL_SIZE = 10;
    private static final boolean DEBUG = ShieldEnvironment.INSTANCE.isDebug();
    protected boolean isOnBind = false;
    private SCLogger diffLogger = new SCLogger().setTag("DiffUtil");
    private Context context;
    private NodeList nodeList;
    private IndexMap<Object> nodeTypeIndex = new IndexMap<>();
    private HashMap<String, ShieldDisplayNode> typeDisplayNodeMap = new HashMap<>();
    private IndexMap<Object> nodeIdIndex = new IndexMap<>();
    private RecyclerView recyclerView;
    private SparseArray<ShieldDisplayNode> preloadNodeArray = new SparseArray<>();
    //managers
    private AttachStatusCollection attachStatusCollection;
    protected View.OnLayoutChangeListener onLayoutChangeListener = new View.OnLayoutChangeListener() {
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            updateStatus(recyclerView, ScrollDirection.STATIC);
        }
    };
    private HashMap<HotZoneStatusInterface, HotZoneAgentManager> hotZoneStatusInterfaceHashMap = new HashMap<>();
    private HashMap<HotZoneItemStatusInterface, HotZoneItemManager> hotZoneItemStatusInterfaceHashMap = new HashMap<>();
    private boolean disableDecoration = false;
    private HorDividerDecoration mHorDividerDecoration;
    private StaggeredGridSpaceDecoration staggeredGridSpaceDecoration;
    private StaggeredGridThemePackage staggeredGridThemePackage;
    private ProcessorHolder processorHolder;
    private String pageName;
    private PerformanceManager performanceManager;
    private GroupBorderDecoration.GroupInfoProvider groupInfoProvider;
    private MergeAdapterTypeRefreshListener typeRefreshListener;
    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            updateStatus(recyclerView, ScrollDirection.STATIC);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            ScrollDirection scrollDirection = getScrollDirection(dy);
            updateStatus(recyclerView, scrollDirection);

        }
    };
    private AttachStatusManager fullScreenAttachManager;
    private TopBottomLocationManager topBottomLocationManager;

    public ShieldDisplayNodeAdapter(Context context) {
        this.context = context;
        mHorDividerDecoration = new HorDividerDecoration(this);
//        topBottomManager = new TopBottomManager(context);
        attachStatusCollection = new AttachStatusCollection();
        fullScreenAttachManager = new AttachStatusManager(0, AttachStatusManager.POSITION_RV_BOTTOM);
        attachStatusCollection.addAttStatusManager(fullScreenAttachManager);
        topBottomLocationManager = new TopBottomLocationManager(0, AttachStatusManager.POSITION_RV_BOTTOM);
        attachStatusCollection.addAttStatusManager(topBottomLocationManager);
    }

    public static boolean needOffset(RecyclerView recyclerView) {
        return (recyclerView instanceof ShieldRecyclerViewInterface)
                || (recyclerView != null
                && recyclerView.getAdapter() != null
                && "HeaderViewRecyclerAdapter".equals(recyclerView.getAdapter().getClass().getSimpleName()));
    }

    public static int getOffset(RecyclerView recyclerView) {
        if (recyclerView instanceof ShieldRecyclerViewInterface) {
            return ((ShieldRecyclerViewInterface) recyclerView).getHeaderCount();
        } else {
            return 1;
        }
    }

    public void addHotZoneLocationManager(HotZoneStatusInterface hotZoneStatusInterface, String prefix, boolean reverseRange, boolean onlyObserverInHotZone) {
        HotZoneYRange hotZoneYRange = hotZoneStatusInterface.defineStatusHotZone();
        HotZoneAgentManager hotZoneAgentManager = null;
        if (reverseRange) {
            hotZoneAgentManager = new HotZoneAgentManager(-1, hotZoneYRange.endY);
        } else {
            hotZoneAgentManager = new HotZoneAgentManager(hotZoneYRange.endY, hotZoneYRange.startY);
        }
        hotZoneAgentManager.setHotZoneStatusInterface(hotZoneStatusInterface, prefix);
        hotZoneAgentManager.setReverseRange(reverseRange);
        hotZoneAgentManager.setOnlyObserverInHotZone(onlyObserverInHotZone);
        hotZoneAgentManager.setNodeList(nodeList);
        hotZoneStatusInterfaceHashMap.put(hotZoneStatusInterface, hotZoneAgentManager);

        attachStatusCollection.addAttStatusManager(hotZoneAgentManager);
    }

    public void removeHotZoneLocationManager(HotZoneStatusInterface hotZoneStatusInterface) {
        attachStatusCollection.removeAttStatusManager(hotZoneStatusInterfaceHashMap.get(hotZoneStatusInterface));
        hotZoneStatusInterfaceHashMap.remove(hotZoneStatusInterface);
    }

    public HashMap<HotZoneStatusInterface, HotZoneAgentManager> getHotZoneStatusInterfaceHashMap() {
        return hotZoneStatusInterfaceHashMap;
    }

    public HashMap<HotZoneItemStatusInterface, HotZoneItemManager> getHotZoneItemStatusInterfaceHashMap() {
        return hotZoneItemStatusInterfaceHashMap;
    }

    public void addHotZoneItemLocationManager(HotZoneItemStatusInterface hotZoneItemStatusInterface, Cell cell, boolean reverseRange, boolean onlyObserverInHotZone) {
        HotZoneYRange hotZoneYRange = hotZoneItemStatusInterface.defineHotZone();
        HotZoneItemManager hotZoneItemManager = null;
        if (reverseRange) {
            hotZoneItemManager = new HotZoneItemManager(-1, hotZoneYRange.endY);
        } else {
            hotZoneItemManager = new HotZoneItemManager(hotZoneYRange.endY, hotZoneYRange.startY);
        }
        hotZoneItemManager.setHotZoneItemStatusInterface(hotZoneItemStatusInterface, cell);
        hotZoneItemManager.setReverseRange(reverseRange);
        hotZoneItemManager.setOnlyObserverInHotZone(onlyObserverInHotZone);
        hotZoneItemManager.setNodeList(nodeList);
        hotZoneItemStatusInterfaceHashMap.put(hotZoneItemStatusInterface, hotZoneItemManager);

        attachStatusCollection.addAttStatusManager(hotZoneItemManager);
    }

    public void removeHotZoneItemLocationManager(HotZoneItemStatusInterface hotZoneItemStatusInterface) {
        attachStatusCollection.removeAttStatusManager(hotZoneItemStatusInterfaceHashMap.get(hotZoneItemStatusInterface));
        hotZoneItemStatusInterfaceHashMap.remove(hotZoneItemStatusInterface);
    }

    public void setTypeRefreshListener(MergeAdapterTypeRefreshListener listener) {
        typeRefreshListener = listener;
    }

    public void setDisableDecoration(boolean disableDecoration) {
        this.disableDecoration = disableDecoration;
        if (recyclerView != null && mHorDividerDecoration != null && disableDecoration) {
            recyclerView.removeItemDecoration(mHorDividerDecoration);
        }
    }

    public void storeCurrentInfo() {
        fullScreenAttachManager.storeCurrentInfo();
    }

    public void clearStoredInfo() {
        fullScreenAttachManager.clearStoredPositionInfo();
    }

    public void loadCurrentInfo() {
        fullScreenAttachManager.loadCurrentInfo();
    }

    public void clearCurrentInfo() {
        fullScreenAttachManager.clearCurrentInfo();
    }

    public void clearAttachStatus() {
        fullScreenAttachManager.clear();
    }

    public void forceUpdateAttachStatus(ScrollDirection scrollDirection) {
        fullScreenAttachManager.onViewLocationChanged(scrollDirection);
    }

    public void updateStatus() {
        updateStatus(ScrollDirection.STATIC);
    }

    public void updateStatus(ScrollDirection scrollDirection) {
        updateStatus(recyclerView, scrollDirection);
    }

    public StaggeredGridSpaceDecoration getStaggeredGridSpaceDecoration() {
        if (staggeredGridSpaceDecoration == null) {
            staggeredGridSpaceDecoration = new StaggeredGridSpaceDecoration();
        }
        return staggeredGridSpaceDecoration;
    }

    public void setStaggeredGridThemePackage(StaggeredGridThemePackage staggeredGridThemePackage) {
        this.staggeredGridThemePackage = staggeredGridThemePackage;
        if (staggeredGridSpaceDecoration != null) {
            staggeredGridSpaceDecoration.setStaggeredGridThemePackage(staggeredGridThemePackage);
        }
    }

    public void setNeedCalculateAppearDisappearEvent(final boolean needCalculateAppearDisappearEvent) {
        setNeedCalculateAppearDisappearEvent(needCalculateAppearDisappearEvent, 0);
    }

    public void setNeedCalculateAppearDisappearEvent(final boolean needCalculateAppearDisappearEvent, long delay) {
        if (delay <= 0) {
            attachStatusCollection.setRunning(needCalculateAppearDisappearEvent);
        } else {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    attachStatusCollection.setRunning(needCalculateAppearDisappearEvent);
                    updateStatus(recyclerView, ScrollDirection.STATIC);
                }
            }, delay);
        }
    }

    public void setGroupInfoProvider(GroupBorderDecoration.GroupInfoProvider groupInfoPrivider) {
        this.groupInfoProvider = groupInfoPrivider;
    }

    public void setProcessorHolder(ProcessorHolder processorHolder) {
        this.processorHolder = processorHolder;
    }

    @NonNull
    private ScrollDirection getScrollDirection(int dy) {
        ScrollDirection scrollDirection = ScrollDirection.STATIC;
        if (dy > 0) {
            scrollDirection = ScrollDirection.UP;
        } else if (dy < 0) {
            scrollDirection = ScrollDirection.DOWN;
        }
        return scrollDirection;
    }

    protected void updateStatus(RecyclerView recyclerView, ScrollDirection scrollDirection) {

        int offset = 0;
        if (needOffset(recyclerView)) {
            offset = -getOffset(recyclerView);
        }

        attachStatusCollection.updateFistLastPositionInfo(recyclerView, offset, scrollDirection);

    }

    public void setTopContainer(FrameLayout frameLayout) {
        topBottomLocationManager.setTopContainer(frameLayout);
    }

    public void setBottomContainer(FrameLayout viewGroup) {
        topBottomLocationManager.setBottomContainer(viewGroup);
    }

    public void updateTopBottomViews() {
        topBottomLocationManager.requestUpdate();
        recyclerView.requestLayout();
    }

    public void clearTop() {
        topBottomLocationManager.clearTop();
    }

    public void setTopList(SparseArray<ShieldDisplayNode> topNodeList) {
        topBottomLocationManager.setTopNodeList(topNodeList);
    }

    public void clearBottom() {
        topBottomLocationManager.clearBottom();
    }

    public void setBottomList(SparseArray<ShieldDisplayNode> bottomNodeList) {
        topBottomLocationManager.setBottomNodeList(bottomNodeList);
    }

    public void setNodeList(NodeList nodeList) {
        this.nodeList = nodeList;
        this.nodeList.registerObserver(this);
        fullScreenAttachManager.setNodeList(nodeList);
        notifyNodeChanged();
    }

    @NonNull
    @Override
    public DNViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Object typeStrOrNode = nodeTypeIndex.getValue(viewType);
        ViewPaintingCallback vi = null;
        String type = null;
        ShieldDisplayNode displayNode = null;
        if (typeStrOrNode instanceof String) {
            type = (String) typeStrOrNode;
            displayNode = typeDisplayNodeMap.get(type);
            if (displayNode != null && displayNode.viewPaintingCallback != null) {
                vi = displayNode.viewPaintingCallback;
            }
        } else if (typeStrOrNode instanceof ShieldDisplayNode) {
            displayNode = (ShieldDisplayNode) typeStrOrNode;
            vi = displayNode.viewPaintingCallback;
        }

        if (vi != null) {
            if (performanceManager != null) {
                AgentInterface agentInterface = displayNode.rowParent.sectionParent.cellParent.owner;
                String agentName = agentInterface.getClass().getCanonicalName();
                String hostName = agentInterface.getHostName();
                String cellName = displayNode.rowParent.sectionParent.cellParent.name;
                String hashCode = "" + agentInterface.hashCode();

                Date start = new Date();
                DNViewHolder dnViewHolder = onCreateViewHolder(parent, vi, displayNode);
                Date end = new Date();

                performanceManager.insertPerformanceRecord(pageName, hostName, agentName, hashCode, cellName, "onCreateView", start.getTime(), end.getTime());

                return dnViewHolder;
            } else {
                return onCreateViewHolder(parent, vi, displayNode);
            }
        }

        return new DNViewHolder(null);
    }

    private DNViewHolder onCreateViewHolder(@NonNull ViewGroup parent, @NonNull ViewPaintingCallback vi, @NonNull ShieldDisplayNode displayNode) {
        View view = vi.onCreateView(displayNode == null ? null : displayNode.context, parent, displayNode.viewType);
        DisplayNodeContainer container = new DisplayNodeContainer(context);
        if (view.getParent() instanceof ViewGroup) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        container.setSubView(view);
        StaggeredGridLayoutManager.LayoutParams layoutParams = null;
//        ViewGroup.LayoutParams viewLayoutParams = view.getLayoutParams();
//        if (viewLayoutParams != null) {
//            layoutParams = new StaggeredGridLayoutManager.LayoutParams(viewLayoutParams);
//        } else {
        layoutParams = new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        }
        layoutParams.setFullSpan(true);
        container.setLayoutParams(layoutParams);
        return new DNViewHolder(container);
    }

    @Override
    public void onBindViewHolder(@NonNull DNViewHolder holder, int position) {
        ShieldDisplayNode displayNode = getDisplayNode(position);
        if (performanceManager != null) {
            AgentInterface agentInterface = displayNode.rowParent.sectionParent.cellParent.owner;
            String agentName = agentInterface.getClass().getCanonicalName();
            String hostName = agentInterface.getHostName();
            String cellName = displayNode.rowParent.sectionParent.cellParent.name;
            String hashCode = "" + agentInterface.hashCode();

            Date start = new Date();
            onBindViewHolder(holder, position, displayNode);
            Date end = new Date();

            performanceManager.insertPerformanceRecord(pageName, hostName, agentName, hashCode, cellName, "onCreateView", start.getTime(), end.getTime());

        } else {
            onBindViewHolder(holder, position, displayNode);
        }
    }

    private void onBindViewHolder(@NonNull DNViewHolder holder, int position, @NonNull ShieldDisplayNode displayNode) {
        isOnBind = true;
        if (holder.itemView instanceof DisplayNodeContainer) {
            ShieldDisplayNode oldNode = ((DisplayNodeContainer) holder.itemView).getNode();
            if (oldNode != null) {
                oldNode.containerView = null;
            }
            ((DisplayNodeContainer) holder.itemView).setNode(displayNode);
            displayNode.containerView = (DisplayNodeContainer) holder.itemView;
            displayNode.view = ((DisplayNodeContainer) holder.itemView).getSubView();
        } else {
            displayNode.containerView = null;
            displayNode.view = holder.itemView;
        }
        holder.displayNode = displayNode;
        if (displayNode.viewPaintingCallback != null) {
            displayNode.viewPaintingCallback.updateView(displayNode.view, displayNode, displayNode.getPath());
        }

        //add StaggeredGrid support
        StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) displayNode.containerView.getLayoutParams();
        if (displayNode.rowParent != null
                && displayNode.rowParent.sectionParent instanceof StaggeredGridSection
                && displayNode.getPath() != null
                && (displayNode.getPath().cellType == CellType.NORMAL
                || displayNode.getPath().cellType == CellType.LOADING_MORE)) {
            layoutParams.setFullSpan(false);
        } else {
            layoutParams.setFullSpan(true);
        }
        displayNode.containerView.setLayoutParams(layoutParams);
        isOnBind = false;
    }

    /**
     * Returns if we are running in the {@link #onBindViewHolder(DNViewHolder, int, ShieldDisplayNode)} method.
     *
     * @return
     */
    public boolean isOnBind() {
        return isOnBind;
    }

    @Override
    public int getItemCount() {
        if (nodeList != null) {
            return nodeList.size();
        } else {
            return 0;
        }
    }

    public ShieldDisplayNode getDisplayNode(int position) {
        if (position >= 0 && position < nodeList.size()) {
            return nodeList.getShieldDisplayNode(position);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        ShieldDisplayNode displayNode = getDisplayNode(position);
        if (displayNode != null) {
            String viewType = displayNode.getViewTypeWithTopInfo();
            if (viewType != null) {
                if (!nodeTypeIndex.containsValue(viewType)) {
                    nodeTypeIndex.putValue(viewType);
                    recyclerView.getRecycledViewPool().setMaxRecycledViews(nodeTypeIndex.getIndex(viewType), DEFAULT_REUSE_POOL_SIZE);
                }
                if (!typeDisplayNodeMap.containsValue(displayNode)) {
                    typeDisplayNodeMap.put(viewType, displayNode);
                }
                return nodeTypeIndex.getIndex(viewType);

            } else {
                if (nodeTypeIndex.containsValue(displayNode)) {
                    return nodeTypeIndex.getIndex(displayNode);
                } else {
                    nodeTypeIndex.putValue(displayNode);
                    return nodeTypeIndex.getIndex(displayNode);
                }
            }
        }
        return RecyclerView.INVALID_TYPE;
    }

    @Override
    public void onViewRecycled(DNViewHolder holder) {
        Log.d("ShieldNode", "onViewRecycled: " + holder + "\n node: " + holder.displayNode);
        if (holder.displayNode != null && !holder.displayNode.isUnique()) {
            holder.displayNode.containerView = null;
            holder.displayNode.view = null;
            holder.displayNode = null;
        }
        super.onViewRecycled(holder);
    }

    public int getGlobalType(String viewType) {
        if (!nodeTypeIndex.containsValue(viewType)) {
            nodeTypeIndex.putValue(viewType);
        }
        return nodeTypeIndex.getIndex(viewType);
    }


    @Override
    public long getItemId(int position) {
        ShieldDisplayNode displayNode = getDisplayNode(position);
        if (displayNode != null && displayNode.stableid != null) {
            if (nodeIdIndex.containsValue(displayNode.stableid)) {
                return nodeIdIndex.getIndex(displayNode.stableid);
            } else {
                nodeIdIndex.putValue(displayNode.stableid);
                return nodeIdIndex.getIndex(displayNode.stableid);
            }
        }
        return RecyclerView.NO_ID;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull DNViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ShieldDisplayNode displayNode = holder.displayNode;
        if (displayNode != null
                && displayNode.attachDetachInterfaceArrayList != null
                && !displayNode.attachDetachInterfaceArrayList.isEmpty()) {
            for (ViewAttachDetachInterface adInterface :
                    displayNode.attachDetachInterfaceArrayList) {
                adInterface.onViewAttachedToWindow(displayNode.view, holder.getAdapterPosition(), displayNode);
            }
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull DNViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        ShieldDisplayNode displayNode = holder.displayNode;
        if (displayNode != null
                && displayNode.attachDetachInterfaceArrayList != null
                && !displayNode.attachDetachInterfaceArrayList.isEmpty()) {
            for (ViewAttachDetachInterface adInterface :
                    displayNode.attachDetachInterfaceArrayList) {
                adInterface.onViewDetachedFromWindow(displayNode.view, holder.getAdapterPosition(), displayNode);
            }
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        this.recyclerView = recyclerView;

        this.recyclerView.addOnScrollListener(onScrollListener);
        this.recyclerView.addOnLayoutChangeListener(onLayoutChangeListener);

        if (recyclerView != null && (!disableDecoration)) {
            recyclerView.addItemDecoration(mHorDividerDecoration);
        }

        topBottomLocationManager.setRecyclerView(recyclerView);

        if (recyclerView != null && needBounds()) {
            recyclerView.addItemDecoration(new GroupBorderDecoration(groupInfoProvider));
        }

        if (needPerformance()) {
            performanceManager = new PerformanceManager(context);
        }

        if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            if (staggeredGridSpaceDecoration == null) {
                staggeredGridSpaceDecoration = new StaggeredGridSpaceDecoration();
                staggeredGridSpaceDecoration.setGapProvider(this);
                staggeredGridSpaceDecoration.setStaggeredGridThemePackage(staggeredGridThemePackage);
            }
            recyclerView.addItemDecoration(staggeredGridSpaceDecoration);
        }
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        topBottomLocationManager.setRecyclerView(null);
        if (recyclerView != null && mHorDividerDecoration != null && (!disableDecoration)) {
            recyclerView.removeItemDecoration(mHorDividerDecoration);
        }

        if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            recyclerView.removeItemDecoration(staggeredGridSpaceDecoration);
        }

        this.recyclerView.removeOnLayoutChangeListener(onLayoutChangeListener);
        this.recyclerView.removeOnScrollListener(onScrollListener);

        this.recyclerView = null;
        super.onDetachedFromRecyclerView(recyclerView);
    }

    public boolean needBounds() {
        SharedPreferences preferences = context.getApplicationContext()
                .getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(NEED_BOUNDS_KEY, false);
    }

    public boolean needPerformance() {
        SharedPreferences preferences = context.getApplicationContext()
                .getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(NEED_PERFORMANCE_KEY, false);
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public DividerConfigInfo getDividerInfo(ShieldDisplayNode displayNode) {
        return displayNode != null ? displayNode.dividerInfo : null;
    }

    @Override
    public float getHeaderHeight(int position) {
        ShieldDisplayNode node = getDisplayNode(position);
        DividerConfigInfo dividerInfo = getDividerInfo(node);
        int nodeInfo = dividerInfo != null ? dividerInfo.headerGapHeight : 0;
        if (position == 0 && processorHolder != null) {
            if (processorHolder.getDividerThemePackage().needAddFirstHeader) {
                int firstHeaderHeight = nodeInfo + ViewUtils.dip2px(context, processorHolder.getDividerThemePackage().firstHeaderExtraHeight);
                if (firstHeaderHeight < 0) firstHeaderHeight = 0;
                return firstHeaderHeight;
            } else {
                return 0;
            }
        }
        return nodeInfo;
    }

    @Override
    public Drawable getHeaderDrawable(int position) {
        ShieldDisplayNode node = getDisplayNode(position);
        DividerConfigInfo dividerInfo = getDividerInfo(node);
        return dividerInfo != null ? dividerInfo.headerGapDrawable : null;
    }

    @Override
    public float getFooterHeight(int position) {
        ShieldDisplayNode node = getDisplayNode(position);
        DividerConfigInfo dividerInfo = getDividerInfo(node);
        int nodeInfo = dividerInfo != null ? dividerInfo.footerGapHeight : 0;
        if (position == nodeList.size() - 1 && processorHolder != null) {
            if (processorHolder.getDividerThemePackage().needAddLastFooter) {
                int lastFooterHeight = nodeInfo + ViewUtils.dip2px(context, processorHolder.getDividerThemePackage().lastFooterExtraHeight);
                if (lastFooterHeight < 0) lastFooterHeight = 0;
                return lastFooterHeight;
            } else {
                return 0;
            }

        }
        return nodeInfo;
    }

    @Override
    public Drawable getFooterDrawable(int position) {
        ShieldDisplayNode node = getDisplayNode(position);
        DividerConfigInfo dividerInfo = getDividerInfo(node);
        return dividerInfo != null ? dividerInfo.footerGapDrawable : null;
    }

    @Override
    public boolean hasTopDividerVerticalOffset(int position) {
        return false;
    }

    @Override
    public boolean hasBottomDividerVerticalOffset(int position) {
        return false;
    }

    @Override
    public Drawable getTopDivider(int position) {
        ShieldDisplayNode node = getDisplayNode(position);
        DividerConfigInfo dividerInfo = getDividerInfo(node);
        return dividerInfo != null ? dividerInfo.cellTopLineDrawable : null;
    }

    @Override
    public Drawable getBottomDivider(int position) {
        ShieldDisplayNode node = getDisplayNode(position);
        DividerConfigInfo dividerInfo = getDividerInfo(node);
        return dividerInfo != null ? dividerInfo.cellBottomLineDrawable : null;
    }

    @Override
    public Rect topDividerOffset(int position) {
        ShieldDisplayNode node = getDisplayNode(position);
        DividerConfigInfo dividerInfo = getDividerInfo(node);
        return dividerInfo != null ? dividerInfo.cellTopLineOffset : null;
    }

    @Override
    public Rect bottomDividerOffset(int position) {
        ShieldDisplayNode node = getDisplayNode(position);
        DividerConfigInfo dividerInfo = getDividerInfo(node);
        return dividerInfo != null ? dividerInfo.cellBottomLineOffset : null;
    }

    public void onNodeChanged() {
        invalidateStaggeredDecorations();
        if (topBottomLocationManager != null) {
            topBottomLocationManager.requestUpdate();
        }

        // 这个时候 updateStatus 的话，
        // recyclerView 已经 request 并且回收掉了所有的 ViewHolder,
        // 会判断为页面上没有 view.
        // 实际上随后的 layout 过程会重新布局, 填满页面，并且会触发 updateStatus
        // updateStatus(recyclerView, ScrollDirection.STATIC);
    }

    public void notifyNodeChanged() {
        if (typeRefreshListener != null) {
            typeRefreshListener.onMergedTypeRefresh();
        }
        notifyDataSetChanged();
        onNodeChanged();
    }

    public void notifyNodeRangeChanged(int positionStart, int itemCount) {
        if (typeRefreshListener != null) {
            typeRefreshListener.onMergedTypeRefresh();
        }
        notifyItemRangeChanged(positionStart, itemCount);
        onNodeChanged();
    }

    public void notifyNodeRangeInserted(int positionStart, int itemCount) {
        if (typeRefreshListener != null) {
            typeRefreshListener.onMergedTypeRefresh();
        }
        notifyItemRangeInserted(positionStart, itemCount);
        onNodeChanged();
    }

    public void notifyNodeRangeRemoved(int positionStart, int itemCount) {
        if (typeRefreshListener != null) {
            typeRefreshListener.onMergedTypeRefresh();
        }
        notifyItemRangeRemoved(positionStart, itemCount);
        onNodeChanged();
    }

    public void notifyNodeMoved(int fromPosition, int toPosition) {
        if (typeRefreshListener != null) {
            typeRefreshListener.onMergedTypeRefresh();
        }
        notifyItemMoved(fromPosition, toPosition);
        onNodeChanged();
    }

    @Override
    public void onChanged() {
        if (isOnBind) return;
        notifyNodeChanged();
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount) {
        if (isOnBind) return;
        notifyNodeRangeChanged(positionStart, itemCount);
    }

    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
        if (isOnBind) return;
        notifyNodeRangeInserted(positionStart, itemCount);
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
        if (isOnBind) return;
        notifyNodeRangeRemoved(positionStart, itemCount);
    }

    @Override
    public void onItemRangeMoved(int fromPosition, int toPosition) {
        if (isOnBind) return;
        notifyNodeMoved(fromPosition, toPosition);
    }

    @Override
    public int getXGap(int position) {
        ShieldDisplayNode node = getDisplayNode(position);
        if (node != null && node.staggeredGridXGap != null) {
            return node.staggeredGridXGap;
        }
        return 0;
    }

    @Override
    public int getYGap(int position) {
        ShieldDisplayNode node = getDisplayNode(position);
        if (node != null && node.staggeredGridYGap != null) {
            return node.staggeredGridYGap;
        }
        return 0;
    }

    @Override
    public int getLeftMargin(int position) {
        ShieldDisplayNode node = getDisplayNode(position);
        if (node != null && node.staggeredGridLeftMargin != null) {
            return node.staggeredGridLeftMargin;
        }
        return 0;
    }

    @Override
    public int getRightMargin(int position) {
        ShieldDisplayNode node = getDisplayNode(position);
        if (node != null && node.staggeredGridRightMargin != null) {
            return node.staggeredGridRightMargin;
        }
        return 0;
    }

    private void invalidateStaggeredDecorations() {
        if (recyclerView != null && recyclerView.getLayoutManager() != null
                && recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            recyclerView.invalidateItemDecorations();
        }
    }

    @Override
    public int getAutoOffset() {
        if (topBottomLocationManager != null) {
            return topBottomLocationManager.getAutoOffset();
        }
        return 0;
    }

    @Override
    public void setAutoOffset(int offset) {
        if (topBottomLocationManager != null) {
            topBottomLocationManager.setAutoOffset(offset);
        }
    }

    protected static class DNViewHolder extends RecyclerView.ViewHolder {

        public ShieldDisplayNode displayNode;

        public DNViewHolder(View itemView) {
            super(itemView);
        }

    }
}
