package com.dianping.agentsdk.manager;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;

import com.dianping.agentsdk.adapter.ExtraCellPieceAdapter;
import com.dianping.agentsdk.adapter.FinalPieceAdapter;
import com.dianping.agentsdk.adapter.LoadingMorePieceAdapter;
import com.dianping.agentsdk.adapter.LoadingPieceAdapter;
import com.dianping.agentsdk.adapter.RowClickAdapter;
import com.dianping.agentsdk.adapter.SectionDividerPieceAdapter;
import com.dianping.agentsdk.adapter.SectionLinkPieceAdapter;
import com.dianping.agentsdk.adapter.SectionPieceAdapter;
import com.dianping.agentsdk.adapter.SectionStableIdPieceAdapter;
import com.dianping.agentsdk.adapter.SetBottomAdapter;
import com.dianping.agentsdk.adapter.SetTopAdapter;
import com.dianping.agentsdk.adapter.SetZoomAdapter;
import com.dianping.agentsdk.framework.AgentInterface;
import com.dianping.agentsdk.framework.Cell;
import com.dianping.agentsdk.framework.CellManagerInterface;
import com.dianping.agentsdk.framework.CellStatusInterface;
import com.dianping.agentsdk.framework.CellStatusMoreInterface;
import com.dianping.agentsdk.framework.DividerInterface;
import com.dianping.agentsdk.framework.ItemClickInterface;
import com.dianping.agentsdk.framework.ItemIdInterface;
import com.dianping.agentsdk.framework.SectionCellInterface;
import com.dianping.agentsdk.framework.SectionExtraCellInterface;
import com.dianping.agentsdk.framework.SectionLinkCellInterface;
import com.dianping.agentsdk.framework.WhiteBoard;
import com.dianping.agentsdk.sectionrecycler.layoutmanager.LinearLayoutManagerWithSmoothOffset;
import com.dianping.agentsdk.sectionrecycler.section.MergeSectionDividerAdapter;
import com.dianping.agentsdk.sectionrecycler.section.PieceAdapter;
import com.dianping.agentsdk.utils.AgentInfoHelper;
import com.dianping.shield.adapter.MergeAdapterTypeRefreshListener;
import com.dianping.shield.consts.ShieldConst;
import com.dianping.shield.entity.CellType;
import com.dianping.shield.entity.ExposedDetails;
import com.dianping.shield.entity.ExposedInfo;
import com.dianping.shield.entity.ScrollDirection;
import com.dianping.shield.feature.ExposeScreenLoadedInterface;
import com.dianping.shield.feature.HotZoneObserverInterface;
import com.dianping.shield.feature.LoadingAndLoadingMoreCreator;
import com.dianping.shield.feature.RecyclerPoolSizeInterface;
import com.dianping.shield.feature.SetBottomInterface;
import com.dianping.shield.feature.SetTopInterface;
import com.dianping.shield.feature.SetZoomInterface;
import com.dianping.shield.utils.ExposedEngine;
import com.dianping.shield.utils.HotZoneEngine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by hezhi on 15/12/11.
 */
public class SectionRecyclerCellManager implements CellManagerInterface<RecyclerView>, MergeAdapterTypeRefreshListener, ExposeScreenLoadedInterface {

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
    protected LinearLayoutManager layoutManager;
    protected MergeSectionDividerAdapter mergeRecyclerAdapter;
    protected boolean needStableId;
    protected View focusedView;
    protected RecyclerView.OnScrollListener mOnScrollListener;
    protected boolean mCanExposeScreen;
    protected Handler mExposeHandler = new Handler();
    protected ExposedEngine mExposedEngine = new ExposedEngine();
    protected LoadingAndLoadingMoreCreator creator;
    //    protected HotZoneEngine hotZoneEngine = new HotZoneEngine();
    protected Map<HotZoneObserverInterface, RecyclerView.OnScrollListener> scrollListenerMap = new LinkedHashMap<>();
    ArrayList<HotZoneEngine> hotZoneEngineArrayList = new ArrayList<>();
    private boolean isScrollingByUser = false;
    private boolean isScrollingForHotZone = false;
    private WhiteBoard whiteBoard;
    private final Runnable notifyCellChanged = new Runnable() {
        @Override
        public void run() {
            handler.removeCallbacks(this);
            updateAgentContainer();
        }

    };

    public SectionRecyclerCellManager(Context mContext) {
        this(mContext, false);
    }

    public SectionRecyclerCellManager(Context mContext, boolean needStableId) {
        this.mContext = mContext;
        this.needStableId = needStableId;
        mergeRecyclerAdapter = new MergeSectionDividerAdapter(mContext);
        mergeRecyclerAdapter.setHasStableIds(needStableId);
        mergeRecyclerAdapter.setTypeRefreshListener(this);
        groupManager = new GroupManager();

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
    }

