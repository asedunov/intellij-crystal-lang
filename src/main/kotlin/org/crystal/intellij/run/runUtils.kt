package org.crystal.intellij.run

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.toNioPath
import com.intellij.openapi.vfs.VirtualFile
import org.crystal.intellij.shards.yaml.model.findAllShardYamls
import org.crystal.intellij.util.isAncestor

private fun suggestWorkingDirectoryByFileToRun(file: VirtualFile, project: Project): VirtualFile? {
    for (shardYaml in project.findAllShardYamls()) {
        val parent = shardYaml.parent ?: continue
        if (parent.isAncestor(file)) return parent
    }
    return null
}

fun CrystalFileRunConfigurationBase.setFileAndWorkingDirectory(mainFile: VirtualFile?, shardFile: VirtualFile? = null) {
    targetFile = mainFile?.path?.toNioPath()
    val wcCandidate = shardFile?.parent ?: mainFile?.let { suggestWorkingDirectoryByFileToRun(it, project) }
    if (wcCandidate != null) {
        workingDirectory = wcCandidate.toNioPath()
    }
}