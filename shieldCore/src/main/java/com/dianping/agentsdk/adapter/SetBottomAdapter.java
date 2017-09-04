package com.dianping.agentsdk.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.dianping.agentsdk.sectionrecycler.section.MergeSectionDividerAdapter;
import com.dianping.agentsdk.sectionrecycler.section.PieceAdapter;
import com.dianping.shield.feature.SetBottomInterface;

/**
 * Created by zdh on 17/3/27.
 */

public class SetBottomAdapter extends WrapperPieceAdapter<SetBottomInterface> {
    private MergeSectionDividerAdapter.BasicHolder emptyBottomViewHolder;
    private MergeSectionDividerAdapter.BasicHolder trueBottomViewHolder;

    public SetBottomAdapter(@NonNull Context context, @NonNull PieceAdapter adapter, SetBottomInterface extraInterface) {
        super(context, adapter, extraInterface);
    }

    @Override
    public void onBindViewHolder(MergeSectionDividerAdapter.BasicHolder holder, int sectionIndex, int row) {
        if (extraInterface != null) {
            int innerType = getInnerType(getItemViewType(sectionIndex, row));
            if (extraInterface.isBottomView(innerType)) {
                if (holder == emptyBottomViewHolder) {
                    super.onBindViewHolder(trueBottomViewHolder, sectionIndex, row);
                    return;
                }
            }
        }
        super.onBindViewHolder(holder, sectionIndex, row);
    }

    @Override
    public MergeSectionDividerAdapter.BasicHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (extraInterface != null && extraInterface.getSetBottomFunctionInterface() != null) {
            if (extraInterface.isBottomView(getInnerType(viewType))) {
                MergeSectionDividerAdapter.BasicHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                if (viewHolder != null && viewHolder.itemView != null) {
                    boolean isSuccess = extraInterface.getSetBottomFunctionInterface().setBottomView(viewHolder.itemView);
                    if (isSuccess){
                        //创建一个空白holder，用来替换置底holder的位置，确保在onBindViewHolder时候可以找到置底Holder，进行更新
                        View emptyView = new View(getContext());
                        emptyBottomViewHolder = new MergeSectionDividerAdapter.BasicHolder(emptyView);
                        trueBottomViewHolder = viewHolder;
                        return emptyBottomViewHolder;
                    }
                }
            }
        }
        return super.onCreateViewHolder(parent, viewType);
    }
}
