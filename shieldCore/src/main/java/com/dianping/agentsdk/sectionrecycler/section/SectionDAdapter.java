package com.dianping.agentsdk.sectionrecycler.section;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;

import com.dianping.agentsdk.framework.DividerInfo;
import com.dianping.agentsdk.framework.ViewUtils;
import com.dianping.agentsdk.sectionrecycler.divider.HorDividerCreator;
import com.dianping.agentsdk.sectionrecycler.divider.HorDividerDecoration;
import com.dianping.agentsdk.sectionrecycler.divider.HorSectionDividerInterface;
import com.dianping.shield.core.R;
import com.dianping.shield.layoutmanager.CoveredYInterface;


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
        extends SectionAdapter<VH> implements HorDividerCreator, HorSectionDividerInterface, InnerDividerInfoInterface {

    public static final boolean DEBUG = false;
    public static final String TAG = SectionDAdapter.class.getSimpleName();
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
     * Default RIGHT offset of the cell top/bottom divider in px
     */
    protected float defaultRightOffset;

    /**
     * Default Header/Footer hight for each section in px
     */
    protected float defaultSpaceHight;
    protected Drawable defaultSpaceDrawable;
    protected Context context;
    private boolean disableDivider = false;
    private boolean disableDecoration = false;
    private HorDividerDecoration mHorDividerDecoration;
    private boolean hasBottomFooterDivider = true;
    private boolean isHeaderFirst = false;

    private boolean hasAddObservable = false;
    private Observer dividerObserver = new Observer();
    private SparseArray<Divider> dividerArrayList = new SparseArray<>();

    public SectionDAdapter(@NonNull Context context) {
        this.context = context;

        // default divider
        defaultDivider = ContextCompat.getDrawable(getContext(), R.drawable.section_recycler_view_divider);

        // default section divider
        defaultSectionDivider = ContextCompat.getDrawable(getContext(), R.drawable.section_recycler_view_section_divider);

        // default left offset is 15dp
        defaultOffset = ViewUtils.dip2px(getContext(), 15);

        // default right offset is 0
        defaultRightOffset = 0;

        // default hight for spaces is 10dp
        defaultSpaceHight = ViewUtils.dip2px(getContext(), 10);

        defaultSpaceDrawable = null;

        mHorDividerDecoration = new HorDividerDecoration(this);
        mHorDividerDecoration.setBottomFooterDivider(hasBottomFooterDivider);
    }

    public void setDisableDecoration(boolean disableDecoration) {
        this.disableDecoration = disableDecoration;
    }

    public void setCoveredYInterface(CoveredYInterface coveredYInterface) {
        mHorDividerDecoration.setCoveredYInterface(coveredYInterface);
    }

    @Override
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        if (!hasAddObservable) {
            super.registerAdapterDataObserver(dividerObserver);
            hasAddObservable = true;
        }
        super.registerAdapterDataObserver(observer);
    }

    public Context getContext() {
        return context;
    }

    public boolean enableDivider() {
        return !disableDivider;
    }

    protected void updateDividerList() {
        log("updateDividerList ==========================================");
        dividerArrayList.clear();
//        for (int i = 0; i < getItemCount(); i++) {

//            Divider divider = new Divider();
//            Pair<Integer, Integer> sec = getSectionIndex(i);
//            createDividerInfo(i, divider, sec);
//        }
    }

    protected void createDividerInfo(int pos, Pair<Integer, Integer> sec) {
        Divider divider = new Divider();
        SectionPosition sectionPosition = getSectionPosition(sec.first, sec.second);
        DividerInfo dividerInfo = getDividerInfo(sec.first, sec.second);

        divider.headerHeight = getHeaderHeightInner(pos, sec, sectionPosition);
        divider.headerDrawable = getHeaderDrawableInner(pos, sec, sectionPosition);

        divider.footerHeight = getFooterHeightInner(pos, sec, sectionPosition);
        divider.footerDrawable = getFooterDrawableInner(pos, sec, sectionPosition);

        divider.hasTopDividerVerticalOffset = hasTopDividerVerticalOffsetInner(pos, sec, sectionPosition);
        divider.hasBottomDividerVerticalOffset = hasBottomDividerVerticalOffsetInner(pos, sec, sectionPosition);

        divider.topDivider = getTopDividerInner(pos, sec, sectionPosition, dividerInfo);
        divider.topDividerOffset = topDividerOffsetInner(pos, sec, sectionPosition, dividerInfo);

        divider.bottomDivider = getBottomDividerInner(pos, sec, sectionPosition, dividerInfo);
        divider.bottomDividerOffset = bottomDividerOffsetInner(pos, sec, sectionPosition, dividerInfo);

        dividerArrayList.put(pos, divider);
        log("divider " + pos + " => " + divider + "\n");

    }

    public Divider getDividerItem(int position) {
        if (position < 0 || position >= getItemCount()) {
            return null;
        }
        if (dividerArrayList.indexOfKey(position) < 0) {
            Pair<Integer, Integer> sectionInfo = getSectionIndex(position);
            createDividerInfo(position, sectionInfo);
        }
        return dividerArrayList.get(position);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        Pair<Integer, Integer> sectionInfo = getSectionIndex(position);
        if (sectionInfo != null) {
            isOnBind = true;
            onBindViewHolder(holder, sectionInfo.first, sectionInfo.second);
            if (dividerArrayList.indexOfKey(position) < 0) {
                createDividerInfo(position, sectionInfo);
            }
            isOnBind = false;
        }
    }

    public void setEnableDivider(boolean enableDivider) {
        this.disableDivider = !enableDivider;
        notifyDataSetChanged();
    }

    public void setSectionGapMode(boolean isHeaderFirst) {
        this.isHeaderFirst = isHeaderFirst;
    }

    /**
     * Set divider ItemDecoration
     **/
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (recyclerView != null && (!disableDecoration)) {
            recyclerView.addItemDecoration(mHorDividerDecoration);
        }
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        if (recyclerView != null && mHorDividerDecoration != null) {
            recyclerView.removeItemDecoration(mHorDividerDecoration);
        }
        super.onDetachedFromRecyclerView(recyclerView);
    }

    public void setBottomFooterDividerDecoration(boolean hasFooterDivider) {
        this.hasBottomFooterDivider = hasFooterDivider;
        if (mHorDividerDecoration != null) {
            mHorDividerDecoration.setBottomFooterDivider(hasFooterDivider);
            notifyDataSetChanged();
        }
    }

    @Override
    public float getHeaderHeight(int position) {
        log("getHeaderHeight: " + position);
        Divider divider = getDividerItem(position);
        if (divider != null) {
            return divider.headerHeight;
        }
        return 0;
    }

    public final float getHeaderHeightInner(int position, Pair<Integer, Integer> sectionInfo, SectionPosition sectionPosition) {
        log("getHeaderHeightInner: " + sectionInfo);
        if (sectionPosition != SectionPosition.TOP && sectionPosition != SectionPosition.SINGLE) {
            return 0;
        }

        float height = getSectionHeaderHeight(sectionInfo.first);

        if (height >= 0) {
            return height;
        }
        //header first 模式下，每个section画10dp header ，第一个Section除外
        if (isHeaderFirst && position != 0 && sectionInfo.second == 0) {
            return defaultSpaceHight;
        }
        return 0;
    }

    @Override
    public Drawable getHeaderDrawable(int position) {
        log("getHeaderDrawable: " + position);
        Divider divider = getDividerItem(position);
        if (divider != null) {
            return divider.headerDrawable;
        }
        return null;
    }

    public Drawable getHeaderDrawableInner(int position, Pair<Integer, Integer> sectionInfo, SectionPosition sectionPosition) {
        log("getHeaderDrawableInner: " + position);
        if (sectionPosition != SectionPosition.TOP && sectionPosition != SectionPosition.SINGLE) {
            return null;
        }

        Drawable headerDrawable = getSectionHeaderDrawable(sectionInfo.first);
        if (headerDrawable != null) {
            return headerDrawable;
        }
        if (isHeaderFirst) {
            return defaultSpaceDrawable;
        } else {
            return null;
        }
    }

    @Override
    public float getFooterHeight(int position) {
        log("getFooterHeight: " + position);
        Divider divider = getDividerItem(position);
        if (divider != null) {
            return divider.footerHeight;
        }
        return 0;
    }

    public final float getFooterHeightInner(int position, Pair<Integer, Integer> sectionInfo, SectionPosition sectionPosition) {
        log("getFooterHeightInner: " + position);
        if (sectionPosition != SectionPosition.BOTTOM && sectionPosition != SectionPosition.SINGLE) {
            return 0;
        }

        float height = getSectionFooterHeight(sectionInfo.first);

        if (height >= 0) {
            return height;
        }

//        int count = getRowCount(sectionInfo.first);
//        if (isHeaderFirst && sectionInfo.second == count - 1) {
//            return 0;
//        }else if (sectionInfo.second == count - 1) {
//            return defaultSpaceHight;
//        }
        if (isHeaderFirst && position != getItemCount() - 1) {
            return 0;
        } else {
            return defaultSpaceHight;
        }
    }

    @Override
    public Drawable getFooterDrawable(int position) {
        log("getFooterDrawable: " + position);
        Divider divider = getDividerItem(position);
        if (divider != null) {
            return divider.footerDrawable;
        }
        return null;
    }

    public Drawable getFooterDrawableInner(int position, Pair<Integer, Integer> sectionInfo, SectionPosition sectionPosition) {
        if (sectionPosition != SectionPosition.BOTTOM && sectionPosition != SectionPosition.SINGLE) {
            return null;
        }

        Drawable footerDrawable = getSectionFooterDrawable(sectionInfo.first);
        if (footerDrawable != null) {
            return footerDrawable;
        }

        if (isHeaderFirst) {
            return null;
        } else {
            return defaultSpaceDrawable;
        }
    }

    @Override
    public boolean hasTopDividerVerticalOffset(int position) {
        log("hasTopDividerVerticalOffset: " + position);
        Divider divider = getDividerItem(position);
        if (divider != null) {
            return divider.hasTopDividerVerticalOffset;
        }
        return false;
    }

    public boolean hasTopDividerVerticalOffsetInner(int position, Pair<Integer, Integer> sectionInfo, SectionPosition sectionPosition) {
        log("hasTopDividerVerticalOffsetInner: " + position);
        if (sectionInfo != null) {
            return hasTopDividerVerticalOffset(sectionInfo.first, sectionInfo.second);
        }
        return false;
    }

    @Override
    public boolean hasBottomDividerVerticalOffset(int position) {
        log("hasBottomDividerVerticalOffset: " + position);
        Divider divider = getDividerItem(position);
        if (divider != null) {
            return divider.hasBottomDividerVerticalOffset;
        }
        return false;
    }

    public boolean hasBottomDividerVerticalOffsetInner(int position, Pair<Integer, Integer> sectionInfo, SectionPosition sectionPosition) {
        log("hasBottomDividerVerticalOffsetInner: " + position);
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
    public Drawable getTopDivider(int position) {
        log("getTopDivider: " + position);
        Divider divider = getDividerItem(position);
        if (divider != null) {
            return divider.topDivider;
        }
        return null;
    }

    public final Drawable getTopDividerInner(int position, Pair<Integer, Integer> sectionInfo, SectionPosition sectionPosition, DividerInfo dividerInfo) {
        log("getTopDividerInner: " + position);

        if (!enableDivider()) {
            return null;
        }

        if (!showTopDivider(sectionInfo.first, sectionInfo.second)) {
            return null;
        }

        // Calls the method override by subclasses
        // to get the top divider.
        // If the subclasses do not return a divider,
        // try to get divider from dividerInfo.
        // Finally, if we still not get a divider,
        // set defaultSectionDivider as top divider
        // for the first item of the section.
        Drawable topDivider = getTopDivider(sectionInfo.first, sectionInfo.second);
        if (topDivider != null) {
            return topDivider;
        }

        if (dividerInfo != null) {

            DividerInfo.DividerStyle dividerStyle = dividerInfo.getStyle();
            if (dividerStyle == DividerInfo.DividerStyle.NONE) {
                return null;
            }
            if (hasTopDivider(sectionPosition, dividerStyle)) {
                Drawable dividerDrawable = null;

                if (dividerStyle == DividerInfo.DividerStyle.TOP || dividerStyle == DividerInfo.DividerStyle.SINGLE
                        || (dividerStyle == DividerInfo.DividerStyle.AUTO && (sectionPosition == SectionPosition.TOP || sectionPosition == SectionPosition.SINGLE))) {
                    dividerDrawable = dividerInfo.getTopDividerDrawable();
                    if (dividerDrawable != null) {
                        return dividerDrawable;
                    }
                    return defaultSectionDivider;
                } else {
                    dividerDrawable = dividerInfo.getMiddleDividerDrawable();
                    if (dividerDrawable != null) {
                        return dividerDrawable;
                    }

                    return defaultDivider;
                }

            }

        }

        if (sectionPosition == SectionPosition.TOP || sectionPosition == SectionPosition.SINGLE) {
            return defaultSectionDivider;
        }
        return null;
    }

    private boolean hasTopDivider(SectionPosition sectionPosition, DividerInfo.DividerStyle dividerStyle) {
        return (dividerStyle == DividerInfo.DividerStyle.AUTO
                && (sectionPosition == SectionPosition.TOP
                || sectionPosition == SectionPosition.SINGLE))
                || dividerStyle == DividerInfo.DividerStyle.TOP
                || dividerStyle == DividerInfo.DividerStyle.SINGLE;
    }

    protected boolean hasTopDividerOffset(SectionPosition sectionPosition, DividerInfo.DividerStyle dividerStyle) {
        return false;
    }

    private boolean hasBottomDivider(SectionPosition sectionPosition, DividerInfo.DividerStyle dividerStyle) {
        return dividerStyle != DividerInfo.DividerStyle.NONE;
    }

    protected boolean hasBottomDividerOffset(SectionPosition sectionPosition, DividerInfo.DividerStyle dividerStyle) {
        return (dividerStyle == DividerInfo.DividerStyle.AUTO
                && (sectionPosition == SectionPosition.TOP
                || sectionPosition == SectionPosition.MIDDLE))
                || dividerStyle == DividerInfo.DividerStyle.TOP
                || dividerStyle == DividerInfo.DividerStyle.MIDDLE;
    }

    @Override
    public Drawable getBottomDivider(int position) {
        log("getBottomDivider: " + position);
        Divider divider = getDividerItem(position);
        if (divider != null) {
            return divider.bottomDivider;
        }
        return null;
    }

    public final Drawable getBottomDividerInner(int position, Pair<Integer, Integer> sectionInfo, SectionPosition sectionPosition, DividerInfo dividerInfo) {
        log("getBottomDividerInner: " + position);
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

        if (dividerInfo != null) {
            DividerInfo.DividerStyle dividerStyle = dividerInfo.getStyle();
            if (dividerStyle == DividerInfo.DividerStyle.NONE) {
                return null;
            }
            if (hasBottomDivider(sectionPosition, dividerStyle)) {
                if (dividerStyle == DividerInfo.DividerStyle.BOTTOM || dividerStyle == DividerInfo.DividerStyle.SINGLE
                        || (dividerStyle == DividerInfo.DividerStyle.AUTO && (sectionPosition == SectionPosition.BOTTOM || sectionPosition == SectionPosition.SINGLE))) {
                    Drawable drawable = dividerInfo.getBottomDividerDrawable();
                    if (drawable != null) {
                        return drawable;
                    }
                    return defaultSectionDivider;
                } else {
                    Drawable drawable = dividerInfo.getMiddleDividerDrawable();
                    if (drawable != null) {
                        return drawable;
                    }
                    return defaultDivider;
                }
            }
        }

        // If the subclasses do not return a divider,
        // set defaultSectionDivider as bottom divider
        // for the last item of the section,
        // and set defaultDivider for the other items
        // as the bottom divider.
        if (sectionPosition == SectionPosition.BOTTOM || sectionPosition == SectionPosition.SINGLE) {
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
    public Rect topDividerOffset(int position) {
        log("topDividerOffset: " + position);
        Divider divider = getDividerItem(position);
        if (divider != null) {
            return divider.topDividerOffset;
        }
        return null;
    }

    public final Rect topDividerOffsetInner(int position, Pair<Integer, Integer> sectionInfo, SectionPosition sectionPosition, DividerInfo dividerInfo) {
        log("topDividerOffsetInner: " + position);

        Rect topOffset = topDividerOffset(sectionInfo.first, sectionInfo.second);
        if (topOffset != null) {
            return topOffset;
        }

        Rect offset = new Rect();
        if (dividerInfo != null) {
            DividerInfo.DividerStyle dividerStyle = dividerInfo.getStyle();
            if (hasTopDividerOffset(sectionPosition, dividerStyle)) {
                boolean leftChanged = false;

                int leftOffset = dividerInfo.getLeftOffset();
                if (leftOffset >= 0) {
                    offset.left = leftOffset;
                    leftChanged = true;
                }

                boolean rightChanged = false;
                int rightOffset = dividerInfo.getRightOffset();
                if (rightOffset >= 0) {
                    offset.right = rightOffset;
                    rightChanged = true;
                }
                if (!leftChanged) {
                    offset.left = (int) defaultOffset;
                }
                if (!rightChanged) {
                    offset.right = (int) defaultRightOffset;
                }
            }
        }

        return offset;
    }

    @Override
    public Rect bottomDividerOffset(int position) {
        log("bottomDividerOffset: " + position);
        Divider divider = getDividerItem(position);
        if (divider != null) {
            return divider.bottomDividerOffset;
        }
        return null;
    }

    public final Rect bottomDividerOffsetInner(int position, Pair<Integer, Integer> sectionInfo, SectionPosition sectionPosition, DividerInfo dividerInfo) {

        log("bottomDividerOffsetInner: " + position);

        Rect bottomOffset = bottomDividerOffset(sectionInfo.first, sectionInfo.second);
        if (bottomOffset != null) {
            return bottomOffset;
        }

        Rect offset = new Rect();
        if (dividerInfo != null) {
            DividerInfo.DividerStyle dividerStyle = dividerInfo.getStyle();
            if (hasBottomDividerOffset(sectionPosition, dividerStyle)) {

                boolean leftChanged = false;
                boolean rightChanged = false;

                int leftOffset = dividerInfo.getLeftOffset();
                if (leftOffset >= 0) {
                    offset.left = leftOffset;
                    leftChanged = true;
                }
                int rightOffset = dividerInfo.getRightOffset();
                if (rightOffset >= 0) {
                    offset.right = rightOffset;
                    rightChanged = true;
                }

                if (!leftChanged) {
                    offset.left = (int) defaultOffset;
                }
                if (!rightChanged) {
                    offset.right = (int) defaultRightOffset;
                }
            }
        } else {
            if (sectionPosition == SectionPosition.MIDDLE || sectionPosition == SectionPosition.TOP) {
                offset.left = (int) defaultOffset;
                offset.right = (int) defaultRightOffset;
            }
        }

        return offset;
    }

    public void setDefaultSectionDivider(Drawable defaultSectionDivider) {
        this.defaultSectionDivider = defaultSectionDivider;
    }

    public void setDefaultDivider(Drawable defaultDivider) {
        this.defaultDivider = defaultDivider;
    }

    public void setDefaultOffset(float defaultLeftOffset) {
        this.defaultOffset = defaultLeftOffset;
    }

    public void setDefaultRightOffset(float defaultRightOffset) {
        this.defaultRightOffset = defaultRightOffset;
    }

    public void setDefaultSpaceHight(float defaultSpaceHight) {
        this.defaultSpaceHight = defaultSpaceHight;
    }

    public void setDefaultSpaceDrawable(Drawable defaultSpaceDrawable) {
        this.defaultSpaceDrawable = defaultSpaceDrawable;
    }

    private void log(String format, String... args) {
        if (DEBUG) {
            Log.d(TAG, String.format(format, (Object[]) args));
        }
    }

    private enum SectionPosition {
        UNKNOWN, TOP, MIDDLE, BOTTOM, SINGLE;
    }

    protected static class Divider {

        public float headerHeight;
        public Drawable headerDrawable;

        public float footerHeight;
        public Drawable footerDrawable;

        public boolean hasTopDividerVerticalOffset;
        public boolean hasBottomDividerVerticalOffset;

        public Drawable topDivider;
        public Rect topDividerOffset;

        public Drawable bottomDivider;
        public Rect bottomDividerOffset;

        @Override
        public String toString() {
            return "Divider{" +
                    "headerGapHeight=" + headerHeight +
                    ", headerGapDrawable=" + headerDrawable +
                    ", footerGapHeight=" + footerHeight +
                    ", footerGapDrawable=" + footerDrawable +
                    ", hasTopDividerVerticalOffset=" + hasTopDividerVerticalOffset +
                    ", hasBottomDividerVerticalOffset=" + hasBottomDividerVerticalOffset +
                    ", topDivider=" + topDivider +
                    ", topDividerOffset=" + topDividerOffset +
                    ", bottomDivider=" + bottomDivider +
                    ", bottomDividerOffset=" + bottomDividerOffset +
                    '}';
        }
    }

    public class Observer extends RecyclerView.AdapterDataObserver {

        public void onChanged() {
            updateDividerList();
        }

        public void onItemRangeChanged(int positionStart, int itemCount) {
            updateDividerList();
        }

        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
        }

        public void onItemRangeInserted(int positionStart, int itemCount) {
            updateDividerList();
        }

        public void onItemRangeRemoved(int positionStart, int itemCount) {
            updateDividerList();
        }

        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            updateDividerList();
        }
    }
}
