package com.dianping.shield.manager

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.widget.FrameLayout
import com.dianping.agentsdk.framework.*
import com.dianping.agentsdk.pagecontainer.SetAutoOffsetInterface
import com.dianping.agentsdk.sectionrecycler.GroupBorderDecoration
import com.dianping.agentsdk.sectionrecycler.layoutmanager.LinearLayoutManagerWithSmoothOffset
import com.dianping.shield.adapter.MergeAdapterTypeRefreshListener
import com.dianping.shield.bridge.feature.AgentGlobalPositionInterface
import com.dianping.shield.bridge.feature.AgentScrollerInterface
import com.dianping.shield.entity.*
import com.dianping.shield.expose.EntrySetHolder
import com.dianping.shield.feature.ExposeScreenLoadedInterface
import com.dianping.shield.feature.HotZoneItemStatusInterface
import com.dianping.shield.feature.HotZoneStatusInterface
import com.dianping.shield.feature.LoadingAndLoadingMoreCreator
import com.dianping.shield.manager.feature.*
import com.dianping.shield.node.DividerThemePackage
import com.dianping.shield.node.StaggeredGridThemePackage
import com.dianping.shield.node.adapter.ShieldDisplayNodeAdapter
import com.dianping.shield.node.cellnode.*
import com.dianping.shield.node.cellnode.callback.lazyload.DefaultShieldRowProvider
import com.dianping.shield.node.cellnode.callback.lazyload.DefaultShieldRowProviderWithItem
import com.dianping.shield.node.processor.NodeCreator
import com.dianping.shield.node.processor.ProcessorHolder
import com.dianping.shield.node.processor.legacy.NodeItemConvertUtils
import com.dianping.shield.node.useritem.SectionItem
import com.dianping.shield.sectionrecycler.ShieldLayoutManagerInterface
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by zhi.he on 2018/6/21.
 */
