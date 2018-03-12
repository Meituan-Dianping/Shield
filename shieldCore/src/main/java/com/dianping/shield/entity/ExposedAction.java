package com.dianping.shield.entity;

import com.dianping.agentsdk.framework.SectionCellInterface;

/**
 * Created by hezhi on 17/2/21.
 */

public class ExposedAction {

//    public enum ExposedType {
//        //新部分曝光
//        NEW_PART,
//
//        //新完全曝光
//        NEW_FULL,
//
//        //从部分曝光到完全曝光
//        PART_TO_FULL,
//
//        //从完全曝光到部分曝光
//        FULL_TO_PART,
//
//        //部分曝光到结束
//        PART_TO_END,
//
//        //完全曝光到结束
//        FULL_TO_END
//    }

    public SectionCellInterface owner;
    public int section;
    public int row;
    public CellType cellType;
    public boolean isAddExposed;//true为增加，false为删除
    public boolean isAgentExposed;//true为全局，此时postion为空，cellType为空，false按行

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExposedAction that = (ExposedAction) o;

        if (section != that.section) return false;
        if (row != that.row) return false;
        if (isAgentExposed != that.isAgentExposed) return false;
        if (!owner.equals(that.owner)) return false;
        return cellType == that.cellType;

    }

    @Override
    public int hashCode() {
        int result = owner.hashCode();
        result = 31 * result + section;
        result = 31 * result + row;
        result = 31 * result + (cellType != null ? cellType.hashCode() : 0);
        result = 31 * result + (isAgentExposed ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ExposedAction{" +
                "owner=" + owner +
                ", section=" + section +
                ", row=" + row +
                ", cellType=" + cellType +
                ", isAddExposed=" + isAddExposed +
                ", isAgentExposed=" + isAgentExposed +
                '}';
    }

    public ExposedAction(SectionCellInterface owner, int section, int row, CellType cellType, boolean isAddExposed, boolean isAgentExposed) {
        this.owner = owner;
        this.section = section;
        this.row = row;
        this.cellType = cellType;
        this.isAddExposed = isAddExposed;
        this.isAgentExposed = isAgentExposed;
    }
}
