package com.dianping.agentsdk.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.dianping.agentsdk.framework.DividerInfo;
import com.dianping.agentsdk.framework.DividerInterface;
import com.dianping.agentsdk.framework.DividerOffsetInterface;
import com.dianping.agentsdk.framework.SectionDividerInfoInterface;
import com.dianping.agentsdk.framework.TopDividerInterface;
import com.dianping.agentsdk.sectionrecycler.divider.DividerInfoInterface;
import com.dianping.agentsdk.sectionrecycler.section.PieceAdapter;
import com.dianping.shield.entity.CellType;

/**
 * Created by hezhi on 16/6/24.
 * 仅供内部SCI使用
 */
public class SectionDividerPieceAdapter extends WrapperPieceAdapter<DividerInterface> {

    protected TopDividerInterface topDividerInterface;
    protected DividerOffsetInterface dividerOffsetInterface;
    protected SectionDividerInfoInterface sectionDividerInfoInterface;
    protected DividerInfoInterface dividerInfoInterface;

    public SectionDividerPieceAdapter(@NonNull Context context, @NonNull PieceAdapter adapter, DividerInterface extraInterface) {
        super(context, adapter, extraInterface);
    }

    public void setTopDividerInterface(TopDividerInterface topDividerInterface) {
        this.topDividerInterface = topDividerInterface;
    }

    public void setDividerOffsetInterface(DividerOffsetInterface dividerOffsetInterface) {
        this.dividerOffsetInterface = dividerOffsetInterface;
    }

    public void setSectionDividerInfoInterface(SectionDividerInfoInterface dividerInfoInterface) {
        this.sectionDividerInfoInterface = dividerInfoInterface;
    }

    public void setDividerInfoInterface(DividerInfoInterface dividerInfoInterface) {
        this.dividerInfoInterface = dividerInfoInterface;
    }

    @Override
    public DividerInfo getDividerInfo(int section, int row) {
        DividerInfo dividerInfo = null;
        if (dividerInfoInterface != null) {
            dividerInfo = this.dividerInfoInterface.getDividerInfo(CellType.NORMAL, section, row);
        }
        if (dividerInfo == null && sectionDividerInfoInterface != null) {
            dividerInfo = this.sectionDividerInfoInterface.getDividerInfo(section);
        }
        if (dividerInfo != null) {
            return dividerInfo;
        }
        return super.getDividerInfo(section, row);
    }

    //第一个row的上线
    @Override
    public Drawable getTopDivider(int section, int row) {
        if (extraInterface != null && extraInterface instanceof TopDividerInterface) {
            return ((TopDividerInterface) extraInterface).getTopDivider(section, row);
        }
        return super.getTopDivider(section, row);
    }

    //第一个row的offset
    @Override
    public Rect topDividerOffset(int section, int row) {
        if (topDividerInterface != null) {
            Rect topDividerOffset = new Rect();
            topDividerOffset.left = topDividerInterface.topDividerLeftOffset(section, row);
            topDividerOffset.right = topDividerInterface.topDividerRightOffset(section, row);
            return topDividerOffset;
        }
        return super.topDividerOffset(section, row);
    }

    @Override
    public Drawable getBottomDivider(int section, int row) {
        if (extraInterface != null)
            return extraInterface.getDivider(section, row);
        return super.getBottomDivider(section, row);
    }

    @Override
    public Rect bottomDividerOffset(int section, int row) {

        if (dividerOffsetInterface != null) {
            Rect bottomDividerOffset = new Rect();
            bottomDividerOffset.left = dividerOffsetInterface.getDividerLeftOffset(section, row);
            bottomDividerOffset.right = dividerOffsetInterface.getDividerRightOffset(section, row);
            if (bottomDividerOffset.left >= 0 || bottomDividerOffset.right >= 0) {
                return bottomDividerOffset;
            }
        }

        if (extraInterface != null) {
            int left = extraInterface.dividerOffset(section, row);
            // com.dianping.agentsdk.framework.DividerInterface.dividerOffset(int, int)
            // 方法返回 负值 视为不进行设置
            if (left < 0) {
                return null;
            }
            Rect bottomDividerOffset = new Rect();
            bottomDividerOffset.left = left;
            return bottomDividerOffset;
        }
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
