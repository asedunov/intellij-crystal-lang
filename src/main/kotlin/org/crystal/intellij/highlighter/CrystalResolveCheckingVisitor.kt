package org.crystal.intellij.highlighter

import com.intellij.codeInsight.daemon.impl.HighlightInfo
import org.crystal.intellij.presentation.presentableKind
import org.crystal.intellij.psi.*
import org.crystal.intellij.resolve.symbols.*

class CrystalResolveCheckingVisitor(
    highlightInfos: MutableList<HighlightInfo>
) : CrystalHighlightingVisitorBase(highlightInfos) {
    override fun visitClass(o: CrClass) {
        super.visitClass(o)

        val sym = o.resolveSymbol() ?: return
        checkKindMismatch<CrClassSym>(sym, o)
        if (sym is CrClassSym) {
            checkSuperKindMismatch(sym, o)
        }
    }

    override fun visitStruct(o: CrStruct) {
        super.visitStruct(o)

        val sym = o.resolveSymbol() ?: return
        checkKindMismatch<CrStructSym>(sym, o)
        if (sym is CrStructSym) {
            checkSuperKindMismatch(sym, o)
        }
    }

    override fun visitModule(o: CrModule) {
        super.visitModule(o)

        val sym = o.resolveSymbol() ?: return
        checkKindMismatch<CrModuleSym>(sym, o)
    }

    override fun visitEnum(o: CrEnum) {
        super.visitEnum(o)

        val sym = o.resolveSymbol() ?: return
        checkKindMismatch<CrEnumSym>(sym, o)
    }

    override fun visitAnnotation(o: CrAnnotation) {
        super.visitAnnotation(o)

        val sym = o.resolveSymbol() ?: return
        checkKindMismatch<CrAnnotationSym>(sym, o)
    }

    override fun visitLibrary(o: CrLibrary) {
        super.visitLibrary(o)

        val sym = o.resolveSymbol() ?: return
        checkKindMismatch<CrLibrarySym>(sym, o)
    }

    override fun visitAlias(o: CrAlias) {
        super.visitAlias(o)

        val sym = o.resolveSymbol() ?: return
        checkRedefinition(sym, o)
    }

    override fun visitTypeDef(o: CrTypeDef) {
        super.visitTypeDef(o)

        val sym = o.resolveSymbol() ?: return
        checkRedefinition(sym, o)
    }

    override fun visitCStruct(o: CrCStruct) {
        super.visitCStruct(o)

        val sym = o.resolveSymbol() ?: return
        checkRedefinition(sym, o)
    }

    override fun visitCUnion(o: CrCUnion) {
        super.visitCUnion(o)

        val sym = o.resolveSymbol() ?: return
        checkRedefinition(sym, o)
    }

    private inline fun <reified Expected : CrSym<*>> checkKindMismatch(sym: CrSym<*>, psi: CrDefinition) {
        if (sym is Expected) return

        val name = sym.name
        val symKind = sym.presentableKind
        val psiKind = psi.presentableKind
        val message = "'$name' is already defined as $symKind and can't be reopened as $psiKind"
        error(psi.defaultAnchor, message)
    }

    private fun checkRedefinition(sym: CrSym<*>, psi: CrDefinition) {
        if (sym.sources.firstOrNull() != psi) {
            error(psi.defaultAnchor, "'${sym.name}' is already defined")
        }
    }

    private fun checkSuperKindMismatch(sym: CrModuleLikeSym, psi: CrSuperTypeAware) {
        val superSym = sym.superClass ?: return
        val superType = psi.superTypeClause?.type ?: return
        if (superSym::class != sym::class) {
            error(superType, "Can't inherit ${sym.presentableKind} from ${superSym.presentableKind}")
        }
    }
}