package com.dianping.shield.utils;

/**
 * Created by zhi.he on 2018/7/18.
 */

import android.support.v4.util.Pools;

import java.util.List;

/**
 * Utility class for managing ObservableList callbacks.
 */
public class ListChangeRegistry
        extends
        CallbackRegistry<ObservableList.OnListChangedCallback, ObservableList,
                ListChangeRegistry.ListChanges> {
    private static final Pools.SynchronizedPool<ListChanges> sListChanges =
            new Pools.SynchronizedPool<ListChanges>(10);

    private static final int ALL = 0;
    private static final int CHANGED = 1;
    private static final int INSERTED = 2;
    private static final int MOVED = 3;
    private static final int REMOVED = 4;
    private static final int REPLACED = 5;

    private static final CallbackRegistry.NotifierCallback<ObservableList.OnListChangedCallback,
            ObservableList, ListChanges> NOTIFIER_CALLBACK = new CallbackRegistry.NotifierCallback<
            ObservableList.OnListChangedCallback, ObservableList, ListChanges>() {
        @Override
        public void onNotifyCallback(ObservableList.OnListChangedCallback callback,
                                     ObservableList sender, int notificationType, ListChanges listChanges) {
            switch (notificationType) {
                case CHANGED:
                    callback.onItemRangeChanged(sender, listChanges.start, listChanges.count, listChanges.oldItems);
                    break;
                case INSERTED:
                    callback.onItemRangeInserted(sender, listChanges.start, listChanges.count);
                    break;
                case MOVED:
                    callback.onItemRangeMoved(sender, listChanges.start, listChanges.to,
                            listChanges.count);
                    break;
                case REMOVED:
                    callback.onItemRangeRemoved(sender, listChanges.start, listChanges.count, listChanges.oldItems);
                    break;
                case REPLACED:
                    callback.onItemRangeReplaced(sender, listChanges.start, listChanges.count, listChanges.oldCount, listChanges.oldItems);
                    break;
                default:
                    callback.onChanged(sender);
                    break;
            }
        }
    };

    public ListChangeRegistry() {
        super(NOTIFIER_CALLBACK);
    }

    private static ListChanges acquire(int start, int to, int count, int oldcount, List oldItems) {
        ListChanges listChanges = sListChanges.acquire();
        if (listChanges == null) {
            listChanges = new ListChanges();
        }
        listChanges.start = start;
        listChanges.to = to;
        listChanges.count = count;
        listChanges.oldCount = oldcount;
        listChanges.oldItems = oldItems;
        return listChanges;
    }

    /**
     * Notify registered callbacks that there was an unknown or whole-list change.
     *
     * @param list The list that changed.
     */
    public void notifyChanged(ObservableList list) {
        notifyCallbacks(list, ALL, null);
    }

    /**
     * Notify registered callbacks that some elements have changed.
     *
     * @param list  The list that changed.
     * @param start The index of the first changed element.
     * @param count The number of changed elements.
     */
    public void notifyChanged(ObservableList list, int start, int count, List oldItems) {
        ListChanges listChanges = acquire(start, 0, count, 0, oldItems);
        notifyCallbacks(list, CHANGED, listChanges);
    }

    /**
     * Notify registered callbacks that elements were inserted.
     *
     * @param list  The list that changed.
     * @param start The index where the elements were inserted.
     * @param count The number of elements that were inserted.
     */
    public void notifyInserted(ObservableList list, int start, int count) {
        ListChanges listChanges = acquire(start, 0, count, 0, null);
        notifyCallbacks(list, INSERTED, listChanges);
    }

    /**
     * Notify registered callbacks that elements were moved.
     *
     * @param list  The list that changed.
     * @param from  The index of the first element moved.
     * @param to    The index of where the element was moved to.
     * @param count The number of elements moved.
     */
    public void notifyMoved(ObservableList list, int from, int to, int count) {
        ListChanges listChanges = acquire(from, to, count, 0, null);
        notifyCallbacks(list, MOVED, listChanges);
    }

    /**
     * Notify registered callbacks that elements were deleted.
     *
     * @param list  The list that changed.
     * @param start The index of the first element to be removed.
     * @param count The number of elements removed.
     */
    public void notifyRemoved(ObservableList list, int start, int count, List oldItems) {
        ListChanges listChanges = acquire(start, 0, count, 0, oldItems);
        notifyCallbacks(list, REMOVED, listChanges);
    }

    /**
     * Notify registered callbacks that elements were deleted.
     *
     * @param list  The list that changed.
     * @param start The index of the first element to be removed.
     * @param count The number of elements removed.
     */
    public void notifyReplaced(ObservableList list, int start, int count, int oldCount, List oldItems) {
        ListChanges listChanges = acquire(start, 0, count, oldCount, oldItems);
        notifyCallbacks(list, REPLACED, listChanges);
    }


    @Override
    public synchronized void notifyCallbacks(ObservableList sender, int notificationType,
                                             ListChanges listChanges) {
        super.notifyCallbacks(sender, notificationType, listChanges);
        if (listChanges != null) {
            sListChanges.release(listChanges);
        }
    }

    static class ListChanges {
        public int start;
        public int count;
        public int to;
        public int oldCount;
        public List oldItems;
    }
}

