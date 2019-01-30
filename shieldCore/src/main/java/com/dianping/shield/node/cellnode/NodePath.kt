package com.dianping.shield.node.cellnode

import com.dianping.shield.entity.CellType

/**
 * Created by zhi.he on 2018/6/24.
 */
class NodePath {
    @JvmField
    var group: Int = -1
    @JvmField
    var cell: Int = -1
    @JvmField
    var section: Int = -1
    @JvmField
    var row: Int = -3 //normal>0 header -1 footer -2 loading 0 loadingmore 0
    @JvmField
    var node: Int = -1

    @JvmField
    var cellType: CellType? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NodePath

        if (group != other.group) return false
        if (cell != other.cell) return false
        if (section != other.section) return false
        if (row != other.row) return false
        if (cellType != other.cellType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = group
        result = 31 * result + cell
        result = 31 * result + section
        result = 31 * result + row
        result = 31 * result + (cellType?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "NodePath(group=$group, cell=$cell, section=$section, row=$row, cellType=$cellType)"
    }


}