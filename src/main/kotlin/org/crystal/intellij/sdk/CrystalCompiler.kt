package org.crystal.intellij.sdk

import com.intellij.openapi.vfs.VirtualFile
import org.crystal.intellij.config.project.CrystalProjectTemplate
import org.crystal.intellij.shards.yaml.model.SHARD_YAML_NAME
import org.crystal.intellij.util.*

class CrystalCompiler(val peer: CrystalToolPeer) {
    fun generateProject(
        baseDir: VirtualFile,
        name: String,
        template: CrystalProjectTemplate
    ): CrProcessResult<CrystalGeneratedProjectLayout> {
        val templateArg = when (template) {
            CrystalProjectTemplate.APPLICATION -> "app"
            CrystalProjectTemplate.LIBRARY -> "lib"
        }
        peer
            .buildCommandLine(listOf("init", templateArg, name, "--force"))
            ?.withWorkDirectory(baseDir.parent.path)
            ?.execute(null) { runProcess() }
            ?.unwrapOrElse { return Result.Err(it) }

        baseDir.fullRefresh()

        val shardYaml = baseDir.findChild(SHARD_YAML_NAME)
        val mainFile = baseDir.findFileByRelativePath("src/$name.cr")
        return Result.Ok(CrystalGeneratedProjectLayout(shardYaml, mainFile))
    }

    val isValid: Boolean
        get() = peer.isValid
}