package com.dianping.agentsdk.sectionrecycler.divider;


import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * A section-cell two-level structure {@link HorDividerCreator}
 * <p>
 * Created by runqi.wei
 * 15:46
 * 21.06.2016.
 */
public interface HorSectionDividerInterface {

    /**
     * Returns the Height left on top of the given section
     * @param section the section index to query
     * @return the height in px.
     */
    float getSectionHeaderHeight(int section);

    Drawable getSectionHeaderDrawable(int section);

    /**
     * Returns the Height left under the given section
     * @param section the section index to query
     * @return the height in px.
     */
    float getSectionFooterHeight(int section);

    Drawable getSectionFooterDrawable(int section);

    /**
     * Returns whether we should leave space for the top divider
     * @param section the section index to query
     * @param position the in-section position index to query
     */
    boolean hasTopDividerVerticalOffset(int section, int position);

    /**
     * Returns whether we should leave space for the bottom divider
     * @param section the section index to query
     * @param position the in-section position index to query
     */
    boolean hasBottomDividerVerticalOffset(int section, int position);

    /**
     * Returns the top divider for the given position
     * @param section the section index to query
     * @param position the in-section position index to query
     */
    Drawable getTopDivider(int section, int position);

    /**
     * Returns the bottom divider for the given position
     * @param section the section index to query
     * @param position the in-section position index to query
     */
    Drawable getBottomDivider(int section, int position);

    /**
     * Returns the LEFT offset of the top divider for the given position
     * @param section the section index to query
     * @param position the in-section position index to query
     */
    Rect topDividerOffset(int section, int position);

    /**
     * Returns the LEFT offset of the bottom divider for the given position
     * @param section the section index to query
     * @param position the in-section position index to query
     */
    Rect bottomDividerOffset(int section, int position);

}
