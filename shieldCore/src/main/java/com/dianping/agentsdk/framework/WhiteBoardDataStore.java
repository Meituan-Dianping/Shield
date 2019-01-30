package com.dianping.agentsdk.framework;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by runqi.wei on 2018/1/11.
 */

public class WhiteBoardDataStore {

    public static final String WHITE_BOARD_DATA_KEY = "White_Board_Persist_Data";

    protected HashMap<String, Object> mData;
    protected HashSet<String> mPersistKeySet;

    /**
     * Default Constructor.
     */
    public WhiteBoardDataStore() {
        this(null, true);
    }

    /**
     * Constructor.
     *
     * @param data
     */
    public WhiteBoardDataStore(Bundle data, boolean needPersist) {
        mData = new HashMap<>();
        mPersistKeySet = new HashSet<>();
        putAll(data, needPersist);
    }

    protected Bundle createBundleForPersistData() {
        Bundle bundle = new Bundle();
        if (mPersistKeySet != null && !mPersistKeySet.isEmpty()) {
            for (String persistKey : mPersistKeySet) {
                writeToBundle(bundle, persistKey, mData.get(persistKey));
            }
        }

        return bundle;
    }

    /**
     * {@hide}
     * @return
     */
    public HashMap<String, Object> getAllData() {

        return mData;
    }

    protected void writeToBundle(Bundle bundle, String key, Object value) {
        if (bundle == null || key == null || key.isEmpty() || value == null) {
            return;
        }

        if (value instanceof Bundle) { // bundle

            bundle.putBundle(key, (Bundle) value);

        } else if (value instanceof Byte) { // byte

            bundle.putByte(key, (Byte) value);

        } else if (value instanceof byte[]) {

            bundle.putByteArray(key, (byte[]) value);

        } else if (value instanceof Boolean) { // boolean

            bundle.putBoolean(key, (Boolean) value);

        } else if (value instanceof boolean[]) {

            bundle.putBooleanArray(key, (boolean[]) value);

        } else if (value instanceof Integer) { // int

            bundle.putInt(key, (Integer) value);

        } else if (value instanceof int[]) {

            bundle.putIntArray(key, (int[]) value);

        } else if (isIntegerArrayList(value)) {

            bundle.putIntegerArrayList(key, (ArrayList<Integer>) value);

        } else if (value instanceof Long) { // long

            bundle.putLong(key, (Long) value);

        } else if (value instanceof long[]) {

            bundle.putLongArray(key, (long[]) value);

        } else if (value instanceof Short) { // short

            bundle.putShort(key, (Short) value);

        } else if (value instanceof short[]) {

            bundle.putShortArray(key, (short[]) value);

        } else if (value instanceof Float) { // float

            bundle.putFloat(key, (Float) value);

        } else if (value instanceof float[]) {

            bundle.putFloatArray(key, (float[]) value);

        } else if (value instanceof Double) { // double

            bundle.putDouble(key, (Double) value);

        } else if (value instanceof double[]) {

            bundle.putDoubleArray(key, (double[]) value);

        } else if (value instanceof Character) { // char

            bundle.putChar(key, (Character) value);

        } else if (value instanceof char[]) {

            bundle.putCharArray(key, (char[]) value);

        } else if (value instanceof String) { // String

            bundle.putString(key, (String) value);

        } else if (value instanceof String[]) {

            bundle.putStringArray(key, (String[]) value);

        } else if (isStringArrayList(value)) {

            bundle.putStringArrayList(key, (ArrayList<String>) value);

        } else if (value instanceof CharSequence) { // CharSequence

            bundle.putCharSequence(key, (CharSequence) value);

        } else if (value instanceof CharSequence[]) {

            bundle.putCharSequenceArray(key, (CharSequence[]) value);

        } else if (isCharSequenceArrayList(value)) {

            bundle.putCharSequenceArrayList(key, (ArrayList<CharSequence>) value);

        } else if (value instanceof Parcelable) { // Parcelable

            bundle.putParcelable(key, (Parcelable) value);

        } else if (value instanceof Parcelable[]) {

            bundle.putParcelableArray(key, (Parcelable[]) value);

        } else if (isParcelableArrayList(value)) {

            bundle.putParcelableArrayList(key, (ArrayList<Parcelable>) value);

        } else if (isSparseParcelableArray(value)) {

            bundle.putSparseParcelableArray(key, (SparseArray<Parcelable>) value);

        } else if (value instanceof Serializable) { // Serializable

            bundle.putSerializable(key, (Serializable) value);

        }
    }

