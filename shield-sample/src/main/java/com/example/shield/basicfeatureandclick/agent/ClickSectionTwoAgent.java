package com.example.shield.basicfeatureandclick.agent;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dianping.agentsdk.framework.DriverInterface;
import com.dianping.agentsdk.framework.ItemClickInterface;
import com.dianping.agentsdk.framework.PageContainerInterface;
import com.dianping.agentsdk.framework.SectionCellInterface;
import com.dianping.agentsdk.framework.ViewUtils;
import com.dianping.shield.agent.LightAgent;
import com.dianping.shield.viewcell.BaseViewCell;
import com.example.shield.util.ItemViewHolder;
import com.example.shield.util.SectionPositionColorUtil;


public class ClickSectionTwoAgent extends LightAgent {

    private ClickViewCell mClickViewCell;

    public ClickSectionTwoAgent(Fragment fragment, DriverInterface bridge, PageContainerInterface pageContainer) {
        super(fragment, bridge, pageContainer);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mClickViewCell = new ClickViewCell(getContext());
        mClickViewCell.setOnItemClickListener(
                new ItemClickInterface.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int section, int position) {
                        Toast.makeText(getHostFragment().getActivity(),
                                "Module1, Body, Section" + section + ", Row" + position + "  clicked", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public SectionCellInterface getSectionCellInterface() {
        return mClickViewCell;
    }

    private class ClickViewCell extends BaseViewCell {
        protected ItemViewHolder itemViewHolder;

        public ClickViewCell(Context context) {
            super(context);
        }

        @Override
        public int getSectionCount() {
            return 2;
        }

        @Override
        public int getRowCount(int sectionPosition) {
            if (sectionPosition == 0)
                return 2;
            else
                return 1;
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
            LinearLayout rootView = new LinearLayout(mContext);
            rootView.setOrientation(LinearLayout.VERTICAL);
            rootView.setBackgroundColor(getContext().getResources().getColor(android.R.color.white));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = ViewUtils.dip2px(getContext(), 30);
            itemViewHolder = new ItemViewHolder();
            TextView textView = new TextView(mContext);
            textView.setHeight(ViewUtils.dip2px(getContext(), 50));
            textView.setGravity(Gravity.CENTER_VERTICAL);
            itemViewHolder.textView = textView;

            rootView.addView(textView, params);
            rootView.setTag(itemViewHolder);
            return rootView;
        }

        @Override
        public void updateView(View view, int sectionPosition, int rowPosition, ViewGroup parent) {
            if (view != null && (view.getTag() instanceof ItemViewHolder)) {
                TextView v = ((ItemViewHolder) view.getTag()).textView;
                v.setText("Module1,Body,Section" + sectionPosition + ",Row" + rowPosition);
                SectionPositionColorUtil.setSectionPositionColor(v, getContext(), sectionPosition, rowPosition);
            }
        }
    }
}