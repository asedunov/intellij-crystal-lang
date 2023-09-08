package org.crystal.intellij.ide.build

import com.intellij.build.events.BuildEvent
import com.intellij.build.events.MessageEvent
import com.intellij.build.output.BuildOutputInstantReader
import com.intellij.build.output.BuildOutputParser
import com.intellij.build.progress.BuildProgress
import com.intellij.build.progress.BuildProgressDescriptor
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.findFile
import com.intellij.pom.Navigatable
import org.crystal.intellij.util.findVirtualFile
import java.nio.file.Path
import java.util.function.Consumer

private val COMPILER_PASS_PATTERN = Regex("\\[(\\d+)/(\\d+)](.*)")
private val ERROR_PATTERN = Regex("Error:(.*)")
private val WARNING_PATTERN = Regex("Warning:(.*)")
private val LOCATION_PATTERN = Regex("In ([^:]*):(\\d+):(\\d+)")

@Suppress("UnstableApiUsage")
class CrystalBuildOutputParser(
    private val buildProgress: BuildProgress<BuildProgressDescriptor>,
    workDirectoryPath: Path,
    val project: Project
) : BuildOutputParser {
    private data class Location(
        val path: String, val line: Int, val column: Int
    )

    private var location: Location? = null

    private val rootDir = workDirectoryPath.findVirtualFile(false)

    override fun parse(
        line: String,
        reader: BuildOutputInstantReader,
        messageConsumer: Consumer<in BuildEvent>
    ): Boolean {
        COMPILER_PASS_PATTERN.matchEntire(line)?.let {
            val groupValues = it.groupValues
            val passNum = groupValues[1].toLong()
            val passCount = groupValues[2].toLong()
            val message = groupValues[3].trim()
            buildProgress.progress(message, passCount, passNum, "passes")
            return true
        }
        LOCATION_PATTERN.matchEntire(line)?.let {
            val groupValues = it.groupValues
            val filePath = groupValues[1]
            val lineNum = groupValues[2].toInt()
            val columnNum = groupValues[3].toInt()
            location = Location(filePath, lineNum, columnNum)
            return true
        }
        ERROR_PATTERN.matchEntire(line)?.let {
            reportMessage(it, MessageEvent.Kind.ERROR)
            return true
        }
        WARNING_PATTERN.matchEntire(line)?.let {
            reportMessage(it, MessageEvent.Kind.WARNING)
            return true
        }
        return true
    }

    private fun reportMessage(result: MatchResult, kind: MessageEvent.Kind) {
        val message = StringUtil.capitalize(result.groupValues[1].trim())
        val navigatable = createNavigatableAndResetLocation()
        buildProgress.message(message, message, kind, navigatable)
    }

    private fun createNavigatableAndResetLocation(): Navigatable? {
        val result = location?.let {
            val file = rootDir?.findFile(it.path) ?: return@let null
            OpenFileDescriptor(project, file, it.line - 1, it.column - 1)
        }
        location = null
        return result
    }
}