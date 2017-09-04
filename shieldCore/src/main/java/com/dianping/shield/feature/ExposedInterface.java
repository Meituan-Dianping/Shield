package com.dianping.shield.feature;

import com.dianping.shield.entity.ExposeScope;

/**
 * Created by hezhi on 17/2/20.
 * 模块包含的SectionCellInterface整体曝光接口。
 * 实现该接口即表示曝光粒度为模块级别。
 * 与CellExposedInterface不冲突,同时实现两个接口均会收到回调。
 */

public interface ExposedInterface {

    //设置判定范围
    ExposeScope getExposeScope();

    //最多允许曝光次数
    int maxExposeCount();

    //两次曝光最短时间间隔
    long exposeDuration();

    //判定曝光最短持续时间
    long stayDuration();

    //曝光回调 count:第几次曝光从1开始
    void onExposed(int count);

}
