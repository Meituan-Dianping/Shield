package com.example.shield.divider.agent;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dianping.agentsdk.framework.DriverInterface;
import com.dianping.agentsdk.framework.PageContainerInterface;
import com.dianping.agentsdk.framework.SectionCellInterface;
import com.dianping.agentsdk.framework.ViewUtils;
import com.dianping.shield.agent.LightAgent;
import com.dianping.shield.viewcell.BaseViewCell;
import com.example.shield.R;
import com.example.shield.util.ItemViewHolder;
import com.example.shield.util.SectionPositionColorUtil;


public class RowDividerAgent extends LightAgent {

    private RowDividerViewCell mRowDividerViewCell;

    public RowDividerAgent(Fragment fragment, DriverInterface bridge, PageContainerInterface pageContainer) {
        super(fragment, bridge, pageContainer);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRowDividerViewCell = new RowDividerViewCell(getContext());

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public SectionCellInterface getSectionCellInterface() {
        return mRowDividerViewCell;
    }

    private class RowDividerViewCell extends BaseViewCell {

        public RowDividerViewCell(Context context) {
            super(context);
        }

        @Override
        public int getSectionCount() {
            return 5;
        }

        @Override
        public int getRowCount(int sectionPosition) {
            if (sectionPosition == 0)
                return 2;
            else
                return 3;
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
        public View onCreateView(ViewGroup parent, int viewType) {
            return createShowView();
        }

        @Override
        public void updateView(View view, int sectionPosition, int rowPosition, ViewGroup parent) {
            if (view != null && (view.getTag() instanceof ItemViewHolder)) {
                TextView v = ((ItemViewHolder) view.getTag()).textView;
                String showHint = "";
                if (sectionPosition == 1 && rowPosition == 0) {
                    showHint = ": drawable";
                } else if (sectionPosition == 3 && (rowPosition == 0 || rowPosition == 2)) {
                    showHint = ": hide divider";
                } else if (sectionPosition == 2) {
                    if (rowPosition == 0)
                        showHint = ": divider_offset 30dp";
                    else
                        showHint = ": divider_offset 50dp";

                } else {
                    showHint = "";
                }

                v.setText("Module0, Body, Section" + sectionPosition + ", " +
                        "Row" + rowPosition + " divider" + showHint);

                SectionPositionColorUtil.setSectionPositionColor(v, getContext(), sectionPosition, rowPosition);

            }
        }

        @Override
        public View onCreateHeaderView(ViewGroup parent, int headerViewType) {
            LinearLayout rootView = (LinearLayout) createShowView();
            if (rootView != null && (rootView.getTag() instanceof ItemViewHolder)) {
                TextView containView = ((ItemViewHolder) rootView.getTag()).textView;
                containView.setText("Header, divider");
                containView.setTextColor(getContext().getResources().getColor(android.R.color.holo_green_dark));
                containView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            }
            return rootView;
        }

        @Override
        public View onCreateFooterView(ViewGroup parent, int footerViewType) {
            LinearLayout rootView = (LinearLayout) createShowView();
            if (rootView != null && (rootView.getTag() instanceof ItemViewHolder)) {
                TextView containView = ((ItemViewHolder) rootView.getTag()).textView;
                containView.setTextColor(getContext().getResources().getColor(android.R.color.holo_green_dark));
                containView.setText("Footer, divider");
                containView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            }
            return rootView;
        }

        @Override
        public boolean hasHeaderForSection(int sectionPostion) {
            if (sectionPostion == 0) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean hasFooterForSection(int sectionPostion) {
            if (sectionPostion == getSectionCount() - 1) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public Drawable getDivider(int sectionPosition, int rowPosition) {
            if (rowPosition == 0 && sectionPosition == 1)
                return getContext().getResources().getDrawable(R.drawable.shield_demo_setdivider_color);
            else
                return getContext().getResources().getDrawable(R.drawable.section_recycler_view_divider);
        }

        @Override
        public boolean showDivider(int sectionPosition, int rowPosition) {
            if (sectionPosition == 3 && (rowPosition == 0 || rowPosition == 2))
                return false;
            else
                return true;
        }

        @Override
        public int dividerOffset(int sectionPosition, int rowPosition) {
            if (sectionPosition == 2) {
                if (rowPosition == 0)
                    return ViewUtils.dip2px(mContext, 30);
                else
                    return ViewUtils.dip2px(mContext, 50);
            } else
                return super.dividerOffset(sectionPosition, rowPosition);
        }

        private View createShowView() {
            LinearLayout rootView = new LinearLayout(mContext);
            rootView.setOrientation(LinearLayout.VERTICAL);
            rootView.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            rootView.setBackgroundColor(getContext().getResources().getColor(android.R.color.white));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = ViewUtils.dip2px(getContext(), 30);

            ItemViewHolder itemViewHolder = new ItemViewHolder();
            TextView textView = new TextView(mContext);
            textView.setHeight(ViewUtils.dip2px(getContext(), 50));
            textView.setGravity(Gravity.CENTER_VERTICAL);
            itemViewHolder.textView = textView;

            rootView.addView(textView, params);
            rootView.setTag(itemViewHolder);
            return rootView;
        }
    }
}