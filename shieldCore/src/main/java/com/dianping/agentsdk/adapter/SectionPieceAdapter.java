package com.dianping.agentsdk.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.dianping.agentsdk.framework.SectionCellInterface;
import com.dianping.agentsdk.sectionrecycler.section.MergeSectionDividerAdapter;
import com.dianping.agentsdk.sectionrecycler.section.PieceAdapter;

/**
 * Created by hezhi on 16/6/23.
 */
public class SectionPieceAdapter extends PieceAdapter {
    SectionCellInterface sectionCellInterface;

    public SectionPieceAdapter(@NonNull Context context, SectionCellInterface sectionCellInterface) {
        super(context);
        this.sectionCellInterface = sectionCellInterface;
    }

    @Override
    public int getSectionCount() {
        if (sectionCellInterface != null) return sectionCellInterface.getSectionCount();
        return super.getSectionCount();
    }

    @Override
    public int getRowCount(int sectionIndex) {
        if (sectionCellInterface != null) return sectionCellInterface.getRowCount(sectionIndex);
        return super.getRowCount(sectionIndex);
    }

    @Override
    public int getItemViewType(int sectionIndex, int position) {
        if (sectionCellInterface != null)
            return sectionCellInterface.getViewType(sectionIndex, position);
        return super.getItemViewType(sectionIndex, position);
    }

    @Override
    public void onBindViewHolder(MergeSectionDividerAdapter.BasicHolder holder, int sectionIndex, int position) {
        sectionCellInterface.updateView(holder.itemView, sectionIndex, position, (ViewGroup) holder.itemView.getParent());
    }

    @Override
    public MergeSectionDividerAdapter.BasicHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = sectionCellInterface.onCreateView(parent, viewType);
        if (view != null && view.getLayoutParams() == null) {
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        MergeSectionDividerAdapter.BasicHolder viewHolder = new MergeSectionDividerAdapter.BasicHolder(view);

        return viewHolder;
    }

}
