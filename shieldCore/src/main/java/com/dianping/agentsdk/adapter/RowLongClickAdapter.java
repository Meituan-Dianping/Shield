package com.dianping.agentsdk.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.dianping.agentsdk.framework.ItemClickInterface;
import com.dianping.agentsdk.framework.ItemLongClickInterface;
import com.dianping.agentsdk.sectionrecycler.section.MergeSectionDividerAdapter;
import com.dianping.agentsdk.sectionrecycler.section.PieceAdapter;
import com.dianping.shield.core.R;
import com.dianping.shield.entity.CellType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by bingwei on 16/12/29.
 */

public class RowLongClickAdapter extends WrapperPieceAdapter<ItemLongClickInterface> {
    public RowLongClickAdapter(@NonNull Context context, @NonNull PieceAdapter adapter, ItemLongClickInterface extraInterface) {
        super(context, adapter, extraInterface);
    }

    @Override
    public void onBindViewHolder(final MergeSectionDividerAdapter.BasicHolder holder, final int sectionIndex, final int row) {

        if (extraInterface != null && extraInterface.getOnItemLongClickListener() != null && adapter.getCellType(sectionIndex, row) == CellType.NORMAL) {

            Pair<Integer, Integer> pair = getInnerPosition(sectionIndex, row);

            if (holder != null
                    && holder.itemView != null
                    && (holder.itemView.getTag(R.id.item_longclick_tag_key_id)==null || needResetLongClickListener(pair.first, pair.second, holder.itemView.getTag(R.id.item_longclick_tag_key_id)))
                    ) {
                OnInnerItemLongClickListener onInnerItemLongClickListener = null;
                if (holder.itemView.getTag(R.id.item_longclick_tag_key_id) instanceof OnInnerItemLongClickListener) {
                    onInnerItemLongClickListener = (OnInnerItemLongClickListener) holder.itemView.getTag(R.id.item_longclick_tag_key_id);
                    onInnerItemLongClickListener.rowPosition = pair.first;
                    onInnerItemLongClickListener.sectionIndex = pair.second;
                } else {
                    onInnerItemLongClickListener = new OnInnerItemLongClickListener(pair.first, pair.second);
                }
                holder.itemView.setOnLongClickListener(onInnerItemLongClickListener);
                holder.itemView.setTag(R.id.item_longclick_tag_key_id, onInnerItemLongClickListener);
            }
        }
        super.onBindViewHolder(holder, sectionIndex, row);
    }

    private boolean needResetLongClickListener(int sectionIndex, int rowPosition, Object onItemLongClickListener) {
        if (!(onItemLongClickListener instanceof OnInnerItemLongClickListener)) {
            return false;
        }
        return ((OnInnerItemLongClickListener) onItemLongClickListener).sectionIndex != sectionIndex || ((OnInnerItemLongClickListener) onItemLongClickListener).rowPosition != rowPosition;

    }

    private class OnInnerItemLongClickListener implements View.OnLongClickListener {
        public int sectionIndex;
        public int rowPosition;

        public OnInnerItemLongClickListener(int sectionIndex, int rowPosition) {
            this.sectionIndex = sectionIndex;
            this.rowPosition = rowPosition;
        }

        @Override
        public boolean onLongClick(View v) {
            if (extraInterface != null && extraInterface.getOnItemLongClickListener() != null) {
                return extraInterface.getOnItemLongClickListener().onItemLongClick(v, sectionIndex, rowPosition);
            } else {
                return false;
            }
        }
    }
}
