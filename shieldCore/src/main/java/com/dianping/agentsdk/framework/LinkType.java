package com.dianping.agentsdk.framework;

/**
 * In AgentFragment
 * sas.agentfragment.view
 * <p>
 * Created by runqi.wei
 * 12:01
 * 20.06.2016.
 */
public class LinkType {

    public enum Previous {
        //section之间不连接
        DEFAULT,
        //与前一个section连接
        LINK_TO_PREVIOUS,
        //禁止前一个section与我连接
        DISABLE_LINK_TO_PREVIOUS
    }

    public enum Next {
        //section之间不连接
        DEFAULT,
        //与后一个section连接
        LINK_TO_NEXT,
        //禁止后一个section与我连接
        DISABLE_LINK_TO_NEXT,
        //Internal Use Only
        LINK_UNSAFE_BETWEEN_GROUP
    }
}