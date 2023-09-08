package org.crystal.intellij.lang.psi

import com.intellij.util.SmartList

interface CrExpression : CrElement, CrFileElement, CrCallArgument {
    override fun accept(visitor: CrVisitor) = visitor.visitExpression(this)

    @Suppress("UNCHECKED_CAST")
    val annotations: List<CrAnnotationExpression>
        get() {
            if (this is CrAnnotationExpression) return emptyList()

            val stubSiblings = prevStubSiblings()
            if (stubSiblings != null) {
                return stubSiblings.takeWhile { it is CrAnnotationExpression }.reversed() as List<CrAnnotationExpression>
            }

            if (isAnnotationTransparent) return emptyList()

            val result = SmartList<CrAnnotationExpression>()

            var e: CrExpression = this
            mainLoop@
            while (true) {
                if (e.isAnnotationTransparent) {
                    val nestedExpression = e.lastChildOfType<CrExpression>()
                    if (nestedExpression != null) {
                        e = nestedExpression
                        continue
                    }
                }

                when {
                    e is CrAnnotationExpression -> result += e
                    e != this -> break@mainLoop
                }

                var prevExpression: CrExpression? = e.prevSiblingOfType()
                while (prevExpression == null) {
                    val p = e.parent as? CrExpression ?: break@mainLoop
                    if (!p.isAnnotationTransparent) break@mainLoop
                    e = p
                    prevExpression = e.prevSiblingOfType()
                }
                e = prevExpression
            }

            result.reverse()
            return result
        }
}