package com.dianping.agentsdk.sectionrecycler.divider;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * <p>
 *     为 {@link HorDividerDecoration} 提供绘制 Item 上下方分隔线所必须的信息。
 * </p>
 *
 * Created by runqi.wei
 * 15:41
 * 20.06.2016.
 */
public interface HorDividerCreator {

    /**
     * 返回需要在 Item 上面预留的高度
     * 不包含为分隔线和 Margin 留下的高度
     * @param position Item 的位置
     * @return <code>float</code> 需要在 Item 上面留下的高度
     */
    float getHeaderHeight(int position);

    Drawable getHeaderDrawable(int position);

    /**
     * 返回需要在 Item 下面预留的高度
     * 不包含为分隔线和 Margin 留下的高度
     * @param position Item 的位置
     * @return float 需要在 Item 下面留下的高度
     */
    float getFooterHeight(int position);

    Drawable getFooterDrawable(int position);

    /**
     * 返回是否需要为 Item 顶部的分隔线留下高度
     * @param position Item 的位置
     * @return boolean 是否需要为 Item 顶部的分隔线留下高度
     *         - true 使得 Item 的顶部分隔线会绘制在 Item 的 Margin 范围之外，
     *                  不会遮住 Item 的顶部 Margin 以及 Item 自身
     *         - false 使得 Item 的顶部分隔线会绘制在 Item 的 Margin 和 Item 自身范围之内，
     *                  会遮住 Item 的顶部 Margin，也有可能遮住 Item 自身
     */
    boolean hasTopDividerVerticalOffset(int position);

    /**
     * 返回是否需要为 Item 底部的分隔线留下高度
     * @param position Item 的位置
     * @return boolean 是否需要为 Item 底部的分隔线留下高度
     *         - true 使得 Item 的底部分隔线会绘制在 Item 的 Margin 范围之外，
     *                  不会遮住 Item 的顶部 Margin 以及 Item 自身
     *         - false 使得 Item 的底部分隔线会绘制在 Item 的 Margin 和 Item 自身范围之内，
     *                  会遮住 Item 的底部 Margin，也有可能遮住 Item 自身
     */
    boolean hasBottomDividerVerticalOffset(int position);

    /**
     * 返回 Item 的顶部分隔线
     * @param position Item 的位置
     * @return null 表示 Item 不需要绘制顶部分隔线
     */
    Drawable getTopDivider(int position);

    /**
     * 返回 Item 的顶部分隔线
     * @param position Item 的位置
     * @return null 表示 Item 不需要绘制底部分隔线
     */
    Drawable getBottomDivider(int position);


    /**
     * 返回 Item 顶部分隔线的 <strong>左侧</strong> 缩进距离
     * @param position Item 的位置
     * @return float 左侧缩近距离
     */
    Rect topDividerOffset(int position);

    /**
     * 返回 Item 底部分隔线的 <strong>左侧</strong> 缩进距离
     * @param position Item 的位置
     * @return float 左侧缩近距离
     */
    Rect bottomDividerOffset(int position);


}
