package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.util.isAncestor
import org.crystal.intellij.parser.CR_PATH_NAME_ELEMENT
import org.crystal.intellij.references.CrPathReference
import org.crystal.intellij.resolve.FqName
import org.crystal.intellij.resolve.cache.newResolveSlice
import org.crystal.intellij.resolve.cache.resolveCache
import org.crystal.intellij.resolve.resolveFacade
import org.crystal.intellij.resolve.scopes.CrScope
import org.crystal.intellij.resolve.symbols.CrModuleLikeSym
import org.crystal.intellij.resolve.symbols.CrOrdinalSym
import org.crystal.intellij.resolve.symbols.CrSym
import org.crystal.intellij.resolve.symbols.CrSymbolOrdinal
import org.crystal.intellij.stubs.api.CrPathStub

@Suppress("UnstableApiUsage")
class CrPathNameElement : CrStubbedElementImpl<CrPathStub>, CrNameElement, CrConstantSource {
    companion object {
        private val PATH_TARGET = newResolveSlice<CrPathNameElement, CrSym<*>>("PATH_TARGET")
    }

    constructor(stub: CrPathStub) : super(stub, CR_PATH_NAME_ELEMENT)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitPathNameElement(this)

    override val kind: CrNameKind
        get() = CrNameKind.PATH

    val isGlobal: Boolean
        get() = name.isEmpty()

    val qualifier: CrPathNameElement?
        get() = stubChildOfType()

    val item: CrConstantName?
        get() = childOfType()

    override fun getName() = greenStub?.name ?: item?.name ?: ""

    override fun setName(name: String): CrNameElement {
        return replaceTyped(CrPsiFactory.getInstance(project).createPathNameElement(name))
    }

    override val sourceName: String
        get() = name

    override val fqName: FqName?
        get() = when (val p = parent) {
            is CrPathNameElement -> p.fqName?.parent
            is CrTypeDefinition -> p.fqName
            else -> null
        }

    override fun ordinal(): CrSymbolOrdinal? {
        var p = parent
        if (p is CrPathTypeElement) p = p.parent
        if (p is CrInstantiatedTypeElement) p = p.parent
        return (p as? CrSymbolOrdinalHolder)?.ordinal()
    }

    private val pathResolveScope: CrScope?
        get() {
            val qualifier = qualifier
            if (qualifier != null) return (qualifier.resolveSymbol() as? CrModuleLikeSym)?.memberScope

            val include = parentStubOrPsiOfType<CrIncludeLikeExpression>()
            if (include != null) return include.pathResolveScope

            val containingMethod = parentStubOrPsiOfType<CrMethod>()
            if (containingMethod != null) {
                return containingMethod.resolveSymbol()?.containedScope
            }

            var containingType = parentStubOrPsiOfType<CrModuleLikeDefinition<*, *>>()
            if (containingType?.nameElement?.isAncestor(this, false) == true ||
                (containingType as? CrSuperTypeAware)?.superTypeClause?.isAncestor(this, false) == true
            ) {
                containingType = containingType.parentStubOrPsiOfType()
            }
            val scopeOwner =
                if (containingType != null) containingType.resolveSymbol() else project.resolveFacade.program
            return (scopeOwner as? CrModuleLikeSym)?.memberScope
        }

    override fun resolveSymbol(): CrSym<*>? {
        if (isGlobal) return project.resolveFacade.program
        return project.resolveCache.getOrCompute(PATH_TARGET, this) {
            val type = pathResolveScope?.getConstant(name, qualifier == null) as? CrOrdinalSym<*> ?: return@getOrCompute null
            if (parentStubOrPsiOfType<CrSupertypeClause>() != null
                || parentStubOrPsiOfType<CrIncludeLikeExpression>() != null) {
                val targetOrdinal = type.ordinal ?: return@getOrCompute null
                val currentOrdinal =
                    parentStubOrPsiOfType<CrSymbolOrdinalHolder>()?.ordinal() ?: return@getOrCompute null
                if (targetOrdinal >= currentOrdinal) return@getOrCompute null
            }
            type
        }
    }

    val ownReference: CrPathReference
        get() = CrPathReference(this)

    override fun getOwnReferences() = listOf(ownReference)
}