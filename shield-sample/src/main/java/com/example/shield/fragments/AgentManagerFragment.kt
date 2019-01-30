package com.example.shield.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dianping.agentsdk.framework.*
import com.dianping.agentsdk.manager.SectionRecyclerCellManager
import com.dianping.shield.bridge.feature.ShieldGlobalFeatureInterface
import com.dianping.shield.framework.ShieldContainerInterface
import com.dianping.shield.framework.ShieldLifeCycler
import com.dianping.shield.manager.LightAgentManager
import java.util.*

/**
 * Created by hezhi on 16/3/3.
 */
abstract class AgentManagerFragment(protected val shieldLifeCycler: ShieldLifeCycler = ShieldLifeCycler()) :
        Fragment(), AgentCellBridgeInterface by shieldLifeCycler,
        ShieldContainerInterface by shieldLifeCycler,
        DriverInterface by shieldLifeCycler, ShieldGlobalFeatureInterface by shieldLifeCycler {

    companion object {
        internal val TAG = AgentManagerFragment::class.java.simpleName
    }

    @JvmField
    internal var cellManager: CellManagerInterface<*>? = null

    @JvmField
    internal var agentManager: AgentManagerInterface? = null

    @JvmField
    internal var whiteBoard: WhiteBoard? = null

    @JvmField
    internal var pageContainer: PageContainerInterface<*>? = null

    init {
        shieldLifeCycler.hostFragment = this
        whiteBoard = shieldLifeCycler.whiteBoard
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //针对复写getWhiteBoard(）方法自定义Whiteboard的情况，需要重新设置一下Whiteboard
        this.whiteBoard = initWhiteBoard()
        this.whiteBoard?.let { shieldLifeCycler.whiteBoard = it }
        shieldLifeCycler.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater?,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        pageContainer = initializePageContainer()
        shieldLifeCycler.pageContainer = pageContainer
        return shieldLifeCycler.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //某些子类会复写onCreateView方法 ，不调super，直接通过访问pageContainer属性设置PageContainer
        //这样会导致shieldLifeCycler中的pageContainer与fragment中的不同步
        //所以在onCreateView之后重新设置一下PageContainer
        if (pageContainer == null) {
            pageContainer = initializePageContainer()
        }
        shieldLifeCycler.pageContainer = pageContainer
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        cellManager = initCellManager()
        cellManager?.let { shieldLifeCycler.cellManager = it }
        agentManager = initAgentManger()
        agentManager?.let { shieldLifeCycler.agentManager = it }
        shieldLifeCycler.shieldConfigs = generaterConfigs()
        shieldLifeCycler.onActivityCreated(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        shieldLifeCycler.onStart()
    }

    override fun onResume() {
        super.onResume()
        shieldLifeCycler.onResume()
    }

    override fun onPause() {
        shieldLifeCycler.onPause()
        super.onPause()
    }

    override fun onStop() {
        shieldLifeCycler.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        shieldLifeCycler.onDestroy()
        cellManager = null
        agentManager = null
        pageContainer = null
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        shieldLifeCycler.onSaveInstanceState(outState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        shieldLifeCycler.onActivityResult(requestCode, resultCode, data)
    }

    @Deprecated(
            "override getCellManager method to custom your CellManager" +
                    " is easy confused and plan to be deprecated," +
                    "use override initCellManager() method instead " +
                    "and initCellManager() method will be called " +
                    "on the fragment's onActivityCreated lifecycle once" +
                    "if your only want to get CellManager instance of this page," +
                    "use com.dianping.shield.framework.ShieldContainerInterface.getHostCellManager() method"
    )
    open fun getCellManager(): CellManagerInterface<*>? {
        if (cellManager == null) {
            cellManager = SectionRecyclerCellManager(context)
        }
        return cellManager
    }

    @Deprecated(
            "override getAgentManager method to custom your AgentManager" +
                    " is easy confused and plan to be deprecated," +
                    "use override initAgentManger() method instead " +
                    "and initAgentManger() method will be called " +
                    "on the fragment's onActivityCreated lifecycle once" +
                    " if your only want to get AgentManager instance of this page," +
                    "use com.dianping.shield.framework.ShieldContainerInterface.getHostAgentManager() method"
    )
    open fun getAgentManager(): AgentManagerInterface? {
        if (agentManager == null) {
            agentManager = LightAgentManager(
                    this,
                    shieldLifeCycler,
                    this,
                    shieldLifeCycler.pageContainer
            )
        }
        return agentManager
    }

    /**
    "override getPageContainer method to custom your PageContainer" +
    " is easy confused and plan to be deprecated," +
    "use override initPageContianer() method instead " +
    "and initPageContianer() method will be called " +
    "on the fragment's onCreateView and onViewCreated lifecycle" +
    "getPageContainer() is only expect to provide host PageContainer "
     */
    open fun getPageContainer(): PageContainerInterface<*>? {
        return shieldLifeCycler.pageContainer
    }


    open fun initCellManager(): CellManagerInterface<*> {
        return getCellManager() ?: SectionRecyclerCellManager(context)
    }

    open fun initAgentManger(): AgentManagerInterface {
        return getAgentManager() ?: LightAgentManager(
                this,
                shieldLifeCycler,
                this,
                shieldLifeCycler.pageContainer
        )
    }

    open fun initializePageContainer(): PageContainerInterface<*>? {
        return getPageContainer()
    }

    open fun initWhiteBoard(): WhiteBoard {
        return getWhiteBoard() ?: shieldLifeCycler.whiteBoard
    }

    open fun isWhiteBoardShared(isShared: Boolean) {
        shieldLifeCycler.isWhiteBoardShared = isShared
    }

    /**
     * 在运行过程中,agentlist有变化的时候调用,更新agentlist列表本身的值,区别于dispatchCellChanged,
     * 单纯只更新现有的agentlist中的agent内容
     *
     * @param savedInstanceState
     */
    override fun resetAgents(savedInstanceState: Bundle?) {
        shieldLifeCycler.shieldConfigs = generaterConfigs()
        shieldLifeCycler.resetAgents(savedInstanceState)
    }

    fun setAgentContainerView(containerView: ViewGroup) {
        shieldLifeCycler.setAgentContainerView(containerView)
    }

    protected fun updateAgentContainer() {
        shieldLifeCycler.updateAgentContainer()
    }

    /**
     * 得到AgentListConfig列表,放在前面的列表会被优先支持
     *
     * @return
     */

    protected abstract fun generaterDefaultConfigAgentList(): ArrayList<AgentListConfig>?

    /**
    "override getWhiteBoard method to custom your WhiteBoard" +
    " is easy confused " +
    "you can use override initCellManager() method instead " +
    "and initCellManager() will be called on the fragment's onCreate lifecycle
    getWhiteBoard() method is only expect to provide host whiteboard to fragment and agents
     */
    override fun getWhiteBoard(): WhiteBoard? {
        return shieldLifeCycler.whiteBoard
    }

    override fun generaterConfigs(): ArrayList<AgentListConfig>? {
        return generaterDefaultConfigAgentList()
    }

    fun setDisableDecoration(disableDecoration: Boolean) {
        shieldLifeCycler.setDisableDecoration(disableDecoration)
    }

    fun setPageName(pageName: String) {
        shieldLifeCycler.setPageName(pageName)
    }

    fun getFeature(): ShieldGlobalFeatureInterface? {
        return this
    }
}