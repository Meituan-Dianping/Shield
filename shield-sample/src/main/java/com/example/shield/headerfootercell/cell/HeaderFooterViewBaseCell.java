package com.example.shield.headerfootercell.cell;

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

public abstract class HeaderFooterViewBaseCell extends BaseViewCell {

    public HeaderFooterViewBaseCell(Context context) {
        super(context);
    }

    @Override
    public View onCreateHeaderView(ViewGroup parent, int headerViewType) {
        return LayoutInflater.from(getContext()).inflate(R.layout.module_cell_item, parent, false);
    }

    @Override
    public void updateHeaderView(View view, int sectionPostion, ViewGroup parent) {
        TextView textView = (TextView) view.findViewById(R.id.header_footer_item_tx);
        textView.setBackgroundColor(Color.parseColor("#DAF1B8"));
        textView.setText("header for section: " + sectionPostion);
    }

    @Override
    public View onCreateFooterView(ViewGroup parent, int footerViewType) {
        return LayoutInflater.from(getContext()).inflate(R.layout.module_cell_item, parent, false);
    }

    @Override
    public void updateFooterView(View view, int sectionPostion, ViewGroup parent) {
        TextView textView = (TextView) view.findViewById(R.id.header_footer_item_tx);
        textView.setBackgroundColor(Color.parseColor("#98D839"));
        textView.setText("footer for section : " + sectionPostion);
    }

    @Override
    public View onCreateView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(getContext()).inflate(R.layout.module_cell_item, parent, false);
    }

    @Override
    public void updateView(View view, final int sectionPosition, final int rowPosition, ViewGroup parent) {
        TextView textView = (TextView) view.findViewById(R.id.header_footer_item_tx);
        textView.setText("module : " + getModuleIndex() + " section : " + sectionPosition + " row : " + rowPosition);
        textView.setTextColor(getTextColor());
    }

    protected abstract int getModuleIndex();

    protected abstract int getTextColor();
}
