package com.dianping.agentsdk.sectionrecycler.section;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;

import com.dianping.shield.debug.PerformanceManager;
import com.dianping.agentsdk.framework.AgentInterface;
import com.dianping.agentsdk.framework.LinkType;
import com.dianping.agentsdk.framework.SectionCellInterface;
import com.dianping.agentsdk.sectionrecycler.GroupBorderDecoration;
import com.dianping.shield.adapter.MergeAdapterTypeRefreshListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
        implements GroupBorderDecoration.GroupInfoPrivider {

    private ArrayList<PieceAdapter> pieces = new ArrayList<>();
    public static final String FILE_NAME = "MergeSharedPerferance";
    public static final String NEED_BOUNDS_KEY = "NeedBounds";
    public static final String NEED_PERFORMANCE_KEY = "NeedPerformance";

    private ArrayList<DetailSectionInfo> sectionInfos = new ArrayList<>();
    private HashMap<Pair<String, Integer>, Integer> typeMap = new HashMap<>();
    private HashMap<String, PieceAdapter> keyAdapterMap = new HashMap<>();
    private int totalType;
    private HashMap<Pair<String, Long>, Long> idMap = new HashMap<>();
    private long totalId;

    private Observer mObserver = new Observer();

    private PerformanceManager performanceManager;
    private String pageName;

    private MergeAdapterTypeRefreshListener typeRefreshListener;

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
        adapter.registerAdapterDataObserver(mObserver);
        pieces.add(adapter);
        updateInfo();
        notifyDataSetChanged();
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
        for (PieceAdapter adapter : pieces) {
            adapter.unregisterAdapterDataObserver(mObserver);
        }
        pieces.clear();
        updateInfo();
        notifyDataSetChanged();
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

        DetailSectionInfo lastSectionInfo = null;
        for (int i = 0; i < pieces.size(); i++) {
            PieceAdapter adapter = pieces.get(i);
            if (adapter == null) {
                continue;
            }

            for (int j = 0; j < adapter.getSectionCount(); j++) {
                if (lastSectionInfo == null || needNewSection(adapter, j)) {
                    lastSectionInfo = new DetailSectionInfo();
                    lastSectionInfo.previousLinkType = adapter.getPreviousLinkType(j);
                    lastSectionInfo.nextLinkType = adapter.getNextLinkType(j);
                    sectionInfos.add(lastSectionInfo);
                } else {
                    // 更新展示 section 的 next link type 为当前最新的 section 的 next link type
                    lastSectionInfo.nextLinkType = adapter.getNextLinkType(j);
                }

                for (int k = 0; k < adapter.getRowCount(j); k++) {
                    DetailSectionPositionInfo positionInfo = new DetailSectionPositionInfo();
                    positionInfo.adapterIndex = i;
                    positionInfo.adapterSectionIndex = j;
                    positionInfo.adapterSectionPosition = k;
                    lastSectionInfo.positionInfos.add(positionInfo);
                }
            }

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

        if (lastSection.nextLinkType == LinkType.Next.DISABLE_LINK_TO_NEXT
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
                    Pair<String, Long> pair = new Pair<>(adapter.getMappingKey(), id);
                    if (!idMap.containsKey(pair)) {
                        idMap.put(pair, totalId);
                        totalId++;
                    }
                }
            }

        }
    }

    public PieceAdapter getPieceAdapter(int adapterIndex){
        if (pieces == null || pieces.size() <= adapterIndex){
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

    public int getGlobalPosition(PieceAdapter adapter, int sectionPosition, int rowPosition) {

        int globalPosition = 0;

        int adapterIndex = getIndex(pieces, adapter);
        if (adapterIndex >= 0 && sectionInfos != null && !sectionInfos.isEmpty()) {

            for (DetailSectionInfo sectionInfo : sectionInfos) {

                if (sectionInfo != null && sectionInfo.positionInfos != null && !sectionInfo.positionInfos.isEmpty()) {

                    for (DetailSectionPositionInfo positionInfo : sectionInfo.positionInfos) {
                        if (positionInfo != null) {
                            if (positionInfo.adapterIndex == adapterIndex) {

                                Pair<Integer, Integer> innerPosition = adapter.getInnerPosition(
                                        positionInfo.adapterSectionIndex,
                                        positionInfo.adapterSectionPosition);

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
    public float getSectionFooterHeight(int section) {
        DetailSectionPositionInfo info = getDetailSectionPositionInfo(section, 0);
        if (info != null) {
            PieceAdapter adapter = pieces.get(info.adapterIndex);
            if (adapter != null) {
                return adapter.getSectionFooterHeight(info.adapterSectionIndex);
            }
        }

        return NO_SPACE_HIGHT;
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
    public int topDividerOffset(int section, int position) {
        DetailSectionPositionInfo info = getDetailSectionPositionInfo(section, position);
        if (info != null) {
            PieceAdapter adapter = pieces.get(info.adapterIndex);
            if (adapter != null) {
                return adapter.topDividerOffset(info.adapterSectionIndex, info.adapterSectionPosition);

            }
        }

        return 0;
    }

    @Override
    public int bottomDividerOffset(int section, int position) {
        DetailSectionPositionInfo info = getDetailSectionPositionInfo(section, position);
        if (info != null) {
            PieceAdapter adapter = pieces.get(info.adapterIndex);
            if (adapter != null) {
                return adapter.bottomDividerOffset(info.adapterSectionIndex, info.adapterSectionPosition);
            }
        }

        return 0;
    }

    @Override
    public int getItemViewType(int sectionIndex, int position) {
        DetailSectionPositionInfo info = getDetailSectionPositionInfo(sectionIndex, position);
        if (info != null) {
            PieceAdapter adapter =
                    pieces.get(info.adapterIndex);
            if (adapter != null) {
                int type = adapter.getItemViewType(info.adapterSectionIndex, info.adapterSectionPosition);
                Pair<String, Integer> pair = new Pair<>(adapter.getMappingKey(), type);
                if (typeMap.containsKey(pair)) {
                    return typeMap.get(pair);
                }
            }
        }
        return 0;
    }

    @Override
    public long getItemId(int section, int position) {
        DetailSectionPositionInfo info = getDetailSectionPositionInfo(section, position);
        if (info != null) {
            PieceAdapter adapter =
                    pieces.get(info.adapterIndex);
            if (adapter != null) {
                long id = adapter.getItemId(info.adapterSectionIndex, info.adapterSectionPosition);
                Pair<String, Long> pair = new Pair<>(adapter.getMappingKey(), id);
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
                    return adapter.onCreateViewHolder(parent, pair.second);
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
    }

    public class Observer extends RecyclerView.AdapterDataObserver {
        public void onChanged() {
            updateInfo();
            notifyDataSetChanged();
        }

        public void onItemRangeChanged(int positionStart, int itemCount) {
            notifyItemRangeChanged(positionStart, itemCount);
        }

        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            // fallback to onItemRangeChanged(positionStart, itemCount) if app
            // does not override this method.
            onItemRangeChanged(positionStart, itemCount);
        }

        public void onItemRangeInserted(int positionStart, int itemCount) {
            notifyItemRangeInserted(positionStart, itemCount);
        }

        public void onItemRangeRemoved(int positionStart, int itemCount) {
            notifyItemRangeRemoved(positionStart, itemCount);
        }

        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            notifyItemMoved(fromPosition, toPosition);
        }
    }
}
