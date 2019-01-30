package com.dianping.shield.manager.feature

import com.dianping.shield.node.cellnode.ShieldCellGroup
import com.dianping.shield.node.cellnode.ShieldViewCell

/**
 * Created by zhi.he on 2018/8/10.
 */
interface CellManagerFeatureInterface {
    fun onCellNodeRefresh(shieldViewCell: ShieldViewCell)
    fun onAdapterNotify(cellGroups: ArrayList<ShieldCellGroup?>)
}