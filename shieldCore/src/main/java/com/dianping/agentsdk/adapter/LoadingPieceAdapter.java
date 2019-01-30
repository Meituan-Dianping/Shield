package com.dianping.agentsdk.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dianping.agentsdk.framework.CellStatus;
import com.dianping.agentsdk.framework.CellStatusInterface;
import com.dianping.agentsdk.framework.CellStatusReuseInterface;
import com.dianping.agentsdk.framework.DividerInfo;
import com.dianping.agentsdk.framework.LinkType;
import com.dianping.agentsdk.framework.ViewUtils;
import com.dianping.agentsdk.sectionrecycler.section.MergeSectionDividerAdapter;
import com.dianping.agentsdk.sectionrecycler.section.PieceAdapter;
import com.dianping.shield.entity.CellType;
import com.dianping.shield.feature.LoadingAndLoadingMoreCreator;

/**
 * Created by hezhi on 16/6/24.
 * 在装饰模式中，不同的装饰间是链式调用
 * 如果对父类中的方法入参进行过装饰的话，必须对所有涉及到的方法进行还原
 */
public class LoadingPieceAdapter extends WrapperPieceAdapter<CellStatusInterface> {
    private final int LOADING_TYPE = 0;
    private final int FAILED_TYPE = 1;
    private final int EMPTY_TYPE = 2;
    private final int TYPE_OFFSET = 3;
    protected LoadingAndLoadingMoreCreator creator;

    public LoadingPieceAdapter(@NonNull Context context, @NonNull PieceAdapter adapter, CellStatusInterface extraInterface) {
        super(context, adapter, extraInterface);
    }

    public void setDefaultLoadingCreator(LoadingAndLoadingMoreCreator creator) {
        this.creator = creator;
    }

