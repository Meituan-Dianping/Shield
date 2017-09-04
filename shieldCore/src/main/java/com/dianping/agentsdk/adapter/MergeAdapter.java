package com.dianping.agentsdk.adapter;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

import java.util.ArrayList;

public class MergeAdapter extends BaseAdapter {
    DataSetObserver observer = new DataSetObserver() {
        @Override
        public void onChanged() {
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            notifyDataSetInvalidated();
        }
    };
    protected ArrayList<ListAdapter> pieces = new ArrayList<ListAdapter>();

    /**
     * Stock constructor, simply chaining to the superclass.
     */
    public MergeAdapter() {
        super();
    }

    /**
     * Adds a new adapter to the roster of things to appear in the aggregate
     * list.
     *
     * @param adapter Source for row views for this section
     */
    public void addAdapter(ListAdapter adapter) {
        pieces.add(adapter);
        try {
            adapter.registerDataSetObserver(observer);
        } catch (Exception e) {

        }
        notifyDataSetChanged();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want
     */
    @Override
    public Object getItem(int position) {
        for (ListAdapter piece : pieces) {
            int size = piece.getCount();

            if (position < size) {
                return (piece.getItem(position));
            }

            position -= size;
        }

        return (null);
    }

    /**
     * How many items are in the data set represented by this Adapter.
     */
    @Override
    public int getCount() {
        int total = 0;

        for (ListAdapter piece : pieces) {
            total += piece.getCount();
        }

        return (total);
    }

    /**
     * Returns the number of types of Views that will be created by getView().
     */
    @Override
    public int getViewTypeCount() {
        int total = 0;

        for (ListAdapter piece : pieces) {
            total += piece.getViewTypeCount();
        }

        return (Math.max(total, 1)); // needed for setListAdapter() before
        // content add'
    }

    /**
     * Get the type of View that will be created by getView() for the specified
     * item.
     *
     * @param position Position of the item whose data we want
     */
    @Override
    public int getItemViewType(int position) {
        int typeOffset = 0;
        int result = -1;

        for (ListAdapter piece : pieces) {
            int size = piece.getCount();

            if (position < size) {
                result = typeOffset + piece.getItemViewType(position);
                break;
            }

            position -= size;
            typeOffset += piece.getViewTypeCount();
        }

        return (result);
    }

    /**
     * Are all items in this ListAdapter enabled? If yes it means all items are
     * selectable and clickable.
     */
    @Override
    public boolean areAllItemsEnabled() {
        return (false);
    }

    /**
     * Returns true if the item at the specified position is not a separator.
     *
     * @param position Position of the item whose data we want
     */
    @Override
    public boolean isEnabled(int position) {
        for (ListAdapter piece : pieces) {
            int size = piece.getCount();

            if (position < size) {
                return (piece.isEnabled(position));
            }

            position -= size;
        }

        return (false);
    }

    /**
     * Get a View that displays the data at the specified position in the data
     * set.
     *
     * @param position    Position of the item whose data we want
     * @param convertView View to recycle, if not null
     * @param parent      ViewGroup containing the returned View
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        for (ListAdapter piece : pieces) {
            int size = piece.getCount();

            if (position < size) {

                return (piece.getView(position, convertView, parent));
            }

            position -= size;
        }

        return (null);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position Position of the item whose data we want
     */
    @Override
    public long getItemId(int position) {
        for (ListAdapter piece : pieces) {
            int size = piece.getCount();

            if (position < size) {
                return (piece.getItemId(position));
            }

            position -= size;
        }

        return (-1);
    }

    /**
     * remove all adapters that has added.
     */
    public void clear() {
        if (pieces != null && pieces.size() > 0) {
            pieces.clear();
        }
    }

    public ArrayList<ListAdapter> getPieces() {
        return pieces;
    }
}
