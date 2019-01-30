package com.dianping.shield.logger;

import android.util.Log;

public class SCLogger {

    protected String defaultTag;

    public SCLogger setTag(String tag) {
        this.defaultTag = tag;
        return this;
    }

    public void v(String format, Object... args) {
        v(defaultTag, format, args);
    }

    public void i(String format, Object... args) {
        i(defaultTag, format, args);
    }

    public void d(String format, Object... args) {
        d(defaultTag, format, args);
    }

    public void w(String format, Object... args) {
        w(defaultTag, format, args);
    }

    public void e(String format, Object... args) {
        e(defaultTag, format, args);
    }

    public static void v(String tag, String format, Object... args) {
//        ShieldEnvironment.INSTANCE.getShieldLogger().v(tag, format, args);
        Log.v(tag, String.format(format, args));
    }

    public static void i(String tag, String format, Object... args) {
//        ShieldEnvironment.INSTANCE.getShieldLogger().i(tag, format, args);
        Log.i(tag, String.format(format, args));
    }

    public static void d(String tag, String format, Object... args) {
//        ShieldEnvironment.INSTANCE.getShieldLogger().d(tag, format, args);
        Log.d(tag, String.format(format, args));
    }

    public static void w(String tag, String format, Object... args) {
//        ShieldEnvironment.INSTANCE.getShieldLogger().w(tag, format, args);
        Log.w(tag, String.format(format, args));
    }

    public static void e(String tag, String format, Object... args) {
//        ShieldEnvironment.INSTANCE.getShieldLogger().e(tag, format, args);
        Log.e(tag, String.format(format, args));
    }

}
