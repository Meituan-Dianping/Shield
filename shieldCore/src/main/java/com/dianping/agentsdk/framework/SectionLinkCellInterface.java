package com.dianping.agentsdk.framework;

/**
 * Created by runqi.wei
 * 15:19
 * 11.07.2016.
 */

public interface SectionLinkCellInterface {

    LinkType.Previous linkPrevious(int sectionPosition);

    LinkType.Next linkNext(int sectionPosition);

    float getSectionHeaderHeight(int sectionPoisition);

    float getSectionFooterHeight(int sectionPoisition);
}
