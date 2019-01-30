package com.dianping.agentsdk.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.ViewGroup;

import com.dianping.agentsdk.framework.DividerInfo;
import com.dianping.agentsdk.framework.SectionExtraCellDividerOffsetInterface;
import com.dianping.agentsdk.framework.SectionExtraCellInterface;
import com.dianping.agentsdk.framework.SectionExtraTopDividerCellInterface;
import com.dianping.agentsdk.sectionrecycler.divider.DividerInfoInterface;
import com.dianping.agentsdk.sectionrecycler.section.MergeSectionDividerAdapter;
import com.dianping.agentsdk.sectionrecycler.section.PieceAdapter;
import com.dianping.shield.entity.CellType;

/**
 * Created by hezhi on 16/6/23.
 * 在装饰模式中，不同的装饰间是链式调用
 * 如果对父类中的方法入参进行过装饰的话，必须对所有涉及到的方法进行还原
 */
public class ExtraCellPieceAdapter extends WrapperPieceAdapter<SectionExtraCellInterface> {

    protected SectionExtraTopDividerCellInterface extraTopDividerCellInterface;
    protected SectionExtraCellDividerOffsetInterface extraCellDividerOffsetInterface;
    protected DividerInfoInterface dividerInfoInterfaceForExtraCell;

    public ExtraCellPieceAdapter(@NonNull Context context, PieceAdapter adapter, SectionExtraCellInterface sectionExtraCellInterface) {
        super(context, adapter, sectionExtraCellInterface);
    }

    public void setExtraTopDividerCellInterface(SectionExtraTopDividerCellInterface extraTopDividerCellInterface) {
        this.extraTopDividerCellInterface = extraTopDividerCellInterface;
    }

    public void setExtraCellDividerOffsetInterface(SectionExtraCellDividerOffsetInterface extraCellDividerOffsetInterface) {
        this.extraCellDividerOffsetInterface = extraCellDividerOffsetInterface;
    }

