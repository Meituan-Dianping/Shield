package com.dianping.shield.entity;

/**
 * Created by hezhi on 17/2/22.
 */

public class ExposedDetails {
    public int section;
    public int row;
    public boolean isComplete;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExposedDetails that = (ExposedDetails) o;

        if (section != that.section) return false;
        if (row != that.row) return false;
        return isComplete == that.isComplete;

    }

    @Override
    public int hashCode() {
        int result = section;
        result = 31 * result + row;
        result = 31 * result + (isComplete ? 1 : 0);
        return result;
    }
}
