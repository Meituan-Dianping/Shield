package com.example.shield.divider.agent;

import android.content.Context;
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
import com.example.shield.util.ItemViewHolder;
import com.example.shield.util.SectionPositionColorUtil;


public class SectionDividerAgent extends LightAgent {

    private SectionDividerViewCell mSectionDividerViewCell;

    public SectionDividerAgent(Fragment fragment, DriverInterface bridge, PageContainerInterface pageContainer) {
        super(fragment, bridge, pageContainer);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSectionDividerViewCell = new SectionDividerViewCell(getContext());

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public SectionCellInterface getSectionCellInterface() {
        return mSectionDividerViewCell;
    }

    private class SectionDividerViewCell extends BaseViewCell {

        public SectionDividerViewCell(Context context) {
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
                String showTypeHint = "default";
                switch (sectionPosition) {
                    case 0:
                        showTypeHint = "default";
                        break;
                    case 1:
                        showTypeHint = "none";
                        break;
                    case 2:
                        showTypeHint = "top_end";
                        break;
                    case 3:
                        showTypeHint = "middle";
                        break;
                    case 4:
                        showTypeHint = "all";
                        break;
                }

                v.setText("Module0, Body, Section" + sectionPosition + ", " +
                        "Row" + rowPosition + " divider: " + showTypeHint);

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
                return rootView;
            }
            return null;
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
        public View onCreateFooterView(ViewGroup parent, int footerViewType) {
            LinearLayout rootView = (LinearLayout) createShowView();
            if (rootView != null && (rootView.getTag() instanceof ItemViewHolder)) {
                TextView containView = ((ItemViewHolder) rootView.getTag()).textView;
                containView.setTextColor(getContext().getResources().getColor(android.R.color.holo_green_dark));
                containView.setText("Footer, divider");
                containView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                return rootView;
            }
            return null;
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
        public ShowType dividerShowType(int sectionPosition) {
            switch (sectionPosition) {
                case 0:
                    return ShowType.DEFAULT;
                case 1:
                    return ShowType.NONE;
                case 2:
                    return ShowType.TOP_END;
                case 3:
                    return ShowType.MIDDLE;
                case 4:
                    return ShowType.ALL;
            }
            return ShowType.DEFAULT;
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