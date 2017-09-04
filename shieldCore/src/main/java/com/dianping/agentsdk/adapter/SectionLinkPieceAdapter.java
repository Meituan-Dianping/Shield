package com.dianping.agentsdk.adapter;

import android.content.Context;
import android.support.annotation.NonNull;

import com.dianping.agentsdk.framework.LinkType;
import com.dianping.agentsdk.framework.SectionLinkCellInterface;
import com.dianping.agentsdk.sectionrecycler.section.PieceAdapter;

/**
 * Created by runqi.wei
 * 15:22
 * 11.07.2016.
 */
public class SectionLinkPieceAdapter extends WrapperPieceAdapter<SectionLinkCellInterface> {

    public SectionLinkPieceAdapter(@NonNull Context context, @NonNull PieceAdapter adapter, SectionLinkCellInterface extraInterface) {
        super(context, adapter, extraInterface);
    }

    @Override
    public float getSectionHeaderHeight(int section) {
        //只有最内部提供的section可以定制sectionheaderheight,避免业务层数组越界
        if (extraInterface != null && isInnerSection(section)) {
            return extraInterface.getSectionHeaderHeight(section);
        }
        return super.getSectionHeaderHeight(section);
    }

    @Override
    public float getSectionFooterHeight(int section) {
        //只有最内部提供的section可以定制sectionfooterheight,避免业务层数组越界
        if (extraInterface != null && isInnerSection(section)) {
            return extraInterface.getSectionFooterHeight(section);
        }
        return super.getSectionFooterHeight(section);
    }

    @Override
    public LinkType.Previous getPreviousLinkType(int section) {
        //只有最内部提供的section可以定制LinkType,避免业务层数组越界
        if (extraInterface != null && isInnerSection(section))
            return extraInterface.linkPrevious(section);
        return super.getPreviousLinkType(section);
    }

    @Override
    public LinkType.Next getNextLinkType(int section) {
        //只有最内部提供的section可以定制LinkType,避免业务层数组越界
        if (extraInterface != null && isInnerSection(section))
            return extraInterface.linkNext(section);
        return super.getNextLinkType(section);
    }
}
