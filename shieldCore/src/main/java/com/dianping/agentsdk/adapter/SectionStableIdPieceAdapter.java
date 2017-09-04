package com.dianping.agentsdk.adapter;

import android.content.Context;
import android.support.annotation.NonNull;

import com.dianping.agentsdk.framework.ItemIdInterface;
import com.dianping.agentsdk.sectionrecycler.section.PieceAdapter;

/**
 * Created by hezhi on 16/8/3.
 */
public class SectionStableIdPieceAdapter extends WrapperPieceAdapter<ItemIdInterface> {
    public SectionStableIdPieceAdapter(@NonNull Context context, @NonNull PieceAdapter adapter, ItemIdInterface extraInterface) {
        super(context, adapter, extraInterface);
    }

    @Override
    public long getItemId(int section, int row) {
        if (extraInterface != null)
            return extraInterface.getItemId(section, row);
        return super.getItemId(section, row);
    }
}