    protected boolean isIntegerArrayList(Object list) {
        if (!(list instanceof ArrayList)) {
            return false;
        }

        for (Object obj : (ArrayList) list) {
            if (!(obj instanceof Integer)) {
                return false;
            }
        }

        return true;
    }

    protected boolean isStringArrayList(Object list) {
        if (!(list instanceof ArrayList)) {
            return false;
        }

        for (Object obj : (ArrayList) list) {
            if (!(obj instanceof String)) {
                return false;
            }
        }

        return true;
    }

    protected boolean isCharSequenceArrayList(Object list) {
        if (!(list instanceof ArrayList)) {
            return false;
        }

        for (Object obj : (ArrayList) list) {
            if (!(obj instanceof CharSequence)) {
                return false;
            }
        }

        return true;
    }

    protected boolean isParcelableArrayList(Object list) {
        if (!(list instanceof ArrayList)) {
            return false;
        }

        for (Object obj : (ArrayList) list) {
            if (!(obj instanceof Parcelable)) {
                return false;
            }
        }

        return true;
    }

    protected boolean isSparseParcelableArray(Object list) {
        if (!(list instanceof SparseArray)) {
            return false;
        }

        for (int i = 0; i < ((SparseArray) list).size(); i++) {
            Object obj = ((SparseArray) list).valueAt(i);
            if (!(obj instanceof Parcelable)) {
                return false;
            }
        }

        return true;
    }

    /**
     * * Life cycle method *
     * Called to initial the WhiteBoardStore from the savedInstanceState.
     *
     * @param savedInstanceState
     */
    public void onCreate(Bundle savedInstanceState) {
        if (mData == null) {
            mData = new HashMap<>();
        }

        if (mPersistKeySet == null) {
            mPersistKeySet = new HashSet<>();
        }

        if (savedInstanceState != null) {
            Bundle data = savedInstanceState.getBundle(WHITE_BOARD_DATA_KEY);
            putAll(data, true);
        }
    }

