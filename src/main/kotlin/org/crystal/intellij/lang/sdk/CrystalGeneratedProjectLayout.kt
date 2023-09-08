package org.crystal.intellij.lang.sdk

import com.intellij.openapi.vfs.VirtualFile

data class CrystalGeneratedProjectLayout(
    val shardYaml: VirtualFile?,
    val mainFile: VirtualFile?
)