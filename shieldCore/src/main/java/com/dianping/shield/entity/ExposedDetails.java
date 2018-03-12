package com.dianping.shield.entity;

/**
 * Created by hezhi on 17/2/22.
 */

public class ExposedDetails {
    public int section;
    public int row;
    public CellType cellType;
    public boolean isComplete;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExposedDetails that = (ExposedDetails) o;

        if (section != that.section) return false;
        if (row != that.row) return false;
        if (isComplete != that.isComplete) return false;
        return cellType == that.cellType;
    }

    @Override
    public int hashCode() {
        int result = section;
        result = 31 * result + row;
        result = 31 * result + (cellType != null ? cellType.hashCode() : 0);
        result = 31 * result + (isComplete ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ExposedDetails{" +
                "section=" + section +
                ", row=" + row +
                ", cellType=" + cellType +
                ", isComplete=" + isComplete +
                '}';
    }
}
