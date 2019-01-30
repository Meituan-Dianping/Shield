package com.dianping.agentsdk.manager;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Pair;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.dianping.agentsdk.adapter.ExtraCellPieceAdapter;
import com.dianping.agentsdk.adapter.FinalPieceAdapter;
import com.dianping.agentsdk.adapter.LoadingMorePieceAdapter;
import com.dianping.agentsdk.adapter.LoadingPieceAdapter;
import com.dianping.agentsdk.adapter.RowClickAdapter;
import com.dianping.agentsdk.adapter.RowLongClickAdapter;
import com.dianping.agentsdk.adapter.SectionDividerPieceAdapter;
import com.dianping.agentsdk.adapter.SectionLinkPieceAdapter;
import com.dianping.agentsdk.adapter.SectionPieceAdapter;
import com.dianping.agentsdk.adapter.SectionStableIdPieceAdapter;
import com.dianping.agentsdk.adapter.SetBottomAdapter;
import com.dianping.agentsdk.adapter.SetTopAdapter;
import com.dianping.agentsdk.adapter.SetZoomAdapter;
import com.dianping.agentsdk.framework.AgentInterface;
import com.dianping.agentsdk.framework.Cell;
import com.dianping.agentsdk.framework.CellNameInterface;
import com.dianping.agentsdk.framework.CellStatusInterface;
import com.dianping.agentsdk.framework.CellStatusMoreInterface;
import com.dianping.agentsdk.framework.DividerInterface;
import com.dianping.agentsdk.framework.DividerOffsetInterface;
import com.dianping.agentsdk.framework.ItemClickInterface;
import com.dianping.agentsdk.framework.ItemIdInterface;
import com.dianping.agentsdk.framework.ItemLongClickInterface;
import com.dianping.agentsdk.framework.SectionCellInterface;
import com.dianping.agentsdk.framework.SectionDividerInfoInterface;
import com.dianping.agentsdk.framework.SectionExtraCellDividerOffsetInterface;
import com.dianping.agentsdk.framework.SectionExtraCellInterface;
import com.dianping.agentsdk.framework.SectionExtraTopDividerCellInterface;
import com.dianping.agentsdk.framework.SectionHeaderFooterDrawableInterface;
import com.dianping.agentsdk.framework.SectionLinkCellInterface;
import com.dianping.agentsdk.framework.TopDividerInterface;
import com.dianping.agentsdk.framework.UIRCellManagerInterface;
import com.dianping.agentsdk.framework.UIRDriverInterface;
import com.dianping.agentsdk.framework.UpdateAgentType;
import com.dianping.agentsdk.framework.WhiteBoard;
import com.dianping.agentsdk.pagecontainer.SetAutoOffsetInterface;
import com.dianping.agentsdk.sectionrecycler.divider.DividerInfoInterface;
import com.dianping.agentsdk.sectionrecycler.layoutmanager.LinearLayoutManagerWithSmoothOffset;
import com.dianping.agentsdk.sectionrecycler.layoutmanager.OnSmoothScrollListener;
import com.dianping.agentsdk.sectionrecycler.section.MergeSectionDividerAdapter;
import com.dianping.agentsdk.sectionrecycler.section.PieceAdapter;
import com.dianping.agentsdk.sectionrecycler.section.SectionDAdapter;
import com.dianping.agentsdk.utils.AgentInfoHelper;
import com.dianping.shield.adapter.MergeAdapterTypeRefreshListener;
import com.dianping.shield.adapter.StaggeredGridCellPieceAdapter;
import com.dianping.shield.adapter.TopPositionAdapter;
import com.dianping.shield.bridge.feature.AgentGlobalPositionInterface;
import com.dianping.shield.bridge.feature.AgentScrollerInterface;
import com.dianping.shield.consts.ShieldConst;
import com.dianping.shield.entity.AgentScrollerParams;
import com.dianping.shield.entity.CellType;
import com.dianping.shield.entity.ExposedDetails;
import com.dianping.shield.entity.ExposedInfo;
import com.dianping.shield.entity.NodeInfo;
import com.dianping.shield.entity.ScrollDirection;
import com.dianping.shield.entity.ScrollScope;
import com.dianping.shield.env.ShieldEnvironment;
import com.dianping.shield.feature.ExposeScreenLoadedInterface;
import com.dianping.shield.feature.ExtraCellBottomInterface;
import com.dianping.shield.feature.ExtraCellTopInterface;
import com.dianping.shield.feature.ExtraCellTopParamsInterface;
import com.dianping.shield.feature.HotZoneItemListener;
import com.dianping.shield.feature.HotZoneObserverInterface;
import com.dianping.shield.feature.LoadingAndLoadingMoreCreator;
import com.dianping.shield.feature.OnTopViewLayoutChangeListenerInterface;
import com.dianping.shield.feature.RecyclerPoolSizeInterface;
import com.dianping.shield.feature.ScrollToTopOffsetInterface;
import com.dianping.shield.feature.SectionTitleInterface;
import com.dianping.shield.feature.SetBottomInterface;
import com.dianping.shield.feature.SetTopInterface;
import com.dianping.shield.feature.SetTopParamsInterface;
import com.dianping.shield.feature.SetZoomInterface;
import com.dianping.shield.feature.StaggeredGridCellInfoInterface;
import com.dianping.shield.feature.TopPositionInterface;
import com.dianping.shield.layoutmanager.CoveredYInterface;
import com.dianping.shield.layoutmanager.TopLinearLayoutManager;
import com.dianping.shield.node.PositionType;
import com.dianping.shield.sectionrecycler.ShieldLayoutManagerInterface;
import com.dianping.shield.sectionrecycler.ShieldRecyclerViewInterface;
import com.dianping.shield.sectionrecycler.itemdecoration.StaggeredGridSpaceDecoration;
import com.dianping.shield.utils.ExposedEngine;
import com.dianping.shield.utils.HotZoneEngine;
import com.dianping.shield.utils.HotZoneItemEngine;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by hezhi on 15/12/11.
 */
public class SectionRecyclerCellManager implements UIRCellManagerInterface<RecyclerView>, MergeAdapterTypeRefreshListener, ExposeScreenLoadedInterface, AgentScrollerInterface, AgentGlobalPositionInterface {

    public final static Handler handler = new Handler(Looper.getMainLooper());
    protected static final Comparator<Cell> cellComparator = new Comparator<Cell>() {
        @Override
        public int compare(Cell lhs, Cell rhs) {
            return lhs.owner.getIndex().equals(rhs.owner.getIndex()) ? lhs.name.compareTo(rhs.name)
                    : lhs.owner.getIndex().compareTo(rhs.owner.getIndex());
        }
    };
    protected final HashMap<String, Cell> cells = new LinkedHashMap<>();
    protected GroupManager groupManager;
    protected ArrayList<Cell> sort;
    protected ArrayList<ArrayList<Cell>> cellGroup;//带分组的二维cell结构
    protected Context mContext;
    protected RecyclerView recyclerView;
    protected RecyclerView.LayoutManager layoutManager;
    protected ShieldLayoutManagerInterface shieldLayoutManager;
    protected MergeSectionDividerAdapter mergeRecyclerAdapter;
    protected boolean needStableId;
    protected View focusedView;
    protected RecyclerView.OnScrollListener mOnScrollListener;
    protected RecyclerView.OnScrollListener mOnAgentScrollListener;
    protected RecyclerView.OnScrollListener mHotZoneItemScrollListener;

    protected boolean mCanExposeScreen;
    protected Handler mExposeHandler = new Handler();
    protected ExposedEngine mExposedEngine = new ExposedEngine();
    protected LoadingAndLoadingMoreCreator creator;
    //hot zone
    protected Map<HotZoneObserverInterface, HotZoneEngine> hotZoneEngineMap = new LinkedHashMap<>();
    protected ArrayList<HotZoneEngine> hotZoneEngineArrayList = new ArrayList<>();

    //hot zone item
    protected Map<HotZoneItemListener, HotZoneItemEngine> itemEngineMap = new LinkedHashMap<>();
    protected ArrayList<HotZoneItemEngine> hotZoneItemEngineArrayList = new ArrayList<>();

    //reuse mapping
    protected HashMap<String, HashMap<String, Integer>> reuseIdentifierMap;
    protected HashMap<String, HashMap<String, Integer>> reuseIdentifierMapForHeader;
    protected HashMap<String, HashMap<String, Integer>> reuseIdentifierMapForFooter;
    protected HashMap<String, HashMap<String, Integer>> cellTypeMap;
    protected HashMap<String, HashMap<String, Integer>> cellTypeMapForHeader;
    protected HashMap<String, HashMap<String, Integer>> cellTypeMapForFooter;
    //sectionTitle
    protected ArrayList<SectionTitleInfo> sectionTitleArray = new ArrayList<>();
    protected ArrayList<String> oldVisibleAgentList = new ArrayList<>();


    //position scrollToOffset
    protected boolean idNeedScroll = true;
    protected AgentInterface scrollToTopAgent;
    protected boolean scrollToTopByFirstMarkedAgent = false;
    protected WhiteBoard whiteBoard;
    private boolean isScrollingByUser = false;
    private boolean isScrollingForHotZone = false;
    private final Runnable notifyCellChanged = new Runnable() {
        @Override
        public void run() {
            handler.removeCallbacks(this);
            updateAgentContainer();
        }

    };

    private StaggeredGridSpaceDecoration staggeredGridSpaceDecoration;

    public SectionRecyclerCellManager(Context mContext) {
        this(mContext, false);
    }

