package com.example.shield.linktype.cell;

import android.content.Context;
import android.graphics.Color;

import com.dianping.agentsdk.framework.LinkType;

/**
 * Created by nihao on 2017/7/17.
 */

public class LinkTypeThirdCell extends LinkTypeFirstCell {
    public LinkTypeThirdCell(Context context) {
        super(context);
    }

    @Override
    public LinkType.Next linkNext(int sectionPosition) {
        if (sectionPosition == 0){
            return LinkType.Next.LINK_TO_NEXT;
        }
        return super.linkNext(sectionPosition);
    }

    @Override
    public LinkType.Previous linkPrevious(int sectionPosition) {
        if (sectionPosition == 2){
            return LinkType.Previous.LINK_TO_PREVIOUS;
        }
        return super.linkPrevious(sectionPosition);
    }

    @Override
    protected int backgroundColor() {
        return Color.parseColor("#FFCC99");
    }

    @Override
    protected String getHint(int sectionPosition, int rowPosition) {
        StringBuilder sb = new StringBuilder();
        sb.append("section : ")
                .append(sectionPosition)
                .append(" row : ")
                .append(rowPosition);
        if (sectionPosition == 0) {
            sb.append(" link_to_next");
        } else if (sectionPosition == 1){
            sb.append(" default_link_type");
        } else if (sectionPosition == 2){
            sb.append(" link_to_previous");
        }
        return sb.toString();
    }
}
