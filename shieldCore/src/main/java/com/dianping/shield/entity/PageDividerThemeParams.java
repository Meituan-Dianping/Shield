package com.dianping.shield.entity;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * Created by zhi.he on 2018/12/12.
 *
 * 只能通过提供的静态方法构造出特定的几种实例
 * 例如:
 * PageDividerThemeParams.dividerLeftOffset(15)
 * PageDividerThemeParams.needLastFooter(false)
 *
 * int型参数单位都为dp
 */

public class PageDividerThemeParams {
    private DividerTheme dividerTheme;
    private Object params;

    private PageDividerThemeParams(DividerTheme dividerTheme, Object params) {
        this.dividerTheme = dividerTheme;
        this.params = params;
    }

    public static PageDividerThemeParams dividerDrawable(Drawable defaultDivider) {
        return new PageDividerThemeParams(DividerTheme.DEFAULT_DIVIDER, defaultDivider);
    }

    public static PageDividerThemeParams dividerLeftOffset(int leftOffset) {
        return new PageDividerThemeParams(DividerTheme.DEFAULT_LEFT_OFFSET, leftOffset);
    }

    public static PageDividerThemeParams dividerRightOffset(int rightOffset) {
        return new PageDividerThemeParams(DividerTheme.DEFAULT_RIGHT_OFFSET, rightOffset);
    }

    public static PageDividerThemeParams sectionDivider(Drawable sectionDivider) {
        return new PageDividerThemeParams(DividerTheme.DEFAULT_SECTION_DIVIDER, sectionDivider);
    }

    public static PageDividerThemeParams sectioinDividerOffset(Rect sectionDividerOffset) {
        return new PageDividerThemeParams(DividerTheme.DEFAULT_SECTION_DIVIDER_OFFSET, sectionDividerOffset);
    }

    public static PageDividerThemeParams sectionTopDivider(Drawable sectionTopDivider) {
        return new PageDividerThemeParams(DividerTheme.DEFAULT_SECTION_TOP_DIVIDER, sectionTopDivider);
    }

    public static PageDividerThemeParams sectionBottomDivider(Drawable sectionBottomDivider) {
        return new PageDividerThemeParams(DividerTheme.DEFAULT_SECTION_BOTTOM_DIVIDER, sectionBottomDivider);
    }

    public static PageDividerThemeParams headerHeight(int headerHeight) {
        return new PageDividerThemeParams(DividerTheme.DEFAULT_HEADER_HEIGHT, headerHeight);
    }

    public static PageDividerThemeParams footerHeight(int footerHeight) {
        return new PageDividerThemeParams(DividerTheme.DEFAULT_FOOTER_HEIGHT, footerHeight);
    }

    public static PageDividerThemeParams firstHeaderExtraHeight(int extraHeight) {
        return new PageDividerThemeParams(DividerTheme.FIRST_HEADER_EXTRA_HEIGHT, extraHeight);
    }

    public static PageDividerThemeParams lastFooterExtraHeight(int extraHeight) {
        return new PageDividerThemeParams(DividerTheme.LAST_FOOTER_EXTRA_HEIGHT, extraHeight);
    }

    public static PageDividerThemeParams needLastFooter(boolean needLastFooter) {
        return new PageDividerThemeParams(DividerTheme.NEED_ADD_LAST_FOOTER, needLastFooter);
    }

    public static PageDividerThemeParams needFirstHeader(boolean needFirstHeader) {
        return new PageDividerThemeParams(DividerTheme.NEED_ADD_FIRST_HEADER, needFirstHeader);
    }

    public static PageDividerThemeParams spaceDrawable(Drawable spaceDrawable) {
        return new PageDividerThemeParams(DividerTheme.DEFAULT_SPACE_DRAWABLE, spaceDrawable);
    }

    public static PageDividerThemeParams enableDivider(boolean enableDivider) {
        return new PageDividerThemeParams(DividerTheme.ENABLE_DIVIDER, enableDivider);
    }

    public DividerTheme getDividerTheme() {
        return dividerTheme;
    }

    public Object getParams() {
        return params;
    }
}
