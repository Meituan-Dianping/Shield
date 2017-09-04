package com.dianping.agentsdk.utils;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

/**
 * Created by hezhi on 16/10/21.
 */

public class IntentUtils {

    /**
     * 先尝试获取getArguments()中的参数<br>
     * 如果getArguments()不包含指定参数，再从Activity的Uri中获取参数<br>
     * 如果Uri中不包含指定参数，则从Activity.getIntent()中获取参数<br>
     * 如果以上均获取不到参数，则返回defaultValue
     */

    public static int getIntParam(String name, int defaultValue, Fragment fragment) {
        if (fragment.getArguments() != null && fragment.getArguments().containsKey(name)) {
            return fragment.getArguments().getInt(name);
        }
        Intent i = fragment.getActivity().getIntent();
        try {
            Uri uri = i.getData();
            if (uri != null) {
                String val = uri.getQueryParameter(name);
                if (!TextUtils.isEmpty(val)) {
                    return Integer.parseInt(val);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return i.getIntExtra(name, defaultValue);
    }

    /**
     * 先尝试获取getArguments()中的参数<br>
     * 如果getArguments()不包含指定参数，再从Activity的Uri中获取参数<br>
     * 如果Uri中不包含指定参数，则从Activity.getIntent()中获取参数<br>
     * 如果以上均获取不到参数，则返回0
     */
    public static int getIntParam(String name, Fragment fragment) {
        return getIntParam(name, 0, fragment);
    }

    // boolean
    public static boolean getBooleanParam(String name, boolean defaultValue, Fragment fragment) {
        if (fragment.getArguments() != null && fragment.getArguments().containsKey(name)) {
            return fragment.getArguments().getBoolean(name);
        }

        Intent i = fragment.getActivity().getIntent();
        try {
            Uri uri = i.getData();
            if (uri != null) {
                String val = uri.getQueryParameter(name);
                if (!TextUtils.isEmpty(val))
                    return Boolean.parseBoolean(val);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i.getBooleanExtra(name, defaultValue);
    }

    public static boolean getBooleanParam(String name, Fragment fragment) {
        return getBooleanParam(name, false, fragment);
    }

    // long
    public static long getLongParam(String name, long defaultValue, Fragment fragment) {
        if (fragment.getArguments() != null && fragment.getArguments().containsKey(name)) {
            return fragment.getArguments().getLong(name);
        }

        Intent i = fragment.getActivity().getIntent();
        try {
            Uri uri = i.getData();
            if (uri != null) {
                String val = uri.getQueryParameter(name);
                if (!TextUtils.isEmpty(val))
                    return Long.parseLong(val);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return i.getLongExtra(name, defaultValue);
    }

    // double
    public static double getDoubleParam(String name, double defaultValue, Fragment fragment) {
        if (fragment.getArguments() != null && fragment.getArguments().containsKey(name)) {
            return fragment.getArguments().getDouble(name);
        }
        Intent i = fragment.getActivity().getIntent();
        try {
            Uri uri = i.getData();
            if (uri != null) {
                String val = uri.getQueryParameter(name);
                if (!TextUtils.isEmpty(val)) {
                    return Double.parseDouble(val);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return i.getDoubleExtra(name, defaultValue);
    }

    public static double getDoubleParam(String name, Fragment fragment) {
        return getDoubleParam(name, 0, fragment);
    }

    // float
    public static float getFloatParam(String name, float defaultValue, Fragment fragment) {
        if (fragment.getArguments() != null && fragment.getArguments().containsKey(name)) {
            return fragment.getArguments().getFloat(name);
        }

        Intent i = fragment.getActivity().getIntent();
        try {
            Uri uri = i.getData();
            if (uri != null) {
                String val = uri.getQueryParameter(name);
                if (!TextUtils.isEmpty(val))
                    return Float.parseFloat(val);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return i.getFloatExtra(name, defaultValue);
    }

    public static float getFloatParam(String name, Fragment fragment) {
        return getFloatParam(name, 0f, fragment);
    }

    // char
    public static char getCharParam(String name, char defaultValue, Fragment fragment) {
        if (fragment.getArguments() != null && fragment.getArguments().containsKey(name)) {
            return fragment.getArguments().getChar(name);
        }

        Intent i = fragment.getActivity().getIntent();
        try {
            Uri uri = i.getData();
            if (uri != null) {
                String val = uri.getQueryParameter(name);
                if (!TextUtils.isEmpty(val))
                    return val.charAt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return i.getCharExtra(name, defaultValue);

    }

    public static char getCharParam(String name, Fragment fragment) {
        return getCharParam(name, (char) 0, fragment);
    }

    // short
    public static short getShortParam(String name, short defaultValue, Fragment fragment) {
        if (fragment.getArguments() != null && fragment.getArguments().containsKey(name)) {
            return fragment.getArguments().getShort(name);
        }
        Intent i = fragment.getActivity().getIntent();
        try {
            Uri uri = i.getData();
            if (uri != null) {
                String val = uri.getQueryParameter(name);
                if (!TextUtils.isEmpty(val))
                    return Short.parseShort(val);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return i.getShortExtra(name, defaultValue);
    }

    public static short getShortParam(String name, Fragment fragment) {
        return getShortParam(name, (short) 0, fragment);
    }

    // byte
    public static byte getByteParam(String name, byte defaultValue, Fragment fragment) {
        if (fragment.getArguments() != null && fragment.getArguments().containsKey(name)) {
            return fragment.getArguments().getByte(name);
        }

        Intent i = fragment.getActivity().getIntent();
        try {
            Uri uri = i.getData();
            if (uri != null) {
                String val = uri.getQueryParameter(name);
                if (!TextUtils.isEmpty(val))
                    return Byte.parseByte(val);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return i.getByteExtra(name, defaultValue);
    }

    public static byte getByteParam(String name, Fragment fragment) {
        return getByteParam(name, (byte) 0, fragment);
    }

    // String
    public static String getStringParam(String name, Fragment fragment) {
        if (fragment.getArguments() != null && fragment.getArguments().containsKey(name)) {
            return fragment.getArguments().getString(name);
        }
        Intent i = fragment.getActivity().getIntent();
        try {
            Uri uri = i.getData();
            if (uri != null) {
                String val = uri.getQueryParameter(name);
                if (val != null)
                    return val;
            }
        } catch (Exception e) {
        }

        return i.getStringExtra(name);
    }

    public long getLongParam(String name, Fragment fragment) {
        return getLongParam(name, 0L, fragment);
    }
}
