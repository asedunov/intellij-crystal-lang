package org.crystal.intellij.sdk

import com.intellij.execution.ExecutionException
import com.intellij.execution.process.CapturingProcessHandler
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.util.ThrowableComputable
import com.intellij.util.PatternUtil
import com.intellij.util.text.SemVer
import org.crystal.intellij.CrystalBundle
import java.util.regex.Pattern
import javax.swing.SwingUtilities

private val LOG = Logger.getInstance(CrystalExe::class.java)

private val VERSION_PATTERN = Pattern.compile("Crystal (\\S+).*")

fun CrystalExe.requestVersion(): SemVer? {
    val parameters = listOf("--version")
    val commandLine = crystalCommandLine(parameters) ?: return null
    val processHandler = try {
        CapturingProcessHandler(commandLine)
    } catch (e: ExecutionException) {
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