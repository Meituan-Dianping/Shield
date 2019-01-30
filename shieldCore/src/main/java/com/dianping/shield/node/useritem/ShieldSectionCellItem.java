package com.dianping.shield.node.useritem;

import com.dianping.agentsdk.framework.CellStatus;
import com.dianping.shield.node.itemcallbacks.LoadingMoreViewPaintingListener;
import com.dianping.shield.node.itemcallbacks.MoveStatusCallback;
import com.dianping.shield.node.itemcallbacks.ViewPaintingCallback;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by zhi.he on 2018/6/18.
 */

public class ShieldSectionCellItem {
//    public String identifier; //节点identifier

//    public ShieldCellGroup rowParent;   //父节点

//    public int groupIndex;

    public ArrayList<SectionItem> sectionItems;//模块的Section节点

    public boolean shouldShow = true;//模块是否展示

    public CellStatus.LoadingStatus loadingStatus = CellStatus.LoadingStatus.UNKNOWN;    //模块整体LoadingStatus

    public ViewItem loadingViewItem;   //模块LoadingView定制
    public ViewItem failedViewItem;//模块LoadingFailedView定制
    public ViewItem emptyViewItem;//模块LoadingEmptyView定制

    public CellStatus.LoadingMoreStatus loadingMoreStatus = CellStatus.LoadingMoreStatus.UNKNOWN;   //模块整体LoadingMoreStatus

    public ViewItem loadingMoreViewItem;   //模块LoadingMoreView定制
    public ViewItem loadingMoreFailedViewItem;//模块LoadingFailedMoreView定制
    public LoadingMoreViewPaintingListener loadingMoreViewPaintingListener;//loadingmore view 监听

//    public LinkType.Previous previousLinkType; //模块默认LinkType.Previous
//
//    public LinkType.Next nextLinkType; //模块默认LinkType.Next

    public ArrayList<ExposeInfo> exposeInfo; //模块曝光配置包括回调

//    public MGEInfo mgeInfo;    // 模块曝光打点

    public MoveStatusCallback moveStatusCallback;   //模块显示状态包括回调

    public boolean needScrollToTop = false;

    public Map<String, Integer> recyclerViewTypeSizeMap = null;

//    public String emptyMessage;
//
//    // 默认sectionHeight高度，不给使用页面的默认值
//    public int sectionHeaderGapHeight;
//
//    // 默认sectionFooter高度，不给使用页面的默认值
//    public int sectionFooterGapHeight;

    public static ShieldSectionCellItem createSimpleSCI(ViewPaintingCallback viewPaintingCallback) {
        return new ShieldSectionCellItem().addSectionItem(new SectionItem().addRowItem(RowItem.createNormalRow(viewPaintingCallback)));
    }

    public static ShieldSectionCellItem createSimpleSCI(ViewPaintingCallback viewPaintingCallback, String viewType) {
        return new ShieldSectionCellItem().addSectionItem(new SectionItem().addRowItem(RowItem.createNormalRow(viewPaintingCallback, viewType)));
    }

    public static ShieldSectionCellItem createSimpleSCI(ViewPaintingCallback viewPaintingCallback, String viewType, Object data) {
        return new ShieldSectionCellItem().addSectionItem(new SectionItem().addRowItem(RowItem.createNormalRow(viewPaintingCallback, viewType, data)));
    }

    public ShieldSectionCellItem addSectionItem(SectionItem sectionItem) {
        if (sectionItems == null) {
            sectionItems = new ArrayList<>();
        }
        sectionItems.add(sectionItem);
        return this;
    }

    public ShieldSectionCellItem setShouldShow(boolean shouldShow) {
        this.shouldShow = shouldShow;
        return this;
    }

    public ShieldSectionCellItem setLoadingStatus(CellStatus.LoadingStatus loadingStatus) {
        this.loadingStatus = loadingStatus;
        return this;
    }

    public ShieldSectionCellItem setLoadingViewItem(ViewItem loadingViewItem) {
        this.loadingViewItem = loadingViewItem;
        return this;
    }

    public ShieldSectionCellItem setFailedViewItem(ViewItem failedViewItem) {
        this.failedViewItem = failedViewItem;
        return this;
    }

    public ShieldSectionCellItem setEmptyViewItem(ViewItem emptyViewItem) {
        this.emptyViewItem = emptyViewItem;
        return this;
    }

    public ShieldSectionCellItem setLoadingMoreStatus(CellStatus.LoadingMoreStatus loadingMoreStatus) {
        this.loadingMoreStatus = loadingMoreStatus;
        return this;
    }

    public ShieldSectionCellItem setLoadingMoreViewItem(ViewItem loadingMoreViewItem) {
        this.loadingMoreViewItem = loadingMoreViewItem;
        return this;
    }

    public ShieldSectionCellItem setLoadingMoreFailedViewItem(ViewItem loadingMoreFailedViewItem) {
        this.loadingMoreFailedViewItem = loadingMoreFailedViewItem;
        return this;
    }

    public ShieldSectionCellItem setLoadingMoreViewPaintingListener(LoadingMoreViewPaintingListener loadingMoreViewPaintingListener) {
        this.loadingMoreViewPaintingListener = loadingMoreViewPaintingListener;
        return this;
    }

    public ShieldSectionCellItem setMoveStatusCallback(MoveStatusCallback moveStatusCallback) {
        this.moveStatusCallback = moveStatusCallback;
        return this;
    }

    public ShieldSectionCellItem setNeedScrollToTop(boolean needScrollToTop) {
        this.needScrollToTop = needScrollToTop;
        return this;
    }

    public ShieldSectionCellItem setRecyclerViewTypeSizeMap(Map<String, Integer> recyclerViewTypeSizeMap) {
        this.recyclerViewTypeSizeMap = recyclerViewTypeSizeMap;
        return this;
    }

    public ShieldSectionCellItem addExposeInfo(ExposeInfo exposeInfo) {
        if (this.exposeInfo == null) {
            this.exposeInfo = new ArrayList<>();
        }
        this.exposeInfo.add(exposeInfo);
        return this;
    }
}
