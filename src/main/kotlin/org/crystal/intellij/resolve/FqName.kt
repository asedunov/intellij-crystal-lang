package org.crystal.intellij.resolve

sealed class FqName(
    val name: String,
    val parent: StableFqName?
) {
    protected abstract val separator: String

    val fullName: String by lazy {
        if (parent != null) "${parent.fullName}$separator$name" else name
    }

    override fun equals(other: Any?) = javaClass == other?.javaClass && fullName == (other as FqName).fullName

    override fun hashCode() = fullName.hashCode()

    companion object {
        fun of(fullName: String?): FqName? {
            if (fullName.isNullOrEmpty()) return null

            var fqName: StableFqName? = null
            var start = 0
            while (true) {
                var sep = fullName.indexOf("::", start)
                if (sep >= 0) {
                    fqName = StableFqName(fullName.substring(start, sep), fqName)
                    start = sep + 2
                }
                else {
                    sep = fullName.indexOf('.', start)
                    return if (sep >= 0) {
                        fqName = StableFqName(fullName.substring(start, sep), fqName)
                        MemberFqName(fullName.substring(sep + 1), fqName)
                    } else {
                        StableFqName(fullName.substring(start), fqName)
                    }
                }
            }
        }
    }
}

class MemberFqName(
    name: String,
    parent: StableFqName?
) : FqName(name, parent) {
    override val separator: String
        get() = "."
}

class StableFqName(
    name: String,
    parent: StableFqName?
) : FqName(name, parent) {
    override val separator: String
        get() = "::"
}

const val NO_NAME = "<no name>"