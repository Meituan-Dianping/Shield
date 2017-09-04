package com.dianping.agentsdk.adapter;

import android.content.Context;
import android.support.annotation.NonNull;

import com.dianping.agentsdk.framework.LinkType;
import com.dianping.agentsdk.manager.GroupManager;
import com.dianping.agentsdk.sectionrecycler.section.PieceAdapter;

/**
 * Created by hezhi on 16/6/24.
 */
public class FinalPieceAdapter extends WrapperPieceAdapter {

    protected GroupManager groupManager;

    public FinalPieceAdapter(@NonNull Context context, PieceAdapter adapter, @NonNull GroupManager groupManager) {
        super(context, adapter, null);
        this.groupManager = groupManager;
    }

    @Override
    public LinkType.Previous getPreviousLinkType(int section) {
        if (atFirstOfAdapter(section) ){
            if (groupManager.atFirstOfGroup(this)) {
                return LinkType.Previous.DISABLE_LINK_TO_PREVIOUS;
            } else {
                if(super.getPreviousLinkType(section) == LinkType.Previous.DISABLE_LINK_TO_PREVIOUS ||
                        super.getPreviousLinkType(section) == LinkType.Previous.DEFAULT){
                    return super.getPreviousLinkType(section);
                }
                return LinkType.Previous.LINK_TO_PREVIOUS;
            }
        }

        return super.getPreviousLinkType(section);
    }

    @Override
    public LinkType.Next getNextLinkType(int section) {
        if (atLastOfAdapter(section) && groupManager.atLastOfGroup(this)) {
            return LinkType.Next.DISABLE_LINK_TO_NEXT;
        }

        return super.getNextLinkType(section);
    }

    public boolean atFirstOfAdapter(int section) {

        for (int i = section - 1; i >= 0; i --) {
            if (getRowCount(i) > 0) {
                return false;
            }
        }

        return true;
    }

    public boolean atLastOfAdapter(int section) {

        for (int i = section + 1; i < getSectionCount(); i ++) {
            if (getRowCount(i) > 0) {
                return false;
            }
        }

        return true;
    }

}
