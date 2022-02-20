package org.crystal.intellij.sdk

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.CapturingProcessHandler
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.util.ThrowableComputable
import com.intellij.util.PatternUtil
import com.intellij.util.io.exists
import com.intellij.util.io.isDirectory
import com.intellij.util.io.isFile
import com.intellij.util.text.SemVer
import org.crystal.intellij.CrystalBundle
import java.nio.file.Path
import java.util.regex.Pattern
import javax.swing.SwingUtilities
import kotlin.io.path.name

abstract class CrystalSdkFlavor {
    abstract val isApplicable: Boolean

    fun suggestCrystalExePaths(): Sequence<Path> {
        return suggestCrystalExeCandidates().filter(::isValidCrystalExePath)
    }

    protected abstract fun suggestCrystalExeCandidates(): Sequence<Path>

    abstract fun crystalCommandLine(crystalExePath: Path, parameters: List<String>): GeneralCommandLine?

    companion object {
        private val EP_NAME: ExtensionPointName<CrystalSdkFlavor> =
            ExtensionPointName.create("org.crystal.sdkFlavor")

        private val LOG = Logger.getInstance(CrystalSdkFlavor::class.java)

        private val VERSION_PATTERN = Pattern.compile("Crystal (\\S+).*")

        val applicableFlavors by lazy {
            EP_NAME.extensionList.filter { it.isApplicable }
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

        fun requestVersion(crystalExePath: Path): SemVer? {
            if (crystalExePath.parent == null) return null
            val parameters = listOf("--version")
            val commandLine = applicableFlavors.firstNotNullOfOrNull {
                it.crystalCommandLine(crystalExePath, parameters)
            } ?: return null
            val processHandler = try {
                CapturingProcessHandler(commandLine)
            }
            catch (e : ExecutionException) {
                return null
            }
            val output = if (SwingUtilities.isEventDispatchThread()) {
                ProgressManager.getInstance().runProcessWithProgressSynchronously(
                    ThrowableComputable { processHandler.runProcess(10000) },
                    CrystalBundle.message("general.wait"),
                    false,
                    null
                )
            } else {
                processHandler.runProcess()
            }

            if (output.exitCode != 0) {
                var errors = output.stderr
                if (errors.isEmpty()) {
                    errors = output.stdout
                }
                LOG.warn(
                    """
                        Couldn't get interpreter environment: process exited with code ${output.exitCode}
                        $errors
                    """.trimIndent()
                )
                return null
            }

            val versionString = PatternUtil.getFirstMatch(output.stdoutLines, VERSION_PATTERN)
            return SemVer.parseFromText(versionString)
        }
    }
}