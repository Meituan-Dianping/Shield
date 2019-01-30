package com.dianping.shield.utils;

import android.support.annotation.NonNull;
import android.util.SparseArray;

import java.util.Collection;
import java.util.HashMap;

/**
 * Created by runqi.wei at 2018/6/21
 */
public class IndexMap<T> {

    private int autoIndex = 0;
    private SparseArray<T> index2Value = new SparseArray<>();
    private HashMap<T, Integer> value2Index = new HashMap<>();

    public int getIndex(@NonNull T value) {
        Integer index = value2Index.get(value);
        if (index == null) {
            return -1;
        }

        return index;
    }

    public T getValue(int index) {
        return index2Value.get(index);
    }

    public void putValue(@NonNull T value) {
        if (!value2Index.containsKey(value)) {
            value2Index.put(value, autoIndex);
            index2Value.put(autoIndex, value);
            autoIndex++;
        }
    }

    public boolean containsValue(T value) {
        return value2Index.containsKey(value);
    }

    public void putValue(Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            return;
        }
        for (T value : collection) {
            if (value == null) {
                continue;
            }

            putValue(value);
        }
    }

    public void removeValue(T value) {
        Integer index = value2Index.remove(value);
        if (index != null) {
            index2Value.remove(index);
        }
    }

    public void removeValue(Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            return;
        }

        for (T value : collection) {
            if (value == null) {
                continue;
            }
            removeValue(value);
        }
    }

    public void clear() {
        value2Index.clear();
        index2Value.clear();
        autoIndex = 0;
    }
}
