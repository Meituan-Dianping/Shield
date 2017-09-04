package com.example.shield.linktype.cell;

import android.content.Context;
import android.graphics.Color;

import com.dianping.agentsdk.framework.LinkType;

/**
 * Created by nihao on 2017/7/17.
 */

public class LinkTypeSecondCell extends LinkTypeFirstCell {
    public LinkTypeSecondCell(Context context) {
        super(context);
    }

    @Override
    public LinkType.Next linkNext(int sectionPosition) {
        if (sectionPosition == 0) {
            return LinkType.Next.LINK_TO_NEXT;
        }

        if (sectionPosition == 1) {
            return LinkType.Next.LINK_TO_NEXT;
        }

        return super.linkNext(sectionPosition);
    }

    @Override
    public LinkType.Previous linkPrevious(int sectionPosition) {
        if (sectionPosition == 1) {
            return LinkType.Previous.LINK_TO_PREVIOUS;
        }

        if (sectionPosition == 2) {
            return LinkType.Previous.DISABLE_LINK_TO_PREVIOUS;
        }

        return super.linkPrevious(sectionPosition);
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
            sb.append(" link_to_next link_to_previous");
        } else if (sectionPosition == 2){
            sb.append(" disable_link_to_previous");
        }
        return sb.toString();
    }

    @Override
    protected int backgroundColor() {
        return Color.parseColor("#99CC33");
    }
}
