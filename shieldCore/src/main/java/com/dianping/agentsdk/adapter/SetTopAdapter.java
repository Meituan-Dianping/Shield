package com.dianping.agentsdk.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.dianping.agentsdk.pagecontainer.SetTopParams;
import com.dianping.agentsdk.sectionrecycler.section.MergeSectionDividerAdapter;
import com.dianping.agentsdk.sectionrecycler.section.PieceAdapter;
import com.dianping.shield.feature.SetTopInterface;
import com.dianping.shield.feature.SetTopParamsInterface;

/**
 * Created by zdh on 17/3/27.
 */

public class SetTopAdapter extends WrapperPieceAdapter<SetTopInterface> {
    private MergeSectionDividerAdapter.BasicHolder emptyTopViewHolder;
    private MergeSectionDividerAdapter.BasicHolder trueTopViewHolder;

    public SetTopAdapter(@NonNull Context context, @NonNull PieceAdapter adapter, SetTopInterface extraInterface) {
        super(context, adapter, extraInterface);
    }

    @Override
    public void onBindViewHolder(MergeSectionDividerAdapter.BasicHolder holder, int sectionIndex, int row) {
        if (extraInterface != null) {
            int innerType = getInnerType(getItemViewType(sectionIndex, row));
            if (extraInterface.isTopView(innerType)) {
                if (holder == emptyTopViewHolder) {
                    if (extraInterface instanceof SetTopParamsInterface && extraInterface.getSetTopFunctionInterface() != null) {
                        SetTopParams params = ((SetTopParamsInterface) extraInterface).getSetTopParams(innerType);
                        extraInterface.getSetTopFunctionInterface().updateSetTopParams(params);
                    }
                    super.onBindViewHolder(trueTopViewHolder, sectionIndex, row);
                    return;
                }
            }
        }
        super.onBindViewHolder(holder, sectionIndex, row);
    }

    @Override
    public MergeSectionDividerAdapter.BasicHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (extraInterface != null && extraInterface.getSetTopFunctionInterface() != null) {
            int innerType = getInnerType(viewType);
            if (extraInterface.isTopView(innerType)) {
                MergeSectionDividerAdapter.BasicHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                if (viewHolder != null && viewHolder.itemView != null) {
                    //获取一个空白View，替换置顶View在列表中的位置
                    SetTopParams params = null;
                    if (extraInterface instanceof SetTopParamsInterface) {
                        params = ((SetTopParamsInterface) extraInterface).getSetTopParams(innerType);
                    }
                    View emptyView = extraInterface.getSetTopFunctionInterface().setTopView(viewHolder.itemView, params);
                    if (emptyView != null) {
                        emptyTopViewHolder = new MergeSectionDividerAdapter.BasicHolder(emptyView);
                        trueTopViewHolder = viewHolder;
                        return emptyTopViewHolder;
                    }
                }
            }
        }
        return super.onCreateViewHolder(parent, viewType);
    }
}
