package com.dianping.agentsdk.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;

import com.dianping.agentsdk.framework.ItemClickInterface;
import com.dianping.agentsdk.sectionrecycler.section.MergeSectionDividerAdapter;
import com.dianping.agentsdk.sectionrecycler.section.PieceAdapter;
import com.dianping.shield.core.R;
import com.dianping.shield.entity.CellType;

/**
 * Created by bingwei on 16/12/29.
 */

public class RowClickAdapter extends WrapperPieceAdapter<ItemClickInterface> {
    public RowClickAdapter(@NonNull Context context, @NonNull PieceAdapter adapter, ItemClickInterface extraInterface) {
        super(context, adapter, extraInterface);
    }

    @Override
    public void onBindViewHolder(final MergeSectionDividerAdapter.BasicHolder holder, final int sectionIndex, final int row) {

        if (extraInterface != null && extraInterface.getOnItemClickListener() != null && adapter.getCellType(sectionIndex, row) == CellType.NORMAL) {

            Pair<Integer, Integer> pair = getInnerPosition(sectionIndex, row);

            if (holder != null
                    && holder.itemView != null && !(holder.itemView instanceof AdapterView)
                    && (!holder.itemView.hasOnClickListeners() || needResetClickListener(pair.first, pair.second, holder.itemView.getTag(R.id.item_click_tag_key_id)))
                    ) {
                OnInnerItemClickListener onInnerItemClickListener = null;
                if (holder.itemView.getTag(R.id.item_click_tag_key_id) instanceof OnInnerItemClickListener) {
                    onInnerItemClickListener = (OnInnerItemClickListener) holder.itemView.getTag(R.id.item_click_tag_key_id);
                    onInnerItemClickListener.sectionIndex = pair.first;
                    onInnerItemClickListener.rowPosition = pair.second;
                } else {
                    onInnerItemClickListener = new OnInnerItemClickListener(pair.first, pair.second);
                }

                holder.itemView.setOnClickListener(onInnerItemClickListener);
                holder.itemView.setTag(R.id.item_click_tag_key_id, onInnerItemClickListener);
            }
        }
        super.onBindViewHolder(holder, sectionIndex, row);
    }

    private boolean needResetClickListener(int sectionIndex, int rowPosition, Object onItemClickListener) {
        if (!(onItemClickListener instanceof OnInnerItemClickListener)) {
            return false;
        }
        return ((OnInnerItemClickListener) onItemClickListener).sectionIndex != sectionIndex || ((OnInnerItemClickListener) onItemClickListener).rowPosition != rowPosition;
    }


    private class OnInnerItemClickListener implements View.OnClickListener {
        public int sectionIndex;
        public int rowPosition;

        public OnInnerItemClickListener(int sectionIndex, int rowPosition) {
            this.sectionIndex = sectionIndex;
            this.rowPosition = rowPosition;
        }

        @Override
        public void onClick(View v) {
            if (extraInterface != null && extraInterface.getOnItemClickListener() != null) {
                extraInterface.getOnItemClickListener().onItemClick(v, sectionIndex, rowPosition);
            }
        }
    }
}
