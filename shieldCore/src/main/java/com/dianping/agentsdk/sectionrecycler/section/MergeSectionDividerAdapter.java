package com.dianping.agentsdk.sectionrecycler.section;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Pair;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.dianping.agentsdk.framework.AgentInterface;
import com.dianping.agentsdk.framework.DividerInfo;
import com.dianping.agentsdk.framework.LinkType;
import com.dianping.agentsdk.framework.SectionCellInterface;
import com.dianping.agentsdk.pagecontainer.SetAutoOffsetInterface;
import com.dianping.agentsdk.sectionrecycler.GroupBorderDecoration;
import com.dianping.shield.adapter.MergeAdapterTypeRefreshListener;
import com.dianping.shield.adapter.TopPositionAdapter;
import com.dianping.shield.debug.PerformanceManager;
import com.dianping.shield.entity.CellType;
import com.dianping.shield.env.ShieldEnvironment;
import com.dianping.shield.feature.TopPositionInterface;
import com.dianping.shield.layoutmanager.TopLinearLayoutManager;
import com.dianping.shield.node.PositionType;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;


/**
 * A Merged Adapter which is made of serveral PieceAdapters.
 * <p>
 * Created by runqi.wei
 * 14:57
 * 21.06.2016.
 */
public class MergeSectionDividerAdapter
        extends SectionDAdapter<MergeSectionDividerAdapter.BasicHolder>
        implements GroupBorderDecoration.GroupInfoProvider, TopLinearLayoutManager.OnViewTopStateChangeListener, SetAutoOffsetInterface {

    public static final String FILE_NAME = "MergeSharedPerferance";
    public static final String NEED_BOUNDS_KEY = "NeedBounds";
    public static final String NEED_PERFORMANCE_KEY = "NeedPerformance";
    protected SparseArray<TopPositionAdapter.TopInfo> topInfoSparseArray = new SparseArray<>();
    SparseArray<TopPositionInterface.OnTopStateChangeListener> topStateChangeListenerSparseArray = new SparseArray<>();
    private ArrayList<PieceAdapter> pieces = new ArrayList<>();
    private ArrayList<DetailSectionInfo> sectionInfos = new ArrayList<>();
    private PositionTypeManager positionTypeManager = new PositionTypeManager();
    private HashMap<Pair<String, Integer>, Integer> typeMap = new HashMap<>();
    private HashMap<String, PieceAdapter> keyAdapterMap = new HashMap<>();
    private int totalType;
    private HashMap<Pair<PieceAdapter, Long>, Long> idMap = new HashMap<>();
    private long totalId;
    private Observer mObserver = new Observer();
    private PerformanceManager performanceManager;
    private String pageName;
    private MergeAdapterTypeRefreshListener typeRefreshListener;
    private OnTopInfoChangeListener onTopInfoChangeListener;

    public MergeSectionDividerAdapter(@NonNull Context context) {
        super(context);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (recyclerView != null && needBounds()) {
            recyclerView.addItemDecoration(new GroupBorderDecoration(this));
        }

        if (needPerformance()) {
            performanceManager = new PerformanceManager(getContext());
        }
    }

    public boolean needBounds() {
        SharedPreferences preferences = getContext().getApplicationContext()
                .getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(NEED_BOUNDS_KEY, false);
    }

    public boolean needPerformance() {
        SharedPreferences preferences = getContext().getApplicationContext()
                .getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(NEED_PERFORMANCE_KEY, false);
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    /**
     * Add a {@link PieceAdapter}
     *
     * @param adapter
     */
    public void addAdapter(PieceAdapter adapter) {
        addAdapter(adapter, true);
    }

    public void addAdapter(PieceAdapter adapter, boolean needNotifyDataSet) {
        adapter.registerAdapterDataObserver(mObserver);
        pieces.add(adapter);
        updateInfo();
        if (needNotifyDataSet) {
            notifyDataSetChanged();
        }
        notifyTopInfoChanged();
    }

    /**
     * Remove a {@link PieceAdapter}
     *
     * @param adapter
     */
    public void removeAdapter(PieceAdapter adapter) {
        adapter.unregisterAdapterDataObserver(mObserver);
        pieces.remove(adapter);
        updateInfo();
        notifyDataSetChanged();
        notifyTopInfoChanged();
    }

    /**
     * Update the section info, type info and id infos,
     * Map each piece's own position, type and id to a
     * global one.
     */
    private void updateInfo() {
        updateSectionInfo();
        updateTypeInfo();
        updateIdInfo();
    }

    /**
     * Clear the section info.
     */

    public void clear() {
        clear(true);
    }

    public void clear(boolean needNotifyDataSet) {
        for (PieceAdapter adapter : pieces) {
            adapter.unregisterAdapterDataObserver(mObserver);
        }
        pieces.clear();
        updateInfo();
        if (needNotifyDataSet) {
            notifyDataSetChanged();
        }
        notifyTopInfoChanged();
    }

    public void setOnTopInfoChangeListener(OnTopInfoChangeListener onTopInfoChangeListener) {
        this.onTopInfoChangeListener = onTopInfoChangeListener;
    }

    /**
     * <p>
     * Update section info, merge the sections in the PieceAdapters
     * to a list of display sections.
     * Fro each section in PieceAdapter, it can be merged to its Previous/Next
     * section according to the adapter's {@link PieceAdapter#getPreviousLinkType(int)}
     * and {@link PieceAdapter#getNextLinkType(int)}.
     * </p>
     * <p>
     * For two neighbouring sections, if the piror one's NextLinkType is set to
     * {@link LinkType.Next#DISABLE_LINK_TO_NEXT},
     * or the subsequent one's PreviousLinkType is set to
     * {@link LinkType.Previous#DISABLE_LINK_TO_PREVIOUS},
     * the two sections will NOT be in the same display section.
     * On the other hand, if none of them is set to DISABLE_XXX
     * and one of them's LinkType is set to
     * {@link LinkType.Next#LINK_TO_NEXT}/{@link LinkType.Previous#LINK_TO_PREVIOUS},
     * they will be put in the same display section.
     * </p>
     */
    private void updateSectionInfo() {

        if (sectionInfos == null) {
            sectionInfos = new ArrayList<>();
        }

        sectionInfos.clear();
        positionTypeManager.reset();
        topInfoSparseArray.clear();
        DetailSectionInfo lastSectionInfo = null;
        int index = 0;
        int adapterCount = pieces.size();
        for (int i = 0; i < adapterCount; i++) {
            PieceAdapter adapter = pieces.get(i);
            if (adapter == null) {
                continue;
            }

            AgentInterface agentInterface = adapter.getAgentInterface();
            String agentInterfaceName = null;
            if (agentInterface != null) {
                agentInterfaceName = agentInterface.getIndex() + ":" + agentInterface.getHostName();
            }

            ArrayList<TopPositionAdapter.TopInfo> topInfoList = adapter.getTopInfoList();
            Iterator<TopPositionAdapter.TopInfo> iterator = null;
            if (topInfoList != null) {
                iterator = topInfoList.iterator();
            }
            TopPositionAdapter.TopInfo topInfoListNext = null;
            if (iterator != null && iterator.hasNext()) {
                topInfoListNext = iterator.next();
            }

            int sectionCount = adapter.getSectionCount();
            int adapterStartIndex = index;
            ArrayList<TopPositionAdapter.TopInfo> moduleTopInfos = new ArrayList<>();
            for (int j = 0; j < sectionCount; j++) {
                boolean needNewSection = needNewSection(adapter, j);
                if (lastSectionInfo == null || needNewSection) {
                    lastSectionInfo = new DetailSectionInfo();
                    lastSectionInfo.previousLinkType = adapter.getPreviousLinkType(j);
                    lastSectionInfo.nextLinkType = adapter.getNextLinkType(j);
                    sectionInfos.add(lastSectionInfo);
                } else {
                    // 更新展示 section 的 next link type 为当前最新的 section 的 next link type
                    lastSectionInfo.nextLinkType = adapter.getNextLinkType(j);
                }

                int rowCount = adapter.getRowCount(j);
                int sectionStartIndex = index;
                ArrayList<TopPositionAdapter.TopInfo> sectionTopInfos = new ArrayList<>();
                for (int k = 0; k < rowCount; k++) {
                    DetailSectionPositionInfo positionInfo = new DetailSectionPositionInfo();
                    positionInfo.adapterIndex = i;
                    positionInfo.adapterSectionIndex = j;
                    positionInfo.adapterSectionPosition = k;
                    lastSectionInfo.positionInfos.add(positionInfo);
                    positionTypeManager.appendPosition(agentInterfaceName, j, k, needNewSection);

                    if (topInfoListNext != null && topInfoListNext.section == j && topInfoListNext.row == k) {
                        topInfoSparseArray.put(index, topInfoListNext);
                        topStateChangeListenerSparseArray.put(index, topInfoListNext.onTopStateChangeListener);
                        sectionTopInfos.add(topInfoListNext);
                        moduleTopInfos.add(topInfoListNext);

                        if (iterator.hasNext()) {
                            topInfoListNext = iterator.next();
                        } else {
                            topInfoListNext = null;
                        }
                    }
                    index++;
                }
                int sectionEndIndex = index - 1;

                for (TopPositionAdapter.TopInfo top : sectionTopInfos) {
                    top.sectionStart = sectionStartIndex;
                    top.sectionEnd = sectionEndIndex;
                }
            }
            int adapterEndIndex = index - 1;
            for (TopPositionAdapter.TopInfo top : moduleTopInfos) {
                top.moduleStart = adapterStartIndex;
                top.moduleEnd = adapterEndIndex;
            }
        }

        positionTypeManager.end();
    }

    protected void notifyTopInfoChanged() {
        if (onTopInfoChangeListener != null) {
            onTopInfoChangeListener.onTopInfoChanged(topInfoSparseArray);
        }
    }

    /**
     * Returns whether this section should be a new display section.
     *
     * @param adapter
     * @param sectionIndex
     * @return
     */
    private boolean needNewSection(PieceAdapter adapter, int sectionIndex) {

        if (adapter == null || sectionIndex < 0 || adapter.getSectionCount() <= sectionIndex) {
            return false;
        }

        if (sectionInfos == null || sectionInfos.isEmpty()) {
            return true;
        }

        DetailSectionInfo lastSection = sectionInfos.get(sectionInfos.size() - 1);
        LinkType.Previous previousLinkType = adapter.getPreviousLinkType(sectionIndex);
        if (lastSection.nextLinkType == LinkType.Next.LINK_UNSAFE_BETWEEN_GROUP) {
            return false;
        } else if (lastSection.nextLinkType == LinkType.Next.DISABLE_LINK_TO_NEXT
                || previousLinkType == LinkType.Previous.DISABLE_LINK_TO_PREVIOUS) {
            return true;

        } else if (lastSection.nextLinkType == LinkType.Next.LINK_TO_NEXT
                || previousLinkType == LinkType.Previous.LINK_TO_PREVIOUS) {
            return false;

        } else {
            return true;
        }
    }

    private void updateTypeInfo() {

        if (typeMap == null) {
            typeMap = new HashMap<>();
        }

        if (keyAdapterMap == null) {
            keyAdapterMap = new HashMap<>();
        }

        for (int i = 0; i < pieces.size(); i++) {
            PieceAdapter adapter = pieces.get(i);
            if (adapter == null) {
                continue;
            }

            if (!keyAdapterMap.containsKey(adapter.getMappingKey())) {
                keyAdapterMap.put(adapter.getMappingKey(), adapter);
            }

            for (int j = 0; j < adapter.getSectionCount(); j++) {
                for (int k = 0; k < adapter.getRowCount(j); k++) {
                    int type = adapter.getItemViewType(j, k);
                    Pair<String, Integer> pair = new Pair<>(adapter.getMappingKey(), type);
                    if (!typeMap.containsKey(pair)) {
                        typeMap.put(pair, totalType);
                        totalType++;
                    }
                }
            }

        }

        if (typeRefreshListener != null) {
            typeRefreshListener.onMergedTypeRefresh();
        }

    }

    public void setTypeRefreshListener(MergeAdapterTypeRefreshListener listener) {
        typeRefreshListener = listener;
    }

    /**
     * Translate the global type to the Adapter's Own Type.
     *
     * @param type global type
     * @return Pair&lt; String, Integer&gt; a Pair contains the Adapter's MappingKey and the in-adapter type
     */
    public Pair<String, Integer> getAdapterType(int type) {
        if (typeMap != null && typeMap.size() > type) {
            for (Map.Entry<Pair<String, Integer>, Integer> entry :
                    typeMap.entrySet()) {
                if (entry.getValue() == type) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    public PieceAdapter getAdapterByMappingKey(String mappingKey) {
        if (keyAdapterMap != null && !TextUtils.isEmpty(mappingKey)) {
            return keyAdapterMap.get(mappingKey);
        }

        return null;
    }

    /**
     * Update Ids. For each id inside the Adapter, create a
     * global id for it and save it in a idMap.
     */
    private void updateIdInfo() {
        if (idMap == null) {
            idMap = new HashMap<>();
        }

        for (int i = 0; i < pieces.size(); i++) {
            PieceAdapter adapter = pieces.get(i);
            if (adapter == null) {
                continue;
            }

            for (int j = 0; j < adapter.getSectionCount(); j++) {
                for (int k = 0; k < adapter.getRowCount(j); k++) {
                    long id = adapter.getItemId(j, k);
                    Pair<PieceAdapter, Long> pair = new Pair<>(adapter, id);
                    if (!idMap.containsKey(pair)) {
                        idMap.put(pair, totalId);
                        totalId++;
                    }
                }
            }

        }
    }

    public PieceAdapter getPieceAdapter(int adapterIndex) {
        if (pieces == null || pieces.size() <= adapterIndex) {
            return null;
        }
        return pieces.get(adapterIndex);
    }

    public DetailSectionInfo getDetailSectionInfo(int displaySection) {
        if (sectionInfos == null || sectionInfos.size() <= displaySection || displaySection < 0) {
            return null;
        }

        return sectionInfos.get(displaySection);
    }

    public DetailSectionPositionInfo getDetailSectionPositionInfo
            (int displaySection, int displayPosition) {

        if (sectionInfos == null || sectionInfos.size() <= displaySection || displaySection < 0) {
            return null;
        }

        DetailSectionInfo sectionInfo = getDetailSectionInfo(displaySection);

        if (sectionInfo == null || sectionInfo.positionInfos == null
                || sectionInfo.positionInfos.size() <= displayPosition
                || displayPosition < 0) {
            return null;
        }

        return sectionInfo.positionInfos.get(displayPosition);
    }

    public PositionType findPositionType(AgentInterface agentInterface, int sectionPosition, int rowPosition) {
        return positionTypeManager.getPositionType(agentInterface.getIndex() + ":" + agentInterface.getHostName(), sectionPosition, rowPosition);
    }

    public PositionType findPositionType(PieceAdapter adapter, int sectionPosition, int rowPosition) {
        int adapterIndex = getIndex(pieces, adapter);

        if (adapterIndex >= 0 && sectionInfos != null && !sectionInfos.isEmpty()) {
            for (int i = 0; i < sectionInfos.size(); i++) {
                DetailSectionInfo sectionInfo = sectionInfos.get(i);
                if (sectionInfo != null && sectionInfo.positionInfos != null && !sectionInfo.positionInfos.isEmpty()) {
                    ArrayList<DetailSectionPositionInfo> positionInfos = sectionInfo.positionInfos;
                    for (int j = 0; j < positionInfos.size(); j++) {
                        DetailSectionPositionInfo positionInfo = positionInfos.get(j);
                        if (positionInfo != null) {
                            if (positionInfo.adapterIndex == adapterIndex
                                    && positionInfo.adapterSectionIndex == sectionPosition
                                    && positionInfo.adapterSectionPosition == rowPosition) {

                                if (j == 0 && j == positionInfos.size() - 1) {
                                    return PositionType.SINGLE;
                                } else if (j == 0) {
                                    return PositionType.FIRST;
                                } else if (j == positionInfos.size() - 1) {
                                    return PositionType.LAST;
                                } else {
                                    return PositionType.MIDDLE;
                                }
                            }
                        }
                    }
                }
            }
        }

        return PositionType.UNKNOWN;
    }

    public int getGlobalPosition(PieceAdapter adapter, int sectionPosition, int rowPosition) {
        return getGlobalPosition(adapter, sectionPosition, rowPosition, true);
    }

    /**
     * 获取 agentInterface - section - row 对应的全局 position
     *
     * @param adapter
     * @param sectionPosition
     * @param rowPosition
     * @param supportCellType {@code true} 表示区分 cell type
     *                        <ol>
     *                        <li>
     *                        {@code rowPosition >= 0} 代表普通 Cell 中的 第 rowPosition 个
     *                        </li>
     *                        <li>
     *                        {@code rowPosition = -1} 代表 Header Cell
     *                        </li>
     *                        <li>
     *                        {@code rowPosition = -2} 代表 Footer Cell
     *                        </li>
     *                        </ol>
     *                        {@code false} 表示不区分 cell type，
     *                        <ol>
     *                        <li>
     *                        如果有 Header Cell, {@code rowPosition = 0} 代表 Header Cell, {@code rowPosition > 0} 代表 Header Cell 中的第 rowPosition - 1 个;<br/>
     *                        否则，{@code rowPosition >= 0} 代表普通 Cell 中的 第 rowPosition 个
     *                        </li>
     *                        <li>
     *                        如果有 Footer Cell, {@code row = count - 1} 代表 Footer Cell;<br/>
     *                        否则，{@code rowPosition >= 0} 代表普通 Cell 中的 第 rowPosition 个
     *                        </li>
     *                        </ol>
     * @return
     */
    public int getGlobalPosition(PieceAdapter adapter, int sectionPosition, int rowPosition, boolean supportCellType) {

        int globalPosition = 0;

        int adapterIndex = getIndex(pieces, adapter);
        if (adapterIndex >= 0 && sectionInfos != null && !sectionInfos.isEmpty()) {

            for (DetailSectionInfo sectionInfo : sectionInfos) {

                if (sectionInfo != null && sectionInfo.positionInfos != null && !sectionInfo.positionInfos.isEmpty()) {

                    for (DetailSectionPositionInfo positionInfo : sectionInfo.positionInfos) {
                        if (positionInfo != null) {
                            if (positionInfo.adapterIndex == adapterIndex) {

                                Pair<Integer, Integer> innerPosition = null;
                                if (supportCellType) {
                                    innerPosition = adapter.getInnerPosition(
                                            positionInfo.adapterSectionIndex,
                                            positionInfo.adapterSectionPosition);
                                } else {
                                    innerPosition = new Pair<>(positionInfo.adapterSectionIndex, positionInfo.adapterSectionPosition);
                                }

                                if (innerPosition != null
                                        && innerPosition.first == sectionPosition
                                        && innerPosition.second == rowPosition) {

                                    return globalPosition;
                                }
                            }
                            globalPosition++;
                        }
                    }

                }

            }
        }

        return INDEX_NOT_EXIST;
    }

    public int getGlobalType(PieceAdapter adapter, int type) {
        if (typeMap != null && !typeMap.isEmpty() && adapter != null) {
            String mappingKey = adapter.getMappingKey();
            if (mappingKey != null) {
                for (Map.Entry<Pair<String, Integer>, Integer> entry : typeMap.entrySet()) {
                    if (mappingKey.equals(entry.getKey().first)
                            && type == adapter.getInnerType(entry.getKey().second)) {
                        return entry.getValue();
                    }
                }
            }
        }

        return TYPE_NOT_EXIST;
    }

    public <T> int getIndex(ArrayList<T> array, T item) {
        if (item == null || array == null || array.isEmpty()) {
            return INDEX_NOT_EXIST;
        }
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i) == item) {
                return i;
            }
        }

        return INDEX_NOT_EXIST;
    }

    @Override
    public float getSectionHeaderHeight(int section) {
        DetailSectionPositionInfo info = getDetailSectionPositionInfo(section, 0);
        if (info != null) {
            PieceAdapter adapter = pieces.get(info.adapterIndex);
            if (adapter != null) {
                return adapter.getSectionHeaderHeight(info.adapterSectionIndex);
            }
        }

        return NO_SPACE_HIGHT;
    }

    @Override
    public Drawable getSectionHeaderDrawable(int section) {
        DetailSectionPositionInfo info = getDetailSectionPositionInfo(section, 0);
        if (info != null) {
            PieceAdapter adapter = pieces.get(info.adapterIndex);
            if (adapter != null) {
                return adapter.getSectionHeaderDrawable(info.adapterSectionIndex);
            }
        }

        return null;
    }

    @Override
    public float getSectionFooterHeight(int section) {
        int row = getRowCount(section);
        DetailSectionPositionInfo info = getDetailSectionPositionInfo(section, (row - 1));
        if (info != null) {
            PieceAdapter adapter = pieces.get(info.adapterIndex);
            if (adapter != null) {
                return adapter.getSectionFooterHeight(info.adapterSectionIndex);
            }
        }

        return NO_SPACE_HIGHT;
    }

    @Override
    public Drawable getSectionFooterDrawable(int section) {
        int row = getRowCount(section);
        DetailSectionPositionInfo info = getDetailSectionPositionInfo(section, (row - 1));
        if (info != null) {
            PieceAdapter adapter = pieces.get(info.adapterIndex);
            if (adapter != null) {
                return adapter.getSectionFooterDrawable(info.adapterSectionIndex);
            }
        }

        return null;
    }

    @Override
    public boolean hasTopDividerVerticalOffset(int section, int position) {
        DetailSectionPositionInfo info = getDetailSectionPositionInfo(section, position);
        if (info != null) {
            PieceAdapter adapter = pieces.get(info.adapterIndex);
            if (adapter != null) {
                return adapter.hasTopDividerVerticalOffset(info.adapterSectionIndex, info.adapterSectionPosition);
            }
        }

        return false;
    }

    @Override
    public boolean hasBottomDividerVerticalOffset(int section, int position) {
        DetailSectionPositionInfo info = getDetailSectionPositionInfo(section, position);
        if (info != null) {
            PieceAdapter adapter = pieces.get(info.adapterIndex);
            if (adapter != null) {
                return adapter.hasBottomDividerVerticalOffset(info.adapterSectionIndex, info.adapterSectionPosition);

            }
        }
        return false;
    }

    @Override
    public boolean showTopDivider(int section, int position) {
        DetailSectionPositionInfo info = getDetailSectionPositionInfo(section, position);
        if (info != null) {
            PieceAdapter adapter =
                    pieces.get(info.adapterIndex);
            if (adapter != null) {
                return adapter.showTopDivider(info.adapterSectionIndex, info.adapterSectionPosition);
            }
        }
        return false;
    }

    @Override
    public boolean showBottomDivider(int section, int position) {
        DetailSectionPositionInfo info = getDetailSectionPositionInfo(section, position);
        if (info != null) {
            PieceAdapter adapter =
                    pieces.get(info.adapterIndex);
            if (adapter != null) {
                return adapter.showBottomDivider(info.adapterSectionIndex, info.adapterSectionPosition);
            }
        }
        return false;
    }

    @Override
    public Drawable getTopDivider(int section, int position) {
        DetailSectionPositionInfo info = getDetailSectionPositionInfo(section, position);
        if (info != null) {
            PieceAdapter adapter =
                    pieces.get(info.adapterIndex);
            if (adapter != null) {
                return adapter.getTopDivider(info.adapterSectionIndex, info.adapterSectionPosition);
            }
        }
        return null;
    }

    @Override
    public Drawable getBottomDivider(int section, int position) {
        DetailSectionPositionInfo info = getDetailSectionPositionInfo(section, position);
        if (info != null) {
            PieceAdapter adapter =
                    pieces.get(info.adapterIndex);
            if (adapter != null) {
                return adapter.getBottomDivider(info.adapterSectionIndex, info.adapterSectionPosition);
            }
        }
        return null;
    }

    @Override
    public Rect topDividerOffset(int section, int position) {
        DetailSectionPositionInfo info = getDetailSectionPositionInfo(section, position);
        if (info != null) {
            PieceAdapter adapter = pieces.get(info.adapterIndex);
            if (adapter != null) {
                return adapter.topDividerOffset(info.adapterSectionIndex, info.adapterSectionPosition);

            }
        }

        return null;
    }

    @Override
    public Rect bottomDividerOffset(int section, int position) {
        DetailSectionPositionInfo info = getDetailSectionPositionInfo(section, position);
        if (info != null) {
            PieceAdapter adapter = pieces.get(info.adapterIndex);
            if (adapter != null) {
                return adapter.bottomDividerOffset(info.adapterSectionIndex, info.adapterSectionPosition);
            }
        }

        return null;
    }

    @Override
    public int getItemViewType(int sectionIndex, int position) {
        DetailSectionPositionInfo info = getDetailSectionPositionInfo(sectionIndex, position);
        int type = -1;
        PieceAdapter adapter = null;
        int adapterSection = -1;
        int adapterRow = -1;
        if (info != null) {
            adapter = pieces.get(info.adapterIndex);
            if (adapter != null) {
                adapterSection = info.adapterSectionIndex;
                adapterRow = info.adapterSectionPosition;
                type = adapter.getItemViewType(info.adapterSectionIndex, info.adapterSectionPosition);
                Pair<String, Integer> pair = new Pair<>(adapter.getMappingKey(), type);
                if (typeMap.containsKey(pair)) {
                    return typeMap.get(pair);
                }
            }
        }
        String agentHostName = null;
        String agentClassName = null;
        if (adapter != null) {
            AgentInterface ai = adapter.getAgentInterface();
            if (ai != null) {
                agentHostName = ai.getHostName();
                agentClassName = ai.getClass().getCanonicalName();
            }
        }
        ShieldEnvironment.INSTANCE.getShieldLogger().codeLogError(this.getClass(),
                String.format(Locale.getDefault(),
                        "Could not find type %d for agent %s(%s) at agent section %d, row %d. (Global position is %d-%d)",
                        type, agentHostName, agentClassName, adapterSection, adapterRow, sectionIndex, position));
        return TYPE_NOT_EXIST;
    }

    @Override
    public long getItemId(int section, int position) {
        DetailSectionPositionInfo info = getDetailSectionPositionInfo(section, position);
        if (info != null) {
            PieceAdapter adapter =
                    pieces.get(info.adapterIndex);
            if (adapter != null) {
                long id = adapter.getItemId(info.adapterSectionIndex, info.adapterSectionPosition);
                Pair<PieceAdapter, Long> pair = new Pair<>(adapter, id);
                if (idMap.containsKey(pair)) {
                    return idMap.get(pair);
                }
            }
        }
        return 0;
    }

    @Override
    public int getSectionCount() {
        return sectionInfos.size();
    }

    @Override
    public int getRowCount(int sectionIndex) {
        if (sectionIndex < sectionInfos.size()) {
            DetailSectionInfo info = sectionInfos.get(sectionIndex);
            if (info != null && info.positionInfos != null) {
                return info.positionInfos.size();
            }
        }
        return 0;
    }

    @Override
    public void onBindViewHolder(BasicHolder holder, int sectionIndex, int position) {
        DetailSectionPositionInfo info = getDetailSectionPositionInfo(sectionIndex, position);
        if (info != null) {
            PieceAdapter adapter =
                    pieces.get(info.adapterIndex);
            if (adapter != null) {
                if (performanceManager != null) {

                    Date start = new Date();
                    adapter.onBindViewHolder(holder, info.adapterSectionIndex, info.adapterSectionPosition);
                    Date end = new Date();

                    performanceManager.insertRecord(pageName, adapter, "updateView", start.getTime(), end.getTime());

                } else {
                    adapter.onBindViewHolder(holder, info.adapterSectionIndex, info.adapterSectionPosition);
                }
            }
        }
    }

    @Override
    public BasicHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Pair<String, Integer> pair = getAdapterType(viewType);
        if (pair != null && !TextUtils.isEmpty(pair.first)) {
            PieceAdapter adapter = getAdapterByMappingKey(pair.first);
            if (adapter != null) {
                if (performanceManager != null) {

                    Date start = new Date();
                    BasicHolder holder = adapter.onCreateViewHolder(parent, pair.second);
                    Date end = new Date();

                    performanceManager.insertRecord(pageName, adapter, "onCreateView", start.getTime(), end.getTime());

                    return holder;
                } else {

                    BasicHolder holder = adapter.onCreateViewHolder(parent, pair.second);
                    if (holder == null || holder.itemView == null) {
                        AgentInterface agentInterface = adapter.getAgentInterface();
                        SectionCellInterface sectionCellInterface = adapter.getSectionCellInterface();
                        String agentName = null;
                        String hostName = null;
                        if (agentInterface != null) {
                            agentName = agentInterface.getClass().getCanonicalName();
                            hostName = agentInterface.getHostName();
                        }
                        String ciName = null;
                        if (sectionCellInterface != null) {
                            ciName = sectionCellInterface.getClass().getCanonicalName();
                        }
                        ShieldEnvironment.INSTANCE.getShieldLogger()
                                .codeLogError(this.getClass(),
                                        String.format("ItemView Man NOT be null, at Agent(host name) = %s AgentInterface %s, CellInterface %s, type = %s",
                                                hostName, agentName, ciName, pair.second));
                    }
                    return holder;

                }
            }
        }
        return null;
    }

    @Override
    public int getGroupPosition(int position) {
        Pair<Integer, Integer> sectionInfo = getSectionIndex(position);
        if (sectionInfo != null) {
            DetailSectionPositionInfo info = getDetailSectionPositionInfo(sectionInfo.first, sectionInfo.second);
            if (info != null) {
                return info.adapterIndex;
            }
        }

        return NO_GROUP;
    }

    @Override
    public String getGroupText(int position) {
        Pair<Integer, Integer> sectionInfo = getSectionIndex(position);
        if (sectionInfo != null) {
            DetailSectionPositionInfo info = getDetailSectionPositionInfo(sectionInfo.first, sectionInfo.second);
            if (info != null) {
                PieceAdapter adapter = pieces.get(info.adapterIndex);
                if (adapter != null) {
                    AgentInterface ai = adapter.getAgentInterface();
                    SectionCellInterface sci = adapter.getSectionCellInterface();
                    String aiName = "";
                    String sciName = "";
                    String hostName = "";

                    if (ai != null) {
                        aiName = ai.getClass().getSimpleName();
                        hostName = ai.getHostName();
                    }
                    if (sci != null) {
                        sciName = sci.getClass().getSimpleName();
                    }
                    return String.format("%s - %s - %s", hostName, aiName, sciName);
                }
            }
        }

        return null;
    }

    public int getItemCountOfSectionRange(PieceAdapter pieceAdapter, int sectionPosition, int sectionRange) {
        int adapterIndex = pieces.indexOf(pieceAdapter);
        if (adapterIndex >= 0) {
            int count = 0;
            if (sectionInfos != null && !sectionInfos.isEmpty()) {
                for (DetailSectionInfo sectionInfo : sectionInfos) {
                    if (sectionInfo != null && sectionInfo.positionInfos != null && !sectionInfo.positionInfos.isEmpty()) {
                        for (DetailSectionPositionInfo positionInfo : sectionInfo.positionInfos) {
                            if (positionInfo == null
                                    || positionInfo.adapterIndex < adapterIndex
                                    || (positionInfo.adapterIndex == adapterIndex && positionInfo.adapterSectionIndex < sectionPosition)) {
                                continue;
                            } else if (positionInfo.adapterIndex == adapterIndex
                                    && sectionPosition <= positionInfo.adapterSectionIndex
                                    && positionInfo.adapterSectionIndex < sectionPosition + sectionRange) {
                                count++;
                            } else {
                                // 上一个positionInfo在当前位置之前，现在的positionInfo在当前位置上或之后，则
                                return count;
                            }
                        }
                    }
                }
            }
            // 如果遍历完了都没有找到，认为是区间一直到末尾，直接返回
            return count;
        }
        return 0;
    }

    /**
     * 此方法用于局部刷新时，adapter已经更新，sectionInfo未更新的情况
     * 首先从sectionInfo中找到要插入位置之前的最后一个item位置，然后+1
     */
    public int getGlobalPositionFromSectionInfo(PieceAdapter pieceAdapter, int sectionPosition, int rowPosition) {
        int adapterIndex = getIndex(pieces, pieceAdapter);
        if (adapterIndex >= 0) {
            int globalPosition = 0;
            if (sectionInfos != null && !sectionInfos.isEmpty()) {
                for (DetailSectionInfo sectionInfo : sectionInfos) {
                    if (sectionInfo != null && sectionInfo.positionInfos != null && !sectionInfo.positionInfos.isEmpty()) {
                        for (DetailSectionPositionInfo positionInfo : sectionInfo.positionInfos) {
                            if (positionInfo == null) {
                                continue;
                            }
                            if (positionInfo.adapterIndex < adapterIndex
                                    || (positionInfo.adapterIndex == adapterIndex && positionInfo.adapterSectionIndex < sectionPosition)
                                    || (positionInfo.adapterIndex == adapterIndex && positionInfo.adapterSectionIndex == sectionPosition && positionInfo.adapterSectionPosition < rowPosition)) {
                                globalPosition++;
                            } else {
                                // 上一个positionInfo在当前位置之前，现在的positionInfo在当前位置上或之后，则
                                return globalPosition;
                            }
                        }
                    }
                }
            }
            // 如果遍历完了都没有找到，认为是插入在最后的，此时的globalPosition就是总的itemCount，直接返回
            return globalPosition;
        }
        return INDEX_NOT_EXIST;
    }

    public ArrayList<String> getAgentVisibiltyList() {
        ArrayList<String> visibleAgentList = new ArrayList<>();
        for (PieceAdapter pieceAdapter : pieces) {
            if (pieceAdapter.getItemCount() > 0) {
                visibleAgentList.add(pieceAdapter.getAgentInterface().getHostName());
            }
        }
        return visibleAgentList;
    }

    @Override
    public DividerInfo getDividerInfo(int section, int row) {
        DetailSectionPositionInfo info = getDetailSectionPositionInfo(section, row);
        if (info != null) {
            PieceAdapter adapter =
                    pieces.get(info.adapterIndex);
            if (adapter != null) {
                return adapter.getDividerInfo(info.adapterSectionIndex, info.adapterSectionPosition);
            }
        }
        return null;
    }

    @Override
    public void onViewTopStateChanged(TopLinearLayoutManager.TopState topState, int position, View view) {

        if (topStateChangeListenerSparseArray == null || topStateChangeListenerSparseArray.size() <= 0) {
            return;
        }

        TopPositionInterface.OnTopStateChangeListener onTopStateChangeListener = topStateChangeListenerSparseArray.get(position);

        if (onTopStateChangeListener == null) {
            return;
        }

        Pair<Integer, Integer> displayPos = getSectionIndex(position);
        if (displayPos == null) {
            return;
        }
        DetailSectionPositionInfo info = getDetailSectionPositionInfo(displayPos.first, displayPos.second);

        if (info == null) {
            return;
        }

        int section = info.adapterSectionIndex;
        int row = info.adapterSectionPosition;
        PieceAdapter adapter = pieces.get(info.adapterIndex);

        if (adapter == null) {
            return;
        }

        CellType cellType = adapter.getCellType(section, row);
        Pair<Integer, Integer> innerPos = adapter.getInnerPosition(section, row);

        if (innerPos == null) {
            return;
        }

        onTopStateChangeListener.onTopStageChanged(cellType, innerPos.first, innerPos.second, topState);

    }
    private int autoOffset;
    @Override
    public void setAutoOffset(int offset) {
        autoOffset = offset;
    }

    @Override
    public int getAutoOffset() {
        return autoOffset;
    }

    public interface OnTopInfoChangeListener {

        void onTopInfoChanged(SparseArray<TopPositionAdapter.TopInfo> topInfoSparseArray);
    }

    public static class BasicHolder extends RecyclerView.ViewHolder {

        public BasicHolder(View itemView) {
            super(itemView);
        }
    }

    protected static class DetailSectionInfo {

        public LinkType.Previous previousLinkType = LinkType.Previous.DEFAULT;
        public LinkType.Next nextLinkType = LinkType.Next.DEFAULT;
        public ArrayList<DetailSectionPositionInfo> positionInfos = new ArrayList<>();
    }

    public static class DetailSectionPositionInfo {

        public int adapterIndex;
        public int adapterSectionIndex;
        public int adapterSectionPosition;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DetailSectionPositionInfo that = (DetailSectionPositionInfo) o;

            if (adapterIndex != that.adapterIndex) return false;
            if (adapterSectionIndex != that.adapterSectionIndex) return false;
            return adapterSectionPosition == that.adapterSectionPosition;
        }

        @Override
        public int hashCode() {
            int result = adapterIndex;
            result = 31 * result + adapterSectionIndex;
            result = 31 * result + adapterSectionPosition;
            return result;
        }
    }

    public class PositionTypeManager {

        protected PositionType lastType = PositionType.UNKNOWN;
        protected PositionType currentType = PositionType.FIRST;
        protected Pair<String, Pair<Integer, Integer>> key;
        protected Pair<String, Pair<Integer, Integer>> lastKey;
        protected HashMap<Pair<String, Pair<Integer, Integer>>, PositionType> positionTypeHashMap = new HashMap<>();

        public void appendPosition(String agentName, int section, int row, boolean needNewSection) {
            lastKey = key;
            key = new Pair<>(agentName, new Pair<>(section, row));
            if (needNewSection && row == 0) {
                if (lastType == PositionType.FIRST) {
                    lastType = PositionType.SINGLE;
                } else {
                    lastType = PositionType.LAST;
                }
                currentType = PositionType.FIRST;
                set(lastKey, lastType);
                set(key, currentType);
            } else {
                lastType = currentType;
                currentType = PositionType.MIDDLE;
                set(key, currentType);
            }
        }

        public void end() {
            if (currentType == PositionType.FIRST) {
                set(key, PositionType.SINGLE);
            } else {
                set(key, PositionType.LAST);
            }
        }

        public void reset() {
            key = null;
            lastKey = null;
            currentType = PositionType.FIRST;
            lastType = PositionType.UNKNOWN;
            positionTypeHashMap.clear();
        }

        public PositionType getPositionType(String agentName, int section, int row) {
            PositionType type = positionTypeHashMap.get(new Pair<>(agentName, new Pair<>(section, row)));
            return type == null ? PositionType.UNKNOWN : type;
        }

        protected void set(Pair<String, Pair<Integer, Integer>> key, PositionType type) {
            if (key != null) {
                positionTypeHashMap.put(key, type);
            }
        }

    }

    public class Observer extends RecyclerView.AdapterDataObserver {

        public void onChanged() {
            updateInfo();
            notifyDataSetChanged();
            notifyTopInfoChanged();
        }

        public void onItemRangeChanged(int positionStart, int itemCount) {
            updateInfo();
            notifyItemRangeChanged(positionStart, itemCount);
            notifyTopInfoChanged();
        }

        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            // fallback to onItemRangeChanged(positionStart, itemCount) if app
            // does not override this method.
            onItemRangeChanged(positionStart, itemCount);
            notifyTopInfoChanged();
        }

        public void onItemRangeInserted(int positionStart, int itemCount) {
            updateInfo();
            notifyItemRangeInserted(positionStart, itemCount);
            notifyTopInfoChanged();
        }

        public void onItemRangeRemoved(int positionStart, int itemCount) {
            updateInfo();
            notifyItemRangeRemoved(positionStart, itemCount);
            notifyTopInfoChanged();
        }

        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            updateInfo();
            notifyItemMoved(fromPosition, toPosition);
            notifyTopInfoChanged();
        }
    }
}
