package com.dianping.shield.viewcell;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

import com.dianping.agentsdk.framework.CellStatus;
import com.dianping.agentsdk.framework.CellStatusInterface;
import com.dianping.agentsdk.framework.CellStatusMoreInterface;
import com.dianping.agentsdk.framework.DividerInfo;
import com.dianping.agentsdk.framework.DividerInterface;
import com.dianping.agentsdk.framework.ItemClickInterface;
import com.dianping.agentsdk.framework.ItemIdInterface;
import com.dianping.agentsdk.framework.ItemLongClickInterface;
import com.dianping.agentsdk.framework.LinkType;
import com.dianping.agentsdk.framework.SectionDividerInfoInterface;
import com.dianping.agentsdk.framework.SectionExtraCellInterface;
import com.dianping.agentsdk.framework.SectionLinkCellInterface;
import com.dianping.shield.feature.SectionTitleInterface;

/**
 * Created by hezhi on 16/6/28.
 */
public abstract class BaseViewCell implements SectionExtraCellInterface,
        DividerInterface, CellStatusInterface,
        CellStatusMoreInterface, SectionLinkCellInterface,
        ItemIdInterface, ItemClickInterface, ItemLongClickInterface,
        SectionTitleInterface, SectionDividerInfoInterface {
    public Context mContext;
    protected OnItemClickListener mOnItemClickListener;
    protected OnItemLongClickListener mOnItemLongClickListener;

    public BaseViewCell(Context context) {
        this.mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public CellStatus.LoadingStatus loadingStatus() {
        return CellStatus.LoadingStatus.UNKNOWN;
    }

    @Override
    public View loadingView() {
        return null;
    }

    @Override
    public View loadingFailedView() {
        return null;
    }

    @Override
    public View emptyView() {
        return null;
    }

    @Override
    public View.OnClickListener loadingRetryListener() {
        return null;
    }

    @Override
    public CellStatus.LoadingMoreStatus loadingMoreStatus() {
        return CellStatus.LoadingMoreStatus.UNKNOWN;
    }

    @Override
    public View loadingMoreView() {
        return null;
    }

    @Override
    public View loadingMoreFailedView() {
        return null;
    }

    @Override
    public View.OnClickListener loadingMoreRetryListener() {
        return null;
    }

    @Override
    public long getItemId(int section, int position) {
        long id = 0;
        for (int i = 0; i < getSectionCount(); i++) {
            if (i < section) {
                for (int j = 0; j < getRowCount(i); j++) {
                    id++;
                }
            } else if (i == section) {
                for (int j = 0; j < getRowCount(i); j++) {
                    if (j < position) {
                        id++;
                    }
                }
            }

        }
        return id;
    }

    @Override
    public boolean hasHeaderForSection(int sectionPostion) {
        return false;
    }

    @Override
    public boolean hasTopDividerForHeader(int sectionPosition) {
        return true;
    }

    @Override
    public boolean hasBottomDividerForHeader(int sectionPosition) {
        return true;
    }

    @Override
    public float getHeaderDividerOffset(int sectionPosition) {
        return -1;
    }

    @Override
    public boolean hasFooterForSection(int sectionPostion) {
        return false;
    }

    @Override
    public boolean hasBottomDividerForFooter(int sectionPosition) {
        return true;
    }

    @Override
    public float getFooterDividerOffset(int sectionPosition) {
        return -1;
    }

    @Override
    public int getHeaderViewType(int sectionPosition) {
        return 0;
    }

    @Override
    public int getFooterViewType(int sectionPosition) {
        return 0;
    }

    @Override
    public int getHeaderViewTypeCount() {
        return 1;
    }

    @Override
    public int getFooterViewTypeCount() {
        return 1;
    }

    @Override
    public View onCreateHeaderView(ViewGroup parent, int headerViewType) {
        return null;
    }

    @Override
    public void updateHeaderView(View view, int sectionPostion, ViewGroup parent) {

    }

    @Override
    public View onCreateFooterView(ViewGroup parent, int footerViewType) {
        return null;
    }

    @Override
    public void updateFooterView(View view, int sectionPostion, ViewGroup parent) {

    }

    @Override
    public boolean showDivider(int sectionPosition, int rowPosition) {
        return true;
    }

    @Override
    public ShowType dividerShowType(int sectionPosition) {
        return null;
    }

    @Override
    public Drawable getDivider(int sectionPosition, int rowPosition) {
        return null;
    }

    @Override
    public int dividerOffset(int sectionPosition, int rowPosition) {
        return -1;
    }

    @Override
    public DividerInfo getDividerInfo(int section) {
        return null;
    }

    @Override
    public LinkType.Previous linkPrevious(int sectionPosition) {
        return null;
    }

    @Override
    public LinkType.Next linkNext(int sectionPosition) {
        return null;
    }

    @Override
    public float getSectionHeaderHeight(int sectionPoisition) {
        return -1;
    }

    @Override
    public float getSectionFooterHeight(int sectionPoisition) {
        return -1;
    }

    @Override
    public void onBindView(CellStatus.LoadingMoreStatus status) {

    }

    @Override
    public OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    @Override
    public OnItemLongClickListener getOnItemLongClickListener() {
        return mOnItemLongClickListener;
    }

    @Override
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mOnItemLongClickListener = listener;
    }

    @Override
    public String getSectionTitle(int section) {
        return null;
    }
}
