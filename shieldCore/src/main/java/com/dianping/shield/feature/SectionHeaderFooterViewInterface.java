package com.dianping.shield.feature;

import android.view.View;

/**
 * Created by zhi.he on 2018/3/21.
 */

public interface SectionHeaderFooterViewInterface {
    //View必须预先创建好
    View getSectionHeaderView(int section);

    View getSectionFooterView(int section);
}
