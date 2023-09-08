package org.crystal.intellij.lang.resolve.symbols

class CrSymbolOrdinal(
    value: Int,
    parent: CrSymbolOrdinal?
) : Comparable<CrSymbolOrdinal> {
    companion object {
        val FALLBACK_ORDINAL = CrSymbolOrdinal(-1, null)
    }

    private val values: IntArray

    init {
        if (parent != null) {
            val parentValues = parent.values
            values = parentValues.copyOf(parentValues.size + 1)
            values[values.lastIndex] = value
        }
        else {
            values = intArrayOf(value)
        }
    }

    override fun compareTo(other: CrSymbolOrdinal): Int {
        var i = 0
        val n = minOf(values.size, other.values.size)
        while (i < n) {
            when {
                values[i] < other.values[i] -> return -1
                values[i] > other.values[i] -> return 1
                else -> i++
            }
        }
        return when {
            i < other.values.size -> -1
            i < values.size -> 1
            else -> 0
        }
    }

    override fun equals(other: Any?) = other is CrSymbolOrdinal && values.contentEquals(other.values)

    override fun hashCode() = values.contentHashCode()

    override fun toString() = values.contentToString()
}