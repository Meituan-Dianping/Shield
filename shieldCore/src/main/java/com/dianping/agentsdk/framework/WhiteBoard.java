package com.dianping.agentsdk.framework;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

public class WhiteBoard {

    /**
     * The data set, a Bundle.
     */
    protected WhiteBoardDataStore mData;

    /**
     * An Observable Map.
     * (Here we actually save Subjects, which give us the ability to emit new changes to the Subscribers. )
     */
    protected HashMap<String, Subject> subjectMap;

    protected WhiteBoardMessageManager messageManager;

    /**
     * Default Constructor.
     */
    public WhiteBoard() {
        this(null);
    }

    /**
     * Constructor.
     *
     * @param data
     */
    public WhiteBoard(Bundle data) {
        this(data, true);
    }

    public WhiteBoard(Bundle data, boolean needPersist) {
        mData = new WhiteBoardDataStore(data, needPersist);
        subjectMap = new HashMap<>();
        messageManager = new WhiteBoardMessageManager();
    }

    /**
     * * Life cycle method *
     * Called to initial the WhiteBoard from the savedInstanceState.
     *
     * @param savedInstanceState
     */
    public void onCreate(Bundle savedInstanceState) {

        if (mData == null) {
            mData = new WhiteBoardDataStore();
        }
        mData.onCreate(savedInstanceState);

        if (messageManager == null) {
            messageManager = new WhiteBoardMessageManager();
        }

        messageManager.onCreate();
    }

