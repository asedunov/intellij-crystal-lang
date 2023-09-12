package org.crystal.intellij.util.crystal

class CrCharReader(
    val source: String,
    private var pos: Int = 0
) {
    var currentChar: CrChar = 0.crChar
        private set

    private var isDone: Boolean = false

    init {
        decodeCurrentChar()
    }

    private fun decodeCurrentChar(): CrChar {
        isDone = pos == source.length
        currentChar = (if (isDone) 0 else source.codePointAt(pos)).crChar
        return currentChar
    }

    fun hasNext()  = !isDone

    fun nextChar(): CrChar {
        if (isDone) throw IndexOutOfBoundsException(pos)
        pos++
        return decodeCurrentChar()
    }
}