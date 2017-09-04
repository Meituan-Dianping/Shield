package com.dianping.shield.feature;

import com.dianping.shield.entity.CellType;
import com.dianping.shield.entity.ScrollDirection;
import com.dianping.shield.entity.ScrollReachTopParams;

/**
 * Created by hai on 2017/7/19.
 */

public interface ScrollReachTopNotifyInterface {
    ScrollReachTopParams getScrollToTopParams(int sectionPosition, int rowPosition, CellType cellType);

    void scrollReach(ScrollDirection scrollDirection, int sectionPosition, int rowPosition, CellType cellType);

    void scrollOut(ScrollDirection scrollDirection, int sectionPosition, int rowPosition, CellType cellType);

}
