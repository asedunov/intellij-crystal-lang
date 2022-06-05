package org.crystal.intellij.highlighter

import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.util.containers.JBIterable
import org.crystal.intellij.presentation.presentableKind
import org.crystal.intellij.psi.*
import org.crystal.intellij.resolve.CrStdFqNames
import org.crystal.intellij.resolve.scopes.getTypeAs
import org.crystal.intellij.resolve.symbols.*

class CrystalResolveCheckingVisitor(
    highlightInfos: MutableList<HighlightInfo>
) : CrystalHighlightingVisitorBase(highlightInfos) {
    override fun visitClass(o: CrClass) {
        super.visitClass(o)

        val sym = o.resolveSymbol() ?: return
        checkKindMismatch<CrClassSym>(sym, o)
        if (sym is CrClassSym) {
            checkSuperclass(sym, o)
        }
    }

    override fun visitStruct(o: CrStruct) {
        super.visitStruct(o)

        val sym = o.resolveSymbol() ?: return
        checkKindMismatch<CrStructSym>(sym, o)
        if (sym is CrStructSym) {
            checkSuperclass(sym, o)
            val superSym = sym.superClass
            if (superSym is CrStructSym && !superSym.isAbstract) {
                error(o.defaultAnchor, "Can't inherit from a non-abstract struct")
            }
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

        if (sym.sources.firstOrNull() == o) {
            val constants = o.body?.childrenOfType<CrEnumConstant>() ?: JBIterable.empty()
            if (constants.isEmpty) {
                error(o.defaultAnchor, "Enum must have at least one constant")
            }
        }
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

    private fun checkSuperclass(sym: CrModuleLikeSym, psi: CrSuperTypeAware) {
        val superSym = sym.superClass ?: return
        val superClause = psi.superTypeClause ?: return
        val superType = superClause.type ?: return
        if (superSym == sym.program.memberScope.getTypeAs<CrModuleLikeSym>(CrStdFqNames.ENUM)) {
            error(superType, "A type can't inherit directly from Enum. Use 'enum' keyword instead")
            return
        }
        if (superSym::class != sym::class) {
            error(superType, "Can't inherit ${sym.presentableKind} from ${superSym.presentableKind}")
        }
        val currentSuperSym = superClause.resolveSymbol()
        if (currentSuperSym != null && currentSuperSym != superSym) {
            error(superType, "Superclass mismatch: ${superSym.fqName} is expected, but ${currentSuperSym.fqName} is found")
        }
    }

    override fun visitIncludeExpression(o: CrIncludeExpression) {
        super.visitIncludeExpression(o)

        checkTargetIsModule(o)
    }

    override fun visitExtendExpression(o: CrExtendExpression) {
        super.visitExtendExpression(o)

        checkTargetIsModule(o)
    }

    private fun checkTargetIsModule(o: CrIncludeLikeExpression) {
        val type = o.type ?: return
        val targetSym = o.targetSymbol ?: return
        if (targetSym !is CrModuleSym) {
            error(type, "'include'/'extend' target must be a module")
        }
    }
}