package com.dianping.shield.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dianping.agentsdk.framework.*
import com.dianping.agentsdk.manager.SectionRecyclerCellManager
import com.dianping.shield.framework.ShieldContainerInterface
import com.dianping.shield.framework.ShieldLifeCycler
import com.dianping.shield.manager.LightAgentManager
import java.util.*

/**
 * Created by hezhi on 16/3/3.
 */
abstract class ShieldFragment(private val shieldLifeCycler: ShieldLifeCycler = ShieldLifeCycler()) :
        Fragment(), AgentCellBridgeInterface by shieldLifeCycler,
        UIRDriverInterface by shieldLifeCycler,
        ShieldContainerInterface by shieldLifeCycler {

    companion object {
        internal val TAG = ShieldFragment::class.java.simpleName
    }

    init {
        shieldLifeCycler.hostFragment = this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shieldLifeCycler.whiteBoard = initWhiteBoard()
        shieldLifeCycler.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater?,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        shieldLifeCycler.pageContainer = initializePageContainer()
        return shieldLifeCycler.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        shieldLifeCycler.cellManager = initCellManager()
        shieldLifeCycler.agentManager = initAgentManger()
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

    open fun initCellManager(): CellManagerInterface<*> {
        return SectionRecyclerCellManager(context)
    }

    open fun initAgentManger(): AgentManagerInterface {
        return LightAgentManager(shieldLifeCycler)
    }

    open fun initializePageContainer(): PageContainerInterface<*>? {
        return null
    }

    open fun initWhiteBoard(): WhiteBoard {
        return shieldLifeCycler.whiteBoard
    }

    open fun isWhiteBoardShared(isShared: Boolean) {
        shieldLifeCycler.isWhiteBoardShared = isShared
    }

    fun setAgentContainerView(containerView: ViewGroup) {
        shieldLifeCycler.setAgentContainerView(containerView)
    }

    protected fun updateAgentContainer() {
        shieldLifeCycler.updateAgentContainer()
    }

    fun findAgent(name: String): AgentInterface? {
        return shieldLifeCycler.findAgent(name)
    }

    override fun resetAgents(savedInstanceState: Bundle?) {
        shieldLifeCycler.shieldConfigs = generaterConfigs()
        shieldLifeCycler.resetAgents(savedInstanceState)
    }

    override abstract fun generaterConfigs(): ArrayList<AgentListConfig>?

}