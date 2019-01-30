package com.dianping.shield.node.adapter.hotzone;

import android.support.annotation.NonNull;
import android.util.SparseArray;

import java.util.ArrayList;

/**
 * Created by runqi.wei at 2018/9/7
 */
public class ArrayDiffUtils {

    public static <T> Result<T> diffForResult(ArrayList<T> oldList, int oldStartPosition, ArrayList<T> newList, int newStartPosition) {
        return diffForResult(oldList, oldStartPosition, newList, newStartPosition, null);
    }

    public static <T> Result<T> diffForResult(ArrayList<T> oldList, int oldStartPosition, ArrayList<T> newList, int newStartPosition, Comparator<T> comparator) {
        Result<T> result = new Result<>();
        diffForProcess(oldList, oldStartPosition, newList, newStartPosition, new ResultProcessor<>(result), comparator);
        return result;
    }

    public static <T> void diffForProcess(ArrayList<T> oldList, int oldStartPosition, ArrayList<T> newList, int newStartPosition, @NonNull Processor<T> processor) {
        diffForProcess(oldList, oldStartPosition, newList, newStartPosition, processor, null);
    }

    public static <T> void diffForProcess(ArrayList<T> oldList, int oldStartPosition, ArrayList<T> newList, int newStartPosition, @NonNull Processor<T> processor, Comparator<T> comparator) {

        int oldCount = (oldList != null) ? oldList.size() : 0;
        int newCount = (newList != null) ? newList.size() : 0;

        int i = 0;
        int j = 0;
        while (i < oldCount && j < newCount) {
            int oldIndex = i + oldStartPosition;
            int newIndex = j + newStartPosition;

            if (oldIndex < newIndex) {
                processor.processDeletedItem(oldIndex, oldList.get(i));
                i++;
            } else if (oldIndex > newIndex) {
                processor.processAddedItem(newIndex, newList.get(j));
                j++;
            } else {
                T oldItem = oldList.get(i);
                T newItem = newList.get(j);
                boolean same;
                if (comparator != null) {
                    same = comparator.equals(oldItem, newItem);
                } else {
                    same = (oldItem == newItem) || (oldItem != null && oldItem.equals(newItem));
                }

                if (!same) {
                    processor.processDeletedItem(oldIndex, oldItem);
                    processor.processAddedItem(newIndex, newItem);
                } else {
                    processor.processUnchangedList(newIndex, newItem);
                }
                i++;
                j++;
            }
        }

        if (i < oldCount) {
            for (; i < oldCount; i++) {
                processor.processDeletedItem(i + oldStartPosition, oldList.get(i));
            }
        }

        if (j < newCount) {
            for (; j < newCount; j++) {
                processor.processAddedItem(j + newStartPosition, newList.get(j));
            }
        }
    }

    public interface Comparator<T> {

        /**
         * Compare two items, return whether they are the same
         *
         * @param o1 item 1
         * @param o2 item 2
         * @return {@code true} when o1 and o2 are the same, else {@code false}
         */
        boolean equals(T o1, T o2);
    }

    public interface Processor<T> {

        void processDeletedItem(int index, T item);

        void processAddedItem(int index, T item);

        void processUnchangedList(int index, T item);
    }

    public static class Result<T> {

        public SparseArray<T> addedList = new SparseArray<>();
        public SparseArray<T> deletedList = new SparseArray<>();
        public SparseArray<T> unchangedList = new SparseArray<>();
    }

    private static class ResultProcessor<T> implements Processor<T> {

        Result<T> result;

        public ResultProcessor(Result<T> result) {
            this.result = result;
        }

        @Override
        public void processDeletedItem(int index, T item) {
            if (result != null) {
                result.deletedList.put(index, item);
            }
        }

        @Override
        public void processAddedItem(int index, T item) {
            if (result != null) {
                result.addedList.put(index, item);
            }
        }

        @Override
        public void processUnchangedList(int index, T item) {
            if (result != null) {
                result.unchangedList.put(index, item);
            }
        }
    }
}
