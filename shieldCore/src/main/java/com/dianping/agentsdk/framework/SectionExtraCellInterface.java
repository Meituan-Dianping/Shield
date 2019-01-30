package com.dianping.agentsdk.framework;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by hezhi on 16/6/21.
 */
public interface SectionExtraCellInterface extends SectionCellInterface {

    //该section是否有headercell
    boolean hasHeaderForSection(int sectionPostion);

    //该section是否有footercell
    boolean hasFooterForSection(int sectionPostion);

    //是否显示该section的headercell的上分割线
    boolean hasTopDividerForHeader(int sectionPosition);

    //是否显示该section的headercell的下分割线
    boolean hasBottomDividerForHeader(int sectionPosition);

    //headercell的分割线左边距
    float getHeaderDividerOffset(int sectionPosition);

    //headercell的类型总数
    int getHeaderViewTypeCount();

    //headercell的类型
    int getHeaderViewType(int sectionPosition);

    //当headercell被创建时调用
    View onCreateHeaderView(ViewGroup parent, int viewType);

    //当headercell被更新时调用
    void updateHeaderView(View view, int section, ViewGroup parent);

    //是否显示该section的footercell的下分割线
    boolean hasBottomDividerForFooter(int sectionPosition);

    //footercell的分割线左边距
    float getFooterDividerOffset(int sectionPosition);

    //footercell的类型总数
    int getFooterViewTypeCount();

    //footercell的类型
    int getFooterViewType(int sectionPosition);

    //当footercell被创建时调用
    View onCreateFooterView(ViewGroup parent, int viewType);

    //当footercell被更新时调用
    void updateFooterView(View view, int section, ViewGroup parent);

}
