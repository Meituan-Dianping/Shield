package com.dianping.shield.feature;

import com.dianping.agentsdk.framework.SectionCellInterface;
import com.dianping.agentsdk.framework.SectionExtraCellInterface;
import com.dianping.shield.entity.CellType;

/**
 * Created by zdh on 17/2/20.
 */

public interface ExposeScreenLoadedInterface {
    void startExpose();

    void startExpose(long delayMilliseconds);

    void finishExpose();

    void pauseExpose();

    void resumeExpose();

    void resetExposeSCI(SectionCellInterface sectionCellInterface);

    void resetExposeRow(SectionCellInterface sectionCellInterface, int section, int row);

    void resetExposeExtraCell(SectionCellInterface extraCellInterface, int section, CellType cellType);
}