    /**
     * * Life cycle method *
     * Called to save the data set to the input parameter.
     *
     * @param outState
     */
    public void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            outState.putBundle(WHITE_BOARD_DATA_KEY, createBundleForPersistData());
        }
    }

    /**
     * * Life cycle method *
     * Called to destroy the WhiteBoard.
     */
    public void onDestroy() {
        clear();
    }

    public void clear() {
        mData.clear();
        mPersistKeySet.clear();
    }

    /**
     * Returns a Set containing the Strings used as keys in this Bundle.
     *
     * @return a Set of String keys
     */
    public Set<String> keySet() {
        return mData.keySet();
    }

    HashSet<String> getPersistKeySet() {
        return mPersistKeySet;
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

    public Object get(String key) {
        return mData.get(key);
    }

    public void remove(String key) {
        mData.remove(key);
        mPersistKeySet.remove(key);
    }

    public void setPersistKey(String key, boolean needPersist) {
        if (needPersist) {
            mPersistKeySet.add(key);
        } else {
            mPersistKeySet.remove(key);
        }
    }

    ////////////////////// PUT METHODS //////////////////////
    protected void putData(String key, Object value, boolean needPersist) {
        if (key == null || key.isEmpty()) {
            return;
        }
        mData.put(key, value);
        setPersistKey(key, needPersist);
    }

    public void putAll(Bundle bundle, boolean needPersist) {
        if (bundle == null) {
            return;
        }
        for (String key : bundle.keySet()) {
            putData(key, bundle.get(key), needPersist);
        }
    }

    public void putBundle(@Nullable String key, @Nullable Bundle value, boolean needPersist) {
        putData(key, value, needPersist);
    }

    public void putByte(@Nullable String key, byte value, boolean needPersist) {
        putData(key, value, needPersist);
    }

    public void putByteArray(@Nullable String key, @Nullable byte[] value, boolean needPersist) {
        putData(key, value, needPersist);
    }

    public void putBoolean(@Nullable String key, boolean value, boolean needPersist) {
        putData(key, value, needPersist);
    }

    public void putBooleanArray(@Nullable String key, @Nullable boolean[] value, boolean needPersist) {
        putData(key, value, needPersist);
    }

    public void putInt(@Nullable String key, int value, boolean needPersist) {
        putData(key, value, needPersist);
    }

    public void putIntArray(@Nullable String key, @Nullable int[] value, boolean needPersist) {
        putData(key, value, needPersist);
    }

    public void putIntegerArrayList(@Nullable String key, @Nullable ArrayList<Integer> value, boolean needPersist) {
        putData(key, value, needPersist);
    }

    public void putLong(@Nullable String key, long value, boolean needPersist) {
        putData(key, value, needPersist);
    }

    public void putLongArray(@Nullable String key, @Nullable long[] value, boolean needPersist) {
        putData(key, value, needPersist);
    }

    public void putShort(@Nullable String key, short value, boolean needPersist) {
        putData(key, value, needPersist);
    }

    public void putShortArray(@Nullable String key, @Nullable short[] value, boolean needPersist) {
        putData(key, value, needPersist);
    }

    public void putFloat(@Nullable String key, float value, boolean needPersist) {
        putData(key, value, needPersist);
    }

    public void putFloatArray(@Nullable String key, @Nullable float[] value, boolean needPersist) {
        putData(key, value, needPersist);
    }

    public void putDouble(@Nullable String key, double value, boolean needPersist) {
        putData(key, value, needPersist);
    }

    public void putDoubleArray(@Nullable String key, @Nullable double[] value, boolean needPersist) {
        putData(key, value, needPersist);
    }

    public void putChar(@Nullable String key, char value, boolean needPersist) {
        putData(key, value, needPersist);
    }

    public void putCharArray(@Nullable String key, @Nullable char[] value, boolean needPersist) {
        putData(key, value, needPersist);
    }


    public void putString(@Nullable String key, @Nullable String value, boolean needPersist) {
        putData(key, value, needPersist);
    }

    public void putStringArray(@Nullable String key, @Nullable String[] value, boolean needPersist) {
        putData(key, value, needPersist);
    }

    public void putStringArrayList(@Nullable String key, @Nullable ArrayList<String> value, boolean needPersist) {
        putData(key, value, needPersist);
    }

    public void putCharSequence(@Nullable String key, @Nullable CharSequence value, boolean needPersist) {
        putData(key, value, needPersist);
    }

    public void putCharSequenceArray(@Nullable String key, @Nullable CharSequence[] value, boolean needPersist) {
        putData(key, value, needPersist);
    }

    public void putCharSequenceArrayList(@Nullable String key, @Nullable ArrayList<CharSequence> value, boolean needPersist) {
        putData(key, value, needPersist);
    }

    public void putParcelable(@Nullable String key, @Nullable Parcelable value, boolean needPersist) {
        putData(key, value, needPersist);
    }

    public void putParcelableArray(@Nullable String key, @Nullable Parcelable[] value, boolean needPersist) {
        putData(key, value, needPersist);
    }

    public void putParcelableArrayList(@Nullable String key, @Nullable ArrayList<? extends Parcelable> value, boolean needPersist) {
        putData(key, value, needPersist);
    }

    public void putSparseParcelableArray(@Nullable String key, @Nullable SparseArray<? extends Parcelable> value, boolean needPersist) {
        putData(key, value, needPersist);
    }

    public void putSerializable(@Nullable String key, @Nullable Serializable value, boolean needPersist) {
        putData(key, value, needPersist);
    }

    ////////////////////// GET METHODS //////////////////////
    protected <T> T getDataOrDefault(String key, T defaultValue) {
        Object res = mData.get(key);
        if (res == null) {
            return defaultValue;
        }
        try {
            return (T) res;
        } catch (ClassCastException e) {
            return defaultValue;
        }
    }

    public Bundle getBundle(String key, Bundle defaultValue) {
        return getDataOrDefault(key, defaultValue);
    }

    public byte getByte(String key, byte defaultValue) {
        return getDataOrDefault(key, defaultValue);
    }

    public byte[] getByteArray(String key, byte[] defaultValue) {
        return getDataOrDefault(key, defaultValue);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return getDataOrDefault(key, defaultValue);
    }

    public boolean[] getBooleanArray(String key, boolean[] defaultValue) {
        return getDataOrDefault(key, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        return getDataOrDefault(key, defaultValue);
    }

    public int[] getIntArray(String key, int[] defaultValue) {
        return getDataOrDefault(key, defaultValue);
    }

    public ArrayList<Integer> getIntegerArrayList(String key, ArrayList<Integer> defaultValue) {
        return getDataOrDefault(key, defaultValue);
    }

    public long getLong(String key, long defaultValue) {
        return getDataOrDefault(key, defaultValue);
    }

    public long[] getLongArray(String key, long[] defaultValue) {
        return getDataOrDefault(key, defaultValue);
    }

    public short getShort(String key, short defaultValue) {
        return getDataOrDefault(key, defaultValue);
    }

    public short[] getShortArray(String key, short[] defaultValue) {
        return getDataOrDefault(key, defaultValue);
    }

    public float getFloat(String key, float defaultValue) {
        return getDataOrDefault(key, defaultValue);
    }

    public float[] getFloatArray(String key, float[] defaultValue) {
        return getDataOrDefault(key, defaultValue);
    }

    public double getDouble(String key, double defaultValue) {
        return getDataOrDefault(key, defaultValue);
    }

    public double[] getDoubleArray(String key, double[] defaultValue) {
        return getDataOrDefault(key, defaultValue);
    }

    public char getChar(String key, char defaultValue) {
        return getDataOrDefault(key, defaultValue);
    }

    public char[] getCharArray(String key, char[] defaultValue) {
        return getDataOrDefault(key, defaultValue);
    }

    public String getString(String key, String defaultValue) {
        return getDataOrDefault(key, defaultValue);
    }

    public String[] getStringArray(String key, String[] defaultValue) {
        return getDataOrDefault(key, defaultValue);
    }

    public ArrayList<String> getStringArrayList(String key, ArrayList<String> defaultValue) {
        return getDataOrDefault(key, defaultValue);
    }

    public CharSequence getCharSequence(String key, CharSequence defaultValue) {
        return getDataOrDefault(key, defaultValue);
    }

    public CharSequence[] getCharSequenceArray(String key, CharSequence[] defaultValue) {
        return getDataOrDefault(key, defaultValue);
    }

    public ArrayList<CharSequence> getCharSequenceArrayList(String key, ArrayList<CharSequence> defaultValue) {
        return getDataOrDefault(key, defaultValue);
    }

    public <T extends Parcelable> T getParcelable(String key, T defaultValue) {
        return getDataOrDefault(key, defaultValue);
    }

    public Parcelable[] getParcelableArray(String key, Parcelable[] defaultValue) {
        return getDataOrDefault(key, defaultValue);
    }

    public <T extends Parcelable> ArrayList<T> getParcelableArrayList(String key, ArrayList<T> defaultValue) {
        return getDataOrDefault(key, defaultValue);
    }

    public <T extends Parcelable> SparseArray<T> getSparseParcelableArray(String key, SparseArray<T> defaultValue) {
        return getDataOrDefault(key, defaultValue);
    }

    public Serializable getSerializable(String key, Serializable defaultValue) {
        return getDataOrDefault(key, defaultValue);
    }

}
