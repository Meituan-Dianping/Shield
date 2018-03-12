package com.dianping.agentsdk.framework;

/**
 * Created by runqi.wei on 2017/9/27.
 */

public interface SectionExtraCellDividerOffsetInterface {

    int getHeaderTopDividerLeftOffset(int section);

    int getHeaderTopDividerRightOffset(int section);

    int getHeaderBottomDividerLeftOffset(int section);

    int getHeaderBottomDividerRightOffset(int section);

    int getFooterTopDividerLeftOffset(int section);

    int getFooterTopDividerRightOffset(int section);

    int getFooterBottomDividerLeftOffset(int section);

    int getFooterBottomDividerRightOffset(int section);

}
