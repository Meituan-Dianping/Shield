package com.dianping.shield.node.useritem;

import android.graphics.drawable.Drawable;

import com.dianping.agentsdk.framework.LinkType;
import com.dianping.shield.node.itemcallbacks.lazy.LazyLoadRowItemProvider;

import java.util.ArrayList;

/**
 * Created by zhi.he on 2018/6/18.
 */

public class SectionItem {
//    public String identifier;  //节点id
//
//    public ShieldSectionCellItem rowParent; //父节点

//    public int section; //在模块中的section位置

    public ArrayList<RowItem> rowItems;  //子Row节点

    public RowItem headerRowItem;  //HeaderCell节点

    public RowItem footerRowItem;  //FooterCell节点

    public boolean showDivider;//是否展示Section内划线

//    public DividerInfo dividerStyle; //Section内划线相关，

    public String sectionTitle; //Section 标题

    public LinkType.Previous previousLinkType;   //sectionLinkType

    public LinkType.Next nextLinkType; //

    public int sectionHeaderGapHeight = -1;// section Gap 的高度定制，在非link状态下有效，单位dp, -1未定制

    public Drawable sectionHeaderGapDrawable;//section Gap的Drawable定制

    public int sectionFooterGapHeight = -1;// section Gap 的高度定制，在非link状态下有效，单位dp，-1未定制

    public Drawable sectionFooterGapDrawable;//section Gap的Drawable定制

    public LayoutType sectionLayoutType = LayoutType.LINEAR_FULL_FILL;  //section布局类型

    public DividerStyle.ShowType dividerShowType = DividerStyle.ShowType.ALL;

    public DividerStyle dividerStyle; //划线定制 如果行内没有定制划线，则以section上的划线为准，如果section没有定制就走默认

    //lazy load row items

    public boolean isLazyLoad = false;

    public int rowCount = 0;

    public LazyLoadRowItemProvider lazyLoadRowItemProvider;

    public SectionItem addRowItem(RowItem rowItem) {
        if (rowItems == null) {
            rowItems = new ArrayList<>();
        }
        rowItems.add(rowItem);
        return this;
    }

    public SectionItem setHeaderRowItem(RowItem headerRowItem) {
        this.headerRowItem = headerRowItem;
        return this;
    }

    public SectionItem setFooterRowItem(RowItem footerRowItem) {
        this.footerRowItem = footerRowItem;
        return this;
    }

    public SectionItem setShowDivider(boolean showDivider) {
        this.showDivider = showDivider;
        return this;
    }

    public SectionItem setSectionTitle(String sectionTitle) {
        this.sectionTitle = sectionTitle;
        return this;
    }

    public SectionItem setPreviousLinkType(LinkType.Previous previousLinkType) {
        this.previousLinkType = previousLinkType;
        return this;
    }

    public SectionItem setNextLinkType(LinkType.Next nextLinkType) {
        this.nextLinkType = nextLinkType;
        return this;
    }

    public SectionItem setSectionHeaderGapHeight(int sectionHeaderGapHeight) {
        this.sectionHeaderGapHeight = sectionHeaderGapHeight;
        return this;
    }

    public SectionItem setSectionHeaderGapDrawable(Drawable sectionHeaderGapDrawable) {
        this.sectionHeaderGapDrawable = sectionHeaderGapDrawable;
        return this;
    }

    public SectionItem setSectionFooterGapHeight(int sectionFooterGapHeight) {
        this.sectionFooterGapHeight = sectionFooterGapHeight;
        return this;
    }

    public SectionItem setSectionFooterGapDrawable(Drawable sectionFooterGapDrawable) {
        this.sectionFooterGapDrawable = sectionFooterGapDrawable;
        return this;
    }

    public SectionItem setSectionLayoutType(LayoutType sectionLayoutType) {
        this.sectionLayoutType = sectionLayoutType;
        return this;
    }

    public SectionItem setDividerShowType(DividerStyle.ShowType dividerShowType) {
        this.dividerShowType = dividerShowType;
        return this;
    }

    public SectionItem setDividerStyle(DividerStyle dividerStyle) {
        this.dividerStyle = dividerStyle;
        return this;
    }

    public SectionItem setLazyLoad(boolean lazyLoad) {
        isLazyLoad = lazyLoad;
        return this;
    }

    public SectionItem setRowCount(int rowCount) {
        this.rowCount = rowCount;
        return this;
    }

    public SectionItem setLazyLoadRowItemProvider(LazyLoadRowItemProvider lazyLoadRowItemProvider) {
        this.lazyLoadRowItemProvider = lazyLoadRowItemProvider;
        return this;
    }
}
