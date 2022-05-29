package org.crystal.intellij.util

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.CapturingProcessHandler
import com.intellij.execution.process.ProcessOutput
import com.intellij.openapi.diagnostic.Logger
import com.intellij.util.io.BaseOutputReader

// Inspired by intellij-rust process utilities

private val log: Logger = Logger.getInstance("org.crystal.intellij.util.ProcessUtilsExt")

typealias CrProcessResult<T> = Result<T, CrProcessExecutionException>

sealed class CrProcessExecutionException : RuntimeException {
    constructor(message: String) : super(message)
    constructor(cause: Throwable) : super(cause)

    class Start(cause: ExecutionException) : CrProcessExecutionException(cause)

    class Cancelled(
        commandLineString: String,
        output: ProcessOutput
    ) : CrProcessExecutionException(errorMessage(commandLineString, output))

    class Timeout(
        commandLineString: String,
        output: ProcessOutput
    ) : CrProcessExecutionException(errorMessage(commandLineString, output))

    class Aborted(
        commandLineString: String,
        output: ProcessOutput
    ) : CrProcessExecutionException(errorMessage(commandLineString, output))

    companion object {
        fun errorMessage(commandLineString: String, output: ProcessOutput): String = """
            |Execution failed (exit code ${output.exitCode}).
            |$commandLineString
            |stdout : ${output.stdout}
            |stderr : ${output.stderr}
        """.trimMargin()
    }
}

class CrCapturingProcessHandler private constructor(commandLine: GeneralCommandLine) : CapturingProcessHandler(commandLine) {
    override fun readerOptions(): BaseOutputReader.Options = BaseOutputReader.Options.BLOCKING

    companion object {
        fun startProcess(commandLine: GeneralCommandLine): Result<CrCapturingProcessHandler, ExecutionException> {
            return try {
                Result.Ok(CrCapturingProcessHandler(commandLine))
            } catch (e: ExecutionException) {
                Result.Err(e)
            }
        }
    }
}

fun GeneralCommandLine.execute(
    stdIn: ByteArray,
    runner: CapturingProcessHandler.() -> ProcessOutput,
): CrProcessResult<ProcessOutput> {
    log.info("Executing `$commandLineString`")

    val handler = CrCapturingProcessHandler
        .startProcess(this) // The OS process is started here
        .unwrapOrElse {
            log.warn("Failed to run executable", it)
            return Result.Err(CrProcessExecutionException.Start(it))
        }
    handler.processInput.use { it.write(stdIn) }

    val output = handler.runner()
    return when {
        output.isCancelled -> Result.Err(CrProcessExecutionException.Cancelled(commandLineString, output))
        output.isTimeout -> Result.Err(CrProcessExecutionException.Timeout(commandLineString, output))
        output.exitCode != 0 -> Result.Err(CrProcessExecutionException.Aborted(commandLineString, output))
        else -> Result.Ok(output)
    }
}