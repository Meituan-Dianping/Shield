package com.example.shield.linktype.cell;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dianping.agentsdk.framework.ViewUtils;
import com.example.shield.R;

/**
 * Created by nihao on 2017/7/17.
 */
public class LinkTypeFourthCell extends LinkTypeFirstCell {
    public LinkTypeFourthCell(Context context) {
        super(context);
    }

    @Override
    public float getSectionHeaderHeight(int sectionPoisition) {
        if (sectionPoisition == 0) {
            return ViewUtils.dip2px(getContext(), 20);
        }

        if (sectionPoisition == 1) {
            return ViewUtils.dip2px(getContext(), 30);
        }

        return super.getSectionHeaderHeight(sectionPoisition);
    }

    @Override
    public View onCreateHeaderView(ViewGroup parent, int headerViewType) {
        return LayoutInflater.from(getContext()).inflate(R.layout.module_cell_item, parent, false);
    }

    @Override
    public boolean hasHeaderForSection(int sectionPostion) {
        return false;
    }

    @Override
    public float getSectionFooterHeight(int sectionPoisition) {
        if (sectionPoisition == 1) {
            return ViewUtils.dip2px(getContext(), 40);
        }
        return super.getSectionFooterHeight(sectionPoisition);
    }

    @Override
    protected int backgroundColor() {
        return Color.parseColor("#FF9900");
    }

    @Override
    protected String getHint(int sectionPosition, int rowPosition) {
        StringBuilder sb = new StringBuilder();
        sb.append("section : ")
                .append(sectionPosition)
                .append(" row : ")
                .append(rowPosition);
        if (sectionPosition == 0) {
            sb.append(" header height: 20dp");
        } else if (sectionPosition == 1){
            sb.append(" header height: 30dp footer height: 40dp");
        }
        return sb.toString();
    }
}
