package com.example.shield.status.agent;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dianping.agentsdk.framework.CellStatus;
import com.dianping.agentsdk.framework.DriverInterface;
import com.dianping.agentsdk.framework.PageContainerInterface;
import com.dianping.agentsdk.framework.SectionCellInterface;
import com.dianping.agentsdk.framework.ViewUtils;
import com.dianping.shield.agent.LightAgent;
import com.example.shield.R;
import com.example.shield.status.cell.LoadingBaseCell;
import com.example.shield.util.ItemViewHolder;
import com.example.shield.util.SectionPositionColorUtil;


public class LoadingStatusMoreAgent extends LightAgent {

    private LoadingStatusMoreCell mLoadingStatusMoreCell;

    public LoadingStatusMoreAgent(Fragment fragment, DriverInterface bridge, PageContainerInterface pageContainer) {
        super(fragment, bridge, pageContainer);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoadingStatusMoreCell = new LoadingStatusMoreCell(getContext());
        mLoadingStatusMoreCell.setResetOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingStatusMoreCell.setStatusMore(CellStatus.LoadingMoreStatus.DONE);
                updateAgentCell();
            }
        });
        mLoadingStatusMoreCell.setLoadingMoreRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getHostFragment().getActivity(), "loadingmore retry 重新加载", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public SectionCellInterface getSectionCellInterface() {
        return mLoadingStatusMoreCell;
    }

    public class LoadingStatusMoreCell extends LoadingBaseCell {
        protected ItemViewHolder itemViewHolder;
        protected int titleType = 0;
        protected int cellType = 1;
        protected CellStatus.LoadingMoreStatus statusMore = CellStatus.LoadingMoreStatus.DONE;
        private int sectionCount = 1;

        public LoadingStatusMoreCell(Context context) {
            super(context);
        }

        public void setStatusMore(CellStatus.LoadingMoreStatus statusMore) {
            this.statusMore = statusMore;
        }

        @Override
        public int getSectionCount() {
            return sectionCount;
        }

        @Override
        public int getRowCount(int sectionPosition) {
            return 4;
        }

        @Override
        public int getViewType(int sectionPosition, int rowPosition) {
            if (sectionPosition == 0 && rowPosition == 0)
                return titleType;
            else
                return cellType;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public View onCreateView(ViewGroup parent, int viewType) {
            LinearLayout rootView = new LinearLayout(mContext);
            rootView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            rootView.setOrientation(LinearLayout.VERTICAL);
            rootView.setBackgroundColor(getContext().getResources().getColor(android.R.color.white));

            if (viewType == 0) {
                View containView = LayoutInflater.from(getContext()).inflate(R.layout.agent_status_loading_more, rootView, false);
                setLoadingStatus(containView);
                rootView.addView(containView);
            } else {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(ViewUtils.dip2px(getContext(), 30), 0, 0, 0);
                itemViewHolder = new ItemViewHolder();
                TextView textView = new TextView(mContext);
                textView.setHeight(ViewUtils.dip2px(getContext(), 50));
                textView.setGravity(Gravity.CENTER_VERTICAL);
                itemViewHolder.textView = textView;
                rootView.addView(textView, params);
                rootView.setTag(itemViewHolder);
            }

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
        public CellStatus.LoadingMoreStatus loadingMoreStatus() {
            return statusMore;
        }

        private void setLoadingStatus(View containView) {
            Button btMoreLoading = (Button) containView.findViewById(R.id.bt_more_loading);
            btMoreLoading.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    statusMore = CellStatus.LoadingMoreStatus.LOADING;
                    updateAgentCell();
                }
            });
            Button btMoreFailed = (Button) containView.findViewById(R.id.bt_more_failed);
            btMoreFailed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    statusMore = CellStatus.LoadingMoreStatus.FAILED;
                    updateAgentCell();
                }
            });
            Button btMoreDone = (Button) containView.findViewById(R.id.bt_more_done);
            btMoreDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    statusMore = CellStatus.LoadingMoreStatus.DONE;
                    updateAgentCell();
                }
            });
            Button btIncreaseSection = (Button) containView.findViewById(R.id.bt_increase_section);
            btIncreaseSection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sectionCount++;
                    updateAgentCell();
                }
            });
        }
    }
}