package com.dianping.shield.entity;

/**
 * Created by zhi.he on 2018/1/3.
 */
//带偏转的行局部位置
public class CellInfo {
    public int section;
    public int row;//normal >0 , header -1,footer -2
    public CellType cellType;

    public CellInfo(int section, int row, CellType cellType) {
        this.section = section;
        this.row = row;
        this.cellType = cellType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CellInfo cellInfo = (CellInfo) o;

        if (section != cellInfo.section) return false;
        if (row != cellInfo.row) return false;
        return cellType == cellInfo.cellType;
    }

    @Override
    public int hashCode() {
        int result = section;
        result = 31 * result + row;
        result = 31 * result + cellType.hashCode();
        return result;
    }
}
