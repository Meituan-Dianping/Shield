package com.dianping.shield.feature;

import java.util.Map;

/**
 * Created by hezhi on 17/2/6.
 */

public interface RecyclerPoolSizeInterface {
    //Map的Key是type ，Value是该Type对应的maxsize
    Map<Integer, Integer> recyclerableViewSizeMap();
}
