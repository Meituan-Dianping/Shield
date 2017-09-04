package com.dianping.agentsdk.sectionrecycler.section;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;

import com.dianping.agentsdk.framework.ViewUtils;
import com.dianping.agentsdk.sectionrecycler.divider.HorDividerCreator;
import com.dianping.agentsdk.sectionrecycler.divider.HorDividerDecoration;
import com.dianping.agentsdk.sectionrecycler.divider.HorSectionDividerInterface;
import com.dianping.shield.core.R;


/**
 * <p>
 * SectionDAdapter, A {@link SectionAdapter} which draws dividers for each item.
 * </p>
 * <p>
 * Here the class implements {@link HorDividerCreator} for {@link HorDividerDecoration}
 * to provides infomations needed while drawing dividers.
 * </p>
 * <p>
 * Besides, this class implements {@link HorSectionDividerInterface} while the methods of
 * the interface is called by {@link HorDividerCreator}'s methods. Thus, the subclasses
 * only need to implements the {@link HorSectionDividerInterface} and do not have to consider
 * the {@link HorDividerCreator}.
 * </p>
 * <p>
 * This class is actually a wrapper whtich wraps its subclasses as a {@link HorDividerCreator}.
 * As {@link HorDividerCreator} deals with 1-Dimension data structure and
 * {@link HorSectionDividerInterface} deals with a Section-Cell 2-Dimension data structure,
 * this class makes the subclasses only need to deal with the 2-Dimension structure.
 * </p>
 * <p/>
 * Created by runqi.wei
 * 10:36
 * 21.06.2016.
 */
