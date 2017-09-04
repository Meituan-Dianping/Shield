package com.dianping.agentsdk.framework;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by hezhi on 16/6/21.
 * 视图组件接口
 */
public interface SectionCellInterface {

    int getSectionCount();

    int getRowCount(int sectionPosition);

    int getViewType(int sectionPosition, int rowPosition);

    int getViewTypeCount();

    View onCreateView(ViewGroup parent, int viewType);

    void updateView(View view, int sectionPosition, int rowPosition, ViewGroup parent);

}
