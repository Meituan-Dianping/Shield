package com.dianping.agentsdk.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.ViewGroup;

import com.dianping.agentsdk.framework.AgentInterface;
import com.dianping.agentsdk.framework.LinkType;
import com.dianping.agentsdk.framework.SectionCellInterface;
import com.dianping.agentsdk.sectionrecycler.section.MergeSectionDividerAdapter;
import com.dianping.agentsdk.sectionrecycler.section.PieceAdapter;
import com.dianping.shield.entity.CellType;

/**
 * Created by hezhi on 16/6/23.
 */
public class WrapperPieceAdapter<EI> extends PieceAdapter {

    protected EI extraInterface;
    protected PieceAdapter adapter;

    public WrapperPieceAdapter(@NonNull Context context,
                               @NonNull PieceAdapter adapter, EI extraInterface) {
        super(context);
        this.extraInterface = extraInterface;
        this.adapter = adapter;
    }

    @Override
    public void onBindViewHolder(MergeSectionDividerAdapter.BasicHolder holder, int sectionIndex, int row) {
        adapter.onBindViewHolder(holder, sectionIndex, row);
    }

    @Override
    public MergeSectionDividerAdapter.BasicHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return adapter.onCreateViewHolder(parent, viewType);
    }


    @Override
    public int getSectionCount() {
        return adapter.getSectionCount();
    }

    @Override
    public int getRowCount(int sectionIndex) {
        return adapter.getRowCount(sectionIndex);
    }

    @Override
    public float getSectionHeaderHeight(int section) {
        return adapter.getSectionHeaderHeight(section);
    }

    @Override
    public float getSectionFooterHeight(int section) {
        return adapter.getSectionFooterHeight(section);
    }

    @Override
    public Drawable getTopDivider(int section, int row) {
        return adapter.getTopDivider(section, row);
    }

    @Override
    public Drawable getBottomDivider(int section, int row) {
        return adapter.getBottomDivider(section, row);
    }

    @Override
    public int topDividerOffset(int section, int row) {
        return adapter.topDividerOffset(section, row);
    }

    @Override
    public int bottomDividerOffset(int section, int row) {
        return adapter.bottomDividerOffset(section, row);
    }

    @Override
    public LinkType.Previous getPreviousLinkType(int section) {
        return adapter.getPreviousLinkType(section);
    }

    @Override
    public LinkType.Next getNextLinkType(int section) {
        return adapter.getNextLinkType(section);
    }

    @Override
    public long getItemId(int section, int row) {
        return adapter.getItemId(section, row);
    }

    @Override
    public int getItemViewType(int sectionIndex, int row) {
        return adapter.getItemViewType(sectionIndex, row);
    }

    @Override
    public int getInnerType(int wrappedType) {
        return adapter.getInnerType(wrappedType);
    }

    @Override
    public Pair<Integer, Integer> getInnerPosition(int wrappedSection, int wrappedRow) {
        return adapter.getInnerPosition(wrappedSection, wrappedRow);
    }

    @Override
    public boolean showTopDivider(int section, int row) {
        return adapter.showTopDivider(section, row);
    }

    @Override
    public boolean showBottomDivider(int section, int row) {
        return adapter.showBottomDivider(section, row);
    }

    @Override
    public String getMappingKey() {
        return adapter.getMappingKey();
    }

    @Override
    public void setMappingKey(String mappingKey) {
        adapter.setMappingKey(mappingKey);
    }

    @Override
    public AgentInterface getAgentInterface() {
        return adapter.getAgentInterface();
    }

    @Override
    public void setAgentInterface(AgentInterface agentInterface) {
        adapter.setAgentInterface(agentInterface);
    }

    @Override
    public SectionCellInterface getSectionCellInterface() {
        return adapter.getSectionCellInterface();
    }

    @Override
    public void setSectionCellInterface(SectionCellInterface sectionCellInterface) {
        adapter.setSectionCellInterface(sectionCellInterface);
    }

    @Override
    public void setAddSpaceForDivider(boolean addSpaceForDivider) {
        adapter.setAddSpaceForDivider(addSpaceForDivider);
    }

    @Override
    public boolean hasBottomDividerVerticalOffset(int section, int row) {
        return adapter.hasBottomDividerVerticalOffset(section, row);
    }

    @Override
    public boolean hasTopDividerVerticalOffset(int section, int row) {
        return adapter.hasTopDividerVerticalOffset(section, row);
    }

    public CellType getCellType(int wrappedSection, int wrappedRow) {
        return adapter.getCellType(wrappedSection, wrappedRow);
    }

    public int getTotalItemCount() {
        return adapter.getTotalItemCount();
    }

    @Override
    public boolean isInnerSection(int wrappedSection) {
        return adapter.isInnerSection(wrappedSection);
    }
}
