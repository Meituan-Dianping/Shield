package com.dianping.shield.node.adapter.hotzone;

import android.support.annotation.NonNull;

import java.util.Stack;

/**
 * Created by runqi.wei at 2018/9/5
 */
public class ItemRecyclePool<T> {

    private Stack<T> infoStack = new Stack<>();

    @NonNull
    private ItemCreator<T> itemCreator;

    public ItemRecyclePool(@NonNull ItemCreator<T> itemCreator) {
        this.itemCreator = itemCreator;
    }

    void recyclerItem(T item) {
        infoStack.push(item);
    }

    T getItem() {
        if (infoStack.isEmpty()) {
            return itemCreator.createItem();
        }

        return infoStack.pop();
    }

    public interface ItemCreator<T>{

        T createItem();
    }
}
