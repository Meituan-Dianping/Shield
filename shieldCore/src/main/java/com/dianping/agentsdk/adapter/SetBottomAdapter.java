package com.dianping.agentsdk.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.dianping.agentsdk.sectionrecycler.section.MergeSectionDividerAdapter;
import com.dianping.agentsdk.sectionrecycler.section.PieceAdapter;
import com.dianping.shield.entity.CellType;
import com.dianping.shield.feature.ExtraCellBottomInterface;
import com.dianping.shield.feature.SetBottomInterface;

/**
 * Created by zdh on 17/3/27.
 */

public class SetBottomAdapter extends WrapperPieceAdapter<SetBottomInterface> {
    protected MergeSectionDividerAdapter.BasicHolder emptyBottomViewHolder;
    protected MergeSectionDividerAdapter.BasicHolder trueBottomViewHolder;
    protected MergeSectionDividerAdapter.BasicHolder emptyBottomViewHolderForHeader;
    protected MergeSectionDividerAdapter.BasicHolder trueBottomViewHolderForHeader;
    protected MergeSectionDividerAdapter.BasicHolder emptyBottomViewHolderForFooter;
    protected MergeSectionDividerAdapter.BasicHolder trueBottomViewHolderForFooter;

    protected ExtraCellBottomInterface extraCellBottomInterface;

    protected ViewGroup parentView;

    public SetBottomAdapter(@NonNull Context context, @NonNull PieceAdapter adapter, SetBottomInterface extraInterface) {
        super(context, adapter, extraInterface);
    }

    public void setExtraCellBottomInterface(ExtraCellBottomInterface extraCellBottomInterface) {
        this.extraCellBottomInterface = extraCellBottomInterface;
    }

    public void setParentView(ViewGroup parentView) {
        this.parentView = parentView;
    }

    @Override
    public void onBindViewHolder(MergeSectionDividerAdapter.BasicHolder holder, int sectionIndex, int row) {
        CellType cellType = getCellType(sectionIndex, row);
        int innerType = getInnerType(getItemViewType(sectionIndex, row));
        if (cellType == CellType.HEADER
                && extraCellBottomInterface != null
                && extraCellBottomInterface.isHeaderBottomView(innerType)
                && holder == emptyBottomViewHolderForHeader) {
            super.onBindViewHolder(trueBottomViewHolderForHeader, sectionIndex, row);
            return;
        } else if (cellType == CellType.FOOTER
                && extraCellBottomInterface != null
                && extraCellBottomInterface.isFooterBottomView(innerType)
                && holder == emptyBottomViewHolderForFooter) {
            super.onBindViewHolder(trueBottomViewHolderForFooter, sectionIndex, row);
            return;
        } else if (extraInterface != null
                && extraInterface.isBottomView(innerType)
                && holder == emptyBottomViewHolder) {
            super.onBindViewHolder(trueBottomViewHolder, sectionIndex, row);
            return;
        }
        super.onBindViewHolder(holder, sectionIndex, row);
    }

    @Override
    public MergeSectionDividerAdapter.BasicHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        CellType cellType = getCellType(viewType);
        int innerType = getInnerType(viewType);

