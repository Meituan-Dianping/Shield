package com.dianping.agentsdk.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.dianping.agentsdk.framework.DividerInterface;
import com.dianping.agentsdk.sectionrecycler.section.PieceAdapter;

/**
 * Created by hezhi on 16/6/24.
 * 仅供内部SCI使用
 */
public class SectionDividerPieceAdapter extends WrapperPieceAdapter<DividerInterface> {
    public SectionDividerPieceAdapter(@NonNull Context context, @NonNull PieceAdapter adapter, DividerInterface extraInterface) {
        super(context, adapter, extraInterface);
    }

    @Override
    public Drawable getBottomDivider(int section, int row) {
        if (extraInterface != null)
            return extraInterface.getDivider(section, row);
        return super.getBottomDivider(section, row);
    }

    @Override
    public int bottomDividerOffset(int section, int row) {
        if (extraInterface != null)
            return extraInterface.dividerOffset(section, row);
        return super.bottomDividerOffset(section, row);
    }

    @Override
    public boolean showTopDivider(int section, int row) {
        if (extraInterface != null) {
            if (extraInterface.dividerShowType(section) != null
                    && extraInterface.dividerShowType(section) != DividerInterface.ShowType.DEFAULT) {
                switch (extraInterface.dividerShowType(section)) {
                    case TOP_END:
                        return true;
                    case MIDDLE:
                        return false;
                    case NO_TOP:
                        return false;
                    case NONE:
                        return false;
                    case ALL:
                        return true;
                }

            } else {
                return extraInterface.showDivider(section, row);
            }
        }

        return super.showTopDivider(section, row);
    }

    @Override
    public boolean showBottomDivider(int section, int row) {
        if (extraInterface.dividerShowType(section) != null
                && extraInterface.dividerShowType(section) != DividerInterface.ShowType.DEFAULT) {
            switch (extraInterface.dividerShowType(section)) {
                case TOP_END:
                    if (row == getRowCount(section) - 1)
                        return true;
                    else return false;
                case MIDDLE:
                    if (row != getRowCount(section) - 1)
                        return true;
                    else return false;
                case NONE:
                    return false;
                case ALL:
                    return true;
            }

        } else {
            return extraInterface.showDivider(section, row);
        }
        return super.showBottomDivider(section, row);
    }
}
