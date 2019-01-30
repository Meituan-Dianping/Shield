package com.dianping.shield.node.useritem;

import com.dianping.shield.node.itemcallbacks.ViewPaintingCallback;
import com.dianping.shield.node.itemcallbacks.lazy.LazyLoadViewItemProvider;

import java.util.ArrayList;

/**
 * Created by zhi.he on 2018/6/18.
 */

public class RowItem {

//    public String identifier; //节点id
//
//    public SectionItem rowParent; //父节点
//
//    public int row; //节点index

    public ArrayList<ViewItem> viewItems;//ViewItem 线性布局只有一个

    public LayoutType layoutType = LayoutType.LINEAR_FULL_FILL; //布局类型 HeaderCell和FooterCell不支持延时加载，不支持其他类型LayoutType

//    public EffectType effectType = EffectType.NORMAL;//效果类型，置顶，置底，下拉放大等

    public TopInfo topInfo = null;

    public BottomInfo bottomInfo = null;

    /**
     * 划线显隐控制
     */
    public boolean showCellTopDivider = true;
    public boolean showCellBottomDivider = true;

    public DividerStyle dividerStyle; //划线定制

    public boolean isLazyLoad = false;

    public int viewCount = 0;

    public LazyLoadViewItemProvider lazyLoadViewItemProvider;

    public ArrayList<ExposeInfo> exposeInfoArray;
    public ArrayList<HotZoneInfo> hotZoneArrayList;

    public static RowItem createNormalRow(ViewPaintingCallback viewPaintingCallback) {
        return new RowItem().addViewItem(ViewItem.simpleViewItem(viewPaintingCallback));
    }

    public static RowItem createNormalRow(ViewPaintingCallback viewPaintingCallback, String viewType) {
        return new RowItem().addViewItem(ViewItem.simpleViewItem(viewPaintingCallback, viewType));
    }

    public static RowItem createNormalRow(ViewPaintingCallback viewPaintingCallback, String viewType, Object data) {
        return new RowItem().addViewItem(ViewItem.simpleViewItem(viewPaintingCallback, viewType, data));
    }

    public RowItem addViewItem(ViewItem viewItem) {
        if (viewItems == null) {
            viewItems = new ArrayList<>();
        }
        viewItems.add(viewItem);
        return this;
    }

    public RowItem setLayoutType(LayoutType layoutType) {
        this.layoutType = layoutType;
        return this;
    }

    public RowItem setTopInfo(TopInfo topInfo) {
        this.topInfo = topInfo;
        return this;
    }

    public RowItem setBottomInfo(BottomInfo bottomInfo) {
        this.bottomInfo = bottomInfo;
        return this;
    }

    public RowItem setShowCellTopDivider(boolean showCellTopDivider) {
        this.showCellTopDivider = showCellTopDivider;
        return this;
    }

    public RowItem setShowCellBottomDivider(boolean showCellBottomDivider) {
        this.showCellBottomDivider = showCellBottomDivider;
        return this;
    }

    public RowItem setDividerStyle(DividerStyle dividerStyle) {
        this.dividerStyle = dividerStyle;
        return this;
    }

    public RowItem setLazyLoad(boolean lazyLoad) {
        isLazyLoad = lazyLoad;
        return this;
    }

    public RowItem setViewCount(int viewCount) {
        this.viewCount = viewCount;
        return this;
    }

    public RowItem setLazyLoadViewItemProvider(LazyLoadViewItemProvider lazyLoadViewItemProvider) {
        this.lazyLoadViewItemProvider = lazyLoadViewItemProvider;
        return this;
    }

    public RowItem addExposeInfo(ExposeInfo exposeInfo) {
        if (this.exposeInfoArray == null) {
            this.exposeInfoArray = new ArrayList<>();
        }
        this.exposeInfoArray.add(exposeInfo);
        return this;
    }

    public RowItem addHotZoneInfo(HotZoneInfo hotZoneInfo) {
        if (this.hotZoneArrayList == null) {
            this.hotZoneArrayList = new ArrayList<>();
        }
        this.hotZoneArrayList.add(hotZoneInfo);
        return this;
    }
}