public abstract class SectionDAdapter<VH extends RecyclerView.ViewHolder>
        extends SectionAdapter<VH> implements HorDividerCreator, HorSectionDividerInterface {

    protected static final int NO_OFFSET = -1;
    protected static final int NO_SPACE_HIGHT = -1;
    public static int INDEX_NOT_EXIST = -1;
    public static int TYPE_NOT_EXIST = -1;
    /**
     * Default cell top/bottom divider
     */
    protected Drawable defaultDivider;
    /**
     * Default section top/bottom divider
     */
    protected Drawable defaultSectionDivider;
    /**
     * Default LEFT offset of the cell top/bottom divider in px
     */
    protected float defaultOffset;
    /**
     * Default Header/Footer hight for each section in px
     */
    protected float defaultSpaceHight;
    protected Context context;
    private boolean disableDivider = false;
    private HorDividerDecoration mHorDividerDecoration;
    private boolean hasBottomFooterDivider = true;

    public SectionDAdapter(@NonNull Context context) {
        this.context = context;

        // default divider
        defaultDivider = ContextCompat.getDrawable(getContext(), R.drawable.section_recycler_view_divider);

        // default section divider
        defaultSectionDivider = ContextCompat.getDrawable(getContext(), R.drawable.section_recycler_view_section_divider);

        // default left offset is 15dp
        defaultOffset = ViewUtils.dip2px(getContext(), 15);

        // default hight for spaces is 10dp
        defaultSpaceHight = ViewUtils.dip2px(getContext(), 10);
    }

    public Context getContext() {
        return context;
    }

    public boolean enableDivider() {
        return !disableDivider;
    }

    public void setEnableDivider(boolean enableDivider) {
        this.disableDivider = !enableDivider;
        notifyDataSetChanged();
    }

    /**
     * Set divider ItemDecoration
     **/
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (recyclerView != null) {
            mHorDividerDecoration = new HorDividerDecoration(this);
            mHorDividerDecoration.setBottomFooterDivider(hasBottomFooterDivider);
            recyclerView.addItemDecoration(mHorDividerDecoration);
        }
    }

    public void setBottomFooterDividerDecoration(boolean hasFooterDivider) {
        this.hasBottomFooterDivider = hasFooterDivider;
        if (mHorDividerDecoration != null) {
            mHorDividerDecoration.setBottomFooterDivider(hasFooterDivider);
            notifyDataSetChanged();
        }
    }

    @Override
    public final float getHeaderHeight(int position) {

        Pair<Integer, Integer> sectionInfo = getSectionIndex(position);
        if (sectionInfo == null) {
            return 0;
        }

        SectionPosition sectionPosition = getSectionPosition(sectionInfo.first, sectionInfo.second);
        if (sectionPosition != SectionPosition.TOP && sectionPosition != SectionPosition.SINGLE) {
            return 0;
        }

        float height = getSectionHeaderHeight(sectionInfo.first);

        if (height >= 0) {
            return height;
        }

        return 0;
    }

    @Override
    public final float getFooterHeight(int position) {

        Pair<Integer, Integer> sectionInfo = getSectionIndex(position);
        if (sectionInfo == null) {
            return 0;
        }

        SectionPosition sectionPosition = getSectionPosition(sectionInfo.first, sectionInfo.second);
        if (sectionPosition != SectionPosition.BOTTOM && sectionPosition != SectionPosition.SINGLE) {
            return 0;
        }

        float height = getSectionFooterHeight(sectionInfo.first);

        if (height >= 0) {
            return height;
        }

        int count = getRowCount(sectionInfo.first);
        if (sectionInfo.second == count - 1) {
            return defaultSpaceHight;
        }
        return 0;
    }

    @Override
    public boolean hasTopDividerVerticalOffset(int position) {
        Pair<Integer, Integer> sectionInfo = getSectionIndex(position);
        if (sectionInfo != null) {
            return hasTopDividerVerticalOffset(sectionInfo.first, sectionInfo.second);
        }
        return false;
    }

    @Override
    public boolean hasBottomDividerVerticalOffset(int position) {
        Pair<Integer, Integer> sectionInfo = getSectionIndex(position);
        if (sectionInfo != null) {
            return hasBottomDividerVerticalOffset(sectionInfo.first, sectionInfo.second);
        }
        return false;
    }

    /**
     * Returns whether to show the top divider for the given position
     *
     * @param section  the section index to query
     * @param position the in-section position index to query
     */
    public boolean showTopDivider(int section, int position) {
        return true;
    }

    /**
     * Returns whether to show the bottom divider for the given position
     *
     * @param section  the section index to query
     * @param position the in-section position index to query
     */
    public boolean showBottomDivider(int section, int position) {
        return true;
    }

    @Override
    public final Drawable getTopDivider(int position) {
        Pair<Integer, Integer> sectionInfo = getSectionIndex(position);

        if (sectionInfo == null) {
            return null;
        }

        if (!enableDivider()) {
            return null;
        }

        if (!showTopDivider(sectionInfo.first, sectionInfo.second)) {
            return null;
        }

        // If the subclasses do not return a divider,
        // Set defaultSectionDivider as top divider
        // for the first item of the section.
        SectionPosition p = getSectionPosition(sectionInfo.first, sectionInfo.second);
        if (p == SectionPosition.TOP || p == SectionPosition.SINGLE) {
            // calls the method overrided by subclasses
            // to get the top divider
            Drawable topDivider = getTopDivider(sectionInfo.first, sectionInfo.second);
            if (topDivider != null) {
                return topDivider;
            }
            return defaultSectionDivider;
        }

        return null;
    }

    @Override
    public final Drawable getBottomDivider(int position) {
        Pair<Integer, Integer> sectionInfo = getSectionIndex(position);

        if (sectionInfo == null) {
            return null;
        }

        if (!enableDivider()) {
            return null;
        }

        if (!showBottomDivider(sectionInfo.first, sectionInfo.second)) {
            return null;
        }

        // Calls the method overrided by subclasses
        // to get the bottom divider.
        Drawable bottomDivider = getBottomDivider(sectionInfo.first, sectionInfo.second);
        if (bottomDivider != null) {
            return bottomDivider;
        }

        // If the subclasses do not return a divider,
        // set defaultSectionDivider as bottom divider
        // for the last item of the section,
        // and set defaultDivider for the other items
        // as the bottom divider.
        SectionPosition p = getSectionPosition(sectionInfo.first, sectionInfo.second);
        if (p == SectionPosition.BOTTOM || p == SectionPosition.SINGLE) {
            return defaultSectionDivider;
        }
        return defaultDivider;
    }

    /**
     * Returns the position type (TOP, MIDDLE, BOTTOM)
     * of the given position in the given section.
     *
     * @param section  the given section index
     * @param position the given position
     * @return the position type.
     */
    private SectionPosition getSectionPosition(int section, int position) {

        if (section < 0 || section >= getSectionCount()) {
            return SectionPosition.UNKNOWN;
        }
        int count = getRowCount(section);

        if (count < 0 || position < 0 || position >= count) {
            return SectionPosition.UNKNOWN;
        }

        if (count == 1) {
            return SectionPosition.SINGLE;
        }

        if (position == 0) {
            return SectionPosition.TOP;
        } else if (position == count - 1) {
            return SectionPosition.BOTTOM;
        } else {
            return SectionPosition.MIDDLE;
        }

    }

    @Override
    public final int topDividerOffset(int position) {
        Pair<Integer, Integer> sectionInfo = getSectionIndex(position);

        if (sectionInfo == null) {
            return 0;
        }

        float topOffset = topDividerOffset(sectionInfo.first, sectionInfo.second);
        if (topOffset >= 0) {
            return (int) topOffset;
        }

        SectionPosition p = getSectionPosition(sectionInfo.first, sectionInfo.second);

        if (p == SectionPosition.TOP || p == SectionPosition.BOTTOM || p == SectionPosition.SINGLE) {
            return 0;
        } else if (p == SectionPosition.MIDDLE) {
            return (int) defaultOffset;
        } else {
            return 0;
        }
    }

    @Override
    public final int bottomDividerOffset(int position) {

        Pair<Integer, Integer> sectionInfo = getSectionIndex(position);

        if (sectionInfo == null) {
            return 0;
        }

        float bottomOffset = bottomDividerOffset(sectionInfo.first, sectionInfo.second);
        if (bottomOffset >= 0) {
            return (int) bottomOffset;
        }

        SectionPosition p = getSectionPosition(sectionInfo.first, sectionInfo.second);

        if (p == SectionPosition.BOTTOM || p == SectionPosition.SINGLE) {
            return 0;
        } else {
            return (int) defaultOffset;
        }
    }

    public void setDefaultSectionDivider(Drawable defaultSectionDivider) {
        this.defaultSectionDivider = defaultSectionDivider;
    }

    public void setDefaultDivider(Drawable defaultDivider) {
        this.defaultDivider = defaultDivider;
    }

    public void setDefaultOffset(float defaultOffset) {
        this.defaultOffset = defaultOffset;
    }

    public void setDefaultSpaceHight(float defaultSpaceHight) {
        this.defaultSpaceHight = defaultSpaceHight;
    }

    private enum SectionPosition {
        UNKNOWN, TOP, MIDDLE, BOTTOM, SINGLE;
    }
}
