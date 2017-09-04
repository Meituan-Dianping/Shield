package com.example.shield.status.agent;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.dianping.agentsdk.framework.DriverInterface;
import com.dianping.agentsdk.framework.PageContainerInterface;
import com.dianping.agentsdk.framework.SectionCellInterface;
import com.dianping.shield.agent.LightAgent;
import com.dianping.shield.viewcell.BaseViewCell;
import com.example.shield.R;
import com.example.shield.status.ControlButtonConstant;

/**
 * Created by bingweizhou on 17/7/18.
 */

public class ControlButtonAgent extends LightAgent {
    private ControlButtonViewCell mControlButtonViewCell;

    public ControlButtonAgent(Fragment fragment, DriverInterface bridge, PageContainerInterface pageContainer) {
        super(fragment, bridge, pageContainer);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mControlButtonViewCell = new ControlButtonViewCell(getContext());
        getWhiteBoard().putString("status", "DONE");
    }

    @Override
    public SectionCellInterface getSectionCellInterface() {
        return mControlButtonViewCell;
    }

    public class ControlButtonViewCell extends BaseViewCell {

        private int sectionCount = 1;

        public ControlButtonViewCell(Context context) {
            super(context);
        }

        @Override
        public int getSectionCount() {
            return 1;
        }

        @Override
        public int getRowCount(int sectionPosition) {
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
            rootView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            rootView.setOrientation(LinearLayout.VERTICAL);
            rootView.setBackgroundColor(getContext().getResources().getColor(android.R.color.white));

           // View containView = LinearLayout.inflate(getContext(), R.layout.agent_status_loading, null);
            View containView = LayoutInflater.from(getContext()).inflate(R.layout.agent_status_loading,rootView,false);
                    setLoadingStatus(containView);
            rootView.addView(containView);

            return rootView;
        }

        private void setLoadingStatus(View containView) {
            Button btLoading = (Button) containView.findViewById(R.id.bt_loading);
            btLoading.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getWhiteBoard().putString(ControlButtonConstant.STATUS, "LOADING");
                }
            });
            Button btFailed = (Button) containView.findViewById(R.id.bt_failed);
            btFailed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getWhiteBoard().putString(ControlButtonConstant.STATUS, "FAILED");
                }
            });
            Button btEmpty = (Button) containView.findViewById(R.id.bt_empty);
            btEmpty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getWhiteBoard().putString(ControlButtonConstant.STATUS, "EMPTY");
                }
            });
            Button btDone = (Button) containView.findViewById(R.id.bt_done);
            btDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getWhiteBoard().putString(ControlButtonConstant.STATUS, "DONE");
                }
            });
            Button btIncreaseSection = (Button) containView.findViewById(R.id.bt_increase_section);
            btIncreaseSection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getWhiteBoard().putInt(ControlButtonConstant.SECTIONCOUNT, sectionCount++);
                }
            });
        }

        @Override
        public void updateView(View view, int sectionPosition, int rowPosition, ViewGroup parent) {

        }
    }
}
