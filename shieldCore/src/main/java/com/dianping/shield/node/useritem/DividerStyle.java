package com.dianping.shield.node.useritem;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;

import com.dianping.shield.node.PositionType;

/**
 * Created by zhi.he on 2018/7/2.
 */

public class DividerStyle {

    /**
     * 自定义的上下划线及缩进 优先级比Style高
     * 定制了就画，不管在什么位置
     * 没定制就走下面的Style划线
     */
    public Rect cellTopLineOffset;
    @ColorInt
    public Integer cellTopLineColor;
    public Drawable cellTopLineDrawable;//如果自定义了TopLineDrawable，不管在什么位置都会画上线

    public Rect cellBottomLineOffset;
    @ColorInt
    public Integer cellBottomLineColor;
    public Drawable cellBottomLineDrawable;//如果自定义了BottomLineDrawable，不管在什么位置都会画下线

    /**
     * 仅设置在Row上有效
     * 自定义划线类型，默认AUTO，根据位置自动计算，其他可以指定某行为特定style，
     * 设置成NONE会使下面的Style定制失效，但不会影响上面的CellLine定制。
     * 如果Line定制了还是会画，没定制设置成NONE就不画了
     */
    public StyleType styleType = StyleType.AUTO;

    /**
     * 不同类型的划线及缩进 drawable比color优先级高
     */
    public Rect topStyleLineOffset;
    @ColorInt
    public Integer topStyleLineColor = null;
    public Drawable topStyleLineDrawable;//当该row在Top位置的时候生效

    public Rect middleStyleLineOffset;
    @ColorInt
    public Integer middleStyleLineColor = null;
    public Drawable middleStyleLineDrawable;//当该row在Middle位置的时候生效

    public Rect bottomStyleLineOffset;
    @ColorInt
    public Integer bottomStyleLineColor = null;
    public Drawable bottomStyleLineDrawable;//当该row在Bottom位置的时候生效

    public Rect getTopLineOffset(PositionType positionType) {

        if (cellTopLineOffset != null) {       //定制的topLineoffset优先级最高
            return cellTopLineOffset;
        } else if (needTopStyle(positionType)) {    //没有定制topLine再看有没有定制topStyle
            return topStyleLineOffset;
        } else {
            return null;
        }
    }

    //通过这个方法的返回 isNull控制是否显示分割线
    public Drawable getTopLineDrawable(PositionType positionType) {
        if (cellTopLineDrawable != null) {
            return cellTopLineDrawable;
        } else if (needTopStyle(positionType)) {
            return topStyleLineDrawable;
        } else {
            return null;
        }
    }

    private boolean needTopStyle(PositionType positionType) {
        return positionType == PositionType.FIRST || positionType == PositionType.SINGLE;
    }

    public Rect getBottomLineOffset(PositionType positionType) {
        if (cellBottomLineOffset != null) {
            return cellBottomLineOffset;
        } else if (needBottomStyle(positionType)) {
            return bottomStyleLineOffset;
        } else {
            return middleStyleLineOffset;
        }
    }

    //通过这个方法的返回 isNull控制是否显示分割线
    public Drawable getBottomLineDrawable(PositionType positionType) {
        if (cellBottomLineDrawable != null) {
            return cellBottomLineDrawable;
        } else if (needBottomStyle(positionType)) {
            return bottomStyleLineDrawable;
        } else {
            return middleStyleLineDrawable;
        }
    }

    private boolean needBottomStyle(PositionType positionType) {
        return positionType == PositionType.LAST || positionType == PositionType.SINGLE;
    }

    public enum StyleType {
        //根据位置自动计算
        AUTO,
        //FIRST Style
        TOP,
        //
        MIDDLE,
        //
        BOTTOM,
        //隐藏Section顶部分割线
        SINGLE,
        //不画线
        NONE
    }

    //Section级别的分割线ShowType
    public enum ShowType {
        //只展示Section顶部和底部分割线
        TOP_BOTTOM,
        //展示所有分割线
        ALL,
        //隐藏所有分割线
        NONE,
        //隐藏Section顶部和底部分割线,只展示Row之间分割线
        MIDDLE,
        //隐藏Section顶部分割线
        NO_TOP,
        //隐藏Section底部分割线
        NO_BOTTOM,

        DEFAULT
    }


}
