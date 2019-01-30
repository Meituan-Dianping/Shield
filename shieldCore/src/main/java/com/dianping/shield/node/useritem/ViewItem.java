package com.dianping.shield.node.useritem;

import com.dianping.shield.node.cellnode.MoveStatusEventListener;
import com.dianping.shield.node.cellnode.ViewAttachDetachInterface;
import com.dianping.shield.node.itemcallbacks.ViewClickCallbackWithData;
import com.dianping.shield.node.itemcallbacks.ViewLongClickCallbackWithData;
import com.dianping.shield.node.itemcallbacks.ViewPaintingCallback;

import java.util.ArrayList;

/**
 * Created by zhi.he on 2018/6/18.
 */

public class ViewItem {

    //    public RowItem parent;
    public String viewType;

    public String stableid;

    public Object data;

    public ViewPaintingCallback viewPaintingCallback;//ViewHolder回调

    public ViewClickCallbackWithData clickCallback;

    public ViewLongClickCallbackWithData longClickCallback;

    public ArrayList<ViewAttachDetachInterface> attachDetachInterfaceArrayList;

    public ArrayList<MoveStatusEventListener<ViewItem>> moveStatusEventListeners;

    public ExposeInfo exposeInfo;

    public MoveStatusInfo moveStatusInfo;

//    public MGEInfo clickMGEInfo;
//    public MGEInfo viewMGEInfo;

    public static ViewItem simpleViewItem(ViewPaintingCallback viewPaintingCallback) {
        return new ViewItem().setViewPaintingCallback(viewPaintingCallback);
    }

    public static ViewItem simpleViewItem(ViewPaintingCallback viewPaintingCallback, String viewType) {
        return new ViewItem().setViewPaintingCallback(viewPaintingCallback).setViewType(viewType);
    }

    public static ViewItem simpleViewItem(ViewPaintingCallback viewPaintingCallback, String viewType, Object data) {
        return new ViewItem().setViewPaintingCallback(viewPaintingCallback).setViewType(viewType).setData(data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ViewItem viewItem = (ViewItem) o;

        if (viewType != null ? !viewType.equals(viewItem.viewType) : viewItem.viewType != null)
            return false;
        if (stableid != null ? !stableid.equals(viewItem.stableid) : viewItem.stableid != null)
            return false;
        if (data != null ? !data.equals(viewItem.data) : viewItem.data != null) return false;
        return viewPaintingCallback != null ? viewPaintingCallback.equals(viewItem.viewPaintingCallback) : viewItem.viewPaintingCallback == null;
    }

    @Override
    public int hashCode() {
        int result = viewType != null ? viewType.hashCode() : 0;
        result = 31 * result + (stableid != null ? stableid.hashCode() : 0);
        result = 31 * result + (data != null ? data.hashCode() : 0);
        result = 31 * result + (viewPaintingCallback != null ? viewPaintingCallback.hashCode() : 0);
        return result;
    }

    public ViewItem setViewType(String viewType) {
        this.viewType = viewType;
        return this;
    }

    public ViewItem setStableid(String stableid) {
        this.stableid = stableid;
        return this;
    }

    public ViewItem setData(Object data) {
        this.data = data;
        return this;
    }

    public ViewItem setViewPaintingCallback(ViewPaintingCallback viewPaintingCallback) {
        this.viewPaintingCallback = viewPaintingCallback;
        return this;
    }

    public ViewItem setClickCallback(ViewClickCallbackWithData clickCallback) {
        this.clickCallback = clickCallback;
        return this;
    }

    public ViewItem setLongClickCallback(ViewLongClickCallbackWithData longClickCallback) {
        this.longClickCallback = longClickCallback;
        return this;
    }

    public ViewItem setExposeInfo(ExposeInfo exposeInfo) {
        this.exposeInfo = exposeInfo;
        return this;
    }

    public ViewItem setMoveStatusInfo(MoveStatusInfo moveStatusInfo) {
        this.moveStatusInfo = moveStatusInfo;
        return this;
    }
}
