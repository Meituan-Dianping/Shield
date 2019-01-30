package com.dianping.shield.manager.feature

import android.os.Parcelable
import com.dianping.agentsdk.framework.WhiteBoard
import com.dianping.agentsdk.manager.SectionRecyclerCellManager
import com.dianping.shield.consts.ShieldConst
import com.dianping.shield.node.cellnode.ShieldCellGroup
import com.dianping.shield.node.cellnode.ShieldViewCell

class SectionTitleArrayCollector(private val whiteBoard: WhiteBoard?, private val looper: LoopCellGroupsCollector) : CellManagerFeatureInterface {
    override fun onCellNodeRefresh(shieldViewCell: ShieldViewCell) {
    }

    override fun onAdapterNotify(cellGroups: ArrayList<ShieldCellGroup?>) {
        looper.addBeforeLoopAction {
            sectionTitleMap.clear()
        }
        looper.addIndexedSectionAction { index, shieldSection ->
            var keyStr = shieldSection.cellParent?.key
            var title = shieldSection.sectionTitle
            if (keyStr?.isNotEmpty() == true && title?.isNotEmpty() == true) {
                val key = Pair(keyStr, index)
                var info = sectionTitleMap[key]
                if (info == null) {
                    sectionTitleMap[key] = SectionRecyclerCellManager.SectionTitleInfo(keyStr, index, title)
                } else {
                    info.cellKey = keyStr
                    info.section = index
                    info.sectionTitle = title
                }
            }
        }
        looper.addAfterLoopAction {
            var arrayList = ArrayList<Parcelable>()
            sectionTitleMap.mapValues {
                it?.value?.apply {
                    arrayList.add(this)
                }
            }
            whiteBoard?.putParcelableArrayList(ShieldConst.SECTION_TITLE_LIST_KEY, arrayList)
        }
    }

    protected val sectionTitleMap = HashMap<Pair<String, Int>, SectionRecyclerCellManager.SectionTitleInfo>()
}