package com.dianping.shield.node.processor

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.dianping.agentsdk.framework.LinkType
import com.dianping.agentsdk.framework.ViewUtils
import com.dianping.shield.node.PositionType
import com.dianping.shield.node.cellnode.ShieldDisplayNode
import com.dianping.shield.node.cellnode.ShieldSection

/**
 * Created by zhi.he on 2018/6/25.
 */
class NodeCreator {
    companion object {
        const val viewTypeSepreator: String = "*"

        const val LOADING_TYPE = "(loading)"

        const val LOADING_TYPE_CUSTOM = "(loadingcustom)"

        const val FAILED_TYPE = "(failed)"

        const val FAILED_TYPE_CUSTOM = "(failedcustom)"

        const val EMPTY_TYPE = "(empty)"

        const val EMPTY_TYPE_CUSTOM = "(emptycustom)"

        const val LOADING_MORE_TYPE = "(loadingmore)"

        const val LOADING_MORE_TYPE_CUSTOM = "(loadingmorecustom)"

        const val LOADING_MORE_FAILED_TYPE = "(loadingmorefailed)"

        const val LOADING_MORE_FAILED_TYPE_CUSTOM = "(loadingmorefailedcustom)"

        @JvmStatic
        private fun isLinkable(lastSection: ShieldSection, thisSection: ShieldSection): Boolean {
            return when {
                lastSection.cellParent?.groupParent === thisSection.cellParent?.groupParent -> lastSection.adjustedNextType != LinkType.Next.DISABLE_LINK_TO_NEXT
                        && thisSection.adjustedPreviousType != LinkType.Previous.DISABLE_LINK_TO_PREVIOUS
                        && (lastSection.adjustedNextType == LinkType.Next.LINK_TO_NEXT
                        || thisSection.adjustedPreviousType == LinkType.Previous.LINK_TO_PREVIOUS)
                lastSection.adjustedNextType == LinkType.Next.LINK_UNSAFE_BETWEEN_GROUP -> thisSection.adjustedPreviousType != LinkType.Previous.DISABLE_LINK_TO_PREVIOUS
                else -> false
            }
        }

        @JvmStatic
        fun adjustSectionLinkType(lastSection: ShieldSection?, thisSection: ShieldSection) {

            //在group分界中调整Section
            if (lastSection?.cellParent?.groupParent != thisSection.cellParent?.groupParent) {
                if (lastSection?.nextLinkType != LinkType.Next.LINK_UNSAFE_BETWEEN_GROUP) {
                    lastSection?.adjustedNextType = LinkType.Next.DISABLE_LINK_TO_NEXT
                    thisSection.adjustedPreviousType = LinkType.Previous.DISABLE_LINK_TO_PREVIOUS
                } else {
                    lastSection.adjustedNextType = lastSection.nextLinkType
                    thisSection.adjustedPreviousType = thisSection.previousLinkType
                }

            } else if (lastSection?.cellParent != thisSection.cellParent) {
                //group内的模块分界

                lastSection?.nextLinkType?.let {
                    lastSection.adjustedNextType = it
                } ?: let {
                    //未定制时设置成Link
                    lastSection?.adjustedNextType = LinkType.Next.LINK_TO_NEXT
                }

                thisSection.previousLinkType?.let {
                    thisSection.adjustedPreviousType = it
                } ?: let {
                    thisSection.adjustedPreviousType = LinkType.Previous.LINK_TO_PREVIOUS
                }
            } else {
                lastSection?.adjustedNextType = lastSection?.nextLinkType
                thisSection.adjustedPreviousType = thisSection.previousLinkType
            }

//            调整完LinkType之后再计算
            lastSection?.let {
                if (NodeCreator.isLinkable(it, thisSection)) {
                    when (it.sectionPositionType) {
                        PositionType.SINGLE -> it.sectionPositionType = PositionType.FIRST
                        PositionType.LAST -> it.sectionPositionType = PositionType.MIDDLE
                        else -> {
                        }
                    }

                    when (thisSection.sectionPositionType) {
                        PositionType.SINGLE -> thisSection.sectionPositionType = PositionType.LAST
                        PositionType.FIRST -> thisSection.sectionPositionType = PositionType.MIDDLE
                        PositionType.UNKNOWN -> {
                            thisSection.sectionPositionType = PositionType.LAST
                        }
                    }
                } else {
                    when (it.sectionPositionType) {
                        PositionType.FIRST -> it.sectionPositionType = PositionType.SINGLE
                        PositionType.MIDDLE -> it.sectionPositionType = PositionType.LAST
                        else -> {
                        }
                    }

                    when (thisSection.sectionPositionType) {
                        PositionType.LAST -> thisSection.sectionPositionType = PositionType.SINGLE
                        PositionType.MIDDLE -> thisSection.sectionPositionType = PositionType.FIRST
                        PositionType.UNKNOWN -> {
                            thisSection.sectionPositionType = PositionType.SINGLE
                        }
                    }
                }
            } ?: let {
                thisSection.sectionPositionType = PositionType.SINGLE
            }

        }

//        @JvmStatic
//        fun repackLastSection(thisSection: ShieldSection) {
//            when (thisSection.sectionPositionType) {
//                PositionType.FIRST -> thisSection.sectionPositionType = PositionType.SINGLE
//                PositionType.MIDDLE -> thisSection.sectionPositionType = PositionType.LAST
//            }
//        }


        @JvmStatic
        fun repackDisplayNodeWithPositionType(dNode: ShieldDisplayNode, holder: ProcessorHolder): ShieldDisplayNode {
            holder.dividerProcessorChain.startProcessor(dNode)
            return dNode
        }
//
//        @JvmStatic
//        fun isMergeableViewType(viewType: String): Boolean {
//            return when (viewType) {
//                LOADING_TYPE, FAILED_TYPE, EMPTY_TYPE -> true
//                else -> false
//            }
//        }

        @JvmStatic
        fun revertViewType(globalViewType: String?): String? {
            return globalViewType?.substringAfterLast(NodeCreator.viewTypeSepreator)
        }

        @JvmStatic
        fun createDefaultView(context: Context, text: String): View {
            return TextView(context).apply {
                gravity = Gravity.CENTER
                setPadding(ViewUtils.dip2px(context, 10f),
                        ViewUtils.dip2px(context, 10f),
                        ViewUtils.dip2px(context, 10f),
                        ViewUtils.dip2px(context, 10f))
                this.text = text
            }
        }
    }
}