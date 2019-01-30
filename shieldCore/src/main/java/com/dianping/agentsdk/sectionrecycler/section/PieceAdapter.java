package com.dianping.agentsdk.sectionrecycler.section;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;

import com.dianping.agentsdk.framework.AgentInterface;
import com.dianping.agentsdk.framework.DividerInfo;
import com.dianping.agentsdk.framework.LinkType;
import com.dianping.agentsdk.framework.SectionCellInterface;
import com.dianping.shield.adapter.TopInfoListProvider;
import com.dianping.shield.adapter.TopPositionAdapter;
import com.dianping.shield.entity.CellType;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by runqi.wei
 * 18:54
 * 21.06.2016.
 */
public abstract class PieceAdapter extends SectionDAdapter<MergeSectionDividerAdapter.BasicHolder> implements TopInfoListProvider {

    protected String mappingKey = "";
    protected String hostName = "";
    protected WeakReference<AgentInterface> agentInterfaceWeakReference;
    protected WeakReference<SectionCellInterface> sectionCellInterfaceWeakReference;

    protected boolean addSpaceForDivider = false;

    protected AdapterObserver adapterObserver = new AdapterObserver();

    public PieceAdapter(@NonNull Context context) {
        super(context);
        registerAdapterDataObserver(adapterObserver);
    }

    public String getMappingKey() {
        return mappingKey;
    }

    public void setMappingKey(String mappingKey) {
        this.mappingKey = mappingKey;
    }

    public AgentInterface getAgentInterface() {
        if (agentInterfaceWeakReference != null) {
            return agentInterfaceWeakReference.get();
        }
        return null;
    }

    public void setAgentInterface(AgentInterface agentInterface) {
        this.agentInterfaceWeakReference = new WeakReference<AgentInterface>(agentInterface);
    }

    public SectionCellInterface getSectionCellInterface() {
        if (sectionCellInterfaceWeakReference != null) {
            return sectionCellInterfaceWeakReference.get();
        }

        return null;
    }

    public void setSectionCellInterface(SectionCellInterface sectionCellInterface) {
        this.sectionCellInterfaceWeakReference = new WeakReference<SectionCellInterface>(sectionCellInterface);
    }

    /**
     * Set whether should the {@link android.support.v7.widget.RecyclerView.ItemDecoration}
     * add space for dividers.
     *
     * @param addSpaceForDivider <p>
     *                           If <code>true</code> The ItemDecoration will add special space for the divider,
     *                           Thus the divider will be drawn BESIDES the item.
     *                           </p>
     *                           <p>
     *                           If <code>false</code> The ItemDecoration will NOT add special space for the divider,
     *                           Thus the divider will be drawn ON the item.
     *                           </p>
     */
    public void setAddSpaceForDivider(boolean addSpaceForDivider) {
        this.addSpaceForDivider = addSpaceForDivider;
    }

    /**
     * Retuens the {@link LinkType} to PREVIOUS section of the given section.
     *
     * @param section the section index
     * @return the LinkType
     */
    public LinkType.Previous getPreviousLinkType(int section) {
        return null;
    }

    /**
     * Returns the {@link LinkType} to NEXT section of the given section.
     *
     * @param section the section index
     * @return the LinkType
     */
    public LinkType.Next getNextLinkType(int section) {
        return null;
    }

    @Override
    public int getSectionCount() {
        return 1;
    }

    @Override
    public int getRowCount(int sectionIndex) {
        return 0;
    }


    public String getSectionTitle(int section) {
        return null;
    }

    @Override
    public float getSectionHeaderHeight(int section) {
        return NO_SPACE_HIGHT;
    }

    @Override
    public Drawable getSectionHeaderDrawable(int section) {
        return null;
    }

    @Override
    public float getSectionFooterHeight(int section) {
        return NO_SPACE_HIGHT;
    }

    @Override
    public Drawable getSectionFooterDrawable(int section) {
        return null;
    }

    @Override
    public boolean hasBottomDividerVerticalOffset(int section, int position) {
        return false;
    }

    @Override
    public boolean hasTopDividerVerticalOffset(int section, int position) {
        return false;
    }

    @Override
    public Drawable getTopDivider(int section, int position) {
        return null;
    }

    @Override
    public Drawable getBottomDivider(int section, int position) {
        return null;
    }

    @Override
    public Rect topDividerOffset(int section, int position) {
        return null;
    }

    @Override
    public Rect bottomDividerOffset(int section, int position) {
        return null;
    }

    @Override
    public boolean showTopDivider(int section, int position) {
        return true;
    }

    @Override
    public boolean showBottomDivider(int section, int position) {
        return true;
    }

    @Override
    public DividerInfo getDividerInfo(int section, int row) {
        return null;
    }

    public int getInnerType(int wrappedType) {
        return wrappedType;
    }

    public Pair<Integer, Integer> getInnerPosition(int wrappedSection, int wrappedPosition) {
        return new Pair<>(wrappedSection, wrappedPosition);
    }

    public CellType getCellType(int wrappedSection, int wrappedPosition) {
        return CellType.NORMAL;
    }

    public CellType getCellType(int viewType) {
        return CellType.NORMAL;
    }

    public boolean isInnerSection(int wrappedSection) {
        return true;
    }

    @Deprecated
    public int getTotalItemCount() {
        int count = 0;
        int sectionCount = getSectionCount();
        for (int i = 0; i < sectionCount; i++) {
            count += getRowCount(i);
        }
        return count;
    }

    @Override
    public ArrayList<TopPositionAdapter.TopInfo> getTopInfoList() {
        return null;
    }

    public void onAdapterChanged() {
    }

    public void onAdapterItemRangeChanged(int positionStart, int itemCount) {
    }

    public void onAdapterItemRangeChanged(int positionStart, int itemCount, Object payload) {
    }

    public void onAdapterItemRangeInserted(int positionStart, int itemCount) {
    }

    public void onAdapterItemRangeRemoved(int positionStart, int itemCount) {
    }

    public void onAdapterItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
    }

    public class AdapterObserver extends RecyclerView.AdapterDataObserver {
        public void onChanged() {
            onAdapterChanged();
        }

        public void onItemRangeChanged(int positionStart, int itemCount) {
            onAdapterItemRangeChanged(positionStart, itemCount);
        }

        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            // fallback to onItemRangeChanged(positionStart, itemCount) if app
            // does not override this method.
            onAdapterItemRangeChanged(positionStart, itemCount, payload);
        }

        public void onItemRangeInserted(int positionStart, int itemCount) {
            onAdapterItemRangeInserted(positionStart, itemCount);
        }

        public void onItemRangeRemoved(int positionStart, int itemCount) {
            onAdapterItemRangeRemoved(positionStart, itemCount);
        }

        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            onAdapterItemRangeMoved(fromPosition, toPosition, itemCount);
        }
    }
}
