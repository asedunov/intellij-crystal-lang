@file:Suppress("UnstableApiUsage")

package org.crystal.intellij.resolve.symbols

import com.intellij.model.Pointer
import com.intellij.model.Symbol
import com.intellij.util.SmartFMap
import com.intellij.util.SmartList
import org.crystal.intellij.psi.CrAnnotationExpression
import org.crystal.intellij.psi.CrElement
import org.crystal.intellij.psi.CrExpression
import org.crystal.intellij.resolve.StableFqName
import org.crystal.intellij.resolve.cache.newResolveSlice
import org.crystal.intellij.resolve.cache.resolveCache
import org.crystal.intellij.resolve.scopes.CrEmptyScope
import org.crystal.intellij.resolve.scopes.CrScope

sealed class CrSym<Source: CrElement>(
    val name: String,
    val sources: List<Source>
) : Symbol, Pointer<Symbol> {
    companion object {
        private val ANNOTATIONS = newResolveSlice<CrSym<*>, Map<StableFqName, List<CrAnnotationExpression>>>("ANNOTATIONS")
    }

    override fun createPointer() = this

    override fun dereference() = this

    val isValid: Boolean
        get() = sources.all { it.isValid }

    abstract val program: CrProgramSym

    abstract val namespace: CrSym<*>

    open val containedScope: CrScope
        get() = CrEmptyScope

    val annotations: Map<StableFqName, List<CrAnnotationExpression>>
        get() = program.project.resolveCache.getOrCompute(ANNOTATIONS, this) {
            var map = SmartFMap.emptyMap<StableFqName, SmartList<CrAnnotationExpression>>()
            for (source in sources) {
                if (source !is CrExpression) continue
                for (annotation in source.annotations) {
                    val fqName = (annotation.targetSymbol as? CrAnnotationSym)?.fqName ?: continue
                    var list = map[fqName]
                    if (list == null) {
                        list = SmartList()
                        map = map.plus(fqName, list)
                    }
                    list.add(annotation)
                }
            }
            map
        } ?: emptyMap()
}