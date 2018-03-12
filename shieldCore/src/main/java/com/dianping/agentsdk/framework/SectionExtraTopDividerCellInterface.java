package com.dianping.agentsdk.framework;

import android.graphics.drawable.Drawable;

/**
 * Created by xianhe.dong on 2017/9/11.
 * email xianhe.dong@dianping.com
 */

public interface SectionExtraTopDividerCellInterface {

    //该section的headercell的上分割线
    Drawable getTopDividerForHeader(int sectionPosition);

    //该section的headercell的下分割线
    Drawable getBottomDividerForHeader(int sectionPosition);

    //该section的footercell的上分割线
    Drawable getTopDividerForFooter(int sectionPosition);

    //该section的footercell的下分割线
    Drawable getBottomDividerForFooter(int sectionPosition);
}
