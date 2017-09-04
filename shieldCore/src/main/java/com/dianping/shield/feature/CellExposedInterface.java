package com.dianping.shield.feature;

import com.dianping.shield.entity.ExposeScope;

/**
 * Created by hezhi on 17/2/22.
 * SectionCellInterface的按行曝光接口。
 * 实现该接口即表示曝光粒度为行级别。
 * 与ExposedInterface不冲突,同时实现两个接口均会收到回调。
 */

public interface CellExposedInterface {

    //设置判定范围
    ExposeScope getExposeScope(int section, int row);

    //最多允许曝光次数
    int maxExposeCount(int section, int row);

    //两次曝光最短时间间隔
    long exposeDuration(int section, int row);

    //判定曝光最短持续时间
    long stayDuration(int section, int row);

    //曝光回调 count:第几次曝光从1开始
    void onExposed(int section, int row, int count);
}
