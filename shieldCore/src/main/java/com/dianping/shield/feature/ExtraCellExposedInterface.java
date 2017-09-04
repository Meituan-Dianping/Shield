package com.dianping.shield.feature;

import com.dianping.shield.entity.CellType;
import com.dianping.shield.entity.ExposeScope;

/**
 * Created by hezhi on 17/2/21.
 * SectionCellInterface的ExtraCell曝光接口。
 * ExtraCell的默认曝光粒度为按行。
 * ExtraCell是指通过SectionExtraCellInterface等接口提供的额外视图行
 * 与ExposedInterface不冲突,同时实现两个接口均会收到回调。
 */

public interface ExtraCellExposedInterface {

    //设置判定范围
    ExposeScope getExtraCellExposeScope(int section, CellType type);

    //最多允许曝光次数
    int maxExtraExposeCount(int section, CellType type);

    //两次曝光最短时间间隔
    long extraCellExposeDuration(int section, CellType type);

    //判定曝光最短持续时间
    long extraCellStayDuration(int section, CellType type);

    //曝光回调 count:第几次曝光从1开始
    void onExtraCellExposed(int section, CellType type, int count);
}
