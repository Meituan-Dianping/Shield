package com.dianping.shield.utils;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by zhi.he on 2018/7/25.
 */

public class RangeRemoveableArrayList<T> extends ArrayList<T> {
    public RangeRemoveableArrayList(int initialCapacity) {
        super(initialCapacity);
    }

    public RangeRemoveableArrayList() {
    }

    public RangeRemoveableArrayList(@NonNull Collection<? extends T> c) {
        super(c);
    }

    @Override

    public void removeRange(int fromIndex, int toIndex) {
        super.removeRange(fromIndex, toIndex);
    }
}
