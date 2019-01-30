package com.dianping.agentsdk.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.dianping.agentsdk.pagecontainer.OnTopViewLayoutChangeListener;
import com.dianping.agentsdk.pagecontainer.SetMultiTopFunctionInterface;
import com.dianping.agentsdk.pagecontainer.SetTopParams;
import com.dianping.agentsdk.pagecontainer.SetTopViewListenerInterface;
import com.dianping.agentsdk.sectionrecycler.section.MergeSectionDividerAdapter;
import com.dianping.agentsdk.sectionrecycler.section.PieceAdapter;
import com.dianping.shield.entity.CellType;
import com.dianping.shield.feature.ExtraCellTopInterface;
import com.dianping.shield.feature.ExtraCellTopParamsInterface;
import com.dianping.shield.feature.OnTopViewLayoutChangeListenerInterface;
import com.dianping.shield.feature.SetTopInterface;
import com.dianping.shield.feature.SetTopParamsInterface;

/**
 * Created by zdh on 17/3/27.
 */

public class SetTopAdapter extends WrapperPieceAdapter<SetTopInterface> {

    private MergeSectionDividerAdapter.BasicHolder emptyTopViewHolder;
    private MergeSectionDividerAdapter.BasicHolder trueTopViewHolder;

    protected ExtraCellTopInterface extraCellTopInterface;
    protected OnTopViewLayoutChangeListenerInterface onTopViewListenerInterface;

    private MergeSectionDividerAdapter.BasicHolder emptyTopViewHolderForHeader;
    private MergeSectionDividerAdapter.BasicHolder trueTopViewHolderForHeader;
    private MergeSectionDividerAdapter.BasicHolder emptyTopViewHolderForFooter;
    private MergeSectionDividerAdapter.BasicHolder trueTopViewHolderForFooter;

    public SetTopAdapter(@NonNull Context context, @NonNull PieceAdapter adapter, SetTopInterface extraInterface) {
        super(context, adapter, extraInterface);
    }

    public void setExtraCellTopInterface(ExtraCellTopInterface extraCellTopInterface) {
        this.extraCellTopInterface = extraCellTopInterface;
    }

    public ExtraCellTopInterface getExtraCellTopInterface() {
        return extraCellTopInterface;
    }

    public OnTopViewLayoutChangeListenerInterface getOnTopViewListenerInterface() {
        return onTopViewListenerInterface;
    }

    public void setOnTopViewListenerInterface(OnTopViewLayoutChangeListenerInterface onTopViewListenerInterface) {
        this.onTopViewListenerInterface = onTopViewListenerInterface;
    }

    @Override
    public void onBindViewHolder(MergeSectionDividerAdapter.BasicHolder holder, int sectionIndex, int row) {
        CellType cellType = getCellType(sectionIndex, row);
        int innerType = getInnerType(getItemViewType(sectionIndex, row));

        if (cellType == CellType.HEADER) {

            if (extraCellTopInterface != null
                    && extraCellTopInterface.isHeaderTopView(innerType)
                    && holder == emptyTopViewHolderForHeader
                    && extraCellTopInterface instanceof ExtraCellTopParamsInterface
                    && ((ExtraCellTopParamsInterface) extraCellTopInterface).getHeaderSetTopParams(innerType) != null) {

                SetTopParams params = ((ExtraCellTopParamsInterface) extraCellTopInterface).getHeaderSetTopParams(innerType);
                extraCellTopInterface.getSetHeaderTopFunctionInterface().updateSetTopParams(trueTopViewHolderForHeader.itemView, params);
                super.onBindViewHolder(trueTopViewHolderForHeader, sectionIndex, row);
                return;
            }

        } else if (cellType == CellType.FOOTER) {

            if (extraCellTopInterface != null
                    && extraCellTopInterface.isFooterTopView(innerType)
                    && holder == emptyTopViewHolderForFooter
                    && extraCellTopInterface instanceof ExtraCellTopParamsInterface
                    && ((ExtraCellTopParamsInterface) extraCellTopInterface).getFooterSetTopParams(innerType) != null) {
                SetTopParams params = ((ExtraCellTopParamsInterface) extraCellTopInterface).getFooterSetTopParams(innerType);
                extraCellTopInterface.getSetFooterTopFunctionInterface().updateSetTopParams(trueTopViewHolderForFooter.itemView, params);
                super.onBindViewHolder(trueTopViewHolderForFooter, sectionIndex, row);
                return;
            }

        } else {

            if (extraInterface.isTopView(innerType)
                    && holder == emptyTopViewHolder
                    && extraInterface instanceof SetTopParamsInterface
                    && extraInterface.getSetTopFunctionInterface() != null) {
                SetTopParams params = ((SetTopParamsInterface) extraInterface).getSetTopParams(innerType);
                extraInterface.getSetTopFunctionInterface().updateSetTopParams(trueTopViewHolder.itemView, params);
                super.onBindViewHolder(trueTopViewHolder, sectionIndex, row);
                return;
            }

        }
        super.onBindViewHolder(holder, sectionIndex, row);
    }

