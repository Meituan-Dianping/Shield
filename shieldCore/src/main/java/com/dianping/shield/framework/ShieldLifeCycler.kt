package com.dianping.shield.framework

import android.content.Intent
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.dianping.agentsdk.framework.*
import com.dianping.agentsdk.manager.SectionRecyclerCellManager
import com.dianping.agentsdk.pagecontainer.CommonPageContainerFunctionInterface
import com.dianping.agentsdk.sectionrecycler.layoutmanager.OnSmoothScrollListener
import com.dianping.shield.bridge.feature.*
import com.dianping.shield.entity.*
import com.dianping.shield.feature.ExposeScreenLoadedInterface
import com.dianping.shield.feature.LoadingAndLoadingMoreCreator
import com.dianping.shield.manager.ShieldNodeCellManager
import com.dianping.shield.node.adapter.DisplayNodeContainer
import com.dianping.shield.sectionrecycler.ShieldLayoutManagerInterface

/**
 * Create By zhi.he at 2018/4/27
 */
open class ShieldLifeCycler : AgentCellBridgeInterface, UIRDriverInterface,
        ShieldContainerInterface, ShieldGlobalFeatureInterface {
    open lateinit var hostFragment: Fragment

    lateinit var agentManager: AgentManagerInterface

    lateinit var cellManager: CellManagerInterface<*>

    var pageContainer: PageContainerInterface<*>? = null

    var shieldLayoutManager: RecyclerView.LayoutManager? = null

    var whiteBoard: WhiteBoard = WhiteBoard()
        @JvmName("getHostWhiteBoard")
        get() {
            return field
        }
    var shieldConfigs: ArrayList<AgentListConfig>? = null

    private var isPauseing = false

    var isWhiteBoardShared = false

    constructor()

    constructor(hostFragment: Fragment) {
        this.hostFragment = hostFragment
    }

    open fun onCreate(savedInstanceState: Bundle?) {
        whiteBoard.onCreate(savedInstanceState)
        pageContainer?.onCreate(savedInstanceState)
    }

    open fun onCreateView(
            inflater: LayoutInflater?,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return pageContainer?.onCreateView(inflater, container, savedInstanceState)
    }

    open fun onActivityCreated(savedInstanceState: Bundle?) {
        if (cellManager is SectionRecyclerCellManager) {
            (cellManager as SectionRecyclerCellManager).setWhiteBoard(whiteBoard)
            if (hostFragment.activity is LoadingAndLoadingMoreCreator) {
                (cellManager as SectionRecyclerCellManager).setDefaultLoadingAndLoadingMoreCreator(
                        hostFragment.activity as LoadingAndLoadingMoreCreator
                )
            }
        }
        //add node support
        if (cellManager is ShieldNodeCellManager) {
            (cellManager as ShieldNodeCellManager).setWhiteBoard(whiteBoard)
            if (hostFragment.activity is LoadingAndLoadingMoreCreator) {
                (cellManager as ShieldNodeCellManager).setLoadingAndLoadingMoreCreator(hostFragment.activity as LoadingAndLoadingMoreCreator)
            }
            if (pageContainer is CommonPageContainerFunctionInterface) {

                val bottomContainer = ZFrameLayout(hostFragment.context)
                val blp: FrameLayout.LayoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                (pageContainer as CommonPageContainerFunctionInterface).recyclerViewLayout.addView(bottomContainer, blp)
                (cellManager as ShieldNodeCellManager).innerSetBottomContainer(bottomContainer)

                val topContainer = ZFrameLayout(hostFragment.context)
                val lp: FrameLayout.LayoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                (pageContainer as CommonPageContainerFunctionInterface).recyclerViewLayout.addView(topContainer, lp)
                (cellManager as ShieldNodeCellManager).innerSetTopContainer(topContainer)
            }
        }
        agentManager.setupAgents(savedInstanceState, shieldConfigs)
        pageContainer?.onActivityCreated(savedInstanceState)
        pageContainer?.agentContainerView?.let { setAgentContainerView(it) }
    }

    open fun onStart() {
        agentManager.startAgents()
    }

    open fun onResume() {
        agentManager.resumeAgents()
        pageContainer?.onResume()
        if (this::hostFragment.isInitialized && isPauseing) {
            when (cellManager) {
                is SectionRecyclerCellManager -> (cellManager as SectionRecyclerCellManager).dispatchAgentAppearWithLifecycle(ScrollDirection.PAGE_RESUME)
                is ShieldNodeCellManager -> {
                    (cellManager as ShieldNodeCellManager).clearAttachStatus()
                    (cellManager as ShieldNodeCellManager).loadCurrentInfo()
                    (cellManager as ShieldNodeCellManager).forceAttachStatusUpdate(ScrollDirection.PAGE_RESUME)
                }
            }
        }
        isPauseing = false
    }

    open fun onPause() {
        // agents
        agentManager.pauseAgents()
        pageContainer?.onPause()
        if (this::hostFragment.isInitialized) {
            when (cellManager) {
                is SectionRecyclerCellManager -> {
                    if (hostFragment.activity.isFinishing) {
                        (cellManager as SectionRecyclerCellManager).dispatchAgentDisappearWithLifecycle(ScrollDirection.PAGE_BACK)
                    } else {
                        (cellManager as SectionRecyclerCellManager).storeMoveStatusMap()
                        (cellManager as SectionRecyclerCellManager).dispatchAgentDisappearWithLifecycle(ScrollDirection.PAGE_AHEAD)
                    }
                }
                is ShieldNodeCellManager -> {
                    if (hostFragment.activity.isFinishing) {
                        (cellManager as ShieldNodeCellManager).clearCurrentInfo()
                        (cellManager as ShieldNodeCellManager).forceAttachStatusUpdate(ScrollDirection.PAGE_BACK)
                    } else {
                        (cellManager as ShieldNodeCellManager).storeCurrentInfo()
                        (cellManager as ShieldNodeCellManager).clearCurrentInfo()
                        (cellManager as ShieldNodeCellManager).forceAttachStatusUpdate(ScrollDirection.PAGE_AHEAD)
                    }
                }
            }

        }
        isPauseing = true
    }

    open fun onStop() {
        // agents
        agentManager.stopAgents()
        pageContainer?.onStop()
    }

    open fun onDestroy() {
        if (this::cellManager.isInitialized) {
            if (cellManager is ExposeScreenLoadedInterface) {
                (cellManager as? ExposeScreenLoadedInterface)?.finishExpose()
            }
            if (cellManager is SectionRecyclerCellManager) {
                (cellManager as? SectionRecyclerCellManager)?.destory()
            }
            if (cellManager is ShieldNodeCellManager) {
                (cellManager as? ShieldNodeCellManager)?.destory()
            }
        }
        if (this::agentManager.isInitialized) {
            agentManager.destroyAgents()
        }
        if (!isWhiteBoardShared) {
            whiteBoard.onDestory()
        }
        pageContainer?.onDestroy()
        isPauseing = false
    }

    open fun onSaveInstanceState(outState: Bundle?) {
        if (this::agentManager.isInitialized) {
            agentManager.onSaveInstanceState(outState)
        }
        whiteBoard.onSaveInstanceState(outState)
        pageContainer?.onSaveInstanceState(outState)
    }

    open fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // agents
        if (this::agentManager.isInitialized) {
            agentManager.onActivityResult(requestCode, resultCode, data)
        }
        pageContainer?.onActivityResult(requestCode, resultCode, data)
    }

    fun setAgentContainerView(containerView: ViewGroup) {
        try {
            cellManager.setAgentContainerView(containerView)
            agentManager.initViewCell()
            if (containerView is RecyclerView) {
                shieldLayoutManager = containerView.layoutManager
            }
            notifyCellChanged()
        } catch (e: Exception) {
            throw NullPointerException("setAgentContainerView method should be called after super.onActivityCreated method")
        }
    }

    fun updateAgentContainer() {
        if (this::cellManager.isInitialized) {
            cellManager.notifyCellChanged()
        }
    }

    override fun findAgent(name: String): AgentInterface? {
        if (this::agentManager.isInitialized) {
            return agentManager.findAgent(name)
        }
        return null
    }

    fun notifyCellChanged() {
        if (this::cellManager.isInitialized) {
            cellManager.notifyCellChanged()
        }
    }

    override fun updateCells(
            addList: ArrayList<AgentInterface>?,
            updateList: ArrayList<AgentInterface>?,
            deleteList: ArrayList<AgentInterface>?
    ) {
        cellManager.updateCells(addList, updateList, deleteList)
    }


    override fun updateAgentCell(agent: AgentInterface) {
        if (this::cellManager.isInitialized) {
            cellManager.updateAgentCell(agent)
        }
    }

    override fun updateAgentCell(
            agent: AgentInterface,
            updateAgentType: UpdateAgentType,
            section: Int,
            row: Int,
            count: Int
    ) {
        if (cellManager is UIRCellManagerInterface<*>) {
            (cellManager as UIRCellManagerInterface<*>).updateAgentCell(
                    agent,
                    updateAgentType,
                    section,
                    row,
                    count
            )
        } else {
            cellManager.updateAgentCell(agent)
        }
    }

    /**
     * 在运行过程中,agentlist有变化的时候调用,更新agentlist列表本身的值,区别于dispatchCellChanged,
     * 单纯只更新现有的agentlist中的agent内容
     *
     * @param savedInstanceState
     */
    override fun resetAgents(savedInstanceState: Bundle?) {
        if (this::agentManager.isInitialized) {
            agentManager.resetAgents(savedInstanceState, shieldConfigs)
        }
        if (this::cellManager.isInitialized && cellManager is SectionRecyclerCellManager) {
            (cellManager as SectionRecyclerCellManager).resetHotZone()
        }
    }

    override fun generaterConfigs(): ArrayList<AgentListConfig>? {
        return shieldConfigs;
    }

    override fun getHostCellManager(): CellManagerInterface<*>? {
        if (this::cellManager.isInitialized) {
            return cellManager
        }
        return null
    }

    override fun getHostAgentManager(): AgentManagerInterface? {
        if (this::agentManager.isInitialized) {
            return agentManager
        }
        return null
    }

    override fun getWhiteBoard(): WhiteBoard? {
        return whiteBoard
    }

    fun setDisableDecoration(disableDecoration: Boolean) {
        var tempCellManager = getHostCellManager()
        if (tempCellManager is SectionRecyclerCellManager) {
            tempCellManager.setDisableDecoration(disableDecoration)
        } else if (tempCellManager is ShieldNodeCellManager) {
            tempCellManager.setDisableDecoration(disableDecoration)
        }
    }

    fun setPageName(pageName: String) {
        var tempCellManager = getHostCellManager()
        if (tempCellManager is SectionRecyclerCellManager) {
            if (!TextUtils.isEmpty(pageName)) {
                tempCellManager.setPageName(pageName)
            }
        } else if (tempCellManager is ShieldNodeCellManager) {
            if (!TextUtils.isEmpty(pageName)) {
                tempCellManager.setPageName(pageName)
            }
        }
    }

    //ShieldGlobalFeatureInterface implement


    override fun setPageDividerTheme(themeParams: PageDividerThemeParams) {
        var tempCellManager = getHostCellManager()
        when (themeParams.dividerTheme) {
            DividerTheme.DEFAULT_LEFT_OFFSET -> {
                if (themeParams.params is Int) {
                    if (tempCellManager is SectionRecyclerCellManager) {
                        tempCellManager.setDefaultOffset(ViewUtils.dip2px(hostFragment.context, (themeParams.params as Int).toFloat()).toFloat())
                    } else if (tempCellManager is ShieldNodeCellManager) {
                        tempCellManager.getDividerThemePackage().defaultDividerOffset?.left =
                                ViewUtils.dip2px(hostFragment.context, (themeParams.params as Int).toFloat())
                    }
                }
            }
            DividerTheme.DEFAULT_DIVIDER -> {
                if (themeParams.params is Drawable) {
                    var defaultDivider = themeParams.params as Drawable
                    if (tempCellManager is SectionRecyclerCellManager) {
                        tempCellManager.setDefaultDivider(defaultDivider)
                    } else if (tempCellManager is ShieldNodeCellManager) {
                        tempCellManager.getDividerThemePackage().defaultDivider = defaultDivider
                    }
                } else if (themeParams.params == null) {
                    if (tempCellManager is SectionRecyclerCellManager) {
                        tempCellManager.setDefaultDivider(null)
                    } else if (tempCellManager is ShieldNodeCellManager) {
                        tempCellManager.getDividerThemePackage().defaultDivider = null
                    }
                }
            }
            DividerTheme.DEFAULT_RIGHT_OFFSET -> {
                if (themeParams.params is Int) {
                    if (tempCellManager is SectionRecyclerCellManager) {
                        tempCellManager.setDefaultRightOffset(ViewUtils.dip2px(hostFragment.context, (themeParams.params as Int).toFloat()).toFloat())
                    } else if (tempCellManager is ShieldNodeCellManager) {
                        tempCellManager.getDividerThemePackage().defaultDividerOffset?.right =
                                ViewUtils.dip2px(hostFragment.context, (themeParams.params as Int).toFloat())
                    }
                }
            }
            DividerTheme.DEFAULT_SECTION_DIVIDER -> {
                if (themeParams.params is Drawable) {
                    var defaultSectionDivider = themeParams.params as Drawable
                    if (tempCellManager is SectionRecyclerCellManager) {
                        tempCellManager.setDefaultSectionDivider(defaultSectionDivider)
                    } else if (tempCellManager is ShieldNodeCellManager) {
                        tempCellManager.getDividerThemePackage().defaultSectionDivider = defaultSectionDivider
                    }
                } else if (themeParams.params == null) {
                    if (tempCellManager is SectionRecyclerCellManager) {
                        tempCellManager.setDefaultSectionDivider(null)
                    } else if (tempCellManager is ShieldNodeCellManager) {
                        tempCellManager.getDividerThemePackage().defaultSectionDivider = null
                    }
                }
            }
            DividerTheme.DEFAULT_SECTION_DIVIDER_OFFSET -> {
                if (themeParams.params is Rect && tempCellManager is ShieldNodeCellManager) {
                    tempCellManager.getDividerThemePackage().defaultSectionDividerOffset = themeParams.params as Rect
                }
            }
            DividerTheme.DEFAULT_SECTION_TOP_DIVIDER -> {
                if (themeParams.params is Drawable && tempCellManager is ShieldNodeCellManager) {
                    tempCellManager.getDividerThemePackage().defaultSectionTopDivider = themeParams.params as Drawable
                } else if (themeParams.params == null && tempCellManager is ShieldNodeCellManager) {
                    tempCellManager.getDividerThemePackage().defaultSectionTopDivider = null
                }
            }
            DividerTheme.DEFAULT_SECTION_BOTTOM_DIVIDER -> {
                if (themeParams.params is Drawable && tempCellManager is ShieldNodeCellManager) {
                    tempCellManager.getDividerThemePackage().defaultSectionBottomDivider = themeParams.params as Drawable
                } else if (themeParams.params == null && tempCellManager is ShieldNodeCellManager) {
                    tempCellManager.getDividerThemePackage().defaultSectionBottomDivider = null
                }
            }
            DividerTheme.DEFAULT_HEADER_HEIGHT -> {
                if (themeParams.params is Int) {
                    if (tempCellManager is SectionRecyclerCellManager) {
                        tempCellManager.setDefaultSpaceHight(ViewUtils.dip2px(hostFragment.context, (themeParams.params as Int).toFloat()).toFloat())
                    } else if (tempCellManager is ShieldNodeCellManager) {
                        tempCellManager.getDividerThemePackage().defaultHeaderHeight = themeParams.params as Int
                    }
                }
            }
            DividerTheme.DEFAULT_FOOTER_HEIGHT -> {
                if (themeParams.params is Int && tempCellManager is ShieldNodeCellManager) {
                    tempCellManager.getDividerThemePackage().defaultFooterHeight = themeParams.params as Int
                }
            }
            DividerTheme.NEED_ADD_FIRST_HEADER -> {
                if (themeParams.params is Boolean && tempCellManager is ShieldNodeCellManager) {
                    tempCellManager.getDividerThemePackage().needAddFirstHeader = themeParams.params as Boolean
                }
            }
            DividerTheme.NEED_ADD_LAST_FOOTER -> {
                if (themeParams.params is Boolean) {
                    var needAddLastFooter = themeParams.params as Boolean
                    if (tempCellManager is SectionRecyclerCellManager) {
                        tempCellManager.setBottomFooterDivider(needAddLastFooter)
                    } else if (tempCellManager is ShieldNodeCellManager) {
                        tempCellManager.getDividerThemePackage().needAddLastFooter = needAddLastFooter
                    }
                }
            }
            DividerTheme.FIRST_HEADER_EXTRA_HEIGHT -> {
                if (themeParams.params is Int && tempCellManager is ShieldNodeCellManager) {
                    tempCellManager.getDividerThemePackage().firstHeaderExtraHeight = themeParams.params as Int
                }
            }
            DividerTheme.LAST_FOOTER_EXTRA_HEIGHT -> {
                if (themeParams.params is Int && tempCellManager is ShieldNodeCellManager) {
                    tempCellManager.getDividerThemePackage().lastFooterExtraHeight = themeParams.params as Int
                }
            }
            DividerTheme.DEFAULT_SPACE_DRAWABLE -> {
                if (themeParams.params is Drawable) {
                    var defaultSpaceDrawable = themeParams.params as Drawable
                    if (tempCellManager is SectionRecyclerCellManager) {
                        tempCellManager.setDefaultSpaceDrawable(defaultSpaceDrawable)
                    } else if (tempCellManager is ShieldNodeCellManager) {
                        tempCellManager.getDividerThemePackage().defaultSpaceDrawable = defaultSpaceDrawable
                    }
                } else if (themeParams.params == null) {
                    if (tempCellManager is SectionRecyclerCellManager) {
                        tempCellManager.setDefaultSpaceDrawable(null)
                    } else if (tempCellManager is ShieldNodeCellManager) {
                        tempCellManager.getDividerThemePackage().defaultSpaceDrawable = null
                    }
                }
            }
            DividerTheme.ENABLE_DIVIDER -> {
                if (themeParams.params is Boolean) {
                    var enableDivider = themeParams.params as Boolean
                    if (tempCellManager is SectionRecyclerCellManager) {
                        tempCellManager.setEnableDivider(enableDivider)
                    } else if (tempCellManager is ShieldNodeCellManager) {
                        tempCellManager.getDividerThemePackage().enableDivider = enableDivider
                    }
                }
            }
        }
    }

    override fun callExposeAction(exposedParam: ExposeAction) {
        if (getHostCellManager() is ExposeScreenLoadedInterface) {
            var exposeScreenLoadedInterface = getHostCellManager() as ExposeScreenLoadedInterface
            when (exposedParam.actionType) {
                ExposeControlActionType.ACTION_START_EXPOSE -> {
                    exposeScreenLoadedInterface.startExpose(exposedParam.startDelay)
                }
                ExposeControlActionType.ACTION_FINISH_EXPOSE -> {
                    exposeScreenLoadedInterface.finishExpose()
                }
                ExposeControlActionType.ACTION_RESUME_EXPOSE -> {
                    exposeScreenLoadedInterface.resumeExpose()
                }
                ExposeControlActionType.ACTION_PAUSE_EXPOSE -> {
                    exposeScreenLoadedInterface.pauseExpose()
                }
                ExposeControlActionType.ACTION_RESET_AGENT_EXPOSE_HISTORY -> {
                    exposedParam.agent?.let { agent ->
                        agent.sectionCellInterface?.let { sci ->
                            exposedParam.cellInfo?.let {
                                when (it.cellType) {
                                    CellType.NORMAL -> {
                                        exposeScreenLoadedInterface.resetExposeRow(sci, it.section, it.row)
                                    }
                                    CellType.HEADER, CellType.FOOTER -> {
                                        exposeScreenLoadedInterface.resetExposeExtraCell(sci, it.section, it.cellType)
                                    }
                                    else -> {
                                    }
                                }
                            } ?: let {
                                exposeScreenLoadedInterface.resetExposeSCI(sci)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun getNodeGlobalPosition(nodeInfo: NodeInfo): Int {
        return if (getHostCellManager() is AgentGlobalPositionInterface) {
            (getHostCellManager() as AgentGlobalPositionInterface).getNodeGlobalPosition(nodeInfo)
        } else -1
    }


    override fun getAgentInfoByGlobalPosition(globalPosition: Int): NodeInfo? {
        return if (getHostCellManager() is AgentGlobalPositionInterface) {
            (getHostCellManager() as AgentGlobalPositionInterface).getAgentInfoByGlobalPosition(globalPosition)
        } else null
    }

    override fun getChildAtIndex(index: Int, isBizView: Boolean): View? {
        val view = when {
            pageContainer is LayoutPositionFuctionInterface -> (pageContainer as LayoutPositionFuctionInterface).getChildAtIndex(index, isBizView)
            shieldLayoutManager is LayoutPositionFuctionInterface -> (shieldLayoutManager as LayoutPositionFuctionInterface).getChildAtIndex(index, isBizView)
            else -> shieldLayoutManager?.getChildAt(index)
        }
        return if (isBizView && view is DisplayNodeContainer) {
            view.subView
        } else view
    }

    override fun findViewAtPosition(position: Int, isBizView: Boolean): View? {
        val view = when {
            pageContainer is LayoutPositionFuctionInterface -> (pageContainer as LayoutPositionFuctionInterface).findViewAtPosition(position, isBizView)
            shieldLayoutManager is LayoutPositionFuctionInterface -> (shieldLayoutManager as LayoutPositionFuctionInterface).findViewAtPosition(position, isBizView)
            else -> shieldLayoutManager?.findViewByPosition(position)
        }
        return if (isBizView && view is DisplayNodeContainer) {
            view.subView
        } else view
    }

    override fun getChildCount(): Int {
        return when {
            pageContainer is LayoutPositionFuctionInterface -> (pageContainer as LayoutPositionFuctionInterface).getChildCount()
            shieldLayoutManager is LayoutPositionFuctionInterface -> (shieldLayoutManager as LayoutPositionFuctionInterface).getChildCount()
            else -> shieldLayoutManager?.childCount ?: 0
        }
    }

    override fun scrollToNode(info: AgentScrollerParams) {
        if (getHostCellManager() is AgentScrollerInterface) {
            (getHostCellManager() as AgentScrollerInterface).scrollToNode(info)
        }
    }

    override fun scrollToPositionWithOffset(globalPosition: Int, offset: Int, isSmoothScroll: Boolean) {
        when {
            pageContainer is ShieldLayoutManagerInterface ->
                (pageContainer as ShieldLayoutManagerInterface).scrollToPositionWithOffset(globalPosition, offset, isSmoothScroll)
            shieldLayoutManager is ShieldLayoutManagerInterface ->
                (shieldLayoutManager as ShieldLayoutManagerInterface).scrollToPositionWithOffset(globalPosition, offset, isSmoothScroll)
            else -> {
            }
        }
    }

    override fun scrollToPositionWithOffset(globalPosition: Int, offset: Int, isSmoothScroll: Boolean, listeners: ArrayList<OnSmoothScrollListener>?) {
        when {
            pageContainer is ShieldLayoutManagerInterface ->
                (pageContainer as ShieldLayoutManagerInterface).scrollToPositionWithOffset(globalPosition, offset, isSmoothScroll, listeners)
            shieldLayoutManager is ShieldLayoutManagerInterface ->
                (shieldLayoutManager as ShieldLayoutManagerInterface).scrollToPositionWithOffset(globalPosition, offset, isSmoothScroll, listeners)
            else -> {
            }
        }
    }

    override fun findFirstVisibleItemPosition(completely: Boolean): Int {
        return when {
            pageContainer is ShieldLayoutManagerInterface ->
                (pageContainer as ShieldLayoutManagerInterface).findFirstVisibleItemPosition(completely)
            shieldLayoutManager is ShieldLayoutManagerInterface ->
                (shieldLayoutManager as ShieldLayoutManagerInterface).findFirstVisibleItemPosition(completely)
            else -> -1
        }
    }

    override fun findLastVisibleItemPosition(completely: Boolean): Int {
        return when {
            pageContainer is ShieldLayoutManagerInterface ->
                (pageContainer as ShieldLayoutManagerInterface).findLastVisibleItemPosition(completely)
            shieldLayoutManager is ShieldLayoutManagerInterface ->
                (shieldLayoutManager as ShieldLayoutManagerInterface).findLastVisibleItemPosition(completely)
            else -> -1
        }
    }

    /**
     * ShieldNodeCellManager will add rootView to a DisplayNodeContainer for each row.
     * It makes rootView.getTop() method return 0 as a wrong value.
     * try to find the DisplayNodeContainer of the view and return DisplayNodeContainer's getTop value to fix this bug
     *
     * @param rootView the rootView of row
     */
    override fun getViewParentRect(rootView: View?): Rect? {
        if (pageContainer is PageContainerCommonFunctionInterface) {
            return (pageContainer as PageContainerCommonFunctionInterface).getViewParentRect(rootView)
        }
        return null
    }


    override fun getItemView(view: View?): View? {
        if (pageContainer is PageContainerCommonFunctionInterface) {
            return (pageContainer as PageContainerCommonFunctionInterface).getItemView(view)
        }
        return null
    }

    override fun getItemViewTop(view: View?): Int {
        if (pageContainer is PageContainerCommonFunctionInterface) {
            return (pageContainer as PageContainerCommonFunctionInterface).getItemViewTop(view)
        }
        return 0
    }

    override fun getItemViewBottom(view: View?): Int {
        if (pageContainer is PageContainerCommonFunctionInterface) {
            return (pageContainer as PageContainerCommonFunctionInterface).getItemViewBottom(view)
        }
        return 0
    }

    override fun getChildAdapterPosition(child: View): Int {
        if (pageContainer is PageContainerCommonFunctionInterface) {
            return (pageContainer as PageContainerCommonFunctionInterface).getChildAdapterPosition(child)
        }
        return -1
    }

    override fun getItemViewLeft(view: View?): Int {
        if (pageContainer is PageContainerCommonFunctionInterface) {
            return (pageContainer as PageContainerCommonFunctionInterface).getItemViewLeft(view)
        }
        return 0
    }

    override fun getItemViewRight(view: View?): Int {
        if (pageContainer is PageContainerCommonFunctionInterface) {
            return (pageContainer as PageContainerCommonFunctionInterface).getItemViewRight(view)
        }
        return 0
    }

    override fun getItemViewWidth(view: View?): Int {
        if (pageContainer is PageContainerCommonFunctionInterface) {
            return (pageContainer as PageContainerCommonFunctionInterface).getItemViewWidth(view)
        }
        return 0
    }

    override fun getItemViewHeight(view: View?): Int {
        if (pageContainer is PageContainerCommonFunctionInterface) {
            return (pageContainer as PageContainerCommonFunctionInterface).getItemViewHeight(view)
        }
        return 0
    }

    override fun setFocusChildScrollOnScreenWhenBack(allow: Boolean) {
        if (pageContainer is PageContainerCommonFunctionInterface) {
            (pageContainer as PageContainerCommonFunctionInterface).setFocusChildScrollOnScreenWhenBack( allow )
        }
    }
    //ShieldGlobalFeatureInterface implement end
}