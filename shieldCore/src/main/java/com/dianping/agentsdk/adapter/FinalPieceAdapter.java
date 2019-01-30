package com.dianping.agentsdk.adapter;

import android.content.Context;
import android.support.annotation.NonNull;

import com.dianping.agentsdk.framework.LinkType;
import com.dianping.agentsdk.manager.GroupManager;
import com.dianping.agentsdk.sectionrecycler.section.PieceAdapter;
import com.dianping.shield.env.ShieldEnvironment;

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
        if (atFirstOfAdapter(section)) {
            if (groupManager.atFirstOfGroup(this)) {
//                ShieldEnvironment.INSTANCE.getShieldLogger().e("Agent:" + getAgentInterface().getHostName() + "@LinkType.Previous:" + LinkType.Previous.DISABLE_LINK_TO_PREVIOUS);
                return LinkType.Previous.DISABLE_LINK_TO_PREVIOUS;
            } else {
                if (super.getPreviousLinkType(section) == LinkType.Previous.DISABLE_LINK_TO_PREVIOUS ||
                        super.getPreviousLinkType(section) == LinkType.Previous.DEFAULT) {
//                    ShieldEnvironment.INSTANCE.getShieldLogger().e("Agent:" + getAgentInterface().getHostName() + "@LinkType.Previous:" + super.getPreviousLinkType(section));
                    return super.getPreviousLinkType(section);
                }
//                ShieldEnvironment.INSTANCE.getShieldLogger().e("Agent:" + getAgentInterface().getHostName() + "@LinkType.Previous:" + LinkType.Previous.LINK_TO_PREVIOUS);
                return LinkType.Previous.LINK_TO_PREVIOUS;
            }
        }

//        ShieldEnvironment.INSTANCE.getShieldLogger().e("Agent:" + getAgentInterface().getHostName() + "@LinkType.Previous:" + super.getPreviousLinkType(section));
        return super.getPreviousLinkType(section);
    }

    @Override
    public LinkType.Next getNextLinkType(int section) {
        LinkType.Next nextLinkType = super.getNextLinkType(section);
        if (atLastOfAdapter(section) && groupManager.atLastOfGroup(this) && nextLinkType != LinkType.Next.LINK_UNSAFE_BETWEEN_GROUP) {
//            ShieldEnvironment.INSTANCE.getShieldLogger().e("Agent:" + getAgentInterface().getHostName() + "@LinkType.Next:" + LinkType.Next.DISABLE_LINK_TO_NEXT);
            return LinkType.Next.DISABLE_LINK_TO_NEXT;
        }
//        ShieldEnvironment.INSTANCE.getShieldLogger().e("Agent:" + getAgentInterface().getHostName() + "@LinkType.Next:" + nextLinkType);
        return nextLinkType;
    }

    public boolean atFirstOfAdapter(int section) {

        for (int i = section - 1; i >= 0; i--) {
            if (getRowCount(i) > 0) {
                return false;
            }
        }

        return true;
    }

    public boolean atLastOfAdapter(int section) {

        for (int i = section + 1; i < getSectionCount(); i++) {
            if (getRowCount(i) > 0) {
                return false;
            }
        }

        return true;
    }

}
