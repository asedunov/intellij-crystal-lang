package org.crystal.intellij.search

import com.intellij.find.usages.api.SearchTarget
import com.intellij.find.usages.api.UsageHandler
import com.intellij.model.Pointer
import com.intellij.model.search.SearchRequest
import com.intellij.navigation.TargetPresentation
import org.crystal.intellij.presentation.getIcon
import org.crystal.intellij.presentation.presentableKind
import org.crystal.intellij.resolve.symbols.CrConstantLikeSym
import org.crystal.intellij.resolve.symbols.CrSym

@Suppress("UnstableApiUsage")
class CrystalConstantLikeSearchTarget(
    val symbol: CrSym<*>
) : SearchTarget {
    override fun createPointer(): Pointer<CrystalConstantLikeSearchTarget> {
        return Pointer.delegatingPointer(symbol.createPointer()) {
            if (it is CrSym<*>) CrystalConstantLikeSearchTarget(it) else null
        }
    }

    val targetName: String
        get() = symbol.name

    override val presentation: TargetPresentation
        get() {
            val icon = symbol.getIcon()
            val name = (symbol as? CrConstantLikeSym<*>)?.fqName?.fullName ?: symbol.name
            val kind = symbol.presentableKind
            val text = "$kind $name"
            return TargetPresentation.builder(text).icon(icon).presentation()
        }

    override val usageHandler: UsageHandler<*>
        get() = UsageHandler.createEmptyUsageHandler(presentation.presentableText)

    override val textSearchRequests: Collection<SearchRequest>
        get() {
            val name = symbol.name
            val fqName = (symbol as? CrConstantLikeSym<*>)?.fqName
            return if (fqName != null) {
                listOf(SearchRequest.of(fqName.fullName), SearchRequest.of(name))
            } else {
                listOf(SearchRequest.of(name))
            }
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CrystalConstantLikeSearchTarget) return false

        if (symbol != other.symbol) return false

        return true
    }

    override fun hashCode(): Int {
        return symbol.hashCode()
    }
}