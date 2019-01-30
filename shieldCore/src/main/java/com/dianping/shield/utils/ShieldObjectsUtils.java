package com.dianping.shield.utils;

import java.util.Arrays;

/**
 * Created by runqi.wei at 2018/6/26
 */
public class ShieldObjectsUtils {

    public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    public static int hash(Object... values) {
        return Arrays.hashCode(values);
    }
}
