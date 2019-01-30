package com.dianping.shield.node.cellnode;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * Created by runqi.wei at 2018/6/27
 */
public class DividerConfigInfo {

    public int headerGapHeight;
    public Drawable headerGapDrawable;

    public int footerGapHeight;
    public Drawable footerGapDrawable;

//    /**
//     * 不同类型的划线及缩进
//     */
//    public Rect topStyleLineOffset;
//    public Drawable topStyleLineDrawable;
//    public Rect middleStyleLineOffset;
//    public Drawable middleStyleLineDrawable;
//    public Rect bottomStyleLineOffset;
//    public Drawable bottomStyleLineDrawable;

    /**
     * 自定义的上下划线及缩进
     */
    public Rect cellTopLineOffset;
    public Drawable cellTopLineDrawable;//比Style优先级更高
    public Rect cellBottomLineOffset;
    public Drawable cellBottomLineDrawable;//比Style优先级更高

//    /**
//     * 划线显隐控制
//     */
//    public boolean showCellTopLineDivider;
//    public boolean showCellBottomLineDivider;

//    public int getHeaderHeight() {
//        return headerGapHeight;
//    }
//
//    public Drawable getHeaderDrawable() {
//        return headerGapDrawable;
//    }
//
//    public int getFooterHeight() {
//        return footerGapHeight;
//    }
//
//    public Drawable getFooterDrawable() {
//        return footerGapDrawable;
//    }
//
//    public Rect getTopLineOffset(PositionType positionType) {
//        if (cellTopLineOffset != null) {
//            return cellTopLineOffset;
//        } else if (needTopStyle(positionType)) {
//            return topStyleLineOffset;
//        } else {
//            return middleStyleLineOffset;
//        }
//    }
//
//    //通过这个方法的返回 isNull控制是否显示分割线
//    public Drawable getTopLineDrawable(PositionType positionType) {
//        if (cellTopLineDrawable != null) {
//            return cellTopLineDrawable;
//        } else if (needTopStyle(positionType)) {
//            return topStyleLineDrawable;
//        } else {
//            return middleStyleLineDrawable;
//        }
//    }
//
//    private boolean needTopStyle(PositionType positionType) {
//        return positionType == PositionType.FIRST || positionType == PositionType.SINGLE;
//    }
//
//    public Rect getBottomLineOffset(PositionType positionType) {
//        if (cellBottomLineOffset != null) {
//            return cellBottomLineOffset;
//        } else if (needBottomStyle(positionType)) {
//            return bottomStyleLineOffset;
//        } else {
//            return middleStyleLineOffset;
//        }
//    }
//
//    //通过这个方法的返回 isNull控制是否显示分割线
//    public Drawable getBottomLineDrawable(PositionType positionType) {
//        if (cellBottomLineDrawable != null) {
//            return cellBottomLineDrawable;
//        } else if (needBottomStyle(positionType)) {
//            return bottomStyleLineDrawable;
//        } else {
//            return middleStyleLineDrawable;
//        }
//    }
//
//    private boolean needBottomStyle(PositionType positionType) {
//        return positionType == PositionType.LAST || positionType == PositionType.SINGLE;
//    }
}