        if (cellType == CellType.HEADER
                && extraCellBottomInterface != null
                && extraCellBottomInterface.isHeaderBottomView(innerType)
                && extraCellBottomInterface.getHeaderSetBottomFunctionInterface() != null) {
            if (emptyBottomViewHolderForHeader == null) {
                onAdapterChanged();
            }
            if (emptyBottomViewHolderForHeader != null) {
                if (emptyBottomViewHolderForHeader.itemView != null && emptyBottomViewHolderForHeader.itemView.getParent() instanceof ViewGroup) {
                    ((ViewGroup) emptyBottomViewHolderForHeader.itemView.getParent()).removeView(emptyBottomViewHolderForHeader.itemView);
                }
                return emptyBottomViewHolderForHeader;
            }

        } else if (cellType == CellType.FOOTER
                && extraCellBottomInterface != null
                && extraCellBottomInterface.isFooterBottomView(innerType)
                && extraCellBottomInterface.getFooterSetBottomFunctionInterface() != null) {
            if (emptyBottomViewHolderForFooter == null) {
                onAdapterChanged();
            }
            if (emptyBottomViewHolderForFooter != null) {
                if (emptyBottomViewHolderForFooter.itemView != null && emptyBottomViewHolderForFooter.itemView.getParent() instanceof ViewGroup) {
                    ((ViewGroup) emptyBottomViewHolderForFooter.itemView.getParent()).removeView(emptyBottomViewHolderForFooter.itemView);
                }
                return emptyBottomViewHolderForFooter;
            }

        } else if (extraInterface != null
                && extraInterface.getSetBottomFunctionInterface() != null
                && extraInterface.isBottomView(innerType)) {
            if (emptyBottomViewHolder == null) {
                onAdapterChanged();
            }
            if (emptyBottomViewHolder != null) {
                if (emptyBottomViewHolder.itemView != null && emptyBottomViewHolder.itemView.getParent() instanceof ViewGroup) {
                    ((ViewGroup) emptyBottomViewHolder.itemView.getParent()).removeView(emptyBottomViewHolder.itemView);
                }
                return emptyBottomViewHolder;
            }
        }

        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onAdapterChanged() {
        super.onAdapterChanged();
        int sectionCount = getSectionCount();
        for (int i = 0; i < sectionCount; i++) {
            int rowCount = getRowCount(i);
            for (int j = 0; j < rowCount; j++) {
                int viewType = getItemViewType(i, j);
                CellType cellType = getCellType(viewType);
                int innerType = getInnerType(viewType);

                if (cellType == CellType.HEADER
                        && extraCellBottomInterface != null
                        && extraCellBottomInterface.isHeaderBottomView(innerType)
                        && extraCellBottomInterface.getHeaderSetBottomFunctionInterface() != null) {
                    MergeSectionDividerAdapter.BasicHolder headerHolder = super.onCreateViewHolder(parentView, viewType);
                    if (headerHolder != null && headerHolder.itemView != null) {
                        boolean isSuccess = extraCellBottomInterface.getHeaderSetBottomFunctionInterface().setBottomView(headerHolder.itemView);
                        if (isSuccess) {
                            View emptyView = new View(getContext());
                            emptyBottomViewHolderForHeader = new MergeSectionDividerAdapter.BasicHolder(emptyView);
                            trueBottomViewHolderForHeader = headerHolder;
                        }
                    }
                    super.onBindViewHolder(headerHolder, i, j);
                } else if (cellType == CellType.FOOTER
                        && extraCellBottomInterface != null
                        && extraCellBottomInterface.isFooterBottomView(innerType)
                        && extraCellBottomInterface.getFooterSetBottomFunctionInterface() != null) {
                    MergeSectionDividerAdapter.BasicHolder footerHolder = super.onCreateViewHolder(parentView, viewType);
                    if (footerHolder != null && footerHolder.itemView != null) {
                        boolean isSuccess = extraCellBottomInterface.getFooterSetBottomFunctionInterface().setBottomView(footerHolder.itemView);
                        if (isSuccess) {
                            View emptyView = new View(getContext());
                            emptyBottomViewHolderForFooter = new MergeSectionDividerAdapter.BasicHolder(emptyView);
                            trueBottomViewHolderForFooter = footerHolder;
                        }
                    }
                    super.onBindViewHolder(footerHolder, i, j);
                } else if (extraInterface != null
                        && extraInterface.getSetBottomFunctionInterface() != null
                        && extraInterface.isBottomView(innerType)) {
                    MergeSectionDividerAdapter.BasicHolder viewHolder = super.onCreateViewHolder(parentView, viewType);
                    if (viewHolder != null && viewHolder.itemView != null) {
                        boolean isSuccess = extraInterface.getSetBottomFunctionInterface().setBottomView(viewHolder.itemView);
                        if (isSuccess) {
                            //创建一个空白holder，用来替换置底holder的位置，确保在onBindViewHolder时候可以找到置底Holder，进行更新
                            View emptyView = new View(getContext());
                            emptyBottomViewHolder = new MergeSectionDividerAdapter.BasicHolder(emptyView);
                            trueBottomViewHolder = viewHolder;
                        }
                    }
                    super.onBindViewHolder(viewHolder, i, j);
                }
            }
        }
    }


}
