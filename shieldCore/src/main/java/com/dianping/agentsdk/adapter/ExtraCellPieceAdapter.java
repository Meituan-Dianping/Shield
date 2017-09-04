package com.dianping.agentsdk.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.ViewGroup;

import com.dianping.agentsdk.framework.SectionExtraCellInterface;
import com.dianping.agentsdk.sectionrecycler.section.MergeSectionDividerAdapter;
import com.dianping.agentsdk.sectionrecycler.section.PieceAdapter;
import com.dianping.shield.entity.CellType;

/**
 * Created by hezhi on 16/6/23.
 * 在装饰模式中，不同的装饰间是链式调用
 * 如果对父类中的方法入参进行过装饰的话，必须对所有涉及到的方法进行还原
 */
public class ExtraCellPieceAdapter extends WrapperPieceAdapter<SectionExtraCellInterface> {

    public ExtraCellPieceAdapter(@NonNull Context context, PieceAdapter adapter, SectionExtraCellInterface sectionExtraCellInterface) {
        super(context, adapter, sectionExtraCellInterface);
    }

    @Override
    public int getRowCount(int sectionIndex) {
        int i = 0;
        if (extraInterface != null) {
            //有headercell 加一行row
            if (extraInterface.hasHeaderForSection(sectionIndex)) i++;
            //有footercell 加一行row
            if (extraInterface.hasFooterForSection(sectionIndex)) i++;
        }
        return super.getRowCount(sectionIndex) + i;
    }

    @Override
    public int getItemViewType(int section, int row) {
        //加row偏转
        if (extraInterface != null) {
            //第一行转到header
            if (row == 0 && extraInterface.hasHeaderForSection(section)) {
                return extraInterface.getHeaderViewType(section);
            }
            //最后一行转到footer
            if (row == getRowCount(section) - 1 && extraInterface.hasFooterForSection(section)) {
                return extraInterface.getFooterViewType(section)
                        + extraInterface.getHeaderViewTypeCount();
            }

            //有header的话viewtype要偏转
            return super.getItemViewType(section,
                    row - (extraInterface.hasHeaderForSection(section) ? 1 : 0))
                    + extraInterface.getHeaderViewTypeCount()
                    + extraInterface.getFooterViewTypeCount();
        }
        return super.getItemViewType(section, row);
    }

    @Override
    public int getInnerType(int wrappedType) {
        if (extraInterface != null) {
            int typeOffset = extraInterface.getHeaderViewTypeCount() + extraInterface.getFooterViewTypeCount();
            if (wrappedType < typeOffset) {
                //只是区分了非内部Type，而没有区分Header还是Footer
                return TYPE_NOT_EXIST;
            }
            return super.getInnerType(wrappedType - typeOffset);
        }
        return super.getInnerType(wrappedType);
    }

    //原始sectoin和row
    @Override
    public CellType getCellType(int wrappedSection, int wrappedRow) {
        //加row偏转
        int innerRow = wrappedRow;
        if (extraInterface != null) {
            if (extraInterface.hasHeaderForSection(wrappedSection)) {
                if (wrappedRow == 0) {
                    //第一行转到header
                    return CellType.HEADER;
                } else {
                    //其他行index减一
                    innerRow--;
                }
            }
            if (extraInterface.hasFooterForSection(wrappedSection)) {
                //最后一行转到footer
                if (wrappedRow == getRowCount(wrappedSection) - 1) {
                    return CellType.FOOTER;
                }
            }
        }
        return super.getCellType(wrappedSection, innerRow);
    }

    //原始sectoin和row
    @Override
    public Pair<Integer, Integer> getInnerPosition(int wrappedSection, int wrappedRow) {
        //加row偏转
        int innerRow = wrappedRow;
        if (extraInterface != null) {
            if (extraInterface.hasHeaderForSection(wrappedSection)) {
                if (wrappedRow == 0) {
                    //返回的section和row要标识出header
                    return new Pair<Integer, Integer>(wrappedSection, 0);
                } else {
                    innerRow--;
                }
            }
            if (extraInterface.hasFooterForSection(wrappedSection)) {
                if (wrappedRow == getRowCount(wrappedSection) - 1) {
                    //返回的section和row要标识出footer
                    return new Pair<Integer, Integer>(wrappedSection, wrappedRow);
                }
            }
        }
        return super.getInnerPosition(wrappedSection, innerRow);
    }


    /**
     * 对所有涉及到row的被包装方法进行row还原 start
     */
    @Override
    public boolean showTopDivider(int section, int row) {
        //加row偏转
        Pair<Integer, Integer> pair = getInnerPosition(section, row);
        if (extraInterface != null) {
            CellType cellType = getCellType(section, row);
            if (cellType == CellType.HEADER) {
                return extraInterface.hasTopDividerForHeader(pair.first);
            }
            if (cellType == CellType.FOOTER) {
                //最后一行转到footer,footer没有topdivider
                return false;
            }
        }
        return super.showTopDivider(pair.first, pair.second);
    }

    @Override
    public boolean showBottomDivider(int section, int row) {
        //加row偏转
        Pair<Integer, Integer> pair = getInnerPosition(section, row);
        if (extraInterface != null) {

            CellType cellType = getCellType(section, row);
            if (cellType == CellType.HEADER) {
                return extraInterface.hasBottomDividerForHeader(pair.first);
            }
            if (cellType == CellType.FOOTER) {
                return extraInterface.hasBottomDividerForFooter(pair.first);
            }

        }
        return super.showBottomDivider(pair.first, pair.second);
    }

