package com.dianping.agentsdk.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.dianping.agentsdk.sectionrecycler.section.MergeSectionDividerAdapter;
import com.dianping.agentsdk.sectionrecycler.section.PieceAdapter;
import com.dianping.shield.feature.SetZoomInterface;

/**
 * Created by xianhe.dong on 2017/7/21.
 * email xianhe.dong@dianping.com
 */

public class SetZoomAdapter extends WrapperPieceAdapter<SetZoomInterface> {

    public SetZoomAdapter(@NonNull Context context, @NonNull PieceAdapter adapter, SetZoomInterface extraInterface) {
        super(context, adapter, extraInterface);
    }

    @Override
    public MergeSectionDividerAdapter.BasicHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MergeSectionDividerAdapter.BasicHolder viewHolder = super.onCreateViewHolder(parent, viewType);
        if (extraInterface != null && extraInterface.getSetZoomFunctionInterface() != null) {
            if (extraInterface.isZoomView(getInnerType(viewType))) {
                if (viewHolder != null && viewHolder.itemView != null) {
                    extraInterface.getSetZoomFunctionInterface().setZoomView(viewHolder.itemView);
                }
            }
        }
        return viewHolder;
    }
}
