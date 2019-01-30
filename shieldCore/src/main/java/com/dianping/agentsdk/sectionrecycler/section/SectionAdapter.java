package com.dianping.agentsdk.sectionrecycler.section;

import android.support.v7.widget.RecyclerView;
import android.util.Pair;

import java.util.List;

/**
 * <p>
 * SectionAdapter, a {@link RecyclerView.Adapter} subclass which hold a two level position info.
 * Here, an Anapter contains serveral sections and a section contains several rows.
 * </p>
 * <p>
 * This class overrides the {@link RecyclerView.Adapter}'s methods
 * and add their section version for the subclasses to override.
 * </p>
 * <p>
 * Created by runqi.wei
 * 12:01
 * 20.06.2016.
 */
public abstract class SectionAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    public static final long NO_ID = -1;

    protected boolean isOnBind;

    protected RecyclerView recyclerView;

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = null;
        super.onDetachedFromRecyclerView(recyclerView);
    }

    /**
     * Deprecated for SectionAdapter
     * Use onBindViewHolder(VH holder, int section, int position) instead.
     *
     * @param holder
     * @param position
     * @param payloads
     */
    @Override
    @Deprecated
    public final void onBindViewHolder(VH holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }


    /**
     * Accacisable for SectionAdapter.
     * For the subclasses of {@link SectionAdapter},
     * override {@link SectionAdapter#getItemViewType(int section, int position)} instead.
     */
    @Override
    public final int getItemViewType(int position) {

        Pair<Integer, Integer> sectionInfo = getSectionIndex(position);
        if (sectionInfo != null) {
            return getItemViewType(sectionInfo.first, sectionInfo.second);
        }

        return 0;
    }

    /**
     * Accacisable for SectionAdapter
     * For the subclasses of {@link SectionAdapter},
     * override {@link SectionAdapter#onBindViewHolder(VH holder, int section, int position)} instead.
     */
    @Override
    public void onBindViewHolder(VH holder, int position) {
        Pair<Integer, Integer> sectionInfo = getSectionIndex(position);
        if (sectionInfo != null) {
            isOnBind = true;
            onBindViewHolder(holder, sectionInfo.first, sectionInfo.second);
            isOnBind = false;
        }
    }

    /**
     * Returns if we are running in the {@link #onBindViewHolder(RecyclerView.ViewHolder, int, int)} method.
     *
     * @return
     */
    public boolean isOnBind() {
        return isOnBind;
    }

    /**
     * Returns the Section position and Row position for a given position
     *
     * @param position the given position of an Item
     * @return Pari &lt;Integer, Integer&gt; a Pair&lt;section position, row position&gt;
     */
    public Pair<Integer, Integer> getSectionIndex(int position) {

        int sectionCount = getSectionCount();
        int p = position;
        if (sectionCount > 0) {
            for (int i = 0; i < sectionCount; i++) {
                int items = getRowCount(i);
                if (p >= items) {
                    p -= items;
                } else {
                    return new Pair<>(i, p);
                }
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public final int getItemCount() {
        int sectionCount = getSectionCount();
        if (sectionCount <= 0) {
            return 0;
        }

        int count = 0;
        for (int i = 0; i < sectionCount; i++) {
            count += getRowCount(i);
        }

        return count;
    }

    /**
     * Accacisable for SectionAdapter
     * For the subclasses of {@link SectionAdapter},
     * override {@link SectionAdapter#getItemId(int section, int position)} instead.
     */
    @Override
    public final long getItemId(int position) {
        Pair<Integer, Integer> sectionInfo = getSectionIndex(position);
        if (sectionInfo != null) {
            return getItemId(sectionInfo.first, sectionInfo.second);
        }
        return position;
    }

    /**
     * Return the stable ID for the item at <code>section, position</code>.
     * If {@link #hasStableIds()} would return false this method should return {@link #NO_ID}.
     * If {@link #hasStableIds()} would return true this method should return impossible repeat id.
     * The default implementation of this method returns position of item.
     *
     * @param section  Adapter section to query
     * @param position Section position to query
     * @return the stable ID of the item at position
     */
    public long getItemId(int section, int position) {
        long id = 0;
        for (int i = 0; i < getSectionCount(); i++) {
            if (i < section) {
                for (int j = 0; j < getRowCount(i); j++) {
                    id++;
                }
            } else if (i == section) {
                for (int j = 0; j < getRowCount(i); j++) {
                    if (j < position) {
                        id++;
                    }
                }
            }

        }
        return id;
    }

    /**
     * Returns the total number of sections in the data set hold by the adapter.
     *
     * @return The total number of sections in this adapter.
     */
    public abstract int getSectionCount();

    /**
     * Returns the total number of items of the given section in the data set hold by the adapter.
     *
     * @param sectionIndex The index of the section to query.
     * @return The total number of items in the given section of this adapter.
     */
    public abstract int getRowCount(int sectionIndex);

    /**
     * Return the view type of the item at <code>section, position</code> for the purposes
     * of view recycling.
     * <p>
     * <p>The default implementation of this method returns 0, making the assumption of
     * a single view type for the adapter. Unlike ListView adapters, types need not
     * be contiguous. Consider using id resources to uniquely identify item view types.
     *
     * @param sectionIndex section to query
     * @param position     position to query
     * @return integer value identifying the type of the view needed to represent the item at
     * <code>section, position</code>. Type codes need not be contiguous.
     */
    public int getItemViewType(int sectionIndex, int position) {
        return 0;
    }

    /**
     * Called by SectionAdapter when RecyclerView calls the {@link #onBindViewHolder(RecyclerView.ViewHolder, int)}
     * method to display the data at the specified position.
     * {@link android.support.v7.widget.RecyclerView.Adapter#onBindViewHolder(RecyclerView.ViewHolder, int)}
     */
    public abstract void onBindViewHolder(VH holder, int sectionIndex, int position);
}