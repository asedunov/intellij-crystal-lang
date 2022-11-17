package org.crystal.intellij.highlighter

import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.util.containers.JBIterable
import org.crystal.intellij.presentation.presentableKind
import org.crystal.intellij.presentation.shortDescription
import org.crystal.intellij.psi.*
import org.crystal.intellij.resolve.CrStdFqNames
import org.crystal.intellij.resolve.scopes.getTypeAs
import org.crystal.intellij.resolve.symbols.*

@Suppress("UnstableApiUsage")
class CrystalResolveCheckingVisitor(
    highlightInfos: MutableList<HighlightInfo>
) : CrystalHighlightingVisitorBase(highlightInfos) {
    override fun visitPathNameElement(o: CrPathNameElement) {
        super.visitPathNameElement(o)

        if (o.parent is CrTypeDefinition) {
            checkPathResolvesToContainerType(o.qualifier)
        }
    }

    private fun checkPathResolvesToContainerType(path: CrPathNameElement?) {
        if (path == null) return

        val item = path.item ?: return
        val sym = path.resolveSymbol()
        if (!(sym == null || sym is CrClassSym || sym is CrStructSym || sym is CrModuleSym)) {
            val desc = sym.shortDescription.lowercase()
            error(item, "Can't declare type inside $desc")
        }
    }

    override fun visitClass(o: CrClass) {
        super.visitClass(o)

        val sym = o.resolveSymbol() as? CrConstantLikeSym<*> ?: return
        if (sym.fqName != CrStdFqNames.CLASS) {
            checkKindMismatch<CrClassSym>(sym, o)
        }
        if (sym is CrClassSym) {
            checkSuperclass(sym, o)
            checkGenericReopening(sym, o)
        }
    }

    override fun visitStruct(o: CrStruct) {
        super.visitStruct(o)

        val sym = o.resolveSymbol() ?: return
        checkKindMismatch<CrStructSym>(sym, o)
        if (sym is CrStructSym) {
            checkSuperclass(sym, o)
            checkGenericReopening(sym, o)
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
        if (sym is CrModuleSym) {
            checkGenericReopening(sym, o)
        }
    }

    override fun visitEnum(o: CrEnum) {
        super.visitEnum(o)

        val sym = o.resolveSymbol() ?: return
        checkKindMismatch<CrEnumSym>(sym, o)

        if (sym is CrEnumSym) {
            val constants = o.body?.childrenOfType<CrEnumConstant>() ?: JBIterable.empty()
            if (sym.sources.firstOrNull() == o) {
                if (constants.isEmpty) {
                    error(o.defaultAnchor, "Enum must have at least one constant")
                }
            }
            else if (constants.isNotEmpty) {
                error(o.defaultAnchor, "Can't add constants to enum definition")
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

        checkTypeArguments(currentSuperSym, superType)
    }

    private fun checkGenericReopening(sym: CrModuleLikeSym, psi: CrTypeParameterHolder) {
        val primaryPsi = sym.sources.firstOrNull() as? CrTypeParameterHolder ?: return
        if (psi == primaryPsi) return
        val primaryParams = primaryPsi.typeParameterList?.elements ?: JBIterable.empty()
        val paramList = psi.typeParameterList
        val params = paramList?.elements ?: return
        if (params.isEmpty) return
        if (primaryParams.isEmpty) {
            error(paramList, "${sym.name} is not a generic ${sym.presentableKind}")
            return
        }
        val primaryParamNames = primaryParams.mapNotNull { if (it.isSplat) "*${it.name}" else it.name }
        val paramNames = params.mapNotNull { if (it.isSplat) "*${it.name}" else it.name }
        if (primaryParamNames != paramNames) {
            val message = buildString {
                append("Type parameter")
                if (primaryParamNames.size > 1) append("s")
                append(" must be ")
                primaryParamNames.joinTo(this)
                append(", not ")
                paramNames.joinTo(this)
            }
            error(paramList, message)
        }
    }

    private fun checkTypeArguments(sym: CrSym<*>?, type: CrTypeElement<*>) {
        if (sym == null) return
        if (sym is CrModuleLikeSym && sym.isGeneric && type !is CrInstantiatedTypeElement) {
            val superDesc = buildString {
                append(sym.name)
                sym.typeParameters.joinTo(this, prefix = "(", postfix = ")") { it.name }
            }
            error(type, "Generic type arguments must be specified when inheriting $superDesc")
        }
    }

    override fun visitIncludeExpression(o: CrIncludeExpression) {
        super.visitIncludeExpression(o)

        checkIncludeExtendModule(o)
    }

    override fun visitExtendExpression(o: CrExtendExpression) {
        super.visitExtendExpression(o)

        checkIncludeExtendModule(o)
    }

    private fun checkIncludeExtendModule(o: CrIncludeLikeExpression) {
        val type = o.type ?: return
        val targetSym = o.targetSymbol ?: return
        if (targetSym !is CrModuleSym) {
            error(type, "'include'/'extend' target must be a module")
        }
        checkTypeArguments(targetSym, type)
    }

    override fun visitAnnotationExpression(o: CrAnnotationExpression) {
        super.visitAnnotationExpression(o)

        val path = o.path ?: return
        val targetSym = o.targetSymbol ?: return
        if (targetSym !is CrAnnotationSym) {
            val kind = targetSym.presentableKind
            error(path, "Annotation is expected, but $kind is found")
            return
        }

        if (targetSym.fqName == CrStdFqNames.EXTERN && !isValidExternOnNonGenericStruct(o)) {
            error(o, "@Extern annotation on non-generic struct must have a single named parameter 'union' with boolean literal as a value")
        }

        val owner = o.owner
        if (owner is CrLibrary) {
            if (targetSym.fqName == CrStdFqNames.LINK) checkLibraryLink(o)
            if (targetSym.fqName == CrStdFqNames.CALL_CONVENTION) checkLibraryCallConvention(o, owner)
        }
    }

    private fun isValidExternOnNonGenericStruct(o: CrAnnotationExpression): Boolean {
        val owner = o.owner ?: return true
        if (owner is CrStruct && !owner.isGeneric) {
            val arguments = o.argumentList?.elements ?: return true
            if (arguments.isEmpty) return true
            val arg = arguments.single()
            return arg is CrNamedArgument
                    && arg.name == "union"
                    && arg.expression is CrBooleanLiteralExpression
        }
        return true
    }

    private fun checkLibraryLink(o: CrAnnotationExpression) {
        val args = o.argumentList?.elements ?: JBIterable.empty()
        if (args.isEmpty) {
            error(o.path ?: o, "Missing link arguments")
            return
        }
        var posCount = 0
        for (arg in args) {
            if (arg is CrNamedArgument) {
                val value = arg.expression ?: continue
                val name = arg.name ?: continue
                when (name) {
                    "lib" -> {
                        if (posCount > 0) error(arg, "'lib' link argument is already specified")
                        if (value !is CrStringLiteralExpression) error(value, "'lib' argument must be a String")
                    }

                    "ldflags" -> {
                        if (posCount > 1) error(arg, "'ldflags' link argument is already specified")
                        if (value !is CrStringLiteralExpression) error(value, "'ldflags' argument must be a String")
                    }

                    "static" -> {
                        if (posCount > 2) error(arg, "'static' link argument is already specified")
                        if (value !is CrBooleanLiteralExpression) error(value, "'static' argument must be a Bool")
                        deprecated(arg, "Specifying static linking for individual libraries is deprecated")
                    }

                    "framework" -> {
                        if (posCount > 3) error(arg, "'framework' link argument is already specified")
                        if (value !is CrStringLiteralExpression) error(value, "'framework' argument must be a String")
                    }

                    "pkg_config" -> {
                        if (value !is CrStringLiteralExpression) error(value, "'pkg_config' argument must be a String")
                    }

                    else -> error(arg.nameElement ?: arg, "Unknown link argument")
                }
            }
            else {
                when (posCount) {
                    0 -> if (arg !is CrStringLiteralExpression) error(arg, "'lib' argument must be a String")

                    1 -> if (arg !is CrStringLiteralExpression) error(arg, "'ldflags' link argument must be a String")

                    2 -> {
                        if (arg !is CrBooleanLiteralExpression) error(arg, "'static' link argument must be a Bool")
                        deprecated(arg, "Specifying static linking for individual libraries is deprecated")
                    }

                    3 -> if (arg !is CrStringLiteralExpression) error(arg, "'framework' link argument must be a String")

                    else -> {
                        error(arg, "Unknown 'link' argument")
                        continue
                    }
                }
                deprecated(arg, "Using non-named arguments for Link annotations is deprecated")
                posCount++
            }
        }
    }

    private fun checkLibraryCallConvention(o: CrAnnotationExpression, lib: CrLibrary) {
        val argList = o.argumentList
        val arg = argList?.elements?.single()
        if (arg == null || arg is CrNamedArgument) {
            error(
                argList ?: o.path ?: o,
                "CallConvention must have a single positional argument"
            )
            return
        }
        if (arg !is CrStringLiteralExpression) {
            error(arg, "CallConvention argument must be a string")
            return
        }
        val value = arg.stringValue?.lowercase()
        if (value !in CALL_CONVENTIONS) {
            error(arg, "Invalid call convention")
        }
        (lib.resolveSymbol() as? CrLibrarySym)?.annotations?.get(CrStdFqNames.CALL_CONVENTION)?.let { annotations ->
            if (annotations.firstOrNull() != o) {
                error(o.path ?: o, "Call convention is already specified")
            }
        }
    }

    companion object {
        val CALL_CONVENTIONS = setOf("c", "fast", "cold", "webkit_js", "anyreg", "x86_stdcall", "x86_fastcall")
    }
}