    //当LoadingStatus为Loading，Failed，或者Empty时展示相应View而不展示内部Section和Row结构
    private boolean needShow() {
        if (extraInterface != null) {
            if (extraInterface.loadingStatus() == CellStatus.LoadingStatus.LOADING
                    || extraInterface.loadingStatus() == CellStatus.LoadingStatus.FAILED || extraInterface.loadingStatus() == CellStatus.LoadingStatus.EMPTY) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isInnerSection(int wrappedSection) {
        if (needShow()) {
            return false;
        }
        return super.isInnerSection(wrappedSection);
    }

    @Override
    public int getSectionCount() {
        if (needShow()) {
            return 1;
        }
        return super.getSectionCount();
    }

    @Override
    public int getRowCount(int sectionIndex) {
        if (needShow()) {
            return 1;
        }
        return super.getRowCount(sectionIndex);
    }

    @Override
    public int getItemViewType(int sectionIndex, int row) {
        if (extraInterface != null) {
            if (extraInterface.loadingStatus() == CellStatus.LoadingStatus.LOADING) {
                return LOADING_TYPE;
            } else if (extraInterface.loadingStatus() == CellStatus.LoadingStatus.FAILED) {
                return FAILED_TYPE;
            } else if (extraInterface.loadingStatus() == CellStatus.LoadingStatus.EMPTY) {
                return EMPTY_TYPE;
            }
        }
        return super.getItemViewType(sectionIndex, row) + TYPE_OFFSET;
    }

    @Override
    public int getInnerType(int wrappedType) {
        if (wrappedType < TYPE_OFFSET) {
            return wrappedType;
        }
        return super.getInnerType(wrappedType - TYPE_OFFSET);
    }

    @Override
    public long getItemId(int section, int row) {
        if (extraInterface != null) {
            if (extraInterface.loadingStatus() == CellStatus.LoadingStatus.LOADING) {
                return LOADING_TYPE;
            } else if (extraInterface.loadingStatus() == CellStatus.LoadingStatus.FAILED) {
                return FAILED_TYPE;
            } else if (extraInterface.loadingStatus() == CellStatus.LoadingStatus.EMPTY) {
                return EMPTY_TYPE;
            }
        }
        return super.getItemId(section, row) + TYPE_OFFSET;
    }

    @Override
    public CellType getCellType(int wrappedSection, int wrappedRow) {
        if (needShow()) {
            return CellType.LOADING;
        } else {
            return super.getCellType(wrappedSection, wrappedRow);
        }
    }

    @Override
    public CellType getCellType(int viewType) {
        if (viewType == LOADING_TYPE || viewType == FAILED_TYPE || viewType == EMPTY_TYPE) {
            return CellType.LOADING;
        }
        return super.getCellType(viewType - TYPE_OFFSET);
    }

    @Override
    public Pair<Integer, Integer> getInnerPosition(int wrappedSection, int wrappedRow) {
        if (needShow()) {
            return new Pair<>(0, 0);
        } else {
            return super.getInnerPosition(wrappedSection, wrappedRow);
        }
    }

    /* 对所有涉及到section和row的被包装方法进行section还原 start **/
    @Override
    public float getSectionHeaderHeight(int section) {
        if (needShow()) {
            return NO_SPACE_HIGHT;
        }
        return super.getSectionHeaderHeight(section);
    }

    @Override
    public float getSectionFooterHeight(int section) {
        if (needShow()) {
            return NO_SPACE_HIGHT;
        }
        return super.getSectionFooterHeight(section);
    }

    @Override
    public Drawable getTopDivider(int section, int row) {
        if (needShow()) {
            return null;
        }
        return super.getTopDivider(section, row);
    }


    @Override
    public Drawable getBottomDivider(int section, int row) {
        if (needShow()) {
            return null;
        }
        return super.getBottomDivider(section, row);
    }


    @Override
    public Rect topDividerOffset(int section, int row) {
        if (needShow()) {
            return null;
        }
        return super.topDividerOffset(section, row);
    }

    @Override
    public Rect bottomDividerOffset(int section, int row) {
        if (needShow()) {
            return null;
        }
        return super.bottomDividerOffset(section, row);
    }

    @Override
    public LinkType.Previous getPreviousLinkType(int section) {
        if (needShow()) {
            return null;
        }
        return super.getPreviousLinkType(section);
    }

    @Override
    public LinkType.Next getNextLinkType(int section) {
        if (needShow()) {
            return null;
        }
        return super.getNextLinkType(section);
    }

    @Override
    public boolean showTopDivider(int section, int row) {
        if (needShow()) {
            return false;
        }
        return super.showTopDivider(section, row);
    }


    @Override
    public boolean showBottomDivider(int section, int row) {
        if (needShow()) {
            return false;
        }
        return super.showBottomDivider(section, row);
    }

    @Override
    public boolean hasBottomDividerVerticalOffset(int section, int row) {
        if (needShow()) {
            return false;
        }
        return super.hasBottomDividerVerticalOffset(section, row);
    }

    @Override
    public boolean hasTopDividerVerticalOffset(int section, int row) {
        if (needShow()) {
            return false;
        }
        return super.hasTopDividerVerticalOffset(section, row);
    }

    @Override
    public DividerInfo getDividerInfo(int section, int row) {
        if (needShow()) {
            return null;
        }
        return super.getDividerInfo(section, row);
    }
    /* 对所有涉及到section和row的被包装方法进行section还原 end **/


    @Override
    public void onBindViewHolder(MergeSectionDividerAdapter.BasicHolder holder, int sectionIndex, int row) {
        if (needShow()) {
            if (getItemViewType(sectionIndex, row) == LOADING_TYPE) {
                if (extraInterface instanceof CellStatusReuseInterface) {
                    ((CellStatusReuseInterface)extraInterface).updateLoadingView(holder.itemView);
                    return;
                }
            } else if (getItemViewType(sectionIndex, row) == FAILED_TYPE) {
                if (extraInterface instanceof CellStatusReuseInterface) {
                    ((CellStatusReuseInterface)extraInterface).updateLoadingFailedView(holder.itemView);
                    return;
                }
            } else if (getItemViewType(sectionIndex, row) == EMPTY_TYPE) {
                if (extraInterface instanceof CellStatusReuseInterface) {
                    ((CellStatusReuseInterface)extraInterface).updateLoadingEmptyView(holder.itemView);
                    return;
                }
            }
        } else {
            super.onBindViewHolder(holder, sectionIndex, row);
        }
    }

    @Override
    public MergeSectionDividerAdapter.BasicHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (extraInterface != null) {
            if (viewType == LOADING_TYPE) {
                View loadingView = extraInterface.loadingView();
                if (loadingView == null) {
                    if (creator == null) {
                        TextView textView = new TextView(getContext());
                        textView.setGravity(Gravity.CENTER);
                        textView.setPadding(ViewUtils.dip2px(getContext(), 10),
                                ViewUtils.dip2px(getContext(), 10),
                                ViewUtils.dip2px(getContext(), 10),
                                ViewUtils.dip2px(getContext(), 10));
                        textView.setText("未设置默认LoadingView");
                        loadingView = textView;
                    } else {
                        loadingView = creator.loadingView();
                    }
                }
                MergeSectionDividerAdapter.BasicHolder loadingHolder = new MergeSectionDividerAdapter.BasicHolder(loadingView);
                return loadingHolder;
            } else if (viewType == FAILED_TYPE) {
                View failedView = extraInterface.loadingFailedView();
                if (failedView == null) {
                    if (creator == null) {
                        TextView textView = new TextView(getContext());
                        textView.setGravity(Gravity.CENTER);
                        textView.setPadding(ViewUtils.dip2px(getContext(), 10),
                                ViewUtils.dip2px(getContext(), 10),
                                ViewUtils.dip2px(getContext(), 10),
                                ViewUtils.dip2px(getContext(), 10));
                        textView.setText("未设置默认FailedView");
                        failedView = textView;
                    } else {
                        failedView = creator.loadingFailedView();
                    }
                }
                if (extraInterface.loadingRetryListener() != null && failedView != null) {
                    failedView.setOnClickListener(extraInterface.loadingRetryListener());
                }
                MergeSectionDividerAdapter.BasicHolder failedHolder = new MergeSectionDividerAdapter.BasicHolder(failedView);
                return failedHolder;
            } else if (viewType == EMPTY_TYPE) {
                View emptyView = extraInterface.emptyView();
                if (emptyView == null) {
                    if (creator == null) {
                        TextView textView = new TextView(getContext());
                        textView.setGravity(Gravity.CENTER);
                        textView.setPadding(ViewUtils.dip2px(getContext(), 10),
                                ViewUtils.dip2px(getContext(), 10),
                                ViewUtils.dip2px(getContext(), 10),
                                ViewUtils.dip2px(getContext(), 10));
                        textView.setText("未设置默认EmptyView");
                        emptyView = textView;
                    } else {
                        emptyView = creator.emptyView();
                    }
                }
                MergeSectionDividerAdapter.BasicHolder emptyHolder = new MergeSectionDividerAdapter.BasicHolder(emptyView);
                return emptyHolder;
            } else {
                return super.onCreateViewHolder(parent, viewType - TYPE_OFFSET);
            }
        }
        return super.onCreateViewHolder(parent, viewType);
    }

}
