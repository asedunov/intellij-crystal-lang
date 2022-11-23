package org.crystal.intellij.sdk

import com.intellij.openapi.vfs.VirtualFile

data class CrystalGeneratedProjectLayout(
    val shardYaml: VirtualFile?,
    val mainFile: VirtualFile?
)