    public void setWhiteBoard(WhiteBoard whiteBoard) {
        this.whiteBoard = whiteBoard;
    }

    public void setPageName(String pageName) {
        if (mergeRecyclerAdapter != null) {
            mergeRecyclerAdapter.setPageName(pageName);
        }
    }

    public void setEnableDivider(boolean enableDivider) {
        mergeRecyclerAdapter.setEnableDivider(enableDivider);
    }

    public void setDefaultOffset(float defaultOffset) {
        mergeRecyclerAdapter.setDefaultOffset(defaultOffset);
    }

    public void setDefaultSpaceHight(float defaultSpaceHight) {
        mergeRecyclerAdapter.setDefaultSpaceHight(defaultSpaceHight);
    }

    public void setBottomFooterDivider(boolean hasBottomFooterDivider) {
        mergeRecyclerAdapter.setBottomFooterDividerDecoration(hasBottomFooterDivider);
    }

    public void setDefaultLoadingAndLoadingMoreCreator(LoadingAndLoadingMoreCreator creator) {
        this.creator = creator;
    }

    public void addHotZoneObserver(HotZoneObserverInterface hotZoneObserverInterface, String prefix) {
        if (recyclerView == null) return;

        final HotZoneEngine hotZoneEngine = new HotZoneEngine();
        hotZoneEngine.setHotZoneObserverInterface(hotZoneObserverInterface, prefix);

        RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (isScrollingForHotZone && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    isScrollingForHotZone = false;
                }
            }


            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!isScrollingForHotZone) {
                    if (dy > 0) {
                        hotZoneEngine.scroll(ScrollDirection.UP, recyclerView, mergeRecyclerAdapter);
                    } else if (dy < 0) {
                        hotZoneEngine.scroll(ScrollDirection.DOWN, recyclerView, mergeRecyclerAdapter);
                    }
                }
            }
        };
        recyclerView.addOnScrollListener(onScrollListener);
        scrollListenerMap.put(hotZoneObserverInterface, onScrollListener);
        hotZoneEngineArrayList.add(hotZoneEngine);

    }

    public void removeHotZoneObserver(HotZoneObserverInterface hotZoneObserverInterface) {
        if (recyclerView == null) return;
        recyclerView.removeOnScrollListener(scrollListenerMap.get(hotZoneObserverInterface));
        scrollListenerMap.remove(hotZoneObserverInterface);
        HotZoneEngine targetEngine = null;
        for (HotZoneEngine hotZoneEngine : hotZoneEngineArrayList) {
            if (hotZoneEngine.hotZoneObserverInterface == hotZoneObserverInterface) {
                targetEngine = hotZoneEngine;
                break;
            }
        }
        hotZoneEngineArrayList.remove(targetEngine);
    }

    public void resetHotZone() {
        for (HotZoneEngine hotZoneEngine : hotZoneEngineArrayList) {
            hotZoneEngine.reset();
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
                    if (finalPieceAdapter.getTotalItemCount() > 0) {
                        whiteBoard.putBoolean(ShieldConst.AGENT_VISIBILITY_KEY + c.owner.getHostName(), true);
                    } else {
                        whiteBoard.putBoolean(ShieldConst.AGENT_VISIBILITY_KEY + c.owner.getHostName(), false);
                    }
                }

                addCellToContainerView(c);
            }
        }
        mergeRecyclerAdapter.notifyDataSetChanged();
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

        if (recyclerView.getLayoutManager() == null) {
            layoutManager = new LinearLayoutManagerWithSmoothOffset(mContext);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
        } else if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        }
        recyclerView.setAdapter(mergeRecyclerAdapter);
        if (mCanExposeScreen) {
            recyclerView.removeOnScrollListener(mOnScrollListener);
            recyclerView.addOnScrollListener(mOnScrollListener);
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

    public void scrollToPosition(AgentInterface agent, int section, int row) {
        scrollToPosition(agent, section, row, false);
    }

    public void scrollToPosition(AgentInterface agent, int section, int row, boolean needPauseExpose) {
        if (layoutManager != null) {
            Cell targetCell = findCellForAgent(agent);
            if (mergeRecyclerAdapter != null
                    && targetCell != null
                    && targetCell.recyclerViewAdapter != null
                    && targetCell.recyclerViewAdapter instanceof PieceAdapter) {
                int globalPosition = mergeRecyclerAdapter.getGlobalPosition((PieceAdapter) targetCell.recyclerViewAdapter, section, row);

                if (globalPosition >= 0) {
                    // 为 dianping 的 PullToRefreshRecyclerView 提供兼容，
                    // 考虑到其中添加的一个 header view，调整相应的position
                    if (recyclerView != null && recyclerView.getAdapter() != null) {
                        RecyclerView.Adapter adapter = recyclerView.getAdapter();
                        if ("HeaderViewRecyclerAdapter".equals(adapter.getClass().getSimpleName())) {
                            globalPosition++;
                        }
                    }
                    if (needPauseExpose) {
                        pauseExpose();
                        isScrollingByUser = true;
                    }
                    isScrollingForHotZone = true;
                    layoutManager.scrollToPosition(globalPosition);
                }

            }
        }
    }

    public void scrollToPositionWithOffset(AgentInterface agent, int section, int row, int offset) {
        scrollToPositionWithOffset(agent, section, row, offset, false);
    }

    public void scrollToPositionWithOffset(AgentInterface agent, int section, int row, int offset, boolean needPauseExpose) {
        if (layoutManager != null) {
            Cell targetCell = findCellForAgent(agent);
            if (mergeRecyclerAdapter != null
                    && targetCell != null
                    && targetCell.recyclerViewAdapter != null
                    && targetCell.recyclerViewAdapter instanceof PieceAdapter) {
                int globalPosition = mergeRecyclerAdapter.getGlobalPosition((PieceAdapter) targetCell.recyclerViewAdapter, section, row);

                if (globalPosition >= 0) {
                    // 为 dianping 的 PullToRefreshRecyclerView 提供兼容，
                    // 考虑到其中添加的一个 header view，调整相应的position
                    if (recyclerView != null && recyclerView.getAdapter() != null) {
                        RecyclerView.Adapter adapter = recyclerView.getAdapter();
                        if ("HeaderViewRecyclerAdapter".equals(adapter.getClass().getSimpleName())) {
                            globalPosition++;
                        }
                    }
                    if (needPauseExpose) {
                        pauseExpose();
                        isScrollingByUser = true;
                    }
                    isScrollingForHotZone = true;
                    layoutManager.scrollToPositionWithOffset(globalPosition, offset);
                }

            }
        }
    }

    public void smoothScrollToPosition(AgentInterface agent, int section, int row) {
        smoothScrollToPosition(agent, section, row, false);
    }

    public void smoothScrollToPosition(AgentInterface agent, int section, int row, boolean needPauseExpose) {
        smoothScrollToPositionWithOffset(agent, section, row, 0, needPauseExpose);
    }

    public void smoothScrollToPositionWithOffset(AgentInterface agent, int section, int row, int offset) {
        smoothScrollToPositionWithOffset(agent, section, row, offset, false);
    }

    public void smoothScrollToPositionWithOffset(AgentInterface agent, int section, int row, int offset, boolean needPauseExpose) {
        if (layoutManager instanceof LinearLayoutManagerWithSmoothOffset) {
            Cell targetCell = findCellForAgent(agent);
            if (mergeRecyclerAdapter != null
                    && targetCell != null
                    && targetCell.recyclerViewAdapter != null
                    && targetCell.recyclerViewAdapter instanceof PieceAdapter) {
                int globalPosition = mergeRecyclerAdapter.getGlobalPosition((PieceAdapter) targetCell.recyclerViewAdapter, section, row);

                if (globalPosition >= 0) {

                    // 为 dianping 的 PullToRefreshRecyclerView 提供兼容，
                    // 考虑到其中添加的一个 header view，调整相应的position
                    if (recyclerView != null && recyclerView.getAdapter() != null) {
                        RecyclerView.Adapter adapter = recyclerView.getAdapter();
                        if ("HeaderViewRecyclerAdapter".equals(adapter.getClass().getSimpleName())) {
                            globalPosition++;
                        }
                    }

                    if (needPauseExpose) {
                        pauseExpose();
                        isScrollingByUser = true;
                    }
                    isScrollingForHotZone = true;
                    ((LinearLayoutManagerWithSmoothOffset) layoutManager).smoothScrollToPosition(globalPosition, offset);
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
        info.section = sectionInfo.adapterSectionIndex;
        info.row = sectionInfo.adapterSectionPosition;

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

    private void exposeSectionItems(ScrollDirection direction) {
        if (!mCanExposeScreen || layoutManager == null || mergeRecyclerAdapter == null) {
            return;
        }
        int firstPosition = layoutManager.findFirstVisibleItemPosition();
        int lastPostion = layoutManager.findLastVisibleItemPosition();
        int firstCompletePosition = layoutManager.findFirstCompletelyVisibleItemPosition();
        int lastCompletePosition = layoutManager.findLastCompletelyVisibleItemPosition();

        // 为 dianping 的 PullToRefreshRecyclerView 提供兼容，
        // 考虑到其中添加的一个 header view，调整相应的position
        if (recyclerView != null && recyclerView.getAdapter() != null) {
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            if ("HeaderViewRecyclerAdapter".equals(adapter.getClass().getSimpleName())) {
                firstPosition--;
                lastPostion--;
                firstCompletePosition--;
                lastCompletePosition--;
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
            mExposeHandler.removeCallbacks(null);
        }
        if (mExposedEngine != null) {
            mExposedEngine.stopExposedDispatcher();
        }
        if (recyclerView != null) {
            recyclerView.removeOnScrollListener(mOnScrollListener);
        }
    }

    @Override
    public void pauseExpose() {
        mCanExposeScreen = false;
        if (mExposedEngine != null) {
            mExposedEngine.pauseExposedDispatcher();
        }
    }

    @Override
    public void resumeExpose() {
        mCanExposeScreen = true;
        exposeSectionItems(ScrollDirection.STATIC);
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

    @Override
    public void updateAgentCell(AgentInterface agent) {
        Cell targetCell = findCellForAgent(agent);
        if (targetCell != null && targetCell.recyclerViewAdapter != null && targetCell.recyclerViewAdapter instanceof PieceAdapter && !((PieceAdapter) targetCell.recyclerViewAdapter).isOnBind()) {
            (targetCell.recyclerViewAdapter).notifyDataSetChanged();
            if (whiteBoard != null) {
                int cellCount = ((PieceAdapter) (targetCell.recyclerViewAdapter)).getTotalItemCount();
                if (cellCount > 0) {
                    whiteBoard.putBoolean(ShieldConst.AGENT_VISIBILITY_KEY + agent.getHostName(), true);
                } else {
                    whiteBoard.putBoolean(ShieldConst.AGENT_VISIBILITY_KEY + agent.getHostName(), false);
                }
            }

        }
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
                    cells.put(getCellName(addAgent), c);
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
                            cells.put(getCellName(updateCell), temp);
                        }
                    }
                }
            }
        }
        //删除需要删除的
        if (deleteList != null && !deleteList.isEmpty()) {
            for (AgentInterface deleteCell : deleteList) {
                Iterator<Map.Entry<String, Cell>> itr = cells.entrySet().iterator();
                while (itr.hasNext()) {
                    Cell c = itr.next().getValue();
                    if (c.owner == deleteCell) {
                        itr.remove();
                    }
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
            cells.put(getCellName(agent), c);
        }
        notifyCellChanged();
    }

    public void removeAllCells(AgentInterface agent) {
        Iterator<Map.Entry<String, Cell>> itr = cells.entrySet().iterator();
        while (itr.hasNext()) {
            Cell c = itr.next().getValue();
            if (c.owner == agent) {
                itr.remove();
            }
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
            agentName = agent.getClass().getCanonicalName();

        }
        String cellName = sCellInterface.getClass().getCanonicalName();

        adapter.setMappingKey(agent.hashCode() + "-" + agentName + "-" + cellName);
        adapter.setAgentInterface(agent);
        adapter.setSectionCellInterface(sCellInterface);

        if (sCellInterface instanceof ItemIdInterface) {
            adapter = new SectionStableIdPieceAdapter(mContext, adapter, (ItemIdInterface) sCellInterface);
        }

        if (sCellInterface instanceof DividerInterface) {
            adapter = new SectionDividerPieceAdapter(mContext, adapter, (DividerInterface) sCellInterface);
        }

        if (sCellInterface instanceof SectionExtraCellInterface) {
            adapter = new ExtraCellPieceAdapter(mContext, adapter, (SectionExtraCellInterface) sCellInterface);
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
            adapter = new SectionLinkPieceAdapter(mContext, adapter, (SectionLinkCellInterface) sCellInterface);
        }

        if (sCellInterface instanceof ItemClickInterface) {
            adapter = new RowClickAdapter(mContext, adapter, (ItemClickInterface) sCellInterface);
        }

        if (sCellInterface instanceof SetTopInterface) {
            adapter = new SetTopAdapter(mContext, adapter, (SetTopInterface) sCellInterface);
        }

        if (sCellInterface instanceof SetBottomInterface) {
            adapter = new SetBottomAdapter(mContext, adapter, (SetBottomInterface) sCellInterface);
        }

        if (sCellInterface instanceof SetZoomInterface) {
            adapter = new SetZoomAdapter(mContext, adapter, (SetZoomInterface) sCellInterface);
        }
        return adapter;
    }

    public Cell findCellForAgent(AgentInterface c) {
        String cellName = getCellName(c);
        if (cells.get(cellName) != null) {
            return cells.get(cellName);
        }
        for (Map.Entry<String, Cell> entry : cells.entrySet()) {
            if (c == entry.getValue().owner) {
                return entry.getValue();
            }
        }
        return null;
    }

    protected String getCellName(AgentInterface agent) {
        return TextUtils.isEmpty(agent.getIndex()) ? agent.getAgentCellName() : agent.getIndex() + ":" + agent.getAgentCellName();
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

    public static class AgentSectionRow {
        public AgentInterface agentInterface;
        public int section;
        public int row;
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
