package com.dianping.shield.entity;

import com.dianping.agentsdk.sectionrecycler.section.PieceAdapter;

/**
 * Created by hezhi on 17/2/21.
 */

public class ExposedInfo {
    //曝光列表
    public PieceAdapter owner;
    public ExposedDetails details;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExposedInfo that = (ExposedInfo) o;

        if (!owner.equals(that.owner)) return false;
        return details.equals(that.details);

    }

    @Override
    public int hashCode() {
        int result = owner.hashCode();
        result = 31 * result + details.hashCode();
        return result;
    }

    public ExposedInfo() {
        this.details = new ExposedDetails();
    }
}