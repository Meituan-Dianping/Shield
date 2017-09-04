package com.dianping.shield.feature;

import com.dianping.shield.entity.ExposeScope;
import com.dianping.shield.entity.ScrollDirection;

/**
 * Created by hezhi on 17/4/1.
 */

public interface MoveStatusInterface {

    void onAppear(ExposeScope scope, ScrollDirection direction);

    void onDisappear(ExposeScope scope, ScrollDirection direction);
}
