package com.dianping.shield.utils;

/**
 * Created by zhi.he on 2018/7/18.
 */

import java.util.List;

/**
 * A {@link List} that notifies when changes are made. An ObservableList bound to the UI
 * will keep the it up-to-date when changes occur.
 * <p>
 * The ObservableList must notify its callbacks whenever a change to the list occurs, using
 * {@link OnListChangedCallback}.
 * <p>
 * ObservableArrayList implements ObservableList with an underlying ArrayList.
 * ListChangeRegistry can help in maintaining the callbacks of other implementations.
 */
public interface ObservableList<T> extends List<T> {

    /**
     * Adds a callback to be notified when changes to the list occur.
     *
     * @param callback The callback to be notified on list changes
     */
    void addOnListChangedCallback(OnListChangedCallback<? extends ObservableList<T>> callback);

    /**
     * Removes a callback previously added.
     *
     * @param callback The callback to remove.
     */
    void removeOnListChangedCallback(OnListChangedCallback<? extends ObservableList<T>> callback);

    /**
     * The callback that is called by ObservableList when the list has changed.
     */
    abstract class OnListChangedCallback<T extends ObservableList> {

        /**
         * Called whenever a change of unknown type has occurred, such as the entire list being
         * set to new values.
         *
         * @param sender The changing list.
         */
        public abstract void onChanged(T sender);

        /**
         * Called whenever one or more items in the list have changed.
         *
         * @param sender        The changing list.
         * @param positionStart The starting index that has changed.
         * @param itemCount     The number of items that have changed.
         * @param oldItems      The old items that have been changed.
         */
        public abstract void onItemRangeChanged(T sender, int positionStart, int itemCount, List oldItems);

        /**
         * Called whenever items have been inserted into the list.
         *
         * @param sender        The changing list.
         * @param positionStart The insertion index
         * @param itemCount     The number of items that have been inserted
         */
        public abstract void onItemRangeInserted(T sender, int positionStart, int itemCount);

        /**
         * Called whenever items in the list have been moved.
         *
         * @param sender       The changing list.
         * @param fromPosition The position from which the items were moved
         * @param toPosition   The destination position of the items
         * @param itemCount    The number of items moved
         */
        public abstract void onItemRangeMoved(T sender, int fromPosition, int toPosition,
                                              int itemCount);

        /**
         * Called whenever items in the list have been deleted.
         *
         * @param sender        The changing list.
         * @param positionStart The starting index of the deleted items.
         * @param itemCount     The number of items removed.
         * @param oldItems      The old items that have been removed.
         */
        public abstract void onItemRangeRemoved(T sender, int positionStart, int itemCount, List oldItems);


        /**
         * Called  whenever items in the list have been replaced with count may changed.
         *
         * @param sender       The changing list.
         * @param fromPosition The starting index of the items will be replaced .
         * @param newItemCount The number of items add.
         * @param oldItemCount The number of items removed.
         * @param oldItems     The old items that have been removed.
         */
        public abstract void onItemRangeReplaced(T sender, int fromPosition, int newItemCount, int oldItemCount, List oldItems);

    }
}
