package org.crystal.intellij.build

import com.intellij.build.BuildContentDescriptor
import com.intellij.build.BuildViewManager
import com.intellij.build.DefaultBuildDescriptor
import com.intellij.build.output.BuildOutputInstantReaderImpl
import com.intellij.build.progress.BuildProgressDescriptor
import com.intellij.execution.process.ProcessAdapter
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.text.StringUtil
import org.crystal.intellij.run.notifyProcessStarted
import org.crystal.intellij.run.notifyProcessTerminated
import org.crystal.intellij.run.notifyProcessTerminating
import org.crystal.intellij.util.findVirtualFile
import org.crystal.intellij.util.fullRefresh
import java.nio.file.Path
import javax.swing.JComponent
import kotlin.io.path.pathString

@Suppress("UnstableApiUsage")
class CrystalBuildProcessListener(
    processHandler: ProcessHandler,
    private val workDirectoryPath: Path,
    private val environment: ExecutionEnvironment,
    buildViewManager: BuildViewManager
) : ProcessAdapter() {
    private val buildProgress = BuildViewManager.createBuildProgress(environment.project)

    private val buildId: Any
        get() = buildProgress.id

    init {
        environment.notifyProcessStarted(processHandler)

        val buildContentDescriptor = BuildContentDescriptor(null, null, object : JComponent() {}, "Build")
        val descriptor = DefaultBuildDescriptor(
            Any(),
            "Crystal Build",
            workDirectoryPath.pathString,
            System.currentTimeMillis()
        ).withContentDescriptor { buildContentDescriptor }

        buildProgress.start(object : BuildProgressDescriptor {
            override fun getTitle() = "Build Running..."

            override fun getBuildDescriptor() = descriptor
        })
    }

    private val instantReader = BuildOutputInstantReaderImpl(
        buildId,
        buildId,
        buildViewManager,
        listOf(CrystalBuildOutputParser(buildProgress, workDirectoryPath, environment.project))
    )

    override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
        instantReader.append(StringUtil.convertLineSeparators(event.text))
    }

    override fun processWillTerminate(event: ProcessEvent, willBeDestroyed: Boolean) {
        environment.notifyProcessTerminating(event.processHandler)
    }

    override fun processTerminated(event: ProcessEvent) {
        instantReader.closeAndGetFuture().whenComplete { _, _ ->
            val isSuccess = event.exitCode == 0
            val endTime = System.currentTimeMillis()
            if (isSuccess) {
                buildProgress.finish(endTime, false, "Build successful")
            }
            else {
                buildProgress.fail(endTime, "Build failed")
            }
            environment.notifyProcessTerminated(event.processHandler, event.exitCode)
            workDirectoryPath.findVirtualFile(true)?.fullRefresh(true)
        }
    }
}