    @Override
    public MergeSectionDividerAdapter.BasicHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (extraInterface != null && extraInterface.getSetTopFunctionInterface() != null) {
            int innerType = getInnerType(viewType);
            CellType cellType = getCellType(viewType);
            if (cellType == CellType.HEADER && extraCellTopInterface != null && extraCellTopInterface.isHeaderTopView(innerType)) {
                MergeSectionDividerAdapter.BasicHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                if (viewHolder != null && viewHolder.itemView != null) {
                    //获取一个空白View，替换置顶View在列表中的位置
                    SetTopParams params = null;
                    if (extraCellTopInterface instanceof ExtraCellTopParamsInterface) {
                        params = ((ExtraCellTopParamsInterface) extraCellTopInterface).getHeaderSetTopParams(innerType);
                    }

                    View emptyView = null;
                    if (extraCellTopInterface.getSetHeaderTopFunctionInterface() instanceof SetMultiTopFunctionInterface
                            && ((SetMultiTopFunctionInterface) extraCellTopInterface.getSetHeaderTopFunctionInterface()).needMultiStickTop()) {
                        emptyView = ((SetMultiTopFunctionInterface) extraCellTopInterface.getSetHeaderTopFunctionInterface()).setMultiTopView(extraCellTopInterface, innerType, viewHolder.itemView, params);
                    } else {
                        emptyView = extraCellTopInterface.getSetHeaderTopFunctionInterface().setTopView(viewHolder.itemView, params);
                    }

                    // set top view layout change listener
                    if (onTopViewListenerInterface != null) {
                        SetTopViewListenerInterface setTopViewListenerInterface = onTopViewListenerInterface.getSetTopViewListenerInterface();
                        OnTopViewLayoutChangeListener listener = onTopViewListenerInterface.getOnTopViewLayoutChangeListener(cellType, viewType);
                        if (setTopViewListenerInterface != null && listener != null) {
                            setTopViewListenerInterface.setOnTopViewLayoutChangeListener(listener);
                        }
                    }

                    if (emptyView != null) {
                        emptyTopViewHolderForHeader = new MergeSectionDividerAdapter.BasicHolder(emptyView);
                        trueTopViewHolderForHeader = viewHolder;
                        return emptyTopViewHolderForHeader;
                    }

                }
            } else if (cellType == CellType.FOOTER && extraCellTopInterface != null && extraCellTopInterface.isFooterTopView(innerType)) {
                MergeSectionDividerAdapter.BasicHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                if (viewHolder != null && viewHolder.itemView != null) {
                    //获取一个空白View，替换置顶View在列表中的位置
                    SetTopParams params = null;
                    if (extraCellTopInterface instanceof ExtraCellTopParamsInterface) {
                        params = ((ExtraCellTopParamsInterface) extraCellTopInterface).getFooterSetTopParams(innerType);
                    }
                    View emptyView = null;
                    if (extraCellTopInterface.getSetFooterTopFunctionInterface() instanceof SetMultiTopFunctionInterface
                            && ((SetMultiTopFunctionInterface) extraCellTopInterface.getSetFooterTopFunctionInterface()).needMultiStickTop()) {
                        emptyView = ((SetMultiTopFunctionInterface) extraCellTopInterface.getSetFooterTopFunctionInterface()).setMultiTopView(extraCellTopInterface, innerType, viewHolder.itemView, params);
                    } else {
                        emptyView = extraCellTopInterface.getSetFooterTopFunctionInterface().setTopView(viewHolder.itemView, params);
                    }

                    // set top view layout change listener
                    if (onTopViewListenerInterface != null) {
                        SetTopViewListenerInterface setTopViewListenerInterface = onTopViewListenerInterface.getSetTopViewListenerInterface();
                        OnTopViewLayoutChangeListener listener = onTopViewListenerInterface.getOnTopViewLayoutChangeListener(cellType, viewType);
                        if (setTopViewListenerInterface != null && listener != null) {
                            setTopViewListenerInterface.setOnTopViewLayoutChangeListener(listener);
                        }
                    }
                    if (emptyView != null) {
                        emptyTopViewHolderForFooter = new MergeSectionDividerAdapter.BasicHolder(emptyView);
                        trueTopViewHolderForFooter = viewHolder;
                        return emptyTopViewHolderForFooter;
                    }

                }
            } else if (cellType == CellType.NORMAL && extraInterface != null && extraInterface.isTopView(innerType)) {
                MergeSectionDividerAdapter.BasicHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                if (viewHolder != null && viewHolder.itemView != null) {
                    SetTopParams params = null;
                    if (extraInterface instanceof SetTopParamsInterface) {
                        params = ((SetTopParamsInterface) extraInterface).getSetTopParams(innerType);
                    }
                    View emptyView = null;
                    if (extraInterface.getSetTopFunctionInterface() instanceof SetMultiTopFunctionInterface
                            && ((SetMultiTopFunctionInterface) extraInterface.getSetTopFunctionInterface()).needMultiStickTop()) {
                        emptyView = ((SetMultiTopFunctionInterface) extraInterface.getSetTopFunctionInterface()).setMultiTopView(extraInterface, innerType, viewHolder.itemView, params);
                    } else {
                        emptyView = extraInterface.getSetTopFunctionInterface().setTopView(viewHolder.itemView, params);
                    }
                    // set top view layout change listener
                    if (onTopViewListenerInterface != null) {
                        SetTopViewListenerInterface setTopViewListenerInterface = onTopViewListenerInterface.getSetTopViewListenerInterface();
                        OnTopViewLayoutChangeListener listener = onTopViewListenerInterface.getOnTopViewLayoutChangeListener(cellType, viewType);
                        if (setTopViewListenerInterface != null && listener != null) {
                            setTopViewListenerInterface.setOnTopViewLayoutChangeListener(listener);
                        }
                    }
                    if (emptyView != null) {
                        emptyTopViewHolder = new MergeSectionDividerAdapter.BasicHolder(emptyView);
                        trueTopViewHolder = viewHolder;
                        return emptyTopViewHolder;
                    }

                }
            }

        }
        return super.onCreateViewHolder(parent, viewType);
    }
}
