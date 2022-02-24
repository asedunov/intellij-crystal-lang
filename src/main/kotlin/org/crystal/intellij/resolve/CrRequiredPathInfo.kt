package org.crystal.intellij.resolve

import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileVisitor
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.PsiFileSystemItem
import com.intellij.psi.ResolveResult
import com.intellij.util.SmartList
import com.intellij.util.containers.addIfNotNull
import com.intellij.util.containers.map2Array
import org.crystal.intellij.psi.CrRequireExpression
import org.crystal.intellij.psi.module
import org.crystal.intellij.util.get
import org.crystal.intellij.util.toPsi

class CrRequiredPathInfo private constructor(
    require: CrRequireExpression,
    private val path: String
) {
    companion object {
        private fun String.trimPath(): String {
            val n = length
            var i = 0
            var j = n - 1
            "".trim()
            while (i < n && get(i).let { it.isWhitespace() || it == '/' }) i++
            while (j >= 0 && get(j).isWhitespace()) j--
            return substring(i, j + 1)
        }

        fun of(require: CrRequireExpression): CrRequiredPathInfo? {
            val path = require.filePath?.trimPath()
            if (path.isNullOrEmpty()) return null
            return CrRequiredPathInfo(require, path)
        }
    }

    private inner class ResolvedPathBuilder(
        private val rootFile: VirtualFile,
        private val results: MutableList<SmartList<PsiFileSystemItem>>
    ) {
        var file = rootFile
            private set
        private val totalSize = segments.size
        private val path = ArrayList<PsiFileSystemItem>(totalSize)
        val psiManager = currentFile.manager!!
        var resolved = false
            private set
        var valid = true
            private set

        fun extendPath(refCount: Int): ResolvedPathBuilder {
            if (refCount < 0) return invalidate()

            val from = path.size
            val to = from + refCount
            if (to > totalSize) return invalidate()
            for (i in from until to) {
                val name = segments[i]
                extendContext(name)
                if (valid) {
                    val psi = file.toPsi(psiManager) ?: return invalidate()
                    path += psi
                }
                else break
            }
            return this
        }

        fun extendPath(name: String): ResolvedPathBuilder {
            if (path.size >= totalSize) return invalidate()
            extendContext(name)
            if (valid) {
                val psi = file.toPsi(psiManager) ?: return invalidate()
                path += psi
            }
            return this
        }

        fun extendContext(name: String): ResolvedPathBuilder {
            val nextFile = file[name] ?: return invalidate()
            file = nextFile
            return this
        }

        private fun invalidate() : ResolvedPathBuilder {
            valid = false
            return this
        }

        fun finishPath() {
            if (valid && path.size == totalSize && !resolved) {
                resolved = true
                results.forEach { it.clear() }
            }

            if (path.size == totalSize || !resolved) {
                for (i in path.indices) {
                    val target = path[i]
                    if (target.isDirectory && i == totalSize) continue
                    if (i == results.size) {
                        results.add(SmartList())
                    }
                    val targets = results[i]
                    if (target !in targets) {
                        targets.add(target)
                    }
                }
            }

            file = rootFile
            path.clear()
            valid = true
        }
    }

    private val currentFile = require.containingFile

    private val segments = path.split('/')

    private fun computeResolveResults(): List<Array<ResolveResult>> {
        val refCount = segments.size
        if (refCount == 0) return emptyList()

        val roots = defaultContexts
        if (roots.isEmpty()) return emptyList()

        val results = ArrayList<SmartList<PsiFileSystemItem>>(refCount)
        for (root in roots) {
            val rootVFile = root.virtualFile ?: continue
            if (resolveInContext(rootVFile, results)) break
        }

        if (results.isEmpty()) return emptyList()
        return results.map { refResults ->
            refResults.map2Array { PsiElementResolveResult(it) }
        }
    }

    private fun resolveInContext(
        root: VirtualFile,
        results: MutableList<SmartList<PsiFileSystemItem>>
    ): Boolean {
        val builder = ResolvedPathBuilder(root, results)
        val refCount = segments.size
        val name = segments.last()
        val isWildCard = name == "*"
        val isRecursiveWildCard = name == "**"
        if (isWildCard || isRecursiveWildCard) {
            builder.extendPath(refCount - 1)
            val rootFile = builder.file.takeIf { builder.valid }
            builder.finishPath()
            if (rootFile == null) return false
            val currentVFile = currentFile.virtualFile
            val targetList = SmartList<PsiFileSystemItem>()
            VfsUtil.visitChildrenRecursively(rootFile, object : VirtualFileVisitor<Unit>() {
                override fun getChildrenIterable(file: VirtualFile): List<VirtualFile> {
                    return if (isRecursiveWildCard || file == rootFile) {
                        file.children.sortedWith { f1, f2 ->
                            when {
                                f1.isDirectory && !f2.isDirectory -> 1
                                !f1.isDirectory && f2.isDirectory -> -1
                                else -> f1.path.compareTo(f2.path)
                            }
                        }
                    } else emptyList()
                }

                override fun visitFileEx(file: VirtualFile): Result {
                    if (file != currentVFile && file.extension == "cr") {
                        targetList.addIfNotNull(file.toPsi(builder.psiManager))
                    }
                    return CONTINUE
                }
            })
            results += targetList
            return true
        }
        else {
            // Prefer direct path to .cr file
            val lastName = if (name.endsWith(".cr")) name else "$name.cr"
            builder
                .extendPath(refCount - 1)
                .extendPath(lastName)
                .finishPath()

            val shardName = segments.first()
            val isRelative = shardName == "."
            val crName = "$name.cr"
            if (!(isRelative || shardName.isBlank())) {
                // "foo/bar/baz" -> "foo/src/bar/baz.cr" (shard, non-namespaced)
                builder
                    .extendPath(1)
                    .extendContext("src")
                    .extendPath(refCount - 2)
                    .extendPath(crName)
                    .finishPath()

                // "foo/bar/baz" -> "foo/src/foo/bar/baz.cr" (shard, namespaced)
                builder
                    .extendContext(shardName)
                    .extendContext("src")
                    .extendPath(refCount - 1)
                    .extendPath(crName)
                    .finishPath()

                // "foo/bar/baz" -> "foo/bar/baz/baz.cr" (std, nested)
                builder
                    .extendPath(refCount - 1)
                    .extendContext(name)
                    .extendPath(crName)
                    .finishPath()

                // "foo/bar/baz" -> "foo/src/bar/baz/baz.cr" (shard, non-namespaced, nested)
                builder
                    .extendPath(1)
                    .extendContext("src")
                    .extendPath(refCount - 2)
                    .extendContext(name)
                    .extendPath(crName)
                    .finishPath()

                // "foo/bar/baz" -> "foo/src/foo/bar/baz/baz.cr" (shard, namespaced, nested)
                builder
                    .extendContext(shardName)
                    .extendContext("src")
                    .extendPath(refCount - 1)
                    .extendContext(name)
                    .extendPath(crName)
                    .finishPath()

                return builder.resolved
            }

            // "foo/bar/baz" -> "foo/bar/baz/baz.cr" (std, nested)
            builder
                .extendPath(refCount - 1)
                .extendContext(name)
                .extendPath(crName)
                .finishPath()

            // "foo/bar/baz" -> "foo/bar/baz/src/baz.cr" exists (shard)
            builder
                .extendPath(refCount - 1)
                .extendContext(name)
                .extendContext("src")
                .extendPath(crName)
                .finishPath()

            return builder.resolved
        }
    }

    val defaultContexts: List<PsiFileSystemItem> by lazy {
        if (path.startsWith('.')) {
            listOfNotNull(currentFile.containingDirectory)
        } else {
            currentFile.module()?.crystalPathRoots() ?: currentFile.project.crystalPathRoots()
        }
    }

    val targets by lazy(::computeResolveResults)
}