    public void setDividerInfoInterfaceForExtraCell(DividerInfoInterface dividerInfoInterfaceForExtraCell) {
        this.dividerInfoInterfaceForExtraCell = dividerInfoInterfaceForExtraCell;
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
            if (wrappedType < extraInterface.getHeaderViewTypeCount()) {
                return wrappedType;
            } else if (wrappedType < extraInterface.getHeaderViewTypeCount() + extraInterface.getFooterViewTypeCount()) {
                return wrappedType - extraInterface.getHeaderViewTypeCount();
            } else {
                int typeOffset = extraInterface.getHeaderViewTypeCount() + extraInterface.getFooterViewTypeCount();
                return super.getInnerType(wrappedType - typeOffset);
            }
        }
        return super.getInnerType(wrappedType);
    }

    @Override
    public Drawable getTopDivider(int section, int row) {
        Pair<Integer, Integer> pair = getInnerPosition(section, row);
        if (extraInterface != null && extraInterface instanceof SectionExtraTopDividerCellInterface) {
            CellType cellType = getCellType(section, row);
            if (cellType == CellType.HEADER) {
                return ((SectionExtraTopDividerCellInterface) extraInterface).getTopDividerForHeader(pair.first);
            }
            if (cellType == CellType.FOOTER) {
                return ((SectionExtraTopDividerCellInterface) extraInterface).getTopDividerForFooter(pair.first);
            }
        }
        return super.getTopDivider(pair.first, pair.second);
    }

    @Override
    public Drawable getBottomDivider(int section, int row) {
        Pair<Integer, Integer> pair = getInnerPosition(section, row);
        if (extraInterface != null) {
            CellType cellType = getCellType(section, row);
            if (extraInterface instanceof SectionExtraTopDividerCellInterface) {
                if (cellType == CellType.HEADER || cellType == CellType.FOOTER) {
                    return ((SectionExtraTopDividerCellInterface) extraInterface).getBottomDividerForHeader(pair.first);
                }
            }
            if (cellType != CellType.HEADER && cellType != CellType.FOOTER) {
                return super.getBottomDivider(pair.first, pair.second);
            }
        }
        return null;
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

    @Override
    public CellType getCellType(int viewType) {
        if (viewType < extraInterface.getHeaderViewTypeCount()) {
            return CellType.HEADER;
        } else if (viewType < extraInterface.getHeaderViewTypeCount() + extraInterface.getFooterViewTypeCount()) {
            return CellType.FOOTER;
        } else {
            return super.getCellType(viewType - extraInterface.getHeaderViewTypeCount() - extraInterface.getFooterViewTypeCount());
        }
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
                    return new Pair<Integer, Integer>(wrappedSection, -1);
                } else {
                    innerRow--;
                }
            }
            if (extraInterface.hasFooterForSection(wrappedSection)) {
                if (wrappedRow == getRowCount(wrappedSection) - 1) {
                    //返回的section和row要标识出footer
                    return new Pair<Integer, Integer>(wrappedSection, -2);
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
                return true;
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
    public Rect bottomDividerOffset(int section, int row) {
        //加row偏转

        Pair<Integer, Integer> pair = getInnerPosition(section, row);
        CellType cellType = getCellType(section, row);

        if (cellType == CellType.HEADER) {

            if (extraCellDividerOffsetInterface != null) {
                Rect bottomDividerOffsetForHeader = new Rect();
                bottomDividerOffsetForHeader.left = extraCellDividerOffsetInterface.getHeaderBottomDividerLeftOffset(section);
                bottomDividerOffsetForHeader.right = extraCellDividerOffsetInterface.getHeaderBottomDividerRightOffset(section);
                return bottomDividerOffsetForHeader;
            }

            if (extraInterface != null) {
                Rect bottomDividerOffsetForHeader = new Rect();
                int headerLeft = (int) extraInterface.getHeaderDividerOffset(section);
                // com.dianping.agentsdk.framework.SectionExtraCellInterface.getHeaderDividerOffset(int)
                // 方法返回 负值 视为不进行设置
                if (headerLeft < 0) {
                    return null;
                }
                bottomDividerOffsetForHeader.left = headerLeft;
                return bottomDividerOffsetForHeader;
            }

            return null;

        } else if (cellType == CellType.FOOTER) {

            if (extraCellDividerOffsetInterface != null) {
                Rect bottomDividerOffsetForFooter = new Rect();
                bottomDividerOffsetForFooter.left = extraCellDividerOffsetInterface.getFooterBottomDividerLeftOffset(section);
                bottomDividerOffsetForFooter.right = extraCellDividerOffsetInterface.getFooterBottomDividerRightOffset(section);
                return bottomDividerOffsetForFooter;
            }

            if (extraInterface != null) {
                Rect bottomDividerOffsetForFooter = new Rect();
                int footerLeft = (int) extraInterface.getFooterDividerOffset(section);
                // com.dianping.agentsdk.framework.SectionExtraCellInterface.getFooterDividerOffset(int)
                // 方法返回 负值 视为不进行设置
                if (footerLeft < 0) {
                    return null;
                }
                bottomDividerOffsetForFooter.left = footerLeft;
                return bottomDividerOffsetForFooter;
            }

            return null;

        } else {
            return super.bottomDividerOffset(pair.first, pair.second);
        }

    }

    @Override
    public Rect topDividerOffset(int section, int row) {
        //加row偏转
        Pair<Integer, Integer> pair = getInnerPosition(section, row);
        CellType cellType = getCellType(section, row);

        if (cellType == CellType.HEADER) {

            if (extraCellDividerOffsetInterface != null) {
                Rect topDividerOffsetForHeader = new Rect();
                topDividerOffsetForHeader.left = extraCellDividerOffsetInterface.getHeaderTopDividerLeftOffset(section);
                topDividerOffsetForHeader.right = extraCellDividerOffsetInterface.getHeaderTopDividerRightOffset(section);
                return topDividerOffsetForHeader;
            }

//            if (extraTopDividerCellInterface != null) {
//                Rect topDividerOffsetForHeader = new Rect();
//                topDividerOffsetForHeader.left = (int) extraTopDividerCellInterface.getHeaderTopDividerLeftOffset(section, row);
//                topDividerOffsetForHeader.right = (int) extraTopDividerCellInterface.getHeaderTopDividerRightOffset(section, row);
//                return topDividerOffsetForHeader;
//            }
            return null;

        } else if (cellType == CellType.FOOTER) {
            if (extraCellDividerOffsetInterface != null) {
                Rect topDividerOffsetForFooter = new Rect();
                topDividerOffsetForFooter.left = extraCellDividerOffsetInterface.getFooterTopDividerLeftOffset(section);
                topDividerOffsetForFooter.right = extraCellDividerOffsetInterface.getFooterTopDividerRightOffset(section);
                return topDividerOffsetForFooter;
            }

//            if (extraTopDividerCellInterface != null) {
//                Rect topDividerOffsetForFooter = new Rect();
//                topDividerOffsetForFooter.left = (int) extraTopDividerCellInterface.getFooterTopDividerLeftOffset(section);
//                topDividerOffsetForFooter.right = (int) extraTopDividerCellInterface.getFooterTopDividerRightOffset(section);
//                return topDividerOffsetForFooter;
//            }

            return null;

        } else {
            return super.topDividerOffset(pair.first, pair.second);
        }
    }

    @Override
    public long getItemId(int section, int row) {
        //加row偏转
        Pair<Integer, Integer> pair = getInnerPosition(section, row);
        if (extraInterface != null) {

            CellType cellType = getCellType(section, row);
            if (cellType == CellType.HEADER) {
                return section * 2;
            }
            if (cellType == CellType.FOOTER) {
                return section * 2 + 1;
            }

        }

        return super.getItemId(pair.first, pair.second) + getSectionCount() * 2;
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

    @Override
    public DividerInfo getDividerInfo(int section, int row) {
        Pair<Integer, Integer> pair = getInnerPosition(section, row);
        if (dividerInfoInterfaceForExtraCell != null) {
            CellType cellType = getCellType(section, row);
            if (cellType == CellType.HEADER || cellType == CellType.FOOTER) {
                return dividerInfoInterfaceForExtraCell.getDividerInfo(cellType, pair.first, pair.second);
            }
        }
        return super.getDividerInfo(pair.first, pair.second);
    }

    /** 对所有涉及到row的被包装方法进行row还原 end */
}
