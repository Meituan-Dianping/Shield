package com.example.shield.mix.cell;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dianping.agentsdk.framework.CellStatus;
import com.dianping.agentsdk.framework.LinkType;
import com.dianping.shield.viewcell.BaseViewCell;
import com.example.shield.R;
import com.example.shield.mix.agent.MixCellAgent;

/**
 * Created by nihao on 2017/7/17.
 */
public class MixCell extends BaseViewCell implements MixLoadingCell.MixLoadingListener {
    private static final int ROW_COUNT = 3;
    private static final int VIEW_TYPE_COUNT = 1;

    private CellStatus.LoadingStatus status = CellStatus.LoadingStatus.DONE;
    private CellStatus.LoadingMoreStatus moreStatus = CellStatus.LoadingMoreStatus.DONE;
    private MixCellAgent agent;
    private int sectionCount = 3;

    public MixCell(Context context, MixCellAgent agent) {
        super(context);
        this.agent = agent;
    }

    @Override
    public int getSectionCount() {
        return sectionCount;
    }

    @Override
    public int getRowCount(int sectionPosition) {
        return ROW_COUNT;
    }

    @Override
    public int getViewType(int sectionPosition, int rowPosition) {
        if (sectionPosition == 0 && rowPosition == 0) {
            return 0;
        }
        return 1;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public boolean hasHeaderForSection(int sectionPostion) {
        return true;
    }

    @Override
    public View onCreateHeaderView(ViewGroup parent, int headerViewType) {
        return LayoutInflater.from(getContext()).inflate(R.layout.module_cell_item, parent, false);
    }

    @Override
    public void updateHeaderView(View view, int sectionPostion, ViewGroup parent) {
        TextView textView = (TextView) view.findViewById(R.id.header_footer_item_tx);
        textView.setTextColor(Color.parseColor("#98D839"));
        textView.setText("header for section: " + sectionPostion);
        switch (sectionPostion) {
            case 0:
                textView.setText("header divider none");
                break;
            case 1:
                textView.setText("header divider middle with custom divider and header bottom divider none");
                break;
            case 2:
                textView.setText("header divider top end");
                break;
        }
    }

    @Override
    public boolean hasFooterForSection(int sectionPostion) {
        return true;
    }

    @Override
    public View onCreateFooterView(ViewGroup parent, int footerViewType) {
        return LayoutInflater.from(getContext()).inflate(R.layout.module_cell_item, parent, false);
    }

    @Override
    public void updateFooterView(View view, int sectionPostion, ViewGroup parent) {
        TextView textView = (TextView) view.findViewById(R.id.header_footer_item_tx);
        textView.setTextColor(Color.parseColor("#FF9900"));
        textView.setText("footer for section : " + sectionPostion);
    }

    @Override
    public View onCreateView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(getContext()).inflate(R.layout.module_cell_item, parent, false);
    }

    @Override
    public void updateView(View view, int sectionPosition, int rowPosition, ViewGroup parent) {
        TextView textView = (TextView) view.findViewById(R.id.header_footer_item_tx);
        textView.setText("section : " + sectionPosition + " row : " + rowPosition);
    }

    @Override
    public ShowType dividerShowType(int sectionPosition) {
        switch (sectionPosition) {
            case 0:
                return ShowType.NONE;// no divider;
            case 1:
                return ShowType.MIDDLE;// top end divider;
            case 2:
                return ShowType.TOP_END;// middle divider;
        }
        return super.dividerShowType(sectionPosition);
    }

    @Override
    public boolean showDivider(int sectionPosition, int rowPosition) {
        if (sectionPosition == 1 && rowPosition == 0) {
            return false;
        }
        return super.showDivider(sectionPosition, rowPosition);
    }

    @Override
    public Drawable getDivider(int sectionPosition, int rowPosition) {
        if (sectionPosition == 1 && rowPosition != 4) {
            return getContext().getResources().getDrawable(R.drawable.shield_demo_setdivider_color);
        }
        return super.getDivider(sectionPosition, rowPosition);
    }

    @Override
    public boolean hasBottomDividerForHeader(int sectionPosition) {
        if (sectionPosition == 1) {
            return false;
        }

        return super.hasBottomDividerForHeader(sectionPosition);
    }

    @Override
    public CellStatus.LoadingStatus loadingStatus() {
        return status;
    }

    @Override
    public CellStatus.LoadingMoreStatus loadingMoreStatus() {
        return moreStatus;
    }

    @Override
    public View loadingView() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.loading_layout, null, false);
        v.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        TextView tv = (TextView) v.findViewById(R.id.loading);
        tv.setText("loading正在加载中...");
        return v;
    }

    @Override
    public View emptyView() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.loading_layout, null, false);
        v.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        TextView tv = (TextView) v.findViewById(R.id.loading);
        tv.setText("no data数据为空...");
        return v;
    }

    @Override
    public View loadingFailedView() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.loading_layout, null, false);
        v.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        TextView tv = (TextView) v.findViewById(R.id.loading);
        tv.setText("Failed请求失败,点击重试...");
        return v;
    }

    @Override
    public View loadingMoreView() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.loading_layout, null, false);
        v.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        TextView tv = (TextView) v.findViewById(R.id.loading);
        tv.setText("正在加载更多...");
        return v;
    }

    @Override
    public View.OnClickListener loadingRetryListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agent.loading();
            }
        };
    }

    @Override
    public void onLoading() {
        status = CellStatus.LoadingStatus.LOADING;
        agent.updateAgentCell();
    }

    @Override
    public void onEmpty() {
        status = CellStatus.LoadingStatus.EMPTY;
        agent.updateAgentCell();
    }

    @Override
    public void onFailed() {
        status = CellStatus.LoadingStatus.FAILED;
        agent.updateAgentCell();
    }

    @Override
    public void onMore() {
        status = CellStatus.LoadingStatus.DONE;
        moreStatus = CellStatus.LoadingMoreStatus.LOADING;
        agent.updateAgentCell();
    }

    public void moredata() {
        sectionCount++;
        moreStatus = CellStatus.LoadingMoreStatus.DONE;
        agent.updateAgentCell();
    }

    @Override
    public void onDone() {
        status = CellStatus.LoadingStatus.DONE;
        moreStatus = CellStatus.LoadingMoreStatus.DONE;
        agent.updateAgentCell();
    }

    @Override
    public LinkType.Next linkNext(int sectionPosition) {
        if (sectionPosition == 0){
            return LinkType.Next.LINK_TO_NEXT;
        }
        return super.linkNext(sectionPosition);
    }

    @Override
    public LinkType.Previous linkPrevious(int sectionPosition) {
        if (sectionPosition == 1){
            return LinkType.Previous.LINK_TO_PREVIOUS;
        }
        return super.linkPrevious(sectionPosition);
    }
}
