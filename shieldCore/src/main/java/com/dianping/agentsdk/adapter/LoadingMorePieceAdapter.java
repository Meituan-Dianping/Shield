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
import com.dianping.agentsdk.framework.CellStatusMoreInterface;
import com.dianping.agentsdk.framework.CellStatusMoreReuseInterface;
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
public class LoadingMorePieceAdapter extends WrapperPieceAdapter<CellStatusMoreInterface> {
    private final int LOADING_TYPE = 0;
    private final int FAILED_TYPE = 1;
    private final int TYPE_OFFSET = 2;
    protected LoadingAndLoadingMoreCreator creator;

    public LoadingMorePieceAdapter(@NonNull Context context, @NonNull PieceAdapter adapter, CellStatusMoreInterface extraInterface) {
        super(context, adapter, extraInterface);
    }

    public void setDefaultLoadingMoreCreator(LoadingAndLoadingMoreCreator creator) {
        this.creator = creator;
    }

    @Override
    public int getSectionCount() {
        if (extraInterface != null) {
            //Loading More 作为额外的section添加到最后
            if (hasExtraSection()) {
                return super.getSectionCount() + 1;
            }
        }
        return super.getSectionCount();
    }

    protected boolean hasExtraSection() {
        return extraInterface != null
                && (extraInterface.loadingMoreStatus() == CellStatus.LoadingMoreStatus.LOADING
                || extraInterface.loadingMoreStatus() == CellStatus.LoadingMoreStatus.FAILED);
    }


