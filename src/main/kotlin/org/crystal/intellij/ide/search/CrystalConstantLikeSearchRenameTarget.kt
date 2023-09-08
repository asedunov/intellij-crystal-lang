package org.crystal.intellij.ide.search

import com.intellij.find.usages.api.SearchTarget
import com.intellij.find.usages.api.UsageHandler
import com.intellij.model.Pointer
import com.intellij.model.search.SearchRequest
import com.intellij.navigation.TargetPresentation
import com.intellij.psi.search.SearchScope
import com.intellij.refactoring.rename.api.RenameTarget
import com.intellij.refactoring.rename.api.ReplaceTextTarget
import com.intellij.refactoring.rename.api.ReplaceTextTargetContext
import org.crystal.intellij.ide.presentation.getIcon
import org.crystal.intellij.ide.presentation.presentableKind
import org.crystal.intellij.lang.resolve.symbols.CrConstantLikeSym
import org.crystal.intellij.lang.resolve.symbols.CrSym

@Suppress("UnstableApiUsage")
class CrystalConstantLikeSearchRenameTarget(
    val symbol: CrSym<*>
) : SearchTarget, RenameTarget {
    override fun createPointer(): Pointer<CrystalConstantLikeSearchRenameTarget> {
        return Pointer.delegatingPointer(symbol.createPointer()) {
            if (it is CrSym<*>) CrystalConstantLikeSearchRenameTarget(it) else null
        }
    }

    override val maximalSearchScope: SearchScope?
        get() = null

    override val targetName: String
        get() = symbol.name

    override fun presentation(): TargetPresentation {
        val icon = symbol.getIcon()
        val name = (symbol as? CrConstantLikeSym<*>)?.fqName?.fullName ?: symbol.name
        val kind = symbol.presentableKind
        val text = "$kind $name"
        return TargetPresentation.builder(text).icon(icon).presentation()
    }

    override val usageHandler: UsageHandler
        get() = UsageHandler.createEmptyUsageHandler(presentation().presentableText)

    override val textSearchRequests: Collection<SearchRequest>
        get() {
            val name = symbol.name
            val fqName = (symbol as? CrConstantLikeSym<*>)?.fqName?.fullName ?: name
            return if (fqName != name) {
                listOf(SearchRequest.of(fqName), SearchRequest.of(name))
            } else {
                listOf(SearchRequest.of(name))
            }
        }

    override fun textTargets(context: ReplaceTextTargetContext) = textSearchRequests.map { request ->
        val searchString = request.searchString
        val index = searchString.lastIndexOf(targetName)
        ReplaceTextTarget(request) { newName: String ->
            searchString.substring(0, index) + searchString.substring(index).replace(this.targetName, newName)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CrystalConstantLikeSearchRenameTarget) return false

        if (symbol != other.symbol) return false

        return true
    }

    override fun hashCode(): Int {
        return symbol.hashCode()
    }
}