    public SectionRecyclerCellManager(Context mContext, boolean needStableId) {
        this.mContext = mContext;
        this.needStableId = needStableId;
        mergeRecyclerAdapter = new MergeSectionDividerAdapter(mContext);
        mergeRecyclerAdapter.setHasStableIds(needStableId);
        mergeRecyclerAdapter.setTypeRefreshListener(this);
        mergeRecyclerAdapter.setOnTopInfoChangeListener(new MergeSectionDividerAdapter.OnTopInfoChangeListener() {
            @Override
            public void onTopInfoChanged(SparseArray<TopPositionAdapter.TopInfo> topInfoSparseArray) {
                if (!(layoutManager instanceof TopLinearLayoutManager)) {
                    return;
                }

                int positionOffset = 0;
                if (recyclerView instanceof ShieldRecyclerViewInterface) {
                    positionOffset = ((ShieldRecyclerViewInterface) recyclerView).getHeaderCount();
                }

                TopLinearLayoutManager tllm = (TopLinearLayoutManager) layoutManager;
                tllm.clearTopPosition();
                if (topInfoSparseArray == null || topInfoSparseArray.size() == 0) {
                    return;
                }

                for (int i = 0; i < topInfoSparseArray.size(); i++) {
                    int pos = topInfoSparseArray.keyAt(i) + positionOffset;
                    TopPositionAdapter.TopInfo topInfo = topInfoSparseArray.valueAt(i);
                    if (topInfo == null) {
                        continue;
                    }

                    int start = pos;
                    int end = Integer.MAX_VALUE;

                    if (topInfo.start == TopPositionInterface.AutoStartTop.ALWAYS) {
                        start = 0;
                    }

                    if (topInfo.end == TopPositionInterface.AutoStopTop.MODULE) {
                        end = topInfo.moduleEnd + positionOffset;
                    } else if (topInfo.end == TopPositionInterface.AutoStopTop.SECTION) {
                        end = topInfo.sectionEnd + positionOffset;
                    } else if (topInfo.end == TopPositionInterface.AutoStopTop.CELL) {
                        end = pos;
                    }


                    tllm.addTopPosition(pos, start, end, topInfo.offset, topInfo.zPosition);
                }

            }
        });
        if (layoutManager instanceof CoveredYInterface) {
            mergeRecyclerAdapter.setCoveredYInterface((CoveredYInterface) layoutManager);
        }

        groupManager = new GroupManager();
        mOnAgentScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (scrollToTopAgent != null && idNeedScroll && newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    idNeedScroll = false;
                }
            }
        };

        mOnScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (isScrollingByUser && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    resumeExpose();
                    isScrollingByUser = false;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    exposeSectionItems(ScrollDirection.UP);
                } else if (dy < 0) {
                    exposeSectionItems(ScrollDirection.DOWN);
                } else {
                    exposeSectionItems(ScrollDirection.STATIC);
                }
            }
        };

        mHotZoneItemScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (isScrollingForHotZone && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    for (HotZoneEngine hotZoneEngine : hotZoneEngineArrayList) {
                        hotZoneEngine.scroll(ScrollDirection.STATIC, recyclerView, mergeRecyclerAdapter);
                    }

                    for (HotZoneItemEngine itemEngine : hotZoneItemEngineArrayList) {
                        itemEngine.scroll(ScrollDirection.STATIC, recyclerView, mergeRecyclerAdapter);
                    }
                    isScrollingForHotZone = false;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!isScrollingForHotZone) {
//                    Log.i("MixCellAgent1", "Scroll ================ " + " & EngSize" + hotZoneItemEngineArrayList.size() + "dy:" + dy);

                    for (HotZoneEngine hotZoneEngine : hotZoneEngineArrayList) {
                        if (dy > 0) {
                            hotZoneEngine.scroll(ScrollDirection.UP, recyclerView, mergeRecyclerAdapter);
                        } else if (dy < 0) {
                            hotZoneEngine.scroll(ScrollDirection.DOWN, recyclerView, mergeRecyclerAdapter);
                        }
                    }

                    for (HotZoneItemEngine itemEngine : hotZoneItemEngineArrayList) {
                        if (dy > 0) {
                            itemEngine.scroll(ScrollDirection.UP, recyclerView, mergeRecyclerAdapter);
                        } else if (dy < 0) {
                            itemEngine.scroll(ScrollDirection.DOWN, recyclerView, mergeRecyclerAdapter);
                        }
                    }
                }
            }
        };
    }

    public void setWhiteBoard(WhiteBoard whiteBoard) {
        this.whiteBoard = whiteBoard;
    }

    public void setCanScroll(boolean canScroll) {
        if (layoutManager instanceof LinearLayoutManagerWithSmoothOffset) {
            ((LinearLayoutManagerWithSmoothOffset) layoutManager).setScrollEnabled(canScroll);
            if (ShieldEnvironment.INSTANCE.isDebug()) {
                ShieldEnvironment.INSTANCE.getShieldLogger().d("@PageName:" + mContext.toString() + "@ setCanScroll to" + canScroll);
            }
        }
    }

    public void setMarkedScrollToTopAgentRule(boolean scrollToTopByFirstMarkedAgent) {
        this.scrollToTopByFirstMarkedAgent = scrollToTopByFirstMarkedAgent;
    }

    public void setPageName(String pageName) {
        if (mergeRecyclerAdapter != null) {
            mergeRecyclerAdapter.setPageName(pageName);
        }
    }

    public void setEnableDivider(boolean enableDivider) {
        mergeRecyclerAdapter.setEnableDivider(enableDivider);
    }

    public void setDefaultOffset(float defaultLeftOffset) {
        mergeRecyclerAdapter.setDefaultOffset(defaultLeftOffset);
        if (ShieldEnvironment.INSTANCE.isDebug()) {
            ShieldEnvironment.INSTANCE.getShieldLogger().d("@PageName:" + mContext.toString() + "@ setDefaultLeftOffset to" + defaultLeftOffset);
        }
    }

    public void setDefaultSpaceHight(float defaultSpaceHeight) {
        mergeRecyclerAdapter.setDefaultSpaceHight(defaultSpaceHeight);
        if (ShieldEnvironment.INSTANCE.isDebug()) {
            ShieldEnvironment.INSTANCE.getShieldLogger().d("@PageName:" + mContext.toString() + "@setDefaultSpaceHeight to" + defaultSpaceHeight);
        }
    }

    public void setDefaultSpaceDrawable(Drawable defaultSpaceDrawable) {
        mergeRecyclerAdapter.setDefaultSpaceDrawable(defaultSpaceDrawable);
        if (ShieldEnvironment.INSTANCE.isDebug() && defaultSpaceDrawable != null) {
            ShieldEnvironment.INSTANCE.getShieldLogger().d("@PageName:" + mContext.toString() + "@setDefaultSpaceDrawable to" + defaultSpaceDrawable.toString());
        }
    }

    public void setDefaultDivider(Drawable defaultDivider) {
        mergeRecyclerAdapter.setDefaultDivider(defaultDivider);
        if (ShieldEnvironment.INSTANCE.isDebug() && defaultDivider != null) {
            ShieldEnvironment.INSTANCE.getShieldLogger().d("@PageName:" + mContext.toString() + "@setDefaultDivider to" + defaultDivider.toString());
        }
    }

    public void setDefaultSectionDivider(Drawable defaultSectionDivider) {
        mergeRecyclerAdapter.setDefaultSectionDivider(defaultSectionDivider);
        if (ShieldEnvironment.INSTANCE.isDebug() && defaultSectionDivider != null) {
            ShieldEnvironment.INSTANCE.getShieldLogger().d("@PageName:" + mContext.toString() + "@setDefaultSectionDivider to" + defaultSectionDivider.toString());
        }
    }

    public void setBottomFooterDivider(boolean hasBottomFooterDivider) {
        mergeRecyclerAdapter.setBottomFooterDividerDecoration(hasBottomFooterDivider);
        if (ShieldEnvironment.INSTANCE.isDebug()) {
            ShieldEnvironment.INSTANCE.getShieldLogger().d("@PageName:" + mContext.toString() + "@setBottomFooterDivider to" + hasBottomFooterDivider);
        }
    }

    public void setSectionGapMode(boolean isHeaderFirst) {
        mergeRecyclerAdapter.setSectionGapMode(isHeaderFirst);
        if (ShieldEnvironment.INSTANCE.isDebug()) {
            ShieldEnvironment.INSTANCE.getShieldLogger().d("@PageName:" + mContext.toString() + "@setSectionGapMode to" + isHeaderFirst);
        }
    }

    public void setDefaultRightOffset(float defaultRightOffset) {
        mergeRecyclerAdapter.setDefaultRightOffset(defaultRightOffset);
        if (ShieldEnvironment.INSTANCE.isDebug()) {
            ShieldEnvironment.INSTANCE.getShieldLogger().d("@PageName:" + mContext.toString() + "@setDefaultRightOffset to" + defaultRightOffset);
        }
    }

    public void setDisableDecoration(boolean disableDecoration) {
        mergeRecyclerAdapter.setDisableDecoration(disableDecoration);
        if (ShieldEnvironment.INSTANCE.isDebug()) {
            ShieldEnvironment.INSTANCE.getShieldLogger().d("@PageName:" + mContext.toString() + "@setDisableDecoration to" + disableDecoration);
        }
    }

    public void setDefaultLoadingAndLoadingMoreCreator(LoadingAndLoadingMoreCreator creator) {
        this.creator = creator;
    }

    public void addHotZoneObserver(HotZoneObserverInterface hotZoneObserverInterface, String prefix) {
        if (recyclerView == null) return;
        if (hotZoneEngineMap.get(hotZoneObserverInterface) == null) {
            HotZoneEngine hotZoneEngine = new HotZoneEngine();
            hotZoneEngine.setHotZoneObserverInterface(hotZoneObserverInterface, prefix);
            hotZoneEngineMap.put(hotZoneObserverInterface, hotZoneEngine);
            hotZoneEngineArrayList.add(hotZoneEngine);
        }
    }

    public void removeHotZoneObserver(HotZoneObserverInterface hotZoneObserverInterface) {
        if (recyclerView == null) return;

        HotZoneEngine hotZoneEngine = hotZoneEngineMap.get(hotZoneObserverInterface);

        if (hotZoneEngine != null) {
            hotZoneEngineArrayList.remove(hotZoneEngine);
            hotZoneEngineMap.remove(hotZoneObserverInterface);
        }
    }

    public void resetHotZone() {
        for (HotZoneEngine hotZoneEngine : hotZoneEngineArrayList) {
            hotZoneEngine.reset();
        }
    }

    public void addHotZoneItemListener(AgentInterface agentInterface, HotZoneItemListener hotZoneItemListener) {
        if (recyclerView == null) return;

        Cell cell = findCellForAgent(agentInterface);

        if (cell == null) return;

        if (itemEngineMap.get(hotZoneItemListener) == null) {
            HotZoneItemEngine hotZoneItemEngine = new HotZoneItemEngine();
            hotZoneItemEngine.setHotZoneItemListener(cell, hotZoneItemListener, mergeRecyclerAdapter);
            itemEngineMap.put(hotZoneItemListener, hotZoneItemEngine);
            hotZoneItemEngineArrayList.add(hotZoneItemEngine);
        }

    }

    public void removeHotZoneItemListener(HotZoneItemListener hotZoneItemListener) {
        if (recyclerView == null) return;

        HotZoneItemEngine hotZoneItemEngine = itemEngineMap.get(hotZoneItemListener);

        if (hotZoneItemEngine != null) {
            hotZoneItemEngineArrayList.remove(hotZoneItemEngine);
            itemEngineMap.remove(hotZoneItemListener);
        }
    }

    public void updateAgentContainer() {
        sort = new ArrayList<Cell>(cells.values());
        Collections.sort(sort, cellComparator);
        resetAgentContainerView();

        //根据多维index（xx.xx.xx.xx）还原实际的二维结构分组。
        cellGroup = new ArrayList<>();
        ArrayList<Cell> cellArrayList = new ArrayList<>();
        CellGroupIndex currentCellGroup = null;
        for (Cell cell : sort) {
            String indexStr = cell.owner.getIndex();
            CellGroupIndex cellGroupIndex = createCellGroup(indexStr);
            if (currentCellGroup == null) {
                cellArrayList = new ArrayList<>();
                cellArrayList.add(cell);
            } else {
                boolean isSameGroup = isSameGroup(currentCellGroup, cellGroupIndex);
                if (isSameGroup) {
                    cellArrayList.add(cell);
                } else {
                    cellGroup.add(cellArrayList);
                    cellArrayList = new ArrayList<>();
                    cellArrayList.add(cell);
                }
            }
            currentCellGroup = cellGroupIndex;
        }
        if (cellArrayList != null && (!cellArrayList.isEmpty())) {
            cellGroup.add(cellArrayList);
        }
        //根据实际的二维结构数组重排index
        if (cellGroup != null) {
            for (int i = 0; i < cellGroup.size(); i++) {
                if (cellGroup.get(i) == null) continue;

                for (int j = 0; j < cellGroup.get(i).size(); j++) {
                    if (cellGroup.get(i).get(j) == null) continue;
                    try {
                        Cell cell = cellGroup.get(i).get(j);
                        cell.groupIndex = AgentInfoHelper.addZeroPrefix(i, AgentInfoHelper.getIntStrLength(cellGroup.size()));
                        cell.innerIndex = AgentInfoHelper.addZeroPrefix(j, AgentInfoHelper.getIntStrLength(cellGroup.get(i).size()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                }
            }
        }

        if (!sort.isEmpty()) {

            for (int i = 0; i < sort.size(); i++) {
                Cell c = sort.get(i);
                if (c.recyclerViewAdapter == null)
                    continue;
                FinalPieceAdapter finalPieceAdapter;

                if (c.recyclerViewAdapter instanceof FinalPieceAdapter) {
                    finalPieceAdapter = (FinalPieceAdapter) c.recyclerViewAdapter;
                } else {
                    finalPieceAdapter = new FinalPieceAdapter(mContext, (PieceAdapter) c.recyclerViewAdapter, groupManager);
                }

                groupManager.addAdapter(finalPieceAdapter, i, c.groupIndex);

                c.recyclerViewAdapter = finalPieceAdapter;
                if (whiteBoard != null) {
                    if (finalPieceAdapter.getItemCount() > 0) {
                        whiteBoard.putBoolean(ShieldConst.AGENT_VISIBILITY_KEY + c.owner.getHostName(), true);
                    } else {
                        whiteBoard.putBoolean(ShieldConst.AGENT_VISIBILITY_KEY + c.owner.getHostName(), false);
                    }
                }
                // 收集全部可见的key
                setVisibleAgentToWhiteBoard();

                if (c.owner.getSectionCellInterface() instanceof ScrollToTopOffsetInterface
                        && ((ScrollToTopOffsetInterface) c.owner.getSectionCellInterface()).needScrollToTop()) {
                    if (scrollToTopByFirstMarkedAgent) {
                        if (scrollToTopAgent == null)
                            scrollToTopAgent = c.owner;
                    } else {
                        scrollToTopAgent = c.owner;
                    }
                }

                addCellToContainerView(c);
            }
        }
        mergeRecyclerAdapter.notifyDataSetChanged();

        if (scrollToTopAgent != null && idNeedScroll) {
            scrollToPositionWithOffset(scrollToTopAgent, 0, 0, 0, true, false);
        }

        if (ShieldEnvironment.INSTANCE.isDebug()) {
            ShieldEnvironment.INSTANCE.getShieldLogger().d("@CellUpdate@" + cells.toString());
        }
        exposeSectionItems(ScrollDirection.STATIC);
        collectSectionTitle();
    }

    public ViewGroup getAgentContainerView() {
        return this.recyclerView;
    }

    @Override
    public void setAgentContainerView(RecyclerView containerView) {

        if (containerView == null) {
            return;
        }

        this.recyclerView = containerView;

        //处理LayoutManager
        layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof ShieldLayoutManagerInterface) {
            shieldLayoutManager = (ShieldLayoutManagerInterface) layoutManager;
        } else if (layoutManager == null || "android.support.v7.widget.LinearLayoutManager".equals(layoutManager.getClass().getCanonicalName())) {
            LinearLayoutManagerWithSmoothOffset layoutManager = new LinearLayoutManagerWithSmoothOffset(mContext);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            this.layoutManager = layoutManager;
            shieldLayoutManager = layoutManager;
        }

        recyclerView.setAdapter(mergeRecyclerAdapter);
        if (mCanExposeScreen) {
            recyclerView.removeOnScrollListener(mOnScrollListener);
            recyclerView.addOnScrollListener(mOnScrollListener);
        }
        recyclerView.addOnScrollListener(mHotZoneItemScrollListener);
        recyclerView.addOnScrollListener(mOnAgentScrollListener);
        if (mergeRecyclerAdapter != null && (layoutManager instanceof CoveredYInterface)) {
            mergeRecyclerAdapter.setCoveredYInterface((CoveredYInterface) layoutManager);
        }
        if (layoutManager instanceof TopLinearLayoutManager && mergeRecyclerAdapter != null) {
            ((TopLinearLayoutManager) layoutManager).addOnViewTopStateChangeListener(mergeRecyclerAdapter);
        }
    }

    public void resetAgentContainerView() {
        mergeRecyclerAdapter.clear();
        groupManager.clear();
    }

    public void addCellToContainerView(Cell cell) {
        if (cell.recyclerViewAdapter != null && cell.recyclerViewAdapter instanceof PieceAdapter)
            mergeRecyclerAdapter.addAdapter((PieceAdapter) cell.recyclerViewAdapter);
    }

    @Override
    public void notifyCellChanged() {
        handler.removeCallbacks(notifyCellChanged);
        handler.post(notifyCellChanged);
    }

    public int getGlobalPosition(AgentInterface agent, int sectionPosition, int rowPosition) {
        return getGlobalPosition(agent, sectionPosition, rowPosition, true);
    }

    /**
     * 获取 agentInterface - section - row 对应的全局 position
     *
     * @param agent
     * @param sectionPosition
     * @param rowPosition
     * @param supportCellType {@code true} 表示区分 cell type
     *                        <ol>
     *                        <li>
     *                        {@code row >= 0} 代表普通 Cell 中的 第 row 个
     *                        </li>
     *                        <li>
     *                        {@code row = -1} 代表 Header Cell
     *                        </li>
     *                        <li>
     *                        {@code row = -2} 代表 Footer Cell
     *                        </li>
     *                        </ol>
     *                        {@code false} 表示不区分 cell type，
     *                        <ol>
     *                        <li>
     *                        如果有 Header Cell, {@code row = 0} 代表 Header Cell, {@code row > 0} 代表 Header Cell 中的第 row - 1 个;<br/>
     *                        否则，{@code row >= 0} 代表普通 Cell 中的 第 row 个
     *                        </li>
     *                        <li>
     *                        如果有 Footer Cell, {@code row = count - 1} 代表 Footer Cell;<br/>
     *                        否则，{@code row >= 0} 代表普通 Cell 中的 第 row 个
     *                        </li>
     *                        </ol>
     * @return
     */
    public int getGlobalPosition(AgentInterface agent, int sectionPosition, int rowPosition, boolean supportCellType) {
        int globalPosition = -1;
        Cell targetCell = findCellForAgent(agent);
        if (mergeRecyclerAdapter != null
                && targetCell != null
                && targetCell.recyclerViewAdapter instanceof PieceAdapter) {
            globalPosition = mergeRecyclerAdapter.getGlobalPosition((PieceAdapter) targetCell.recyclerViewAdapter, sectionPosition, rowPosition, supportCellType);
        }
        return globalPosition;
    }

    public void scrollToPosition(AgentInterface agent, int section, int row) {
        scrollToPosition(agent, section, row, false);
    }

    public void scrollToPosition(AgentInterface agent, int section, int row, boolean needPauseExpose) {
        if (layoutManager != null) {
            int globalPosition = getGlobalPosition(agent, section, row);
            if (globalPosition >= 0) {
                if (needPauseExpose) {
                    pauseExpose();
                    isScrollingByUser = true;
                }
                isScrollingForHotZone = true;
                shieldLayoutManager.scrollToPositionWithOffset(globalPosition, 0, false);
            }
        }
    }

    public void scrollToPositionWithOffset(AgentInterface agent, int section, int row, int offset) {
        scrollToPositionWithOffset(agent, section, row, offset, false);
    }

    public void scrollToPositionWithOffset(AgentInterface agent, int section, int row, int offset, boolean needPauseExpose) {
        scrollToPositionWithOffset(agent, section, row, offset, false, needPauseExpose);
    }

    public void scrollToPositionWithOffset(AgentInterface agent, int section, int row, int offset, boolean needAutoOffset, boolean needPauseExpose) {
        if (layoutManager != null) {
            int globalPosition = getGlobalPosition(agent, section, row);
            if (globalPosition >= 0) {
                if (needPauseExpose) {
                    pauseExpose();
                    isScrollingByUser = true;
                }
                isScrollingForHotZone = true;

                if (needAutoOffset && (layoutManager instanceof SetAutoOffsetInterface)) {
                    shieldLayoutManager.scrollToPositionWithOffset(globalPosition,
                            offset + ((SetAutoOffsetInterface) layoutManager).getAutoOffset(), false);
                } else {
                    shieldLayoutManager.scrollToPositionWithOffset(globalPosition, offset, false);
                }
            }

        }
    }

    public void smoothScrollToPosition(AgentInterface agent, int section, int row) {
        smoothScrollToPosition(agent, section, row, null);
    }

    public void smoothScrollToPosition(AgentInterface agent, int section, int row, ArrayList<OnSmoothScrollListener> listeners) {
        smoothScrollToPosition(agent, section, row, false, listeners);
    }

    public void smoothScrollToPosition(AgentInterface agent, int section, int row, boolean needPauseExpose) {
        smoothScrollToPosition(agent, section, row, needPauseExpose, null);
    }

    public void smoothScrollToPosition(AgentInterface agent, int section, int row, boolean needPauseExpose, ArrayList<OnSmoothScrollListener> listeners) {
        smoothScrollToPositionWithOffset(agent, section, row, 0, needPauseExpose, listeners);
    }

    public void smoothScrollToPositionWithOffset(AgentInterface agent, int section, int row, int offset) {
        smoothScrollToPositionWithOffset(agent, section, row, offset, null);
    }

    public void smoothScrollToPositionWithOffset(AgentInterface agent, int section, int row, int offset, ArrayList<OnSmoothScrollListener> listeners) {
        smoothScrollToPositionWithOffset(agent, section, row, offset, false, listeners);
    }

    public void smoothScrollToPositionWithOffset(AgentInterface agent, int section, int row, int offset, boolean needPauseExpose) {
        smoothScrollToPositionWithOffset(agent, section, row, offset, needPauseExpose, null);
    }

    public void smoothScrollToPositionWithOffset(AgentInterface agent, int section, int row, int offset, boolean needPauseExpose, ArrayList<OnSmoothScrollListener> listeners) {
        smoothScrollToPositionWithOffset(agent, section, row, offset, false, needPauseExpose, listeners);
    }

    public void smoothScrollToPositionWithOffset(AgentInterface agent, int section, int row, int offset, boolean needAutoOffset, boolean needPauseExpose) {
        smoothScrollToPositionWithOffset(agent, section, row, offset, needAutoOffset, needPauseExpose, null);
    }

    public void smoothScrollToPositionWithOffset(AgentInterface agent, int section, int row, int offset, boolean needAutoOffset, boolean needPauseExpose, ArrayList<OnSmoothScrollListener> listeners) {
        if (layoutManager != null) {
            int globalPosition = getGlobalPosition(agent, section, row);
            if (globalPosition >= 0) {
                if (needPauseExpose) {
                    pauseExpose();
                    isScrollingByUser = true;
                }
                isScrollingForHotZone = true;

                if (needAutoOffset && (layoutManager instanceof SetAutoOffsetInterface)) {
                    shieldLayoutManager.scrollToPositionWithOffset(globalPosition,
                            offset + ((SetAutoOffsetInterface) layoutManager).getAutoOffset(), true, listeners);
                } else {
                    shieldLayoutManager.scrollToPositionWithOffset(globalPosition, offset, true, listeners);
                }
            }
        }
    }


    public void smoothScrollToTopWithOffset(int offset, boolean needPauseExpose) {
        smoothScrollToTopWithOffset(offset, needPauseExpose, null);
    }

    public void smoothScrollToTopWithOffset(int offset, boolean needPauseExpose, ArrayList<OnSmoothScrollListener> listeners) {
        if (layoutManager instanceof LinearLayoutManagerWithSmoothOffset) {
            if (needPauseExpose) {
                pauseExpose();
                isScrollingByUser = true;
            }
            isScrollingForHotZone = true;
            ((LinearLayoutManagerWithSmoothOffset) layoutManager).smoothScrollToPosition(0, offset, listeners);
        }
    }


    public void smoothScrollToAgentTopWithOffset(AgentInterface agent, int offset, boolean needPauseExpose) {
        smoothScrollToAgentTopWithOffset(agent, offset, false, needPauseExpose);
    }

    public void smoothScrollToAgentTopWithOffset(AgentInterface agent, int offset, boolean needAutoOffset, boolean needPauseExpose) {
        Cell targetCell = findCellForAgent(agent);
        scrollToAgentSectionTopWithOffset(targetCell, 0, offset, needAutoOffset, needPauseExpose, true);
    }

    public void smoothScrollToAgentSectionTopWithOffset(String cellkey, int section, int offset, boolean needAutoOffset, boolean needPauseExpose) {
        Cell targetCell = cells.get(cellkey);
        scrollToAgentSectionTopWithOffset(targetCell, section, offset, needAutoOffset, needPauseExpose, true);
    }

    public void smoothScrollToAgentSectionTopWithOffset(String cellkey, int section, int offset, boolean needPauseExpose) {
        smoothScrollToAgentSectionTopWithOffset(cellkey, section, offset, false, needPauseExpose);
    }

    public void scrollToAgentSectionTopWithOffset(String cellkey, int section, int offset, boolean needAutoOffset, boolean needPauseExpose, boolean isSmoothScroll) {
        Cell targetCell = cells.get(cellkey);
        scrollToAgentSectionTopWithOffset(targetCell, section, offset, needAutoOffset, needPauseExpose, isSmoothScroll);
    }

    public void scrollToAgentSectionTopWithOffset(String cellkey, int section, int offset, boolean needPauseExpose, boolean isSmoothScroll) {
        Cell targetCell = cells.get(cellkey);
        scrollToAgentSectionTopWithOffset(targetCell, section, offset, false, needPauseExpose, isSmoothScroll);
    }

//    private void smoothScrollToAgentSectionTopWithOffset(Cell targetCell, int section, int offset, boolean needPauseExpose) {
//        scrollToAgentSectionTopWithOffset(targetCell, section, offset, needPauseExpose, true);
//    }
//
//    private void scrollToAgentSectionTopWithOffset(Cell targetCell, int section, int offset, boolean needPauseExpose, boolean isSmoothScroll) {
//        scrollToAgentSectionTopWithOffset(targetCell, section, offset, false, needPauseExpose, isSmoothScroll);
//    }


    public void scrollToAgentTopWithOffset(AgentInterface agent, int offset, boolean needAutoOffset, boolean needPauseExpose, boolean isSmoothScroll) {
        scrollToAgentTopWithOffset(agent, offset, needAutoOffset, needPauseExpose, isSmoothScroll, null);
    }

    public void scrollToAgentTopWithOffset(AgentInterface agent, int offset, boolean needAutoOffset, boolean needPauseExpose, boolean isSmoothScroll, ArrayList<OnSmoothScrollListener> listeners) {
        scrollToAgentSectionTopWithOffset(agent, 0, offset, needAutoOffset, needPauseExpose, isSmoothScroll, listeners);
    }

    public void scrollToAgentSectionTopWithOffset(AgentInterface agent, int section, int offset, boolean needAutoOffset, boolean needPauseExpose, boolean isSmoothScroll) {
        scrollToAgentSectionTopWithOffset(agent, section, offset, needAutoOffset, needPauseExpose, isSmoothScroll, null);
    }

    public void scrollToAgentSectionTopWithOffset(AgentInterface agent, int section, int offset, boolean needAutoOffset, boolean needPauseExpose, boolean isSmoothScroll, ArrayList<OnSmoothScrollListener> listeners) {
        Cell targetCell = findCellForAgent(agent);
        scrollToAgentSectionTopWithOffset(targetCell, section, offset, needAutoOffset, needPauseExpose, isSmoothScroll, listeners);
    }

    private void scrollToAgentSectionTopWithOffset(Cell targetCell, int section, int offset, boolean needAutoOffset, boolean needPauseExpose, boolean isSmoothScroll) {
        scrollToAgentSectionTopWithOffset(targetCell, section, offset, needAutoOffset, needPauseExpose, isSmoothScroll, null);
    }

    private void scrollToAgentSectionTopWithOffset(Cell targetCell, int section, int offset, boolean needAutoOffset, boolean needPauseExpose, boolean isSmoothScroll, ArrayList<OnSmoothScrollListener> listeners) {
//            Cell targetCell = findCellForAgent(agent);
        if (layoutManager instanceof LinearLayoutManagerWithSmoothOffset
                && targetCell != null
                && targetCell.owner != null
                && targetCell.recyclerViewAdapter != null
                && targetCell.recyclerViewAdapter.getItemCount() > 0) {
            int globalPosition = getGlobalPosition(targetCell.owner, section, 0, false);

            if (globalPosition >= 0) {

                if (needPauseExpose) {
                    pauseExpose();
                    isScrollingByUser = true;
                }
                isScrollingForHotZone = true;

                if (needAutoOffset && (layoutManager instanceof SetAutoOffsetInterface)) {
                    shieldLayoutManager.scrollToPositionWithOffset(globalPosition, offset + ((SetAutoOffsetInterface) layoutManager).getAutoOffset(), isSmoothScroll, listeners);
                } else {
                    shieldLayoutManager.scrollToPositionWithOffset(globalPosition, offset, isSmoothScroll, listeners);
                }

            }

        }
    }


    public AgentSectionRow getAgentInfo(int globalPosition) {
        if (globalPosition < 0 || globalPosition >= mergeRecyclerAdapter.getItemCount()) {
            return null;
        }

        Pair<Integer, Integer> temSectionInfo = mergeRecyclerAdapter.getSectionIndex(globalPosition);
        if (temSectionInfo == null) {
            return null;
        }
        MergeSectionDividerAdapter.DetailSectionPositionInfo sectionInfo = mergeRecyclerAdapter.getDetailSectionPositionInfo(temSectionInfo.first, temSectionInfo.second);
        if (sectionInfo == null) {
            return null;
        }

        AgentSectionRow info = new AgentSectionRow();
        PieceAdapter adapter = mergeRecyclerAdapter.getPieceAdapter(sectionInfo.adapterIndex);
        if (adapter != null) {
            info.agentInterface = adapter.getAgentInterface();
        }
        Pair<Integer, Integer> innerPair = adapter.getInnerPosition(sectionInfo.adapterSectionIndex, sectionInfo.adapterSectionPosition);
        if (innerPair != null) {
            info.section = innerPair.first;
            info.row = innerPair.second;
            if (innerPair.second >= 0) {
                info.cellType = CellType.NORMAL;
            } else if (innerPair.second == -1) {
                info.cellType = CellType.HEADER;
            } else if (innerPair.second == -2) {
                info.cellType = CellType.FOOTER;
            }
        }

        return info;
    }

    public void setRecyclerPoolSize(AgentInterface agent, int type, int size) {
        Cell cell = findCellForAgent(agent);
        if (mergeRecyclerAdapter != null
                && cell != null
                && cell.recyclerViewAdapter != null
                && cell.recyclerViewAdapter instanceof PieceAdapter) {
            int globalType = mergeRecyclerAdapter.getGlobalType((PieceAdapter) cell.recyclerViewAdapter, type);
            if (globalType > 0) {
                recyclerView.getRecycledViewPool().setMaxRecycledViews(globalType, size);
            }
        }
    }

    public void destory() {
        finishExpose();
        mExposedEngine.stopMoveStatusDispatch();
        mExposedEngine.clearMoveStatusMap();
        if (recyclerView != null) {
            recyclerView.removeOnScrollListener(mHotZoneItemScrollListener);
            hotZoneEngineMap.clear();
            hotZoneEngineArrayList.clear();
            itemEngineMap.clear();
            hotZoneItemEngineArrayList.clear();
        }
    }

    protected void exposeSectionItems(ScrollDirection direction) {
        if (!mCanExposeScreen || layoutManager == null || mergeRecyclerAdapter == null || shieldLayoutManager == null) {
            return;
        }
        int firstPosition = shieldLayoutManager.findFirstVisibleItemPosition(false);
        int lastPostion = shieldLayoutManager.findLastVisibleItemPosition(false);
        int firstCompletePosition = shieldLayoutManager.findFirstVisibleItemPosition(true);
        int lastCompletePosition = shieldLayoutManager.findLastVisibleItemPosition(true);

        //过滤auto offset的遮挡情况
        if (layoutManager instanceof SetAutoOffsetInterface) {
            int autoOffset = ((SetAutoOffsetInterface) layoutManager).getAutoOffset();
            if (autoOffset > 0) {
                for (int index = 0; index < recyclerView.getChildCount(); index++) {
                    View itemView = recyclerView.getChildAt(index);
                    if (itemView != null) {
                        int viewPosition;
                        if (recyclerView instanceof ShieldRecyclerViewInterface) {
                            viewPosition = ((ShieldRecyclerViewInterface) recyclerView).getShieldChildAdapterPosition(itemView);
                        } else {
                            viewPosition = recyclerView.getChildAdapterPosition(itemView);
                        }
                        if (viewPosition >= firstPosition) {
                            Rect itemRect = new Rect();
                            itemView.getHitRect(itemRect);

                            if (itemRect.bottom > autoOffset) {
                                if (itemRect.top < autoOffset) {
                                    firstPosition = viewPosition;
                                } else if (itemRect.top > autoOffset) {
                                    firstCompletePosition = viewPosition;
                                    break;
                                } else {
                                    firstPosition = viewPosition;
                                    firstCompletePosition = viewPosition;
                                    break;
                                }
                            }
                        }
                    }
                }

            }
        }

        ArrayList<ExposedInfo> exposedInfos = new ArrayList<>(lastPostion - firstPosition + 2);
        for (int k = firstPosition; k <= lastPostion; k++) {

            Pair<Integer, Integer> temSectionInfo = mergeRecyclerAdapter.getSectionIndex(k);
            if (temSectionInfo == null) {
                continue;
            }
            MergeSectionDividerAdapter.DetailSectionPositionInfo sectionInfo
                    = mergeRecyclerAdapter.getDetailSectionPositionInfo(temSectionInfo.first, temSectionInfo.second);
            if (sectionInfo == null) {
                continue;
            }
            ExposedInfo exposedInfo = new ExposedInfo();
            exposedInfo.owner = mergeRecyclerAdapter.getPieceAdapter(sectionInfo.adapterIndex);
            exposedInfo.details = new ExposedDetails();
            exposedInfo.details.isComplete = false;
            exposedInfo.details.section = sectionInfo.adapterSectionIndex;
            exposedInfo.details.row = sectionInfo.adapterSectionPosition;
            exposedInfo.details.cellType = exposedInfo.owner.getCellType(exposedInfo.details.section, exposedInfo.details.row);

            if (k >= firstCompletePosition && k <= lastCompletePosition) {
                exposedInfo.details.isComplete = true;
            }

            exposedInfos.add(exposedInfo);
        }
        mExposedEngine.updateExposedSections(exposedInfos, direction);
    }

    @Override
    public void startExpose() {
        mCanExposeScreen = true;
        exposeSectionItems(ScrollDirection.STATIC);
        if (recyclerView != null) {
            recyclerView.removeOnScrollListener(mOnScrollListener);
            recyclerView.addOnScrollListener(mOnScrollListener);
        }
        if (ShieldEnvironment.INSTANCE.isDebug()) {
            ShieldEnvironment.INSTANCE.getShieldLogger().d("@PageName:" + mContext.toString() + "@StartExpose");
        }
    }

    @Override
    public void startExpose(long delayMilliseconds) {
        mExposeHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mExposeHandler.removeCallbacks(this);
                startExpose();
            }
        }, delayMilliseconds);
    }

    @Override
    public void finishExpose() {
        mCanExposeScreen = false;
        if (mExposeHandler != null) {
            mExposeHandler.removeCallbacksAndMessages(null);
        }
        if (recyclerView != null) {
            recyclerView.removeOnScrollListener(mOnScrollListener);
        }
        if (mExposedEngine != null) {
            mExposedEngine.stopExposedDispatcher();
        }

        if (ShieldEnvironment.INSTANCE.isDebug()) {
            ShieldEnvironment.INSTANCE.getShieldLogger().d("@PageName:" + mContext.toString() + "@FinishExpose");
        }
    }

    @Override
    public void pauseExpose() {
        mCanExposeScreen = false;
        if (mExposedEngine != null) {
            mExposedEngine.pauseExposedDispatcher();
        }

        if (ShieldEnvironment.INSTANCE.isDebug()) {
            ShieldEnvironment.INSTANCE.getShieldLogger().d("@PageName:" + mContext.toString() + "@PauseExpose");
        }
    }

    @Override
    public void resumeExpose() {
        mCanExposeScreen = true;
        exposeSectionItems(ScrollDirection.STATIC);

        if (ShieldEnvironment.INSTANCE.isDebug()) {
            ShieldEnvironment.INSTANCE.getShieldLogger().d("@PageName:" + mContext.toString() + "@ResumeExpose");
        }
    }

    @Override
    public void resetExposeSCI(SectionCellInterface sectionCellInterface) {
        ArrayList<ExposedInfo> exposedInfos = new ArrayList<>(mExposedEngine.getInnerInfos().size());
        for (ExposedInfo exposedInfo : mExposedEngine.getInnerInfos()) {
            if (exposedInfo.owner.getSectionCellInterface() != sectionCellInterface) {
                exposedInfos.add(exposedInfo);
            }
        }
        //先移除原有的
        mExposedEngine.updateExposedSections(exposedInfos, ScrollDirection.STATIC);
        //根据实际情况曝光
        exposeSectionItems(ScrollDirection.STATIC);
    }

    @Override
    public void resetExposeRow(SectionCellInterface sectionCellInterface, int section, int row) {
        ArrayList<ExposedInfo> exposedInfos = new ArrayList<>(mExposedEngine.getInnerInfos().size());
        for (ExposedInfo exposedInfo : mExposedEngine.getInnerInfos()) {
            if (exposedInfo.owner.getSectionCellInterface() != sectionCellInterface) {
                exposedInfos.add(exposedInfo);
            } else {
                Pair<Integer, Integer> pair = exposedInfo.owner.getInnerPosition(exposedInfo.details.section, exposedInfo.details.row);

                if (pair.first != section || pair.second != row) {
                    exposedInfos.add(exposedInfo);
                }
            }
        }
        //先移除原有的
        mExposedEngine.updateExposedSections(exposedInfos, ScrollDirection.STATIC);
        //根据实际情况曝光
        exposeSectionItems(ScrollDirection.STATIC);
    }

    @Override
    public void resetExposeExtraCell(SectionCellInterface sectionCellInterface, int section, CellType cellType) {
        ArrayList<ExposedInfo> exposedInfos = new ArrayList<>(mExposedEngine.getInnerInfos().size());
        for (ExposedInfo exposedInfo : mExposedEngine.getInnerInfos()) {
            if (exposedInfo.owner.getSectionCellInterface() != sectionCellInterface) {
                exposedInfos.add(exposedInfo);
            } else {
                CellType innerCellType = exposedInfo.owner.getCellType(exposedInfo.details.section, exposedInfo.details.row);

                if (exposedInfo.details.section != section || innerCellType != cellType) {
                    exposedInfos.add(exposedInfo);
                }
            }
        }
        //先移除原有的
        mExposedEngine.updateExposedSections(exposedInfos, ScrollDirection.STATIC);
        //根据实际情况曝光
        exposeSectionItems(ScrollDirection.STATIC);
    }

    public void dispatchAgentDisappearWithLifecycle(ScrollDirection scrollDirection) {
        mExposedEngine.dispatchAgentDisappearWithLifecycle(scrollDirection);
    }

    public void dispatchAgentAppearWithLifecycle(ScrollDirection scrollDirection) {
        mExposedEngine.dispatchAgentAappearWithLifecycle(scrollDirection);
    }

    public void storeMoveStatusMap() {
        mExposedEngine.storeMoveStatusMap();
    }

    @Override
    public void updateAgentCell(AgentInterface agent) {
        updateAgentCell(agent, UpdateAgentType.UPDATE_ALL, 0, 0, 0);
    }

    @Override
    public void updateAgentCell(AgentInterface agent, UpdateAgentType updateAgentType, int section, int row, int count) {
        Cell targetCell = findCellForAgent(agent);
        if (targetCell != null && targetCell.recyclerViewAdapter != null && targetCell.recyclerViewAdapter instanceof PieceAdapter && !((PieceAdapter) targetCell.recyclerViewAdapter).isOnBind()) {
            //瀑布流布局下动态改变spanCount
            if (layoutManager instanceof StaggeredGridLayoutManager) {
                SectionCellInterface pieceSCi = ((PieceAdapter) targetCell.recyclerViewAdapter).getSectionCellInterface();
                if (pieceSCi instanceof StaggeredGridCellInfoInterface) {
                    StaggeredGridCellInfoInterface extraInterface = (StaggeredGridCellInfoInterface) pieceSCi;
                    for (int i = 0; i < pieceSCi.getSectionCount(); i++) {
                        //只取第一个spanCount>2的模块
                        if (extraInterface.spanCount(i) > 1 && ((StaggeredGridLayoutManager) layoutManager).getSpanCount() != extraInterface.spanCount(i)) {
                            ((StaggeredGridLayoutManager) layoutManager).setSpanCount(extraInterface.spanCount(section));
                            if (extraInterface.xStaggeredGridGap(i) > 0 || extraInterface.yStaggeredGridGap(i) > 0) {
                                if (staggeredGridSpaceDecoration == null) {
                                    staggeredGridSpaceDecoration = new StaggeredGridSpaceDecoration();
                                    recyclerView.addItemDecoration(staggeredGridSpaceDecoration);
                                }
                                staggeredGridSpaceDecoration.setXGap(extraInterface.xStaggeredGridGap(i));
                                staggeredGridSpaceDecoration.setYGap(extraInterface.yStaggeredGridGap(i));
                                staggeredGridSpaceDecoration.setLeftMargin(extraInterface.staggeredGridLeftMargin(i));
                                staggeredGridSpaceDecoration.setRightMargin(extraInterface.staggeredGridRightMargin(i));
                            }
                            break;
                        }

                    }
                }
            }
            updateSectionItems((PieceAdapter) targetCell.recyclerViewAdapter, updateAgentType, section, row, count);
            exposeSectionItems(ScrollDirection.STATIC);
            collectSectionTitle();
            if (whiteBoard != null) {
                int cellCount = targetCell.recyclerViewAdapter.getItemCount();
                if (cellCount > 0) {
                    whiteBoard.putBoolean(ShieldConst.AGENT_VISIBILITY_KEY + agent.getHostName(), true);
                } else {
                    whiteBoard.putBoolean(ShieldConst.AGENT_VISIBILITY_KEY + agent.getHostName(), false);
                }
            }
            // 收集全部可见的key
            setVisibleAgentToWhiteBoard();

            for (HotZoneItemEngine itemEngine : hotZoneItemEngineArrayList) {
                itemEngine.scroll(ScrollDirection.STATIC, recyclerView, mergeRecyclerAdapter);
            }

            if (ShieldEnvironment.INSTANCE.isDebug()) {
                ShieldEnvironment.INSTANCE.getShieldLogger().d("@CellUpdate@" + cells.toString());
            }
        }
        if (scrollToTopAgent != null && idNeedScroll) {
            scrollToPositionWithOffset(scrollToTopAgent, 0, 0, 0, true, false);
        }
    }

    private void setVisibleAgentToWhiteBoard() {
        if (whiteBoard != null) {
            ArrayList<String> visibleAgentList = mergeRecyclerAdapter.getAgentVisibiltyList();
            if (visibleAgentList.size() != oldVisibleAgentList.size() || !visibleAgentList.equals(oldVisibleAgentList)) {
                oldVisibleAgentList = visibleAgentList;
                whiteBoard.putSerializable(ShieldConst.AGENT_VISIBILITY_LIST_KEY, visibleAgentList);
            }
        }
    }

    /**
     * 细分刷列表行为
     * 此处pieceAdapter的数据已经更新，但sectionInfo的数据还没有更新
     * <p>
     * See {@link UIRDriverInterface#updateAgentCell(AgentInterface, UpdateAgentType, int, int, int)}
     */
    private void updateSectionItems(PieceAdapter pieceAdapter, UpdateAgentType updateAgentType, int section, int row, int count) {
        try {
            int position = -1;
            int range = -1;
            switch (updateAgentType) {
                case UPDATE_ALL:
                    pieceAdapter.notifyDataSetChanged();
                    break;
                case INSERT_SECTION:
                    // 首先从sectionInfo中找到要插入的位置，但该位置可能本身不存在，因此通过该位置之前的最后一个位置后的第一个位置来获取
                    // 然后从adapter中找到插入的range
                    // 最后执行插入
                    position = mergeRecyclerAdapter.getGlobalPositionFromSectionInfo(pieceAdapter, section, 0);
                    range = getItemCountOfSectionRange(pieceAdapter, section, count);
                    notifyItemRangeInserted(pieceAdapter, position, range);
                    break;
                case REMOVE_SECTION:
                    // 首先从sectionInfo中找到要删除的位置，但该位置可能本身不存在，因此通过该位置之前的最后一个位置后的第一个位置来获取
                    // 然后从sectionInfo中找到要删除的range
                    // 最后执行删除
                    position = mergeRecyclerAdapter.getGlobalPositionFromSectionInfo(pieceAdapter, section, 0);
                    range = mergeRecyclerAdapter.getItemCountOfSectionRange(pieceAdapter, section, count);
                    notifyItemRangeRemoved(pieceAdapter, position, range);
                    break;
                case UPDATE_SECTION:
                    // 这里要求前后getItemCount数量一致，因此可以直接获取位置
                    position = mergeRecyclerAdapter.getGlobalPosition(pieceAdapter, section, 0);
                    range = getItemCountOfSectionRange(pieceAdapter, section, count);
                    notifyItemRangeChanged(pieceAdapter, position, range);
                    break;
                case INSERT_ROW:
                    // 首先从sectionInfo中找到要插入的位置，但该位置可能本身不存在，因此通过该位置之前的最后一个位置后的第一个位置来获取
                    // 认为range就是count，正确性由上层保证
                    // 然后执行插入
                    position = mergeRecyclerAdapter.getGlobalPositionFromSectionInfo(pieceAdapter, section, row);
                    range = count;
                    notifyItemRangeInserted(pieceAdapter, position, range);
                    break;
                case REMOVE_ROW:
                    // 首先从sectionInfo中找到要删除的位置，但该位置可能本身不存在，因此通过该位置之前的最后一个位置后的第一个位置来获取
                    // 认为range就是count，正确性由上层保证
                    // 最后执行删除
                    position = mergeRecyclerAdapter.getGlobalPositionFromSectionInfo(pieceAdapter, section, row);
                    range = count;
                    notifyItemRangeRemoved(pieceAdapter, position, range);
                    break;
                case UPDATE_ROW:
                    // 这里要求前后getItemCount数量一致，因此可以直接获取位置
                    position = mergeRecyclerAdapter.getGlobalPosition(pieceAdapter, section, row);
                    range = count;
                    notifyItemRangeChanged(pieceAdapter, position, range);
                    break;


            }
        } catch (Exception e) {
            // 对应RecyclerView中常见的：throw new IndexOutOfBoundsException("Inconsistency detected....")
            // 包括但不限于如下情况：
            // 调用notifyItemRangeChanged，但adapter的getItemCount又发生改变
            // 调用notifyItemRangeXXX，但position非法或range小于1
            // 调用notifyItemRangeInserted，但adapter的getItemCount小于position + range，且最后一个item有露出等
            ShieldEnvironment.INSTANCE.getShieldLogger().codeLogError(SectionRecyclerCellManager.class, "Probably meets an inconsistency detected problem");
        }
    }

    private void notifyItemRangeInserted(PieceAdapter adapter, int position, int range) {
        if (position != SectionDAdapter.INDEX_NOT_EXIST && range >= 1) {
            adapter.notifyItemRangeInserted(position, range);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void notifyItemRangeRemoved(PieceAdapter adapter, int position, int range) {
        if (position != SectionDAdapter.INDEX_NOT_EXIST && range >= 1) {
            adapter.notifyItemRangeRemoved(position, range);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void notifyItemRangeChanged(PieceAdapter adapter, int position, int range) {
        if (position != SectionDAdapter.INDEX_NOT_EXIST && range >= 1) {
            adapter.notifyItemRangeChanged(position, range);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 获取[section, section + count) 这些section的item count总数
     *
     * @param adapter
     * @param sectionPosition
     * @param sectionCount
     * @return
     */
    protected int getItemCountOfSectionRange(PieceAdapter adapter, int sectionPosition, int sectionCount) {
        int count = 0;
        for (int i = 0; i < sectionCount; i++) {
            int sectionIndex = sectionPosition + i;
            if (0 <= sectionIndex && sectionIndex < adapter.getSectionCount()) {
                count += adapter.getRowCount(sectionIndex);
            }
        }
        return count;
    }

    @Override
    public void updateCells(ArrayList<AgentInterface> addList, ArrayList<AgentInterface> updateList, ArrayList<AgentInterface> deleteList) {
        //添加新的
        if (addList != null && !addList.isEmpty()) {
            for (AgentInterface addAgent : addList) {
                if (addAgent.getSectionCellInterface() != null) {
                    RecyclerView.Adapter adapter = createRecyclerViewAdapter(addAgent);

                    Cell c = new Cell();
                    c.owner = addAgent;
                    c.name = addAgent.getAgentCellName();
                    c.recyclerViewAdapter = adapter;
                    c.key = getCellName(addAgent);
                    cells.put(c.key, c);
                }
            }
        }
        //更新原来有的位置
        //只更新之前存在的Agent的Cell的index
        //因为是viewgroup会有多个cell,需要把agent对应的多个cell的index都更新,之后统一notify
        if (updateList != null && !updateList.isEmpty()) {
            HashMap<String, Cell> copyOfCells = (HashMap<String, Cell>) cells.clone();
            for (AgentInterface updateCell : updateList) {
                if (updateCell.getSectionCellInterface() != null) {
                    for (Map.Entry<String, Cell> entry : copyOfCells.entrySet()) {
                        //判断owner属于该agent,并且之前的name是和目前cellNam+内部顺序一致(找到对应的Cell)
                        if (entry.getValue().owner == updateCell) {
                            //替换cell的index
                            Cell temp = entry.getValue();
                            cells.remove(entry.getKey());
                            temp.key = getCellName(updateCell);
                            cells.put(temp.key, temp);
                        }
                    }
                }
            }
        }
        //删除需要删除的
        if (deleteList != null && !deleteList.isEmpty()) {
            for (AgentInterface deleteCell : deleteList) {
                if (cells.containsKey(getCellName(deleteCell))) {
                    cells.remove(getCellName(deleteCell));
                }
            }
        }
        notifyCellChanged();
    }

    public void addAgentCell(AgentInterface agent) {
        if (agent.getSectionCellInterface() != null) {
            RecyclerView.Adapter adapter;
            SectionCellInterface cellInterface = agent.getSectionCellInterface();
            adapter = createRecyclerViewAdapter(agent);

            Cell c = new Cell();
            c.owner = agent;
            c.name = agent.getAgentCellName();
            c.recyclerViewAdapter = adapter;
            c.key = getCellName(agent);
            cells.put(c.key, c);
        }
        notifyCellChanged();
    }

    public void removeAllCells(AgentInterface agent) {
        if (cells.containsKey(getCellName(agent))) {
            cells.remove(getCellName(agent));
        }
        notifyCellChanged();
    }

    @Override
    public void onMergedTypeRefresh() {
        //costom pool size
        if (!sort.isEmpty()) {
            for (int i = 0; i < sort.size(); i++) {
                Cell c = sort.get(i);
                if (c.recyclerViewAdapter != null && c.owner.getSectionCellInterface() instanceof RecyclerPoolSizeInterface) {
                    Map<Integer, Integer> sizeMap = ((RecyclerPoolSizeInterface) c.owner.getSectionCellInterface()).recyclerableViewSizeMap();
                    if (sizeMap != null && !sizeMap.isEmpty()) {
                        for (Map.Entry<Integer, Integer> entry : sizeMap.entrySet()) {
                            setRecyclerPoolSize(c.owner, entry.getKey(), entry.getValue());
                        }
                    }
                }
            }
        }
    }

    private RecyclerView.Adapter createRecyclerViewAdapter(final AgentInterface agent) {

        if (agent == null) {
            return null;
        }

        SectionCellInterface sCellInterface = agent.getSectionCellInterface();

        if (sCellInterface == null) {
            return null;
        }

        PieceAdapter adapter = new SectionPieceAdapter(mContext, sCellInterface);

        String agentName = agent.getAgentCellName();
        if (TextUtils.isEmpty(agentName)) {
            agentName = agent.hashCode() + agent.getClass().getCanonicalName();

        }
        String cellName = "";
        if (sCellInterface instanceof CellNameInterface) {
            cellName = ((CellNameInterface) sCellInterface).getCellName();
        }
        if (TextUtils.isEmpty(cellName)) {
            cellName = sCellInterface.getClass().getCanonicalName();
        }
        adapter.setMappingKey(agentName + "-" + cellName);
        adapter.setAgentInterface(agent);
        adapter.setSectionCellInterface(sCellInterface);

        if (sCellInterface instanceof ItemIdInterface) {
            adapter = new SectionStableIdPieceAdapter(mContext, adapter, (ItemIdInterface) sCellInterface);
        }

        if (sCellInterface instanceof DividerInterface) {
            SectionDividerPieceAdapter sectionDividerPieceAdapter = new SectionDividerPieceAdapter(mContext, adapter, (DividerInterface) sCellInterface);
            if (sCellInterface instanceof DividerOffsetInterface) {
                sectionDividerPieceAdapter.setDividerOffsetInterface((DividerOffsetInterface) sCellInterface);
            }
            if (sCellInterface instanceof TopDividerInterface) {
                sectionDividerPieceAdapter.setTopDividerInterface((TopDividerInterface) sCellInterface);
            }
            if (sCellInterface instanceof SectionDividerInfoInterface) {
                sectionDividerPieceAdapter.setSectionDividerInfoInterface((SectionDividerInfoInterface) sCellInterface);
            }
            if (sCellInterface instanceof DividerInfoInterface) {
                sectionDividerPieceAdapter.setDividerInfoInterface((DividerInfoInterface) sCellInterface);
            }
            adapter = sectionDividerPieceAdapter;
        }

        if (sCellInterface instanceof SectionExtraCellInterface) {
            ExtraCellPieceAdapter extraCellPieceAdapter = new ExtraCellPieceAdapter(mContext, adapter, (SectionExtraCellInterface) sCellInterface);
            if (sCellInterface instanceof SectionExtraCellDividerOffsetInterface) {
                extraCellPieceAdapter.setExtraCellDividerOffsetInterface((SectionExtraCellDividerOffsetInterface) sCellInterface);
            }
            if (sCellInterface instanceof SectionExtraTopDividerCellInterface) {
                extraCellPieceAdapter.setExtraTopDividerCellInterface((SectionExtraTopDividerCellInterface) sCellInterface);
            }
            if (sCellInterface instanceof DividerInfoInterface) {
                extraCellPieceAdapter.setDividerInfoInterfaceForExtraCell((DividerInfoInterface) sCellInterface);
            }
            adapter = extraCellPieceAdapter;
        }

        if (sCellInterface instanceof CellStatusMoreInterface) {
            adapter = new LoadingMorePieceAdapter(mContext, adapter, (CellStatusMoreInterface) sCellInterface);
            ((LoadingMorePieceAdapter) adapter).setDefaultLoadingMoreCreator(creator);
        }

        if (sCellInterface instanceof CellStatusInterface) {
            adapter = new LoadingPieceAdapter(mContext, adapter, (CellStatusInterface) sCellInterface);
            ((LoadingPieceAdapter) adapter).setDefaultLoadingCreator(creator);
        }

        if (sCellInterface instanceof SectionLinkCellInterface) {
            SectionLinkPieceAdapter sectionLinkPieceAdapter = new SectionLinkPieceAdapter(mContext, adapter, (SectionLinkCellInterface) sCellInterface);
            if (sCellInterface instanceof SectionHeaderFooterDrawableInterface) {
                sectionLinkPieceAdapter.setHeaderFooterDrawableInterface((SectionHeaderFooterDrawableInterface) sCellInterface);
            }
            if (sCellInterface instanceof SectionTitleInterface) {
                sectionLinkPieceAdapter.setSectionTitleInterface((SectionTitleInterface) sCellInterface);
            }
            adapter = sectionLinkPieceAdapter;
        }

        if (sCellInterface instanceof ItemClickInterface) {
            adapter = new RowClickAdapter(mContext, adapter, (ItemClickInterface) sCellInterface);
        }

        if (sCellInterface instanceof ItemLongClickInterface) {
            adapter = new RowLongClickAdapter(mContext, adapter, (ItemLongClickInterface) sCellInterface);
        }

        if (sCellInterface instanceof SetTopInterface) {
            SetTopAdapter setTopAdapter = new SetTopAdapter(mContext, adapter, (SetTopInterface) sCellInterface);
            if (sCellInterface instanceof ExtraCellTopInterface) {
                setTopAdapter.setExtraCellTopInterface((ExtraCellTopInterface) sCellInterface);
            }
            if (sCellInterface instanceof OnTopViewLayoutChangeListenerInterface) {
                setTopAdapter.setOnTopViewListenerInterface((OnTopViewLayoutChangeListenerInterface) sCellInterface);
            }
            adapter = setTopAdapter;
        }

        if (sCellInterface instanceof TopPositionInterface || sCellInterface instanceof SetTopInterface || sCellInterface instanceof ExtraCellTopInterface) {
            TopPositionInterface topPositionInterface = null;
            if (sCellInterface instanceof TopPositionInterface) {
                topPositionInterface = (TopPositionInterface) sCellInterface;
            }
            TopPositionAdapter topPositionAdapter = new TopPositionAdapter(mContext, adapter, topPositionInterface);
            if (sCellInterface instanceof SetTopInterface) {
                topPositionAdapter.setSetTopInterface((SetTopInterface) sCellInterface);
            }
            if (sCellInterface instanceof SetTopParamsInterface) {
                topPositionAdapter.setSetTopParamsInterface((SetTopParamsInterface) sCellInterface);
            }
            if (sCellInterface instanceof ExtraCellTopInterface) {
                topPositionAdapter.setExtraCellTopInterface((ExtraCellTopInterface) sCellInterface);
            }
            if (sCellInterface instanceof ExtraCellTopParamsInterface) {
                topPositionAdapter.setExtraCellTopParamsInterface((ExtraCellTopParamsInterface) sCellInterface);
            }
            adapter = topPositionAdapter;
        }

        if (sCellInterface instanceof SetBottomInterface) {
            SetBottomAdapter setBottomAdapter = new SetBottomAdapter(mContext, adapter, (SetBottomInterface) sCellInterface);
            setBottomAdapter.setParentView(recyclerView);
            if (sCellInterface instanceof ExtraCellBottomInterface) {
                setBottomAdapter.setExtraCellBottomInterface((ExtraCellBottomInterface) sCellInterface);
            }
            adapter = setBottomAdapter;
        }

        if (sCellInterface instanceof SetZoomInterface) {
            adapter = new SetZoomAdapter(mContext, adapter, (SetZoomInterface) sCellInterface);
        }

        if (layoutManager instanceof StaggeredGridLayoutManager) {
            if (sCellInterface instanceof StaggeredGridCellInfoInterface) {
                adapter = new StaggeredGridCellPieceAdapter(mContext, adapter, (StaggeredGridCellInfoInterface) sCellInterface);
                ((StaggeredGridCellPieceAdapter) adapter).setStaggerGridLayoutManager((StaggeredGridLayoutManager) layoutManager);
            } else {
                adapter = new StaggeredGridCellPieceAdapter(mContext, adapter, null);
            }
        }
        return adapter;
    }

    public Cell findCellForAgent(AgentInterface agent) {
        if (agent == null) return null;
        String cellName = getCellName(agent);
        Cell cell = cells.get(cellName);
        if (cell != null) {
            return cell;
        }
        for (Map.Entry<String, Cell> entry : cells.entrySet()) {
            if (agent == entry.getValue().owner) {
                return entry.getValue();
            }
        }
        return null;
    }

    protected String getCellName(AgentInterface agent) {
        if (agent == null) return null;
        return TextUtils.isEmpty(agent.getIndex()) ? agent.getHostName() : agent.getIndex() + ":" + agent.getHostName();
    }

    public boolean isSameGroup(CellGroupIndex currentGroup, CellGroupIndex nextGroup) {
        if (!currentGroup.groupindex.equals(nextGroup.groupindex)) {
            return false;
        } else if (currentGroup.groupindex.equals(nextGroup.groupindex) && (!currentGroup.innerindex.equals(nextGroup.innerindex))) {
            return true;
        } else if (currentGroup.groupindex.equals(nextGroup.groupindex) && currentGroup.innerindex.equals(nextGroup.innerindex)) {
            if (currentGroup.childs == null) {
                return true;
            } else if (currentGroup.childs != null && nextGroup.childs != null) {
                return isSameGroup(currentGroup.childs, nextGroup.childs);
            } else {
                return true;
            }
        }
        return true;
    }

    public CellGroupIndex createCellGroup(String indexStr) {
        CellGroupIndex cellGroupIndex = new CellGroupIndex();
        char separator = '.';
        int pointIndex = indexStr.indexOf(separator);
        if (pointIndex < 0) return null;
        String groupIndex = indexStr.substring(0, pointIndex);
        cellGroupIndex.groupindex = groupIndex;
        String leftIndex = indexStr.substring(pointIndex + 1, indexStr.length());
        int leftPointIndex = leftIndex.indexOf(separator);
        if (leftPointIndex < 0) {
            cellGroupIndex.innerindex = leftIndex;
        } else {
            cellGroupIndex.innerindex = leftIndex.substring(0, leftPointIndex);
            String leftSubIndex = leftIndex.substring(leftPointIndex + 1, leftIndex.length());
            cellGroupIndex.childs = createCellGroup(leftSubIndex);
        }

        return cellGroupIndex;
    }

    public HashMap<String, Integer> getReuseIdentifierMap(String hostName) {
        return getReuseIdentifierMap(reuseIdentifierMap, hostName);
    }

    public HashMap<String, Integer> getReuseIdentifierMapForHeader(String hostName) {
        return getReuseIdentifierMap(reuseIdentifierMapForHeader, hostName);
    }

    public HashMap<String, Integer> getReuseIdentifierMapForFooter(String hostName) {
        return getReuseIdentifierMap(reuseIdentifierMapForFooter, hostName);
    }

    public HashMap<String, Integer> getCellTypeMap(String hostName) {
        return getReuseIdentifierMap(cellTypeMap, hostName);
    }

    public HashMap<String, Integer> getCellTypeMapForHeader(String hostName) {
        return getReuseIdentifierMap(cellTypeMapForHeader, hostName);
    }

    public HashMap<String, Integer> getCellTypeMapForFooter(String hostName) {
        return getReuseIdentifierMap(cellTypeMapForFooter, hostName);
    }

    protected HashMap<String, Integer> getReuseIdentifierMap(HashMap<String, HashMap<String, Integer>> mapCollection, String hostName) {
        if (TextUtils.isEmpty(hostName)) {
            return null;
        }

        if (mapCollection == null) {
            mapCollection = new HashMap<>();
        }

        HashMap<String, Integer> agentIdentifierMap = mapCollection.get(hostName);
        if (agentIdentifierMap == null) {
            agentIdentifierMap = new HashMap<>();
            mapCollection.put(hostName, agentIdentifierMap);
        }

        return agentIdentifierMap;
    }

    public PositionType getPositionType(AgentInterface agentInterface, int section, int row) {
//        Cell cell = findCellForAgent(agentInterface);
//        if (cell != null && cell.recyclerViewAdapter instanceof PieceAdapter) {
//            return mergeRecyclerAdapter.findPositionType((PieceAdapter) cell.recyclerViewAdapter, section, row);
//        }
//        return MergeSectionDividerAdapter.PositionType.UNKNOWN;
        return mergeRecyclerAdapter.findPositionType(agentInterface, section, row);
    }

    public int findFirstVisibleItemGlobalPosition() {
        if (shieldLayoutManager != null) {
            return shieldLayoutManager.findFirstVisibleItemPosition(false);
        }
        return -1;
    }

    public int findLastVisibleItemGlobalPosition() {
        if (shieldLayoutManager != null) {
            return shieldLayoutManager.findLastVisibleItemPosition(false);
        }

        return -1;
    }

    public int getAdapterItemCount() {
        if (mergeRecyclerAdapter != null) {
            return mergeRecyclerAdapter.getItemCount();
        }

        return 0;
    }

    public int findFirstCompletelyVisibleItemGlobalPosition() {
        if (shieldLayoutManager != null) {
            return shieldLayoutManager.findFirstVisibleItemPosition(true);
        }

        return -1;
    }

    public int findLastCompletelyVisibleItemGlobalPosition() {
        if (shieldLayoutManager != null) {
            return shieldLayoutManager.findLastVisibleItemPosition(true);
        }

        return -1;
    }

    protected void collectSectionTitle() {

        if (sort != null && !sort.isEmpty()) {
            sectionTitleArray.clear();
            for (int i = 0; i < sort.size(); i++) {
                Cell c = sort.get(i);
                if (!(c.recyclerViewAdapter instanceof PieceAdapter))
                    continue;
                PieceAdapter finalPieceAdapter = (PieceAdapter) c.recyclerViewAdapter;

                for (int j = 0; j < finalPieceAdapter.getSectionCount(); j++) {
                    String sectionTitle = finalPieceAdapter.getSectionTitle(j);
                    if (!TextUtils.isEmpty(sectionTitle)) {
                        sectionTitleArray.add(new SectionTitleInfo(c.key, j, sectionTitle));
                    }
                }
            }
            whiteBoard.putParcelableArrayList(ShieldConst.SECTION_TITLE_LIST_KEY, sectionTitleArray);
        }

    }

    @Override
    public void scrollToNode(@NotNull AgentScrollerParams params) {
        if (params.getScope() == ScrollScope.PAGE) {
            smoothScrollToTopWithOffset(params.offset, params.needPauseExpose, params.listenerArrayList);
        } else {
            if (params.getNodeInfo() == null) {
                return;
            }
            AgentInterface agent = params.getNodeInfo().getAgent();
            switch (params.getScope()) {
                case AGENT: {
                    scrollToAgentTopWithOffset(agent, params.offset, params.needAutoOffset, params.needPauseExpose, params.isSmooth, params.listenerArrayList);
                }
                break;
                case SECTION: {
                    scrollToAgentSectionTopWithOffset(agent, params.getNodeInfo().getSection(), params.offset, params.needAutoOffset, params.needPauseExpose, params.isSmooth, params.listenerArrayList);
                }
                break;
                case ROW: {
                    if (params.isSmooth) {
                        smoothScrollToPositionWithOffset(agent, params.getNodeInfo().getSection(), params.getNodeInfo().getCellInfo().row, params.offset, params.needAutoOffset, params.needPauseExpose, params.listenerArrayList);
                    } else {
                        scrollToPositionWithOffset(agent, params.getNodeInfo().getSection(), params.getNodeInfo().getCellInfo().row, params.offset, params.needAutoOffset, params.needPauseExpose);
                    }
                }
                break;
                case HEADER: {
                    if (params.isSmooth) {
                        smoothScrollToPositionWithOffset(agent, params.getNodeInfo().getSection(), -1, params.offset, params.needAutoOffset, params.needPauseExpose, params.listenerArrayList);
                    } else {
                        scrollToPositionWithOffset(agent, params.getNodeInfo().getSection(), -1, params.offset, params.needAutoOffset, params.needPauseExpose);
                    }
                }
                break;
                case FOOTER: {
                    if (params.isSmooth) {
                        smoothScrollToPositionWithOffset(agent, params.getNodeInfo().getSection(), -2, params.offset, params.needAutoOffset, params.needPauseExpose, params.listenerArrayList);
                    } else {
                        scrollToPositionWithOffset(agent, params.getNodeInfo().getSection(), -2, params.offset, params.needAutoOffset, params.needPauseExpose);
                    }
                }
                break;
                default: {
                    return;
                }
            }
        }

    }


    @Override
    public int getNodeGlobalPosition(@NotNull NodeInfo nodeInfo) {

        switch (nodeInfo.getScope()) {

            case AGENT:
                return getGlobalPosition(nodeInfo.getAgent(), 0, 0, false);
            case SECTION:
                return getGlobalPosition(nodeInfo.getAgent(), nodeInfo.getSection(), 0, false);
            case ROW:
                return getGlobalPosition(nodeInfo.getAgent(), nodeInfo.getSection(), nodeInfo.getRow());
            case HEADER:
                return getGlobalPosition(nodeInfo.getAgent(), nodeInfo.getSection(), NodeInfo.ROW_HEADR);
            case FOOTER:
                return getGlobalPosition(nodeInfo.getAgent(), nodeInfo.getSection(), NodeInfo.ROW_FOOTER);
        }
        return -1;
    }

    @Nullable
    @Override
    public NodeInfo getAgentInfoByGlobalPosition(int globalPosition) {
        AgentSectionRow agentSectionRow = getAgentInfo(globalPosition);
        if (agentSectionRow != null) {
            switch (agentSectionRow.cellType) {
                case NORMAL: {
                    return NodeInfo.row(agentSectionRow.agentInterface, agentSectionRow.section, agentSectionRow.row);
                }
                case HEADER: {
                    return NodeInfo.header(agentSectionRow.agentInterface, agentSectionRow.section);
                }
                case FOOTER: {
                    return NodeInfo.footer(agentSectionRow.agentInterface, agentSectionRow.section);
                }
                case LOADING:
                case LOADING_MORE: {
                    NodeInfo nodeInfo = NodeInfo.agent(agentSectionRow.agentInterface);
                    nodeInfo.getCellInfo().section = agentSectionRow.section;
                    nodeInfo.getCellInfo().row = agentSectionRow.row;
                    nodeInfo.getCellInfo().cellType = agentSectionRow.cellType;
                    return nodeInfo;
                }
                default:
                    return null;
            }
        } else return null;
    }


//    public interface OnSectionTitleChangeListener {
//        void onSectionTitleChanged(ArrayList<AgentSectionRow> sectionTitleArray);
//    }

    public static class AgentSectionRow {

        public AgentInterface agentInterface;
        public int section;
        public int row;
        public CellType cellType;
    }

    public static class SectionTitleInfo implements Parcelable {

        public static final Creator<SectionTitleInfo> CREATOR = new Creator<SectionTitleInfo>() {
            @Override
            public SectionTitleInfo createFromParcel(Parcel source) {
                return new SectionTitleInfo(source);
            }

            @Override
            public SectionTitleInfo[] newArray(int size) {
                return new SectionTitleInfo[size];
            }
        };
        public String cellKey;
        public int section;
        public String sectionTitle;

        public SectionTitleInfo(String cellKey, int section, String sectionTitle) {
            this.cellKey = cellKey;
            this.section = section;
            this.sectionTitle = sectionTitle;
        }

        public SectionTitleInfo() {
        }

        protected SectionTitleInfo(Parcel in) {
            this.cellKey = in.readString();
            this.section = in.readInt();
            this.sectionTitle = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.cellKey);
            dest.writeInt(this.section);
            dest.writeString(this.sectionTitle);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SectionTitleInfo that = (SectionTitleInfo) o;

            if (section != that.section) return false;
            if (cellKey != null ? !cellKey.equals(that.cellKey) : that.cellKey != null)
                return false;
            return sectionTitle != null ? sectionTitle.equals(that.sectionTitle) : that.sectionTitle == null;
        }

        @Override
        public int hashCode() {
            int result = cellKey != null ? cellKey.hashCode() : 0;
            result = 31 * result + section;
            result = 31 * result + (sectionTitle != null ? sectionTitle.hashCode() : 0);
            return result;
        }
    }

    public class BasicHolder extends RecyclerView.ViewHolder {

        public View view;

        public BasicHolder(View view) {
            super(view);
            this.view = view;
        }
    }

    public class CellGroupIndex {

        public String groupindex;
        public String innerindex;
        public CellGroupIndex childs;
    }
}
