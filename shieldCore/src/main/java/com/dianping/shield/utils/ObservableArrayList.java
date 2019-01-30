package com.dianping.shield.utils;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by zhi.he on 2018/7/18.
 */

public class ObservableArrayList<T> extends RangeRemoveableArrayList<T> implements ObservableList<T> {
    private transient ListChangeRegistry mListeners = new ListChangeRegistry();

    public ObservableArrayList(int initialCapacity) {
        super(initialCapacity);
    }

    public ObservableArrayList() {
    }

    public ObservableArrayList(@NonNull Collection<? extends T> c) {
        super(c);
    }

    @Override
    public void addOnListChangedCallback(OnListChangedCallback listener) {
        if (mListeners == null) {
            mListeners = new ListChangeRegistry();
        }
        mListeners.add(listener);
    }

    @Override
    public void removeOnListChangedCallback(OnListChangedCallback listener) {
        if (mListeners != null) {
            mListeners.remove(listener);
        }
    }

    @Override
    public boolean add(T object) {
        super.add(object);
        notifyAdd(size() - 1, 1);
        return true;
    }

    @Override
    public void add(int index, T object) {
        super.add(index, object);
        notifyAdd(index, 1);
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        int oldSize = size();
        boolean added = super.addAll(collection);
        if (added) {
            notifyAdd(oldSize, size() - oldSize);
        }
        return added;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> collection) {
        boolean added = super.addAll(index, collection);
        if (added) {
            notifyAdd(index, collection.size());
        }
        return added;
    }

    @Override
    public void clear() {
        int oldSize = size();
        ArrayList<T> oldItems = (ArrayList<T>) this.clone();
        super.clear();
        if (oldSize != 0) {
            notifyRemove(0, oldSize, oldItems);
        }
    }

    @Override
    public T remove(int index) {
//        notifyBeforeRemove(index, 1);
        T val = super.remove(index);
        ArrayList<T> oldItems = new ArrayList<>();
        oldItems.add(val);
        notifyRemove(index, 1, oldItems);
        return val;
    }

    @Override
    public boolean remove(Object object) {
        int index = indexOf(object);
        if (index >= 0) {
            remove(index);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public T set(int index, T object) {
        T val = super.set(index, object);
        ArrayList<T> oldItems = new ArrayList<>();
        oldItems.add(val);
        if (mListeners != null) {
            mListeners.notifyChanged(this, index, 1, oldItems);
        }
        return val;
    }

    public void setAll(int startindex, ArrayList<? extends T> collection) {

        ArrayList<T> oldItems = new ArrayList<>();
        for (int setIndex = 0; setIndex < collection.size(); setIndex++) {
            T val = super.set(startindex + setIndex, collection.get(setIndex));
            oldItems.add(val);
        }

        if (mListeners != null) {
            mListeners.notifyChanged(this, startindex, collection.size(), oldItems);
        }
        return;
    }

    @Override
    public void removeRange(int fromIndex, int toIndex) {
        ArrayList<T> oldItems = new ArrayList<>();
        oldItems.addAll(subList(fromIndex, toIndex));
        super.removeRange(fromIndex, toIndex);
        notifyRemove(fromIndex, toIndex - fromIndex, oldItems);
    }

    public void replaceWithRemoveAndInsert(int fromIndex, int toIndex, @NonNull Collection<? extends T> collection) {
        ArrayList<T> oldItems = new ArrayList<>();
        oldItems.addAll(subList(fromIndex, toIndex));
        super.removeRange(fromIndex, toIndex);
        boolean added = super.addAll(fromIndex, collection);
        if (added||(toIndex-fromIndex>0)) {
            notifyReplace(fromIndex, collection.size(), toIndex - fromIndex, oldItems);
        }
    }


    private void notifyAdd(int start, int count) {
        if (mListeners != null) {
            mListeners.notifyInserted(this, start, count);
        }
    }

    private void notifyRemove(int start, int count, List oldItems) {
        if (mListeners != null) {
            mListeners.notifyRemoved(this, start, count, oldItems);
        }
    }

    private void notifyReplace(int start, int count, int oldCount, List oldItems) {
        if (mListeners != null) {
            mListeners.notifyReplaced(this, start, count, oldCount, oldItems);
        }
    }
}

