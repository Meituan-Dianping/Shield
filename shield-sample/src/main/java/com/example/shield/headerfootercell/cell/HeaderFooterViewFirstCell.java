package com.example.shield.headerfootercell.cell;

import android.content.Context;
import android.graphics.Color;

/**
 * Created by nihao on 2017/7/14.
 */

public class HeaderFooterViewFirstCell extends HeaderFooterViewBaseCell {
    public HeaderFooterViewFirstCell(Context context) {
        super(context);
    }

    @Override
    public int getSectionCount() {
        return 3;
    }

    @Override
    public boolean hasHeaderForSection(int sectionPostion) {
        return sectionPostion == 0;
    }

    @Override
    public boolean hasFooterForSection(int sectionPostion) {
        return sectionPostion == 2;
    }

    @Override
    public int getRowCount(int sectionPosition) {
        switch (sectionPosition) {
            case 0: // section0;
                return 2;
            case 1: // section1;
                return 3;
            case 2: // section2;
                return 3;
            default: // default;
                return 0;
        }
    }

    @Override
    public int getViewType(int sectionPosition, int rowPosition) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    protected int getModuleIndex() {
        return 0;
    }

    @Override
    protected int getTextColor() {
        return Color.parseColor("#00CC66");
    }
}
