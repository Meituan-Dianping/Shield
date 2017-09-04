package com.example.shield.status.agent;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dianping.agentsdk.framework.CellStatus;
import com.dianping.agentsdk.framework.DriverInterface;
import com.dianping.agentsdk.framework.PageContainerInterface;
import com.dianping.agentsdk.framework.SectionCellInterface;
import com.dianping.agentsdk.framework.ViewUtils;
import com.dianping.shield.agent.LightAgent;
import com.example.shield.status.ControlButtonConstant;
import com.example.shield.status.cell.LoadingBaseCell;
import com.example.shield.util.ItemViewHolder;
import com.example.shield.util.SectionPositionColorUtil;

import rx.Subscription;
import rx.functions.Action1;


public class LoadingStatusAgent extends LightAgent {

    private LoadingStatusCell mLoadingStatusCell;
    private Subscription statusSubscription;
    private Subscription sectionCountSubscription;
    private String mStatusStr;
    private int mSectionCount;

    public LoadingStatusAgent(Fragment fragment, DriverInterface bridge, PageContainerInterface pageContainer) {
        super(fragment, bridge, pageContainer);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoadingStatusCell = new LoadingStatusCell(getContext());
        mLoadingStatusCell.setLoadingRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getHostFragment().getActivity(), "loading retry: 重新加载", Toast.LENGTH_SHORT).show();
            }
        });

        mLoadingStatusCell.setResetOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingStatusCell.setStatus(CellStatus.LoadingStatus.DONE);
                updateAgentCell();
            }
        });

        statusSubscription = getWhiteBoard().getObservable(ControlButtonConstant.STATUS).subscribe(new Action1() {
            @Override
            public void call(Object o) {
                if (o instanceof String) {
                    mStatusStr = (String) o;
                    if (mStatusStr.equals("LOADING")) {
                        mLoadingStatusCell.setStatus(CellStatus.LoadingStatus.LOADING);

                    } else if (mStatusStr.equals("FAILED")) {
                        mLoadingStatusCell.setStatus(CellStatus.LoadingStatus.FAILED);

                    } else if (mStatusStr.equals("EMPTY")) {
                        mLoadingStatusCell.setStatus(CellStatus.LoadingStatus.EMPTY);

                    } else {
                        mLoadingStatusCell.setStatus(CellStatus.LoadingStatus.DONE);
                    }
                    updateAgentCell();
                }
            }
        });
        sectionCountSubscription = getWhiteBoard().getObservable(ControlButtonConstant.SECTIONCOUNT).subscribe(new Action1() {
            @Override
            public void call(Object o) {
                if (o instanceof Integer) {
                    mSectionCount = (Integer) o;
                    if (mSectionCount != 1) {
                        mLoadingStatusCell.setSectionCount(mSectionCount);
                    }
                    updateAgentCell();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        if (statusSubscription != null) {
            statusSubscription.unsubscribe();
            statusSubscription = null;
        }
        if (sectionCountSubscription != null) {
            sectionCountSubscription.unsubscribe();
            sectionCountSubscription = null;
        }
        super.onDestroy();
    }

    @Override
    public SectionCellInterface getSectionCellInterface() {
        return mLoadingStatusCell;
    }

    public class LoadingStatusCell extends LoadingBaseCell {
        protected ItemViewHolder itemViewHolder;

        private CellStatus.LoadingStatus status = CellStatus.LoadingStatus.DONE;

        public void setSectionCount(int sectionCount) {
            this.mSectionCount = sectionCount;
        }

        private int mSectionCount = 1;


        public void setStatus(CellStatus.LoadingStatus status) {
            this.status = status;
        }

        public LoadingStatusCell(Context context) {
            super(context);
        }

        @Override
        public int getSectionCount() {
            return mSectionCount;
        }

        @Override
        public int getRowCount(int sectionPosition) {
            return 4;
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
                v.setText("Section" + sectionPosition + ",Row" + rowPosition);

                SectionPositionColorUtil.setSectionPositionColor(v, getContext(), sectionPosition, rowPosition);
            }
        }

        @Override
        public CellStatus.LoadingStatus loadingStatus() {
            return status;
        }
    }
}