    @Override
    public Drawable getBottomDivider(int section, int row) {
        Pair<Integer, Integer> pair = getInnerPosition(section, row);
        if (extraInterface != null) {
            CellType cellType = getCellType(section, row);
            if (cellType != CellType.HEADER && cellType != CellType.FOOTER) {
                return super.getBottomDivider(pair.first, pair.second);
            }
        }
        return null;
    }

    @Override
    public int bottomDividerOffset(int section, int row) {
        //加row偏转

        Pair<Integer, Integer> pair = getInnerPosition(section, row);
        if (extraInterface != null) {
            CellType cellType = getCellType(section, row);
            if (cellType == CellType.HEADER) {
                // 控制 Header View 的 Divider Offset
                float headerDividerOffset = extraInterface.getHeaderDividerOffset(pair.first);
                if (headerDividerOffset >= 0) {
                    return (int) headerDividerOffset;
                } else {
                    return NO_OFFSET;
                }
            }
            if (cellType == CellType.FOOTER) {
                // 控制 Footer View 的 Divider Offset
                float footerDividerOffset = extraInterface.getFooterDividerOffset(pair.first);
                if (footerDividerOffset >= 0) {
                    return (int) footerDividerOffset;
                } else {
                    return NO_OFFSET;
                }
            }
        }
        return super.bottomDividerOffset(pair.first, pair.second);

    }

    @Override
    public int topDividerOffset(int section, int row) {
        //加row偏转
        Pair<Integer, Integer> pair = getInnerPosition(section, row);
        if (extraInterface != null) {

            CellType cellType = getCellType(section, row);
            if (cellType == CellType.HEADER) {
                return NO_OFFSET;
            }
            if (cellType == CellType.FOOTER) {
                return NO_OFFSET;
            }

        }

        return super.topDividerOffset(pair.first, pair.second);

    }

    @Override
    public long getItemId(int section, int row) {
        //加row偏转
        Pair<Integer, Integer> pair = getInnerPosition(section, row);
        if (extraInterface != null) {

            CellType cellType = getCellType(section, row);
            if (cellType == CellType.HEADER) {
                return 0;
            }
            if (cellType == CellType.FOOTER) {
                return 1;
            }

        }

        return super.getItemId(pair.first, pair.second) + 2;
    }

    @Override
    public void onBindViewHolder(MergeSectionDividerAdapter.BasicHolder holder, int section, int row) {
        //加row偏转
        Pair<Integer, Integer> pair = getInnerPosition(section, row);
        if (extraInterface != null) {

            CellType cellType = getCellType(section, row);
            if (cellType == CellType.HEADER) {
                extraInterface.updateHeaderView(holder.itemView, pair.first, (ViewGroup) holder.itemView.getParent());
                return;
            }
            if (cellType == CellType.FOOTER) {
                extraInterface.updateFooterView(holder.itemView, pair.first, (ViewGroup) holder.itemView.getParent());
                return;
            }

        }

        super.onBindViewHolder(holder, pair.first, pair.second);
    }

    @Override
    public MergeSectionDividerAdapter.BasicHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (extraInterface != null) {

            if (viewType >= 0 && viewType < extraInterface.getHeaderViewTypeCount()) {
                MergeSectionDividerAdapter.BasicHolder viewHolder =
                        new MergeSectionDividerAdapter.BasicHolder(
                                extraInterface.onCreateHeaderView(parent, viewType));
                return viewHolder;
            }

            if (viewType >= extraInterface.getHeaderViewTypeCount()
                    && viewType < extraInterface.getHeaderViewTypeCount()
                    + extraInterface.getFooterViewTypeCount()) {
                MergeSectionDividerAdapter.BasicHolder viewHolder =
                        new MergeSectionDividerAdapter.BasicHolder(
                                extraInterface.onCreateFooterView(parent, viewType - extraInterface.getHeaderViewTypeCount()));
                return viewHolder;
            } else {
                return super.onCreateViewHolder(parent,
                        viewType - extraInterface.getHeaderViewTypeCount()
                                - extraInterface.getFooterViewTypeCount());
            }
        }

        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public boolean hasBottomDividerVerticalOffset(int section, int row) {
        //加row偏转
        Pair<Integer, Integer> pair = getInnerPosition(section, row);
        if (extraInterface != null) {
            CellType cellType = getCellType(section, row);
            if (cellType == CellType.HEADER || cellType == CellType.FOOTER) {
                return false;
            }
        }

        return super.hasBottomDividerVerticalOffset(pair.first, pair.second);
    }

    @Override
    public boolean hasTopDividerVerticalOffset(int section, int row) {

        //加row偏转
        Pair<Integer, Integer> pair = getInnerPosition(section, row);
        if (extraInterface != null) {
            CellType cellType = getCellType(section, row);
            if (cellType == CellType.HEADER || cellType == CellType.FOOTER) {
                return false;
            }
        }

        return super.hasTopDividerVerticalOffset(pair.first, pair.second);
    }

    /** 对所有涉及到row的被包装方法进行row还原 end */
}
