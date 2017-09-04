package com.example.shield.linktype.cell;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dianping.shield.viewcell.BaseViewCell;
import com.example.shield.R;

/**
 * Created by nihao on 2017/7/14.
 */

public class LinkTypeFirstCell extends BaseViewCell {
    private static final int SECTION_COUNT = 3;
    private static final int ROW_COUNT = 1;
    private static final int VIEW_TYPE_COUNT = 1;

    public LinkTypeFirstCell(Context context) {
        super(context);
    }

    @Override
    public int getSectionCount() {
        return SECTION_COUNT;
    }

    @Override
    public int getRowCount(int sectionPosition) {
        return ROW_COUNT;
    }

    @Override
    public int getViewType(int sectionPosition, int rowPosition) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public View onCreateView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(getContext()).inflate(R.layout.module_cell_item, parent, false);
    }

    @Override
    public void updateView(View view, int sectionPosition, int rowPosition, ViewGroup parent) {
        TextView textView = (TextView) view.findViewById(R.id.header_footer_item_tx);
        textView.setText(getHint(sectionPosition, rowPosition));
        textView.setTextColor(backgroundColor());
    }

    protected String getHint(int sectionPosition, int rowPosition) {
        return "section : " + sectionPosition + " row : " + rowPosition + " default_link_type";
    }

    protected int backgroundColor() {
        return Color.parseColor("#00CC66");
    }
}