    private boolean isExtraSection(int wrappedSection) {
        if (hasExtraSection()
                && wrappedSection == getSectionCount() - 1) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isInnerSection(int wrappedSection) {
        if (isExtraSection(wrappedSection)) {
            return false;
        }
        return super.isInnerSection(wrappedSection);
    }


    @Override
    public int getRowCount(int sectionIndex) {
        if (extraInterface != null) {
            if (isExtraSection(sectionIndex)) {
                return 1;
            }
        }
        return super.getRowCount(sectionIndex);
    }

    @Override
    public int getInnerType(int wrappedType) {
        if (wrappedType < TYPE_OFFSET) {
            //只是区分了非内部type，而没有区分loading还是failed
            return wrappedType;
        }
        return super.getInnerType(wrappedType - TYPE_OFFSET);
    }

    @Override
    public CellType getCellType(int viewType) {
        if (viewType == LOADING_TYPE || viewType == FAILED_TYPE) {
            return CellType.LOADING_MORE;
        }
        return super.getCellType(viewType - TYPE_OFFSET);
    }

    @Override
    public CellType getCellType(int wrappedSection, int wrappedRow) {
        if (isExtraSection(wrappedSection)) {
            //只是区分了非内部type，而没有区分loading还是failed
            return CellType.LOADING_MORE;
        } else {
            return super.getCellType(wrappedSection, wrappedRow);
        }
    }

    @Override
    public Pair<Integer, Integer> getInnerPosition(int wrappedSection, int wrappedRow) {
        if (isExtraSection(wrappedSection)) {
            return new Pair<>(wrappedSection, 0);
        } else {
            return super.getInnerPosition(wrappedSection, wrappedRow);
        }
    }

    /* 对所有涉及到section的被包装方法进行section还原 start **/
    @Override
    public float getSectionHeaderHeight(int section) {
        if (isExtraSection(section)) {
            return NO_SPACE_HIGHT;
        }
        return super.getSectionHeaderHeight(section);
    }

    @Override
    public float getSectionFooterHeight(int section) {
        if (isExtraSection(section)) {
            return NO_SPACE_HIGHT;
        }
        return super.getSectionFooterHeight(section);
    }

    @Override
    public Drawable getTopDivider(int section, int row) {
        if (isExtraSection(section)) {
            return null;
        }
        return super.getTopDivider(section, row);
    }


    @Override
    public Drawable getBottomDivider(int section, int row) {
        if (isExtraSection(section)) {
            return null;
        }
        return super.getBottomDivider(section, row);
    }


    @Override
    public Rect topDividerOffset(int section, int row) {
        if (isExtraSection(section)) {
            return null;
        }
        return super.topDividerOffset(section, row);
    }

    @Override
    public Rect bottomDividerOffset(int section, int row) {
        if (isExtraSection(section)) {
            return null;
        }
        return super.bottomDividerOffset(section, row);
    }

    @Override
    public LinkType.Previous getPreviousLinkType(int section) {
        if (isExtraSection(section)) {
            return null;
        }
        return super.getPreviousLinkType(section);
    }

    @Override
    public LinkType.Next getNextLinkType(int section) {
        if (isExtraSection(section)) {
            return null;
        }
        return super.getNextLinkType(section);
    }

    @Override
    public long getItemId(int section, int row) {
        if (extraInterface != null) {
            if (extraInterface.loadingMoreStatus() == CellStatus.LoadingMoreStatus.LOADING && section == getSectionCount() - 1) {
                return LOADING_TYPE;
            } else if (extraInterface.loadingMoreStatus() == CellStatus.LoadingMoreStatus.FAILED && section == getSectionCount() - 1) {
                return FAILED_TYPE;
            }
        }
        return super.getItemId(section, row) + TYPE_OFFSET;
    }

    @Override
    public int getItemViewType(int sectionIndex, int row) {
        if (extraInterface != null) {
            if (extraInterface.loadingMoreStatus() == CellStatus.LoadingMoreStatus.LOADING && sectionIndex == getSectionCount() - 1) {
                return LOADING_TYPE;
            } else if (extraInterface.loadingMoreStatus() == CellStatus.LoadingMoreStatus.FAILED && sectionIndex == getSectionCount() - 1) {
                return FAILED_TYPE;
            }
        }
        return super.getItemViewType(sectionIndex, row) + TYPE_OFFSET;
    }

    @Override
    public boolean showTopDivider(int section, int row) {
        if (isExtraSection(section)) {
            return false;
        }
        return super.showTopDivider(section, row);
    }


    @Override
    public boolean showBottomDivider(int section, int row) {
        if (isExtraSection(section)) {
            return false;
        }
        return super.showBottomDivider(section, row);
    }

    @Override
    public boolean hasBottomDividerVerticalOffset(int section, int row) {
        if (isExtraSection(section)) {
            return false;
        }
        return super.hasBottomDividerVerticalOffset(section, row);
    }

    @Override
    public boolean hasTopDividerVerticalOffset(int section, int row) {
        if (isExtraSection(section)) {
            return false;
        }
        return super.hasTopDividerVerticalOffset(section, row);
    }

    @Override
    public DividerInfo getDividerInfo(int section, int row) {
        if (isExtraSection(section)) {
            return null;
        }
        return super.getDividerInfo(section, row);
    }

    /* 对所有涉及到section的被包装方法进行section还原 end **/

    @Override
    public void onBindViewHolder(MergeSectionDividerAdapter.BasicHolder holder, int sectionIndex, int row) {
        if (extraInterface != null) {
            if (getItemViewType(sectionIndex, row) == LOADING_TYPE) {
                extraInterface.onBindView(CellStatus.LoadingMoreStatus.LOADING);
                if (extraInterface instanceof CellStatusMoreReuseInterface) {
                    ((CellStatusMoreReuseInterface)extraInterface).updateLoadingMoreView(holder.itemView);
                }
                return;
            } else if (getItemViewType(sectionIndex, row) == FAILED_TYPE) {
                extraInterface.onBindView(CellStatus.LoadingMoreStatus.FAILED);
                if (extraInterface instanceof CellStatusMoreReuseInterface) {
                    ((CellStatusMoreReuseInterface)extraInterface).updateLoadingMoreFailedView(holder.itemView);
                }
                return;
            }
        }
        super.onBindViewHolder(holder, sectionIndex, row);
    }

    @Override
    public MergeSectionDividerAdapter.BasicHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (extraInterface != null) {
            if (viewType == LOADING_TYPE) {
                View loadingMoreView = extraInterface.loadingMoreView();
                if (loadingMoreView == null) {

                    if (creator == null) {
                        TextView textView = new TextView(getContext());
                        textView.setGravity(Gravity.CENTER);
                        textView.setPadding(ViewUtils.dip2px(getContext(), 10),
                                ViewUtils.dip2px(getContext(), 10),
                                ViewUtils.dip2px(getContext(), 10),
                                ViewUtils.dip2px(getContext(), 10));
                        textView.setText("未设置默认LoadingMoreView");
                        loadingMoreView = textView;
                    } else {
                        loadingMoreView = creator.loadingMoreView();
                    }
                }
                MergeSectionDividerAdapter.BasicHolder loadingHolder = new MergeSectionDividerAdapter.BasicHolder(loadingMoreView);
                return loadingHolder;
            } else if (viewType == FAILED_TYPE) {
                View failedView = extraInterface.loadingMoreFailedView();
                if (failedView == null) {
                    if (creator == null) {
                        TextView textView = new TextView(getContext());
                        textView.setGravity(Gravity.CENTER);
                        textView.setPadding(ViewUtils.dip2px(getContext(), 10),
                                ViewUtils.dip2px(getContext(), 10),
                                ViewUtils.dip2px(getContext(), 10),
                                ViewUtils.dip2px(getContext(), 10));
                        textView.setText("未设置默认LoadingMoreFailedView");
                        failedView = textView;
                    } else {
                        failedView = creator.loadingMoreFailedView();
                    }
                }
                if (extraInterface.loadingMoreRetryListener() != null && failedView != null) {
                    failedView.setOnClickListener(extraInterface.loadingMoreRetryListener());
                }
                MergeSectionDividerAdapter.BasicHolder failedHolder = new MergeSectionDividerAdapter.BasicHolder(failedView);
                return failedHolder;
            } else {
                return super.onCreateViewHolder(parent, viewType - TYPE_OFFSET);
            }
        }
        return super.onCreateViewHolder(parent, viewType);
    }

}