class ShieldNodeCellManager(private val mContext: Context) : UIRCellManagerInterface<RecyclerView>,
        MergeAdapterTypeRefreshListener, ExposeScreenLoadedInterface,
        GroupBorderDecoration.GroupInfoProvider, AgentScrollerInterface, AgentGlobalPositionInterface {


    private val handler = Handler(Looper.getMainLooper())

    private val notifyCellChanged = object : Runnable {
        override fun run() {
            handler.removeCallbacks(this)
            updateAgentContainer()
        }
    }

    private val cells: HashMap<String, Cell> = LinkedHashMap()
    private var sortedCells: ArrayList<Cell> = ArrayList()
    private val cellComparator: Comparator<Cell> = Comparator { lhs, rhs ->
        if (lhs.owner?.index == rhs.owner?.index)
            lhs.name?.compareTo(rhs.name ?: "") ?: -1
        else
            lhs.owner?.index?.compareTo(rhs.owner?.index ?: "") ?: -1
    }
    private var cellGroups: ArrayList<ShieldCellGroup?> = ArrayList()

    private var sectionList = ArrayList<ShieldSection>()

    private var recyclerView: RecyclerView? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var shieldLayoutManager: ShieldLayoutManagerInterface? = null
    private var nodeAdapter = ShieldDisplayNodeAdapter(mContext)

    private var cellManagerOnScrollListener = CellManagerOnScrollListener();

    private var processorHolder: ProcessorHolder = ProcessorHolder(mContext)

    private var featureList: ArrayList<CellManagerFeatureInterface> = ArrayList()

    private var scrollListenerList: ArrayList<CellManagerScrollListenerInterface> = ArrayList()
    private var hotZoneScrollListener = HotZoneScrollListener(nodeAdapter)

    private var agentScrollTop = AgentScrollTop(this)

    private var whiteBoard: WhiteBoard? = null


    fun setWhiteBoard(whiteBoard: WhiteBoard) {
        this.whiteBoard = whiteBoard
    }

    fun setLoadingAndLoadingMoreCreator(creator: LoadingAndLoadingMoreCreator?) {
        processorHolder.creator = creator
    }

    fun setDividerThemePackage(dividerThemePackage: DividerThemePackage) {
        processorHolder.dividerThemePackage = dividerThemePackage
    }

    fun getDividerThemePackage(): DividerThemePackage {
        return processorHolder.dividerThemePackage
    }

    fun setPageName(pageName: String) {
        nodeAdapter.setPageName(pageName)
    }

    fun setEnableDivider(enableDivider: Boolean) {
        processorHolder.dividerThemePackage.enableDivider = enableDivider
    }

    fun setDisableDecoration(disableDecoration: Boolean) {
        nodeAdapter.setDisableDecoration(disableDecoration)
    }

    fun addHotZoneStatusObserver(hotZoneStatusInterface: HotZoneStatusInterface, prefix: String, reverseRange: Boolean, onlyObserverInHotZone: Boolean) {
        nodeAdapter.addHotZoneLocationManager(hotZoneStatusInterface, prefix, reverseRange, onlyObserverInHotZone)
    }

    fun removeHotZoneStatusObserver(hotZoneStatusInterface: HotZoneStatusInterface) {
        nodeAdapter.removeHotZoneLocationManager(hotZoneStatusInterface)
    }

    fun addHotZoneItemStatusObserver(agentInterface: AgentInterface, hotZoneItemStatusInterface: HotZoneItemStatusInterface, reverseRange: Boolean, onlyObserverInHotZone: Boolean) {
        val cell = findCellForAgent(agentInterface) ?: return
        nodeAdapter.addHotZoneItemLocationManager(hotZoneItemStatusInterface, cell, reverseRange, onlyObserverInHotZone)
    }

    fun removeHotZoneItemStatusObserver(hotZoneItemStatusInterface: HotZoneItemStatusInterface) {
        nodeAdapter.removeHotZoneItemLocationManager(hotZoneItemStatusInterface)
    }

    fun setStaggeredGridThemePackage(staggeredGridThemePackage: StaggeredGridThemePackage) {
        nodeAdapter.setStaggeredGridThemePackage(staggeredGridThemePackage)
    }

    fun setMarkedScrollToTopAgentRule(scrollToTopByFirstMarkedAgent: Boolean) {
        agentScrollTop.scrollToTopByFirstMarkedAgent = scrollToTopByFirstMarkedAgent
    }
    //目前框架保留符号 @ $ - _ * ( )

    //    protected var groupManager: GroupManager = GroupManager()
//    protected var cellGroups: ArrayList<ArrayList<Cell>>? = null//带分组的二维cell结构
    fun innerSetTopContainer(frameLayout: FrameLayout?) {
        nodeAdapter.setTopContainer(frameLayout)
    }

    fun innerSetBottomContainer(container: FrameLayout) {
        nodeAdapter.setBottomContainer(container)
    }

    override fun onMergedTypeRefresh() {
        //costom pool size
        cellGroups.forEach { cellGroup ->
            cellGroup?.shieldViewCells?.forEach { viewcell ->
                viewcell.recyclerViewTypeSizeMap?.forEach {
                    val mappedViewType = "${viewcell.name}${NodeCreator.viewTypeSepreator}${it.key}"
                    val globalType = nodeAdapter.getGlobalType(mappedViewType)
                    if (globalType > 0) {
                        recyclerView?.recycledViewPool?.setMaxRecycledViews(globalType, it.value)
                    }
                }
            }
        }
    }

    override fun setAgentContainerView(containerView: RecyclerView?) {
        if (containerView == null) {
            return
        }

        this.recyclerView = containerView

        //处理LayoutManager
        layoutManager = recyclerView?.layoutManager
        if (layoutManager is ShieldLayoutManagerInterface) {
            shieldLayoutManager = layoutManager as ShieldLayoutManagerInterface
        } else if (layoutManager == null || "android.support.v7.widget.LinearLayoutManager" == layoutManager?.javaClass?.canonicalName) {
            val layoutManager = LinearLayoutManagerWithSmoothOffset(mContext)
            layoutManager.orientation = LinearLayoutManager.VERTICAL
            recyclerView?.layoutManager = layoutManager
            this.layoutManager = layoutManager
            shieldLayoutManager = layoutManager
        }
//        nodeAdapter.setNeedAutoDiff(true)
        nodeAdapter.setGroupInfoProvider(this)
        nodeAdapter.setTypeRefreshListener(this)
        nodeAdapter.setProcessorHolder(processorHolder)
        recyclerView?.adapter = nodeAdapter
        nodeAdapter.setNodeList(processorHolder.shieldSectionManager)

        recyclerView?.removeOnScrollListener(cellManagerOnScrollListener)
        recyclerView?.addOnScrollListener(cellManagerOnScrollListener)

        //添加功能集
        featureList.clear()
        val looper = LoopCellGroupsCollector()
        var staggeredGridCollector = StaggeredGridCollector(recyclerView?.layoutManager, nodeAdapter, looper);

        featureList.add(AgentVisibiltyCollector(whiteBoard, looper))
        featureList.add(SectionTitleArrayCollector(whiteBoard, looper))
        featureList.add(TopNodeCollector(nodeAdapter, processorHolder.shieldSectionManager, looper))
        featureList.add(staggeredGridCollector)
        featureList.add(looper)
        featureList.add(agentScrollTop)

        //添加recycleView滚动监听
        scrollListenerList.clear()
        scrollListenerList.add(agentScrollTop)
        scrollListenerList.add(staggeredGridCollector)
        scrollListenerList.add(hotZoneScrollListener)
    }

    override fun updateCells(
            addList: ArrayList<AgentInterface>?,
            updateList: ArrayList<AgentInterface>?,
            deleteList: ArrayList<AgentInterface>?
    ) {

        //删除需要删除的，先删除减小cell大小
        deleteList?.let {
            for (deleteAgent in deleteList) {
                val cellName = getCellName(deleteAgent)
                if (cells.containsKey(cellName)) {
                    cells.remove(cellName)
                }
            }
        }
        updateList?.let {
            val copyOfCells: HashMap<String, Cell> = cells.clone() as HashMap<String, Cell>
            for (updateAgent in it) {
                for ((key, value) in copyOfCells) {
                    if (value.owner === updateAgent) {
                        val temp = value
                        cells.remove(key)
                        temp.key = getCellName(updateAgent)
                        temp.nextCell = null
                        temp.lastCell = null
                        cells[temp.key ?: key] = temp
                    }
                }
            }
        }

        //添加新的
        addList?.let {
            for (addAgent in it) {
                //去除该判断，当前没有sectionCellItems或者sectionCellInterface的模块如果后面updateAgentCell的话会重新加载
//                if (addAgent.sectionCellItems != null || addAgent.sectionCellInterface != null) {
                val cell = Cell().apply {
                    owner = addAgent
                    name = addAgent.agentCellName
                    key = getCellName(addAgent)
                }
                cells[cell.key ?: addAgent.hostName] = cell
            }
        }

        sortedCells = ArrayList(cells.values)
        sortedCells.sortWith(cellComparator)
        //distinct cell 避免cell链上出现相同节点
        sortedCells.distinct()

//        cells.forEach {
//            it.value.lastCell = null
//            it.value.nextCell = null
//        }
        //构造cell链，由于cells中存在旧的cell被重排的情况，会导致链上存在相同的节点，出现死循环。
        for ((index, cell) in sortedCells.withIndex()) {
            if (index > 0) {
                cell.lastCell = sortedCells[index - 1]
                cell.lastCell?.nextCell = cell
            }
        }

        notifyCellChanged()
    }

    override fun notifyCellChanged() {
        handler.removeCallbacks(notifyCellChanged)
        handler.post(notifyCellChanged)
    }

    private var isBuildingCellChain = false

    private var updateAgentCellInterrupted = false

    private fun updateAgentContainer() {
        isBuildingCellChain = true

        var currentCellGroup: ShieldCellGroup? = null
        var currentGroupIndex: CellGroupIndex? = null
        sectionList.clear()
        cellGroups.clear()
        val startTime = System.nanoTime()

        for (cell in sortedCells) {
            val cellGroupIndex = createCellGroupIndex(cell.owner?.index ?: "")
            currentGroupIndex?.let { currentGroupIndex ->
                if (!isSameGroup(currentGroupIndex, cellGroupIndex)) {
                    cellGroups.add(currentCellGroup)
                    currentCellGroup = ShieldCellGroup().apply {
                        groupIndex = cellGroups.size
                        shieldViewCells = ArrayList()
                    }
                }
                currentCellGroup?.let { currentCellGroup ->
                    val startCellTime = System.nanoTime()
                    val shieldViewCell = updateOrInitCell(cell, currentCellGroup, sectionList)
                    val endCellTime = System.nanoTime()
                    Log.d("onecellTime", "timemiles:${endCellTime - startCellTime}")
                    currentCellGroup.shieldViewCells?.add(shieldViewCell)
                }


            } ?: let {
                currentCellGroup = ShieldCellGroup().apply {
                    groupIndex = cellGroups.size
                    shieldViewCells = ArrayList()
                    val startCellTime = System.nanoTime()
                    val shieldViewCell = updateOrInitCell(cell, this, sectionList)
                    val endCellTime = System.nanoTime()
                    Log.d("onecellTime", "timemiles:${endCellTime - startCellTime}")
                    shieldViewCells?.add(shieldViewCell)
                }


            }
            currentGroupIndex = cellGroupIndex
        }
        currentCellGroup?.let { cellGroups.add(it) }

        var endTime = System.nanoTime()

        //考虑到多线程的情形，在执行initAllSections时，已经加入到sectionList的Cell有可能在updateAgentCell中再次被改变
        //这种情形需要重新把cellGroups里的section作为这次notify的实际sectionList
        if (updateAgentCellInterrupted) {
            sectionList.clear()
            cellGroups.forEach {
                it?.shieldViewCells?.forEach {
                    it.forEachNodeChild {
                        it?.let { sectionList.add(it) }
                    }
                }
            }
        }

        //遍历featureList
        processorHolder.shieldSectionManager.initAllSections(sectionList)

        Log.e("fullTime", "timemiles:${endTime - startTime}")
        updateAgentCellInterrupted = false
        isBuildingCellChain = false
        //遍历featureList
        featureList.forEach {
            it.onAdapterNotify(cellGroups)
        }
    }

    private fun updateOrInitCell(
            cell: Cell,
            currentCellGroup: ShieldCellGroup,
            sectionList: ArrayList<ShieldSection>
    ): ShieldViewCell {
        //这个地方需要考虑反复调用时的Cell复用，避免反复创建ShieldViewCell
        val shieldViewCell = cell.shieldViewCell?.shieldSections?.let { oldSections ->
            sectionList.addAll(oldSections)
            cell.shieldViewCell?.groupParent = currentCellGroup

            //重排时被复用的话要清除已生成节点path,为空没生成的不管
            oldSections.forEach {
                it.shieldRows?.forEach {
                    it?.shieldDisplayNodes?.forEach {
                        it?.path = null
                    }
                }
            }
            cell.shieldViewCell
        } ?: let {
            createShieldViewCell(cell, currentCellGroup, sectionList)
        }
        shieldViewCell.viewCellIndex = currentCellGroup.shieldViewCells?.size ?: -1
        cell.shieldViewCell = shieldViewCell
        //遍历featureList
        featureList.forEach {
            it.onCellNodeRefresh(shieldViewCell)
        }
        return shieldViewCell
    }

    protected fun getCellName(agent: AgentInterface?): String? {
        return agent?.let {
            if (TextUtils.isEmpty(it.index)) it.hostName else it.index + ":" + it.hostName
        }
    }

    private fun isSameGroup(currentGroup: CellGroupIndex?, nextGroup: CellGroupIndex?): Boolean {
        if (currentGroup == null || nextGroup == null) return false
        if (currentGroup.groupIndex != nextGroup.groupIndex) {
            return false
        } else if (currentGroup.groupIndex == nextGroup.groupIndex && currentGroup.innerIndex != nextGroup.innerIndex) {
            return true
        } else if (currentGroup.groupIndex == nextGroup.groupIndex && currentGroup.innerIndex == nextGroup.innerIndex) {
            return if (currentGroup.childs == null) {
                true
            } else if (currentGroup.childs != null && nextGroup.childs != null) {
                isSameGroup(currentGroup.childs, nextGroup.childs)
            } else {
                true
            }
        }
        return true
    }

    private fun createShieldViewCell(
            cell: Cell,
            parent: ShieldCellGroup?,
            addList: ArrayList<ShieldSection>
    ): ShieldViewCell {
        return ShieldViewCell().apply vc@{
            key = cell.key
            name = cell.name
            owner = cell.owner
            this.groupParent = parent
            var sectionCellItem = cell.owner?.sectionCellItem ?: let {
                cell.owner?.sectionCellInterface?.let {
                    NodeItemConvertUtils.convertInterfaceToItem(it, mContext, processorHolder)
                }
            }
            sectionCellItem?.let {
                if (it.shouldShow) {
                    this.shouldShow = true
                    processorHolder.cellProcessorChain.startProcessor(it, this, addList)
                } else {
                    this.shouldShow = false
                }
            }
        }
    }

    private fun createCellGroupIndex(indexStr: String): CellGroupIndex? {
        val cellGroupIndex = CellGroupIndex()
        val separator = '.'
        if (indexStr.indexOf(separator) < 0) return null
        val groupIndex = indexStr.substringBefore(separator)
        cellGroupIndex.groupIndex = groupIndex

        val leftIndex = indexStr.substringAfter(separator)
        if (leftIndex.indexOf(separator) < 0) {
            cellGroupIndex.innerIndex = leftIndex
        } else {
            cellGroupIndex.innerIndex = leftIndex.substringBefore(separator)
            cellGroupIndex.childs = createCellGroupIndex(leftIndex.substringAfter(separator))
        }

        return cellGroupIndex
    }

    override fun updateAgentCell(agent: AgentInterface) {
        updateAgentCell(agent, UpdateAgentType.UPDATE_ALL, 0, 0, 0)
    }

    //不包含header和footer
    override fun updateAgentCell(
            agent: AgentInterface?,
            updateAgentType: UpdateAgentType?,
            section: Int,
            row: Int,
            count: Int
    ) {
        /**
         * 全量更新及部分更新原则
         * 先生成新的节点
         * 通过前一个节点或后一个节点寻找开始位置
         * 根据旧的节点获取更多信息，remove时的count或者add remove之类
         * 更新group树
         * 更新SectionManager
         * */
        val targetCell = findCellForAgent(agent)
        targetCell?.shieldViewCell?.let {
            when (updateAgentType) {
                UpdateAgentType.UPDATE_ALL -> {
                    updateCellAll(targetCell)
                }
                UpdateAgentType.INSERT_SECTION -> {
                    insertSection(targetCell, section, count)
                }
                UpdateAgentType.REMOVE_SECTION -> {
                    removeSection(targetCell, section, count)
                }
                UpdateAgentType.UPDATE_SECTION -> {
                    updateSection(targetCell, section, count)
                }
                UpdateAgentType.INSERT_ROW -> {
                    val sectionItem = getSectionItemOfPosition(targetCell, section) ?: return

                    targetCell.shieldViewCell?.shieldSections?.get(section)?.let { shieldSection ->
                        updateRowProvider(sectionItem, shieldSection)
                        shieldSection.notifyRowInsert(row, count)
                    }
                }
                UpdateAgentType.REMOVE_ROW -> {
                    targetCell.shieldViewCell?.shieldSections?.get(section)?.let { targetSection ->
                        //先通知section内变化
                        targetSection.notifyRowRemove(row, count)
                        //变化后为0 说明删空了，sectioncount发生了变化
                        if (targetSection.getRange() == 0) {
                            //直接删除原来的section
                            targetCell.shieldViewCell?.shieldSections?.removeAt(section)
                        } else {
                            val sectionItem = getSectionItemOfPosition(targetCell, section)
                                    ?: return
                            updateRowProvider(sectionItem, targetSection)
                        }
                    }
                }
                UpdateAgentType.UPDATE_ROW -> {
                    val sectionItem = getSectionItemOfPosition(targetCell, section) ?: return

                    targetCell.shieldViewCell?.shieldSections?.get(section)?.let { shieldSection ->
                        updateRowProvider(sectionItem, shieldSection)
                        shieldSection.notifyRowUpdate(row, count)
                    }
                }
                else -> {

                }
            }

        }
        targetCell?.shieldViewCell?.apply {
            //遍历featureList
            featureList.forEach {
                it.onCellNodeRefresh(this)
                it.onAdapterNotify(cellGroups)
            }
        }
    }

    private fun updateCellAll(cell: Cell) {
        var cellSectionList = ArrayList<ShieldSection>()

        var cellIndex: Int = cell.shieldViewCell?.viewCellIndex ?: -1

        var shieldViewCell =
                createShieldViewCell(cell, cell.shieldViewCell?.groupParent, cellSectionList)

        shieldViewCell.viewCellIndex = cellIndex
        cell.shieldViewCell?.groupParent?.shieldViewCells?.set(cellIndex, shieldViewCell)

        cell.shieldViewCell = shieldViewCell

        //如果updateAgentContainer正在执行initAllSection,那么就只更新Cell
        //与全量的initAllSection一起notify
        if (isBuildingCellChain) {
            updateAgentCellInterrupted = true
            return
        }

        val lastCellTail = cell.getLastCellTailSection()
        val startPosition = lastCellTail?.let {
            processorHolder.shieldSectionManager.sectionRangeDispatcher.indexOf(it) + 1
        } ?: 0

        val nextCellHead = cell.getNextCellHeadSection()
        var endPosition = nextCellHead?.let {
            processorHolder.shieldSectionManager.sectionRangeDispatcher.indexOf(it)
        } ?: -1

        if (endPosition < 0)
            endPosition = processorHolder.shieldSectionManager.sectionRangeDispatcher.size

        //        if (cell.shieldViewCell?.shouldShow == false || (cell.shieldViewCell?.shieldSections?.isEmpty() != false)) {
//            //新增
//            cell.shieldViewCell = shieldViewCell
//            processorHolder.shieldSectionManager.sectionRangeDispatcher.addAll(
//                    startPosition,
//                    cellSectionList
//            )
//        } else if (!shieldViewCell.shouldShow || shieldViewCell.shieldSections?.isEmpty() != false) {
//            //删除
//            cell.shieldViewCell = shieldViewCell
//            processorHolder.shieldSectionManager.sectionRangeDispatcher.removeRange(
//                    startPosition,
//                    endPosition
//            )
//        } else {

        //更新

        processorHolder.shieldSectionManager.sectionRangeDispatcher.replaceWithRemoveAndInsert(
                startPosition,
                endPosition,
                cellSectionList
        )
//        }
    }

    private fun insertSection(cell: Cell, sectionPosition: Int, sectionCount: Int) {

        val sectionCellItem = cell.owner?.sectionCellItem ?: let {
            cell.owner?.sectionCellInterface?.let {
                NodeItemConvertUtils.convertInterfaceToItem(it, mContext, processorHolder)
            }
        }

        val sectionList = ArrayList<ShieldSection>()
        for (i in sectionPosition until sectionPosition + sectionCount) {
            //先占位
            val section = ShieldSection().apply sc@{
                cellParent = cell.shieldViewCell
            }
            sectionList.add(section)
        }

        cell.shieldViewCell?.shieldSections?.addAll(sectionPosition, sectionList)

        for (i in sectionPosition until sectionPosition + sectionCount) {
            val sectionItem = sectionCellItem?.sectionItems?.get(i)
            processorHolder.sectionProcessorChain.startProcessor(sectionItem, cell.shieldViewCell?.shieldSections?.get(i))
        }

        markElseNodeOutDate(cell, sectionPosition)

        //如果updateAgentContainer正在执行initAllSection,那么就只更新Cell
        //与全量的initAllSection一起notify
        if (isBuildingCellChain) {
            updateAgentCellInterrupted = true
            return
        }

        val lastSection = if (sectionPosition > 0) {
            cell.shieldViewCell?.shieldSections?.get(sectionPosition - 1)
        } else {
            cell.getLastCellTailSection()
        }

        val sectionStartPosition =
                processorHolder.shieldSectionManager.sectionRangeDispatcher.indexOf(lastSection) + 1

        processorHolder.shieldSectionManager.sectionRangeDispatcher.addAll(
                sectionStartPosition,
                sectionList
        )

    }

    private fun removeSection(cell: Cell, sectionPosition: Int, sectionCount: Int) {
        cell.shieldViewCell?.shieldSections?.removeRange(
                sectionPosition,
                sectionPosition + sectionCount
        )
        markElseNodeOutDate(cell, sectionPosition)

        //如果updateAgentContainer正在执行initAllSection,那么就只更新Cell
        //与全量的initAllSection一起notify
        if (isBuildingCellChain) {
            updateAgentCellInterrupted = true
            return
        }

        val lastSection = if (sectionPosition > 0) {
            cell.shieldViewCell?.shieldSections?.get(sectionPosition - 1)
        } else {
            cell.getLastCellTailSection()
        }
        val sectionStartPosition =
                processorHolder.shieldSectionManager.sectionRangeDispatcher.indexOf(lastSection) + 1
        processorHolder.shieldSectionManager.sectionRangeDispatcher.removeRange(
                sectionStartPosition,
                sectionStartPosition + sectionCount
        )
    }

    private fun updateSection(cell: Cell, sectionPosition: Int, sectionCount: Int) {
        //insert row， romove row，update row
        //通过section的startindex和range计算，再通知adapter
        var newSections = ArrayList<ShieldSection>()
        val position = processorHolder.shieldSectionManager.sectionRangeDispatcher.indexOf(
                cell.shieldViewCell?.shieldSections?.get(sectionPosition)
        )
        for (i in sectionPosition until sectionPosition + sectionCount) {

            val sectionItem = getSectionItemOfPosition(cell, i) ?: continue

            cell.shieldViewCell?.shieldSections?.get(i)?.let {
                val newSection = ShieldSection().apply sc@{
                    cellParent = it.cellParent
                }
                //先占位
                cell.shieldViewCell?.shieldSections?.set(i, newSection)
                processorHolder.sectionProcessorChain.startProcessor(sectionItem, newSection)
                newSections.add(newSection)
            }
        }

        //如果updateAgentContainer正在执行initAllSection,那么就只更新Cell
        //与全量的initAllSection一起notify
        if (isBuildingCellChain) {
            updateAgentCellInterrupted = true
            return
        }

        if (position >= 0) {
            processorHolder.shieldSectionManager.sectionRangeDispatcher.setAll(position, newSections)
        }
    }


    private fun markElseNodeOutDate(cell: Cell, sectionPosition: Int) {
        val lastIndex = cell.shieldViewCell?.shieldSections?.lastIndex ?: -1
        for (i in sectionPosition..lastIndex) {
            cell.shieldViewCell?.shieldSections?.get(i)?.shieldRows?.forEach {
                it?.shieldDisplayNodes?.forEach { node ->
                    node?.path = null
                }
            }
        }
    }

    private fun getSectionItemOfPosition(cell: Cell, sectionPosition: Int): SectionItem? {
        return cell.owner?.sectionCellItem?.sectionItems?.getOrNull(sectionPosition)
                ?: let {
                    cell.owner?.sectionCellInterface?.let {
                        if (sectionPosition < it.sectionCount) {
                            SectionItem().apply {
                                processorHolder.sectionInterfaceProcessorChain.startProcessor(
                                        it,
                                        this,
                                        sectionPosition
                                )
                            }
                        } else null
                    }
                }
    }

    private fun updateRowProvider(sectionItem: SectionItem, shieldSection: ShieldSection) {
        if (sectionItem.isLazyLoad) {
            shieldSection.rowProvider =
                    DefaultShieldRowProvider(sectionItem.lazyLoadRowItemProvider, processorHolder)
        } else {
            shieldSection.rowProvider =
                    DefaultShieldRowProviderWithItem(sectionItem.rowItems, processorHolder)
        }
    }

    inner class CellGroupIndex {
        var groupIndex: String? = null
        var innerIndex: String? = null
        var childs: CellGroupIndex? = null
    }

    fun findCellForAgent(agent: AgentInterface?): Cell? {
        if (agent == null) return null
        val cellName = getCellName(agent)
        val cell = cells[cellName]
        if (cell != null) {
            return cell
        }
        for ((_, value) in cells) {
            if (agent === value.owner) {
                return value
            }
        }
        return null
    }

    /**
     * 获取 Section 内的包含 HEADER，FOOTER 的全局 row index
     *
     * e.g. 假设 Section 的形式如下:
     *      index | cell type
     *      ---------------------
     *          0 | Header Cell,
     *          1 | Row 0,
     *          2 | Row 1,
     *          3 | Row 2,
     *          4 | Footer Cell
     *      当 参数 {@code row} == 0 时，返回值为 1
     *      当 参数 {@code row} == 1 时，返回值为 2
     *      当 参数 {@code row} == 2 时，返回值为 3
     *      当 参数 {@code row} == -1 时，返回值为 0
     *      当 参数 {@code row} == -2 时，返回值为 4
     */
    private fun getOffsetRowPosition(section: ShieldSection, row: Int): Int {

        var innerRowCount: Int = section.shieldRows?.size ?: 0
        if (section.hasHeaderCell) innerRowCount--
        if (section.hasFooterCell) innerRowCount--
        return when {
            (row in 0..(innerRowCount - 1)) -> if (section.hasHeaderCell) row + 1 else row
            row == -1 -> if (section.hasHeaderCell) 0 else -1
            row == -2 -> if (section.hasFooterCell) section.shieldRows?.lastIndex ?: -1 else -1
            else -> -1
        }
    }


    /**
     * 滚动模块的归一方法
     * 支持滚动到模块顶部，某个section顶部，某个row顶部
     * 支持offset
     * 支持平滑滚动
     *
     * 当某个模块并没有实际节点，支持滚动到相应的占位（即下一个模块的顶部）
     * */
    override fun scrollToNode(params: AgentScrollerParams) {
        hotZoneScrollListener.isScrollingForHotZone = true

        val globalPosition = if (params.scope == ScrollScope.PAGE) 0 else {
            params.nodeInfo?.let { getNodeGlobalPosition(it) } ?: -1
        }
        if (globalPosition < 0) {
            return
        }

        val offset = if (params.needAutoOffset) {
            when {
                shieldLayoutManager is SetAutoOffsetInterface -> params.offset + (shieldLayoutManager as SetAutoOffsetInterface).autoOffset
                recyclerView?.adapter is SetAutoOffsetInterface -> params.offset + (recyclerView?.adapter as SetAutoOffsetInterface).autoOffset
                else -> params.offset
            }
        } else {
            params.offset
        }

        shieldLayoutManager?.scrollToPositionWithOffset(globalPosition, offset, params.isSmooth, params.listenerArrayList)

    }

    /**
     * 当某个模块并没有实际节点，支持找到相应的占位（即下一个模块的顶部）
     * */
    override fun getNodeGlobalPosition(nodeInfo: NodeInfo): Int {
        val cell = findCellForAgent(nodeInfo.agent)
        return cell?.let { cell ->
            if (cell.shieldViewCell?.shouldShow == true && cell.shieldViewCell?.shieldSections?.isEmpty() == false) {
                when (nodeInfo.scope) {
                    NodeInfo.Scope.AGENT -> {
                        cell.shieldViewCell?.shieldSections?.getOrNull(0)?.let {
                            processorHolder.shieldSectionManager.sectionRangeDispatcher.getStartPosition(
                                    it as RangeHolder
                            )
                        } ?: -1
                    }
                    NodeInfo.Scope.SECTION -> {
                        cell.shieldViewCell?.shieldSections?.getOrNull(nodeInfo.section)?.let {
                            processorHolder.shieldSectionManager.sectionRangeDispatcher.getStartPosition(
                                    it as RangeHolder
                            )
                        } ?: -1
                    }
                    NodeInfo.Scope.ROW -> {
                        cell.shieldViewCell?.shieldSections?.getOrNull(nodeInfo.section)?.let {
                            var sectionStartPosition =
                                    processorHolder.shieldSectionManager.sectionRangeDispatcher.getStartPosition(
                                            it as RangeHolder
                                    )
                            sectionStartPosition += Math.max(0, it.rangeDispatcher.getStartPosition(
                                    getOffsetRowPosition(it, nodeInfo.cellInfo.row)
                            ))
                            sectionStartPosition
                        } ?: -1
                    }
                    NodeInfo.Scope.HEADER -> {
                        cell.shieldViewCell?.shieldSections?.getOrNull(nodeInfo.section)?.let {
                            if (it.hasHeaderCell) {
                                processorHolder.shieldSectionManager.sectionRangeDispatcher.getStartPosition(
                                        it as RangeHolder
                                )
                            } else -1

                        } ?: -1
                    }
                    NodeInfo.Scope.FOOTER -> {
                        cell.shieldViewCell?.shieldSections?.getOrNull(nodeInfo.section)?.let {
                            if (it.hasFooterCell) {
                                var sectionStartPosition =
                                        processorHolder.shieldSectionManager.sectionRangeDispatcher.getStartPosition(
                                                it as RangeHolder
                                        )
                                sectionStartPosition + it.getRange() - 1
                            } else -1

                        } ?: -1
                    }
                }
            } else {
                //模块未展示
                if (isBuildingCellChain) {
                    -1
                } else {
                    when (nodeInfo.scope) {
                        NodeInfo.Scope.AGENT -> {
                            cell.getNextCellHeadSection()?.let {
                                //滚动到下一个section的头
                                processorHolder.shieldSectionManager.sectionRangeDispatcher.getStartPosition(
                                        it as RangeHolder
                                )
                            } ?: let {
                                cell.getLastCellTailSection()?.let {
                                    val lastStartPosition =
                                            processorHolder.shieldSectionManager.sectionRangeDispatcher.getStartPosition(
                                                    it as RangeHolder
                                            )
                                    lastStartPosition + it.getRange()
                                } ?: -1
                            }
                        }
                        else -> -1
                    }
                }
            }
        } ?: -1
    }

    override fun getAgentInfoByGlobalPosition(globalPosition: Int): NodeInfo? {
        val shieldDisplayNode = nodeAdapter.getDisplayNode(globalPosition)
        val agent = shieldDisplayNode.rowParent?.sectionParent?.cellParent?.owner
        val section = shieldDisplayNode.path?.section ?: -1
        val row = shieldDisplayNode.path?.row ?: -3
        return when (shieldDisplayNode.path?.cellType) {
            CellType.NORMAL -> {
                NodeInfo.row(agent, section, row)
            }
            CellType.HEADER -> {
                NodeInfo.header(agent, section)
            }
            CellType.FOOTER -> {
                NodeInfo.footer(agent, section)
            }
            CellType.LOADING, CellType.LOADING_MORE -> {
                NodeInfo.agent(agent).apply {
                    this.cellInfo.section = section
                    this.cellInfo.row = row
                    this.cellInfo.cellType = shieldDisplayNode.path?.cellType
                }
            }
            else -> null
        }
    }


    fun findShieldViewCell(sectionCellInterface: SectionCellInterface?): ShieldViewCell? {
        sectionCellInterface?.let {
            cellGroups.forEach { shieldCellGroup ->
                shieldCellGroup?.shieldViewCells?.forEach { shieldViewCell ->
                    if (shieldViewCell.owner?.sectionCellInterface == sectionCellInterface) {
                        return shieldViewCell
                    }
                }
            }
        }

        return null
    }

    override fun getGroupPosition(position: Int): Int {
        if (position < 0 || position >= processorHolder.shieldSectionManager.size()) return -1
        val rangeInfo =
                processorHolder.shieldSectionManager.sectionRangeDispatcher.getInnerPosition(position)
        val key = rangeInfo?.let {
            (rangeInfo.obj as ShieldSection).cellParent?.key
        } ?: ""
        return cells[key]?.let {
            sortedCells.indexOf(it)
        } ?: GroupBorderDecoration.GroupInfoProvider.NO_GROUP
    }

    override fun getGroupText(position: Int): String {
        var rangeInfo =
                processorHolder.shieldSectionManager.sectionRangeDispatcher.getInnerPosition(position)
        val cellParent = rangeInfo?.let {
            (rangeInfo.obj as? ShieldSection)?.cellParent
        }
        return "${cellParent?.key}-${cellParent?.owner?.javaClass?.simpleName}-${cellParent?.owner?.sectionCellInterface?.javaClass?.simpleName
                ?: cellParent?.owner?.sectionCellItem?.javaClass?.simpleName}"
    }

    inline fun <T> EntrySetHolder<T>.forEachNodeChild(action: (T?) -> Unit) {
        for (i in 0 until this.getEntryCount()) {
            this.getEntry(i)?.apply(action)
        }
    }

    fun traverseAppearanceEventListener(handler: HandleAppearanceEventListener) {
        cellGroups.forEach { shieldCellGroup ->
            shieldCellGroup?.shieldViewCells?.forEach { shieldViewCell ->
                traverseCellAppearanceEventListener(shieldViewCell, handler)
            }
        }
    }

    fun traverseCellAppearanceEventListener(shieldViewCell: ShieldViewCell, handler: HandleAppearanceEventListener) {
        shieldViewCell.moveStatusEventListenerList?.forEach { cellListener ->
            handler.handleListener(shieldViewCell, cellListener) // shieldViewCell
        }
        shieldViewCell.forEachNodeChild { shieldSection ->
            shieldSection?.moveStatusEventListenerList?.forEach { sectionListener ->
                handler.handleListener(shieldSection, sectionListener)
            }
            shieldSection?.forEachNodeChild { shieldRow ->
                shieldRow?.moveStatusEventListenerList?.forEach { rowListener ->
                    handler.handleListener(shieldRow, rowListener)
                }
                shieldRow?.forEachNodeChild { shieldDisplayNode ->
                    shieldDisplayNode?.moveStatusEventListenerList?.forEach { nodeListener ->
                        handler.handleListener(shieldDisplayNode, nodeListener)
                    }
                }
            }
        }
    }

    override fun startExpose() {
        nodeAdapter.setNeedCalculateAppearDisappearEvent(true)
        nodeAdapter.clearAttachStatus()
        nodeAdapter.updateStatus()
    }

    fun storeCurrentInfo() {
        nodeAdapter.storeCurrentInfo()
    }

    fun loadCurrentInfo() {
        nodeAdapter.loadCurrentInfo()
    }

    fun clearAttachStatus() {
        nodeAdapter.clearAttachStatus()
    }

    fun clearCurrentInfo() {
        nodeAdapter.clearCurrentInfo()
    }

    fun forceAttachStatusUpdate(scrollDirection: ScrollDirection) {
        nodeAdapter.forceUpdateAttachStatus(scrollDirection)
    }

    protected var mExposeHandler: Handler = Handler()

    override fun startExpose(delayMilliseconds: Long) {
        mExposeHandler.postDelayed(object : Runnable {
            override fun run() {
                mExposeHandler.removeCallbacks(this)
                startExpose()
            }
        }, delayMilliseconds)
    }

    override fun finishExpose() {
        mExposeHandler.removeCallbacksAndMessages(null)
        nodeAdapter.clearAttachStatus()
        traverseAppearanceEventListener(
                HandleAppearanceEventListener { item: Any, moveStatusEventListener: MoveStatusEventListener<Any> ->
                    moveStatusEventListener.reset(item)
                })
    }

    override fun pauseExpose() {
        nodeAdapter.setNeedCalculateAppearDisappearEvent(false)
    }

    override fun resumeExpose() {
        nodeAdapter.setNeedCalculateAppearDisappearEvent(true)
        nodeAdapter.updateStatus()
    }

    override fun resetExposeSCI(sectionCellInterface: SectionCellInterface?) {
        findShieldViewCell(sectionCellInterface)?.let { shieldViewCell ->
            traverseCellAppearanceEventListener(shieldViewCell,
                    HandleAppearanceEventListener { item: Any, moveStatusEventListener: MoveStatusEventListener<Any> ->
                        moveStatusEventListener.reset(item)
                    })
        }
        nodeAdapter.updateStatus()
    }

    override fun resetExposeRow(
            sectionCellInterface: SectionCellInterface?,
            section: Int,
            row: Int
    ) {
        findShieldViewCell(sectionCellInterface)?.let { shieldViewCell ->
            shieldViewCell.shieldSections?.get(section)?.apply {
                shieldRows?.get(getOffsetRowPosition(this, row))?.apply shieldRow@{
                    this.moveStatusEventListenerList?.forEach { rowListener ->
                        rowListener.reset(this@shieldRow)
                    }
                    this.shieldDisplayNodes?.forEach { node ->
                        node?.moveStatusEventListenerList?.forEach { nodeListener ->
                            nodeListener.reset(node)
                        }
                    }
                }
            }
        }
        nodeAdapter.updateStatus()

    }

    override fun resetExposeExtraCell(
            extraCellInterface: SectionCellInterface?,
            section: Int,
            cellType: CellType?
    ) {
        findShieldViewCell(extraCellInterface)?.let { shieldViewCell ->
            shieldViewCell.shieldSections?.get(section)?.apply {
                var pos = 0
                if (cellType == CellType.HEADER) {
                    pos = -1
                } else if (cellType == CellType.FOOTER) {
                    pos = -2
                }
                if (pos != 0) {
                    shieldRows?.get(getOffsetRowPosition(this, pos))?.apply shieldRow@{
                        this.moveStatusEventListenerList?.forEach { rowListener ->
                            rowListener.reset(this@shieldRow)
                        }
                        this.shieldDisplayNodes?.forEach { node ->
                            node?.moveStatusEventListenerList?.forEach { nodeListener ->
                                nodeListener.reset(node)
                            }
                        }
                    }
                }
            }
        }
        nodeAdapter.updateStatus()
    }

    fun destory() {
        recyclerView?.removeOnScrollListener(cellManagerOnScrollListener)
        processorHolder.creator = null
        nodeAdapter.clearStoredInfo()
    }

    inner class CellManagerOnScrollListener : RecyclerView.OnScrollListener() {
        @Override
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            scrollListenerList.forEach {
                it.onScrollStateChanged(recyclerView, newState);
            }
        }

        @Override
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            scrollListenerList.forEach {
                it.onScrolled(recyclerView, dx, dy);
            }
        }
    }


    //support old version

    //reuse mapping
    protected var reuseIdentifierMap: HashMap<String, HashMap<String, Int>>? = null
    protected var reuseIdentifierMapForHeader: HashMap<String, HashMap<String, Int>>? = null
    protected var reuseIdentifierMapForFooter: HashMap<String, HashMap<String, Int>>? = null
    protected var cellTypeMap: HashMap<String, HashMap<String, Int>>? = null
    protected var cellTypeMapForHeader: HashMap<String, HashMap<String, Int>>? = null
    protected var cellTypeMapForFooter: HashMap<String, HashMap<String, Int>>? = null


    fun getReuseIdentifierMap(hostName: String): HashMap<String, Int>? {
        return getReuseIdentifierMap(reuseIdentifierMap, hostName)
    }

    fun getReuseIdentifierMapForHeader(hostName: String): HashMap<String, Int>? {
        return getReuseIdentifierMap(reuseIdentifierMapForHeader, hostName)
    }

    fun getReuseIdentifierMapForFooter(hostName: String): HashMap<String, Int>? {
        return getReuseIdentifierMap(reuseIdentifierMapForFooter, hostName)
    }

    fun getCellTypeMap(hostName: String): HashMap<String, Int>? {
        return getReuseIdentifierMap(cellTypeMap, hostName)
    }

    fun getCellTypeMapForHeader(hostName: String): HashMap<String, Int>? {
        return getReuseIdentifierMap(cellTypeMapForHeader, hostName)
    }

    fun getCellTypeMapForFooter(hostName: String): HashMap<String, Int>? {
        return getReuseIdentifierMap(cellTypeMapForFooter, hostName)
    }

    protected fun getReuseIdentifierMap(
            mapCollection: HashMap<String, HashMap<String, Int>>?,
            hostName: String
    ): HashMap<String, Int>? {
        var mapCollection = mapCollection
        if (TextUtils.isEmpty(hostName)) {
            return null
        }

        if (mapCollection == null) {
            mapCollection = HashMap()
        }

        var agentIdentifierMap: HashMap<String, Int>? = mapCollection[hostName]
        if (agentIdentifierMap == null) {
            agentIdentifierMap = HashMap()
            mapCollection[hostName] = agentIdentifierMap
        }

        return agentIdentifierMap
    }

}