    /**
     * * Life cycle method *
     * Called to save the data set to the input parameter.
     *
     * @param outState
     */
    public void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            mData.onSaveInstanceState(outState);
        }
    }

    /**
     * * Life cycle method *
     * Called to destory the WhiteBoard.
     */
    public void onDestory() {
        subjectMap.clear();
        mData.onDestroy();
        messageManager.onDestroy();
    }

    public void clear() {
        subjectMap.clear();
        mData.clear();
    }

    /**
     * For Test
     */
    public WhiteBoardDataStore getData() {
        return mData;
    }

    /**
     * For Test
     */
    WhiteBoardMessageManager getMessageManager() {
        return messageManager;
    }

    public ArrayList<Object> queryMessage(@NonNull String key, Object parameter) {
        return messageManager.queryMessage(key, parameter);
    }

    public void registerMessageHandler(@NonNull String key, @NonNull MessageHandler handler) {
        messageManager.registerMessageHandler(key, handler);
    }

    public String registerMessageHandlerWithId(@NonNull String key, @NonNull MessageHandler handler) {
        return messageManager.registerMessageHandler(key, handler);
    }

    public void registerMessageHandler(@NonNull String key, @NonNull MessageHandlerWithKey handler) {
        messageManager.registerMessageHandler(key, handler);
    }

    public String registerMessageHandlerWithId(@NonNull String key, @NonNull MessageHandlerWithKey handler) {
        return messageManager.registerMessageHandler(key, handler);
    }

    public void removeMessageHandler(@NonNull String id) {
        messageManager.removeMessageHandler(id);
    }

    public void removeMessageHandler(@NonNull String key, @NonNull MessageHandler handler) {
        messageManager.removeMessageHandler(key, handler);
    }

    public void removeMessageHandler(@NonNull MessageHandler handler) {
        messageManager.removeMessageHandler(handler);
    }

    public void removeMessageHandler(@NonNull String key, @NonNull MessageHandlerWithKey handler) {
        messageManager.removeMessageHandler(key, handler);
    }

    public void removeMessageHandler(@NonNull MessageHandlerWithKey handler) {
        messageManager.removeMessageHandler(handler);
    }

    /**
     * Find the value of the key in the data set, and
     * returns an Observable with the value as its initial state.
     * If the key is not contained in the data set,
     * returns an Observable with null as its initial state.
     *
     * @param key The key to find the value
     * @return an Observable with the value as its initial state if the value is not null,
     * an Observable without initial state if the value is null
     */
    public Observable getObservable(final String key) {

        Subject res = null;
        if (subjectMap.containsKey(key)) {
            res = subjectMap.get(key);
        } else {
            res = PublishSubject.create();
            subjectMap.put(key, res);
        }
        if (getData(key) != null) {
            return res.startWith(getData(key));
        } else {
            return res;
        }
    }

    /**
     * Update the Observable of a data of a given key.
     */
    protected void notifyDataChanged(String key) {
        if (subjectMap.containsKey(key)) {
            subjectMap.get(key).onNext(mData.get(key));
        }
    }

    /**
     * Remove the value of a given key.
     *
     * @param key
     */
    public void removeData(String key) {
        mData.remove(key);
        notifyDataChanged(key);
    }

    /**
     * Get the value of a given key.
     *
     * @param key
     * @return
     */
    public Object getData(String key) {
        return mData.get(key);
    }

    /**
     * Check if data set contains a certain key.
     *
     * @param key
     * @return true if data set contains the key, else false
     */
    public boolean containsKey(String key) {
        return mData.containsKey(key);
    }

    /**
     * Returns a Set containing the Strings used as keys in this Bundle.
     *
     * @return a Set of String keys
     */
    public Set<String> keySet() {
        return mData.keySet();
    }

    /**
     * Inserts all mappings from the given Bundle into this Bundle.
     *
     * @param bundle a Bundle
     */
    public void putAll(Bundle bundle) {
        putAll(bundle, true);
    }

    /**
     * Inserts all mappings from the given Bundle into this Bundle.
     *
     * @param bundle      a Bundle
     * @param needPersist {@code true} means the value will be saved in OnSaveInstance handleData
     */
    public void putAll(Bundle bundle, boolean needPersist) {
        mData.putAll(bundle, needPersist);
        for (String key : bundle.keySet()) {
            notifyDataChanged(key);
        }
    }

    /**
     * Inserts a Bundle value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a Bundle object, or null
     */
    public void putBundle(@Nullable String key, @Nullable Bundle value) {
        putBundle(key, value, true);
    }

    /**
     * Inserts a Bundle value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key         a String, or null
     * @param value       a Bundle object, or null
     * @param needPersist {@code true} means the value will be saved in OnSaveInstance handleData
     */
    public void putBundle(@Nullable String key, @Nullable Bundle value, boolean needPersist) {
        mData.putBundle(key, value, needPersist);
        notifyDataChanged(key);
    }

    /**
     * Inserts a byte value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value a byte
     */
    public void putByte(@Nullable String key, byte value) {
        putByte(key, value, true);
    }

    /**
     * Inserts a byte value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key         a String, or null
     * @param value       a byte
     * @param needPersist {@code true} means the value will be saved in OnSaveInstance handleData
     */
    public void putByte(@Nullable String key, byte value, boolean needPersist) {
        mData.putByte(key, value, needPersist);
        notifyDataChanged(key);
    }

    /**
     * Inserts a byte array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a byte array object, or null
     */
    public void putByteArray(@Nullable String key, @Nullable byte[] value) {
        putByteArray(key, value, true);
    }

    /**
     * Inserts a byte array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key         a String, or null
     * @param value       a byte array object, or null
     * @param needPersist {@code true} means the value will be saved in OnSaveInstance handleData
     */
    public void putByteArray(@Nullable String key, @Nullable byte[] value, boolean needPersist) {
        mData.putByteArray(key, value, needPersist);
        notifyDataChanged(key);
    }

    /**
     * Inserts a Boolean value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a boolean
     */
    public void putBoolean(@Nullable String key, boolean value) {
        putBoolean(key, value, true);
    }

    /**
     * Inserts a Boolean value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key         a String, or null
     * @param value       a boolean
     * @param needPersist {@code true} means the value will be saved in OnSaveInstance handleData
     */
    public void putBoolean(@Nullable String key, boolean value, boolean needPersist) {
        mData.putBoolean(key, value, needPersist);
        notifyDataChanged(key);
    }

    /**
     * Inserts a boolean array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a boolean array object, or null
     */
    public void putBooleanArray(@Nullable String key, @Nullable boolean[] value) {
        putBooleanArray(key, value, true);
    }

    /**
     * Inserts a boolean array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key         a String, or null
     * @param value       a boolean array object, or null
     * @param needPersist {@code true} means the value will be saved in OnSaveInstance handleData
     */
    public void putBooleanArray(@Nullable String key, @Nullable boolean[] value, boolean needPersist) {
        mData.putBooleanArray(key, value, needPersist);
        notifyDataChanged(key);
    }

    /**
     * Inserts an int value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value an int
     */
    public void putInt(@Nullable String key, int value) {
        putInt(key, value, true);
    }

    /**
     * Inserts an int value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key         a String, or null
     * @param value       an int
     * @param needPersist {@code true} means the value will be saved in OnSaveInstance handleData
     */
    public void putInt(@Nullable String key, int value, boolean needPersist) {
        mData.putInt(key, value, needPersist);
        notifyDataChanged(key);
    }

    /**
     * Inserts an int array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value an int array object, or null
     */
    public void putIntArray(@Nullable String key, @Nullable int[] value) {
        putIntArray(key, value, true);
    }

    /**
     * Inserts an int array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key         a String, or null
     * @param value       an int array object, or null
     * @param needPersist {@code true} means the value will be saved in OnSaveInstance handleData
     */
    public void putIntArray(@Nullable String key, @Nullable int[] value, boolean needPersist) {
        mData.putIntArray(key, value, needPersist);
        notifyDataChanged(key);
    }

    /**
     * Inserts an ArrayList<Integer> value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value an ArrayList<Integer> object, or null
     */
    public void putIntegerArrayList(@Nullable String key, @Nullable ArrayList<Integer> value) {
        putIntegerArrayList(key, value, true);
    }

    /**
     * Inserts an ArrayList<Integer> value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key         a String, or null
     * @param value       an ArrayList<Integer> object, or null
     * @param needPersist {@code true} means the value will be saved in OnSaveInstance handleData
     */
    public void putIntegerArrayList(@Nullable String key, @Nullable ArrayList<Integer> value, boolean needPersist) {
        mData.putIntegerArrayList(key, value, needPersist);
        notifyDataChanged(key);
    }

    /**
     * Inserts a long value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value a long
     */
    public void putLong(@Nullable String key, long value) {
        putLong(key, value, true);
    }

    /**
     * Inserts a long value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key         a String, or null
     * @param value       a long
     * @param needPersist {@code true} means the value will be saved in OnSaveInstance handleData
     */
    public void putLong(@Nullable String key, long value, boolean needPersist) {
        mData.putLong(key, value, needPersist);
        notifyDataChanged(key);
    }

    /**
     * Inserts a long array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a long array object, or null
     */
    public void putLongArray(@Nullable String key, @Nullable long[] value) {
        putLongArray(key, value, true);
    }

    /**
     * Inserts a long array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key         a String, or null
     * @param value       a long array object, or null
     * @param needPersist {@code true} means the value will be saved in OnSaveInstance handleData
     */
    public void putLongArray(@Nullable String key, @Nullable long[] value, boolean needPersist) {
        mData.putLongArray(key, value, needPersist);
        notifyDataChanged(key);
    }

    /**
     * Inserts a short value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value a short
     */
    public void putShort(@Nullable String key, short value) {
        putShort(key, value, true);
    }

    /**
     * Inserts a short value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key         a String, or null
     * @param value       a short
     * @param needPersist {@code true} means the value will be saved in OnSaveInstance handleData
     */
    public void putShort(@Nullable String key, short value, boolean needPersist) {
        mData.putShort(key, value, needPersist);
        notifyDataChanged(key);
    }

    /**
     * Inserts a short array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a short array object, or null
     */
    public void putShortArray(@Nullable String key, @Nullable short[] value) {
        putShortArray(key, value, true);
    }

    /**
     * Inserts a short array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key         a String, or null
     * @param value       a short array object, or null
     * @param needPersist {@code true} means the value will be saved in OnSaveInstance handleData
     */
    public void putShortArray(@Nullable String key, @Nullable short[] value, boolean needPersist) {
        mData.putShortArray(key, value, needPersist);
        notifyDataChanged(key);
    }

    /**
     * Inserts a float value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value a float
     */
    public void putFloat(@Nullable String key, float value) {
        putFloat(key, value, true);
    }

    /**
     * Inserts a float value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key         a String, or null
     * @param value       a float
     * @param needPersist {@code true} means the value will be saved in OnSaveInstance handleData
     */
    public void putFloat(@Nullable String key, float value, boolean needPersist) {
        mData.putFloat(key, value, needPersist);
        notifyDataChanged(key);
    }

    /**
     * Inserts a float array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a float array object, or null
     */
    public void putFloatArray(@Nullable String key, @Nullable float[] value) {
        putFloatArray(key, value, true);
    }

    /**
     * Inserts a float array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key         a String, or null
     * @param value       a float array object, or null
     * @param needPersist {@code true} means the value will be saved in OnSaveInstance handleData
     */
    public void putFloatArray(@Nullable String key, @Nullable float[] value, boolean needPersist) {
        mData.putFloatArray(key, value, needPersist);
        notifyDataChanged(key);
    }


    /**
     * Inserts a double value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value a double
     */
    public void putDouble(@Nullable String key, double value) {
        putDouble(key, value, true);
    }

    /**
     * Inserts a double value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key         a String, or null
     * @param value       a double
     * @param needPersist {@code true} means the value will be saved in OnSaveInstance handleData
     */
    public void putDouble(@Nullable String key, double value, boolean needPersist) {
        mData.putDouble(key, value, needPersist);
        notifyDataChanged(key);
    }

    /**
     * Inserts a double array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a double array object, or null
     */
    public void putDoubleArray(@Nullable String key, @Nullable double[] value) {
        putDoubleArray(key, value, true);
    }

    /**
     * Inserts a double array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key         a String, or null
     * @param value       a double array object, or null
     * @param needPersist {@code true} means the value will be saved in OnSaveInstance handleData
     */
    public void putDoubleArray(@Nullable String key, @Nullable double[] value, boolean needPersist) {
        mData.putDoubleArray(key, value, needPersist);
        notifyDataChanged(key);
    }

    /**
     * Inserts a char value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value a char
     */
    public void putChar(@Nullable String key, char value) {
        putChar(key, value, true);
    }

    /**
     * Inserts a char value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key         a String, or null
     * @param value       a char
     * @param needPersist {@code true} means the value will be saved in OnSaveInstance handleData
     */
    public void putChar(@Nullable String key, char value, boolean needPersist) {
        mData.putChar(key, value, needPersist);
        notifyDataChanged(key);
    }

    /**
     * Inserts a char array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a char array object, or null
     */
    public void putCharArray(@Nullable String key, @Nullable char[] value) {
        putCharArray(key, value, true);
    }

    /**
     * Inserts a char array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key         a String, or null
     * @param value       a char array object, or null
     * @param needPersist {@code true} means the value will be saved in OnSaveInstance handleData
     */
    public void putCharArray(@Nullable String key, @Nullable char[] value, boolean needPersist) {
        mData.putCharArray(key, value, needPersist);
        notifyDataChanged(key);
    }

    /**
     * Inserts a String value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a String, or null
     */
    public void putString(@Nullable String key, @Nullable String value) {
        putString(key, value, true);
    }

    /**
     * Inserts a String value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key         a String, or null
     * @param value       a String, or null
     * @param needPersist {@code true} means the value will be saved in OnSaveInstance handleData
     */
    public void putString(@Nullable String key, @Nullable String value, boolean needPersist) {
        mData.putString(key, value, needPersist);
        notifyDataChanged(key);
    }

    /**
     * Inserts a String array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a String array object, or null
     */
    public void putStringArray(@Nullable String key, @Nullable String[] value) {
        putStringArray(key, value, true);
    }

    /**
     * Inserts a String array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key         a String, or null
     * @param value       a String array object, or null
     * @param needPersist {@code true} means the value will be saved in OnSaveInstance handleData
     */
    public void putStringArray(@Nullable String key, @Nullable String[] value, boolean needPersist) {
        mData.putStringArray(key, value, needPersist);
        notifyDataChanged(key);
    }

    /**
     * Inserts an ArrayList<String> value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value an ArrayList<String> object, or null
     */
    public void putStringArrayList(@Nullable String key, @Nullable ArrayList<String> value) {
        putStringArrayList(key, value, true);
    }

    /**
     * Inserts an ArrayList<String> value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key         a String, or null
     * @param value       an ArrayList<String> object, or null
     * @param needPersist {@code true} means the value will be saved in OnSaveInstance handleData
     */
    public void putStringArrayList(@Nullable String key, @Nullable ArrayList<String> value, boolean needPersist) {
        mData.putStringArrayList(key, value, needPersist);
        notifyDataChanged(key);
    }

    /**
     * Inserts a CharSequence value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a CharSequence, or null
     */
    public void putCharSequence(@Nullable String key, @Nullable CharSequence value) {
        putCharSequence(key, value, true);
    }

    /**
     * Inserts a CharSequence value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key         a String, or null
     * @param value       a CharSequence, or null
     * @param needPersist {@code true} means the value will be saved in OnSaveInstance handleData
     */
    public void putCharSequence(@Nullable String key, @Nullable CharSequence value, boolean needPersist) {
        mData.putCharSequence(key, value, needPersist);
        notifyDataChanged(key);
    }

    /**
     * Inserts a CharSequence array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a CharSequence array object, or null
     */
    public void putCharSequenceArray(@Nullable String key, @Nullable CharSequence[] value) {
        putCharSequenceArray(key, value, true);
    }

    /**
     * Inserts a CharSequence array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key         a String, or null
     * @param value       a CharSequence array object, or null
     * @param needPersist {@code true} means the value will be saved in OnSaveInstance handleData
     */
    public void putCharSequenceArray(@Nullable String key, @Nullable CharSequence[] value, boolean needPersist) {
        mData.putCharSequenceArray(key, value, needPersist);
        notifyDataChanged(key);
    }

    /**
     * Inserts an ArrayList<CharSequence> value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value an ArrayList<CharSequence> object, or null
     */
    public void putCharSequenceArrayList(@Nullable String key,
                                         @Nullable ArrayList<CharSequence> value) {
        putCharSequenceArrayList(key, value, true);
    }

    /**
     * Inserts an ArrayList<CharSequence> value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key         a String, or null
     * @param value       an ArrayList<CharSequence> object, or null
     * @param needPersist {@code true} means the value will be saved in OnSaveInstance handleData
     */
    public void putCharSequenceArrayList(@Nullable String key,
                                         @Nullable ArrayList<CharSequence> value,
                                         boolean needPersist) {
        mData.putCharSequenceArrayList(key, value, needPersist);
        notifyDataChanged(key);
    }

    /**
     * Inserts a Parcelable value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a Parcelable object, or null
     */
    public void putParcelable(@Nullable String key, @Nullable Parcelable value) {
        putParcelable(key, value, true);
    }

    /**
     * Inserts a Parcelable value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key         a String, or null
     * @param value       a Parcelable object, or null
     * @param needPersist {@code true} means the value will be saved in OnSaveInstance handleData
     */
    public void putParcelable(@Nullable String key, @Nullable Parcelable value, boolean needPersist) {
        mData.putParcelable(key, value, needPersist);
        notifyDataChanged(key);
    }

    /**
     * Inserts an array of Parcelable values into the mapping of this Bundle,
     * replacing any existing value for the given key.  Either key or value may
     * be null.
     *
     * @param key   a String, or null
     * @param value an array of Parcelable objects, or null
     */
    public void putParcelableArray(@Nullable String key, @Nullable Parcelable[] value) {
        putParcelableArray(key, value, true);
    }

    /**
     * Inserts an array of Parcelable values into the mapping of this Bundle,
     * replacing any existing value for the given key.  Either key or value may
     * be null.
     *
     * @param key         a String, or null
     * @param value       an array of Parcelable objects, or null
     * @param needPersist {@code true} means the value will be saved in OnSaveInstance handleData
     */
    public void putParcelableArray(@Nullable String key, @Nullable Parcelable[] value, boolean needPersist) {
        mData.putParcelableArray(key, value, needPersist);
        notifyDataChanged(key);
    }

    /**
     * Inserts a List of Parcelable values into the mapping of this Bundle,
     * replacing any existing value for the given key.  Either key or value may
     * be null.
     *
     * @param key   a String, or null
     * @param value an ArrayList of Parcelable objects, or null
     */
    public void putParcelableArrayList(@Nullable String key,
                                       @Nullable ArrayList<? extends Parcelable> value) {
        putParcelableArrayList(key, value, true);
    }

    /**
     * Inserts a List of Parcelable values into the mapping of this Bundle,
     * replacing any existing value for the given key.  Either key or value may
     * be null.
     *
     * @param key         a String, or null
     * @param value       an ArrayList of Parcelable objects, or null
     * @param needPersist {@code true} means the value will be saved in OnSaveInstance handleData
     */
    public void putParcelableArrayList(@Nullable String key,
                                       @Nullable ArrayList<? extends Parcelable> value,
                                       boolean needPersist) {
        mData.putParcelableArrayList(key, value, needPersist);
        notifyDataChanged(key);
    }

    /**
     * Inserts a SparceArray of Parcelable values into the mapping of this
     * Bundle, replacing any existing value for the given key.  Either key
     * or value may be null.
     *
     * @param key   a String, or null
     * @param value a SparseArray of Parcelable objects, or null
     */
    public void putSparseParcelableArray(@Nullable String key,
                                         @Nullable SparseArray<? extends Parcelable> value) {
        putSparseParcelableArray(key, value, true);
    }

    /**
     * Inserts a SparceArray of Parcelable values into the mapping of this
     * Bundle, replacing any existing value for the given key.  Either key
     * or value may be null.
     *
     * @param key         a String, or null
     * @param value       a SparseArray of Parcelable objects, or null
     * @param needPersist {@code true} means the value will be saved in OnSaveInstance handleData
     */
    public void putSparseParcelableArray(@Nullable String key,
                                         @Nullable SparseArray<? extends Parcelable> value,
                                         boolean needPersist) {
        mData.putSparseParcelableArray(key, value, needPersist);
        notifyDataChanged(key);
    }

    /**
     * Inserts a Serializable value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a Serializable object, or null
     */
    public void putSerializable(@Nullable String key, @Nullable Serializable value) {
        putSerializable(key, value, true);
    }

    /**
     * Inserts a Serializable value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key         a String, or null
     * @param value       a Serializable object, or null
     * @param needPersist {@code true} means the value will be saved in OnSaveInstance handleData
     */
    public void putSerializable(@Nullable String key, @Nullable Serializable value, boolean needPersist) {
        mData.putSerializable(key, value, needPersist);
        notifyDataChanged(key);
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return a Bundle value, or null
     */
    public Bundle getBundle(String key) {
        return mData.getBundle(key, null);
    }

    /**
     * Returns the value associated with the given key, or (byte) 0 if
     * no mapping of the desired type exists for the given key.
     *
     * @param key a String
     * @return a byte value
     */
    public byte getByte(String key) {
        return mData.getByte(key, (byte) 0);
    }

    /**
     * Returns the value associated with the given key, or defaultValue if
     * no mapping of the desired type exists for the given key.
     *
     * @param key          a String
     * @param defaultValue Value to return if key does not exist
     * @return a byte value
     */
    public byte getByte(String key, byte defaultValue) {
        return mData.getByte(key, defaultValue);
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return a byte[] value, or null
     */
    public byte[] getByteArray(String key) {
        return mData.getByteArray(key, null);
    }

    /**
     * Returns the value associated with the given key, or false if
     * no mapping of the desired type exists for the given key.
     *
     * @param key a String
     * @return a boolean value
     */
    public boolean getBoolean(String key) {
        return mData.getBoolean(key, false);
    }

    /**
     * Returns the value associated with the given key, or defaultValue if
     * no mapping of the desired type exists for the given key.
     *
     * @param key          a String
     * @param defaultValue Value to return if key does not exist
     * @return a boolean value
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        return mData.getBoolean(key, defaultValue);
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return a boolean[] value, or null
     */
    public boolean[] getBooleanArray(String key) {
        return mData.getBooleanArray(key, null);
    }

    /**
     * Returns the value associated with the given key, or 0 if
     * no mapping of the desired type exists for the given key.
     *
     * @param key a String
     * @return an int value
     */
    public int getInt(String key) {
        return mData.getInt(key, 0);
    }

    /**
     * Returns the value associated with the given key, or defaultValue if
     * no mapping of the desired type exists for the given key.
     *
     * @param key          a String
     * @param defaultValue Value to return if key does not exist
     * @return an int value
     */
    public int getInt(String key, int defaultValue) {
        return mData.getInt(key, defaultValue);
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return an int[] value, or null
     */
    public int[] getIntArray(String key) {
        return mData.getIntArray(key, null);
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return an ArrayList<String> value, or null
     */
    public ArrayList<Integer> getIntegerArrayList(String key) {
        return mData.getIntegerArrayList(key, null);
    }

    /**
     * Returns the value associated with the given key, or 0L if
     * no mapping of the desired type exists for the given key.
     *
     * @param key a String
     * @return a long value
     */
    public long getLong(String key) {
        return mData.getLong(key, (long) 0);
    }

    /**
     * Returns the value associated with the given key, or defaultValue if
     * no mapping of the desired type exists for the given key.
     *
     * @param key          a String
     * @param defaultValue Value to return if key does not exist
     * @return a long value
     */
    public long getLong(String key, long defaultValue) {
        return mData.getLong(key, defaultValue);
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return a long[] value, or null
     */
    public long[] getLongArray(String key) {
        return mData.getLongArray(key, null);
    }

    /**
     * Returns the value associated with the given key, or (short) 0 if
     * no mapping of the desired type exists for the given key.
     *
     * @param key a String
     * @return a short value
     */
    public short getShort(String key) {
        return mData.getShort(key, (short) 0);
    }

    /**
     * Returns the value associated with the given key, or defaultValue if
     * no mapping of the desired type exists for the given key.
     *
     * @param key          a String
     * @param defaultValue Value to return if key does not exist
     * @return a short value
     */
    public short getShort(String key, short defaultValue) {
        return mData.getShort(key, defaultValue);
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return a short[] value, or null
     */
    public short[] getShortArray(String key) {
        return mData.getShortArray(key, null);
    }

    /**
     * Returns the value associated with the given key, or 0.0f if
     * no mapping of the desired type exists for the given key.
     *
     * @param key a String
     * @return a float value
     */
    public float getFloat(String key) {
        return mData.getFloat(key, 0f);
    }

    /**
     * Returns the value associated with the given key, or defaultValue if
     * no mapping of the desired type exists for the given key.
     *
     * @param key          a String
     * @param defaultValue Value to return if key does not exist
     * @return a float value
     */
    public float getFloat(String key, float defaultValue) {
        return mData.getFloat(key, defaultValue);
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return a float[] value, or null
     */
    public float[] getFloatArray(String key) {
        return mData.getFloatArray(key, null);
    }

    /**
     * Returns the value associated with the given key, or 0.0 if
     * no mapping of the desired type exists for the given key.
     *
     * @param key a String
     * @return a double value
     */
    public double getDouble(String key) {
        return mData.getDouble(key, 0.0);
    }

    /**
     * Returns the value associated with the given key, or defaultValue if
     * no mapping of the desired type exists for the given key.
     *
     * @param key          a String
     * @param defaultValue Value to return if key does not exist
     * @return a double value
     */
    public double getDouble(String key, double defaultValue) {
        return mData.getDouble(key, defaultValue);
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return a double[] value, or null
     */
    public double[] getDoubleArray(String key) {
        return mData.getDoubleArray(key, null);
    }

    /**
     * Returns the value associated with the given key, or (char) 0 if
     * no mapping of the desired type exists for the given key.
     *
     * @param key a String
     * @return a char value
     */
    public char getChar(String key) {
        return mData.getChar(key, (char) 0);
    }

    /**
     * Returns the value associated with the given key, or defaultValue if
     * no mapping of the desired type exists for the given key.
     *
     * @param key          a String
     * @param defaultValue Value to return if key does not exist
     * @return a char value
     */
    public char getChar(String key, char defaultValue) {
        return mData.getChar(key, defaultValue);
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return a char[] value, or null
     */
    public char[] getCharArray(String key) {
        return mData.getCharArray(key, null);
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return a String value, or null
     */
    public String getString(String key) {
        return mData.getString(key, null);
    }

    /**
     * Returns the value associated with the given key, or defaultValue if
     * no mapping of the desired type exists for the given key or if a null
     * value is explicitly associated with the given key.
     *
     * @param key          a String, or null
     * @param defaultValue Value to return if key does not exist or if a null
     *                     value is associated with the given key.
     * @return the String value associated with the given key, or defaultValue
     * if no valid String object is currently mapped to that key.
     */
    public String getString(String key, String defaultValue) {
        return mData.getString(key, defaultValue);
    }

    public String[] getStringArray(String key) {
        return mData.getStringArray(key, null);
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return an ArrayList<String> value, or null
     */
    public ArrayList<String> getStringArrayList(String key) {
        return mData.getStringArrayList(key, null);
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return a CharSequence value, or null
     */
    public CharSequence getCharSequence(String key) {
        return mData.getCharSequence(key, null);
    }

    /**
     * Returns the value associated with the given key, or defaultValue if
     * no mapping of the desired type exists for the given key or if a null
     * value is explicitly associatd with the given key.
     *
     * @param key          a String, or null
     * @param defaultValue Value to return if key does not exist or if a null
     *                     value is associated with the given key.
     * @return the CharSequence value associated with the given key, or defaultValue
     * if no valid CharSequence object is currently mapped to that key.
     */
    public CharSequence getCharSequence(String key, CharSequence defaultValue) {
        return mData.getCharSequence(key, defaultValue);
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return a CharSequence[] value, or null
     */
    public CharSequence[] getCharSequenceArray(String key) {
        return mData.getCharSequenceArray(key, null);
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return an ArrayList<CharSequence> value, or null
     */
    public ArrayList<CharSequence> getCharSequenceArrayList(String key) {
        return mData.getCharSequenceArrayList(key, null);
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return a Parcelable value, or null
     */
    public <T extends Parcelable> T getParcelable(String key) {
        return mData.getParcelable(key, null);
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return a Parcelable[] value, or null
     */
    public Parcelable[] getParcelableArray(String key) {
        return mData.getParcelableArray(key, null);
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return an ArrayList<T> value, or null
     */
    public <T extends Parcelable> ArrayList<T> getParcelableArrayList(String key) {
        return mData.getParcelableArrayList(key, null);
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return a SparseArray of T values, or null
     */
    public <T extends Parcelable> SparseArray<T> getSparseParcelableArray(String key) {
        return mData.getSparseParcelableArray(key, null);
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return a Serializable value, or null
     */
    public Serializable getSerializable(String key) {
        return mData.getSerializable(key, null);
    }

    public HashMap<String, Object> getContent() {
        HashMap<String, Object> content = new HashMap<>();
        for (String key : keySet()) {
            content.put(key, getData(key));
        }

        return content;
    }

    public interface MessageHandler {
        Object handleMessage(Object parameter);
    }

    public interface MessageHandlerWithKey {
        Object handleMessage(String key, Object parameter);
    }

}
