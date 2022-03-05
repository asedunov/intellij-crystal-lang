package org.crystal.intellij.sdk

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.util.io.exists
import com.intellij.util.io.isDirectory
import com.intellij.util.io.isFile
import java.nio.file.Path
import kotlin.io.path.name

abstract class CrystalSdkFlavor {
    abstract val isApplicable: Boolean

    fun suggestCrystalExePaths(): Sequence<Path> {
        return suggestCrystalExeCandidates().filter(::isValidCrystalExePath)
    }

    protected abstract fun suggestCrystalExeCandidates(): Sequence<Path>

    open fun createCrystalExe(crystalExePath: String): CrystalExe = CrystalExe(crystalExePath)

    companion object {
        private val EP_NAME: ExtensionPointName<CrystalSdkFlavor> =
            ExtensionPointName.create("org.crystal.sdkFlavor")

        val INSTANCE by lazy {
            EP_NAME.extensionList.singleOrNull { it.isApplicable }
        }

        fun suggestStdlibPath(crystalExePath: Path): Path? {
            return listOfNotNull(
                crystalExePath.parent?.parent?.resolve("src"),
                crystalExePath.parent?.parent?.resolve("share/crystal/src")
            ).firstOrNull(::isValidStdlibPath)
        }

        fun isValidCrystalExePath(path: Path): Boolean {
            return path.exists() && path.isFile() && path.name == "crystal"
        }

        fun isValidStdlibPath(path: Path): Boolean {
            return path.exists() &&
                    path.isDirectory() &&
                    path.name == "src" &&
                    path.resolve("object.cr").exists()
        }
    }
}