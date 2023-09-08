package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.util.elementType
import org.crystal.intellij.lang.lexer.*
import org.crystal.intellij.lang.parser.CR_SIMPLE_NAME_ELEMENT
import org.crystal.intellij.lang.references.CrSimpleReference
import org.crystal.intellij.lang.resolve.symbols.CrSym
import org.crystal.intellij.lang.stubs.api.CrNameStub

@Suppress("UnstableApiUsage")
class CrSimpleNameElement : CrStubbedElementImpl<CrNameStub<*>>, CrNameElement {
    companion object {
        val EMPTY_ARRAY = arrayOf<CrSimpleNameElement>()
    }

    constructor(stub: CrNameStub<*>) : super(stub, CR_SIMPLE_NAME_ELEMENT)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitSimpleNameElement(this)

    override fun getName(): String? {
        greenStub?.let { return it.actualName }

        val e = firstChild
        if (e is CrStringLiteralExpression) return e.stringValue
        val name = text
        return when (e.elementType) {
            CR_INSTANCE_VAR, CR_GLOBAL_VAR, CR_MACRO_VAR -> name.substring(1)
            CR_CLASS_VAR -> name.substring(2)
            else -> name
        }
    }

    override fun setName(name: String): CrNameElement {
        return replaceTyped(CrPsiFactory.getInstance(project).createSimpleNameElement(name))
    }

    override val sourceName: String?
        get() {
            greenStub?.let { return it.sourceName }

            val e = firstChild
            return if (e is CrStringLiteralExpression) e.text else name
        }

    override val kind: CrNameKind
        get() {
            greenStub?.let { return it.kind }
            return body?.kind ?: CrNameKind.UNKNOWN
        }

    val body: CrNameKindAware?
        get() = firstChild as? CrNameKindAware

    val isQuestion: Boolean
        get() = name?.lastOrNull() == '?'

    val isExclamation: Boolean
        get() = name?.lastOrNull() == '!'

    val isSelfRef: Boolean
        get() = firstChild?.elementType == CR_SELF

    val isMetaClassRef: Boolean
        get() = firstChild?.elementType == CR_CLASS

    private val ownReference: CrSimpleReference
        get() = CrSimpleReference(this)

    override fun getOwnReferences() = when (parent) {
        is CrMacro, is CrCallLikeExpression -> listOf(ownReference)
        else -> emptyList()
    }

    override fun resolveSymbol(): CrSym<*>? {
        (parent as? CrMacro)?.let { return it.resolveSymbol() }
        (parent as? CrCallLikeExpression)?.let { return it.resolveCall()?.macro }
        return null
    }

    override fun resolveCandidates(): List<CrSym<*>> {
        val call = parent as? CrCallLikeExpression
        if (call != null) return call.resolveCandidateCalls().map { it.macro }

        val symbol = resolveSymbol()
        if (symbol != null) return listOf(symbol)

        return emptyList()
    }
}