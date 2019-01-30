package com.dianping.agentsdk.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.dianping.agentsdk.framework.LinkType;
import com.dianping.agentsdk.framework.SectionHeaderFooterDrawableInterface;
import com.dianping.agentsdk.framework.SectionLinkCellInterface;
import com.dianping.agentsdk.sectionrecycler.section.PieceAdapter;
import com.dianping.shield.feature.SectionTitleInterface;

/**
 * Created by runqi.wei
 * 15:22
 * 11.07.2016.
 */
public class SectionLinkPieceAdapter extends WrapperPieceAdapter<SectionLinkCellInterface> {

    protected SectionHeaderFooterDrawableInterface headerFooterDrawableInterface;
    protected SectionTitleInterface sectionTitleInterface;

    public SectionLinkPieceAdapter(@NonNull Context context, @NonNull PieceAdapter adapter, SectionLinkCellInterface extraInterface) {
        super(context, adapter, extraInterface);
    }

    public void setSectionTitleInterface(SectionTitleInterface sectionTitleInterface) {
        this.sectionTitleInterface = sectionTitleInterface;
    }

    public void setHeaderFooterDrawableInterface(SectionHeaderFooterDrawableInterface headerFooterDrawableInterface) {
        this.headerFooterDrawableInterface = headerFooterDrawableInterface;
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
    public Drawable getSectionHeaderDrawable(int section) {
        if (headerFooterDrawableInterface != null && isInnerSection(section)) {
            return headerFooterDrawableInterface.getHeaderDrawable(section);
        }
        return super.getSectionHeaderDrawable(section);
    }

    @Override
    public Drawable getSectionFooterDrawable(int section) {
        if (headerFooterDrawableInterface != null && isInnerSection(section)) {
            return headerFooterDrawableInterface.getFooterDrawable(section);
        }
        return super.getSectionFooterDrawable(section);
    }

    @Override
    public String getSectionTitle(int section) {
        if (sectionTitleInterface != null && isInnerSection(section)) {
            return sectionTitleInterface.getSectionTitle(section);
        }
        return super.getSectionTitle(section);
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
