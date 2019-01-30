package com.dianping.agentsdk.framework;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;

public class DividerInfo {

    private DividerStyle style = DividerStyle.AUTO;
    private int leftOffset = -1;//只对middle生效
    private int rightOffset = -1;
    private Drawable topDividerDrawable;
    private Drawable middleDividerDrawable;
    private Drawable bottomDividerDrawable;

    public DividerStyle getStyle() {
        return style == null ? DividerStyle.AUTO : style;
    }

    public void setStyle(DividerStyle style) {
        this.style = style;
    }

    public int getLeftOffset() {
        return leftOffset;
    }

    public void setLeftOffset(int leftOffset) {
        this.leftOffset = leftOffset;
    }

    public int getRightOffset() {
        return rightOffset;
    }

    public void setRightOffset(int rightOffset) {
        this.rightOffset = rightOffset;
    }

    public Drawable getTopDividerDrawable() {
        return topDividerDrawable;
    }

    public void setTopDividerDrawable(Drawable topDividerDrawable) {
        this.topDividerDrawable = topDividerDrawable;
    }

    public Drawable getMiddleDividerDrawable() {
        return middleDividerDrawable;
    }

    public void setMiddleDividerDrawable(Drawable middleDividerDrawable) {
        this.middleDividerDrawable = middleDividerDrawable;
    }

    public Drawable getBottomDividerDrawable() {
        return bottomDividerDrawable;
    }

    public void setBottomDividerDrawable(Drawable bottomDividerDrawable) {
        this.bottomDividerDrawable = bottomDividerDrawable;
    }

    public void setTopDividerColor(@ColorInt int color) {
        setTopDividerDrawable(createDrawable(color));
    }

    public void setMiddleDividerColor(@ColorInt int color) {
        setMiddleDividerDrawable(createDrawable(color));
    }

    public void setBottomDividerColor(@ColorInt int color) {
        setBottomDividerDrawable(createDrawable(color));
    }

    protected Drawable createDrawable(@ColorInt int color) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(color);
        gradientDrawable.setSize(gradientDrawable.getIntrinsicWidth(), 1);
        return gradientDrawable;
    }

    public enum DividerStyle {
        AUTO,
        NONE,
        TOP,
        MIDDLE,
        BOTTOM,
        SINGLE;
    }
}
