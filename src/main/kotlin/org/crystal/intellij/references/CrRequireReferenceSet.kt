package org.crystal.intellij.references

import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.rootManager
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileVisitor
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.PsiFileSystemItem
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.PsiManagerEx
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet
import com.intellij.util.SmartList
import com.intellij.util.containers.addIfNotNull
import com.intellij.util.containers.map2Array
import org.crystal.intellij.config.crystalWorkspaceSettings
import org.crystal.intellij.psi.CrStringLiteralExpression
import org.crystal.intellij.util.get
import org.crystal.intellij.util.toPsi

class CrRequireReferenceSet(
    path: CrStringLiteralExpression
) : FileReferenceSet(
    path.valueRangeInElement.substring(path.text),
    path,
    path.valueRangeInElement.startOffset,
    null,
    true
) {
    override fun getElement() = super.getElement() as CrStringLiteralExpression

    override fun createFileReference(range: TextRange, index: Int, text: String): FileReference {
        return CrRequireReference(this, range, index, text)
    }

    override fun computeDefaultContexts(): Collection<PsiFileSystemItem> {
        val pathString = pathString
        if (pathString.startsWith('.')) {
            return super.computeDefaultContexts()
        }
        else {
            val contexts = ArrayList<PsiFileSystemItem>()
            val module = ModuleUtilCore.findModuleForPsiElement(element) ?: return super.computeDefaultContexts()
            val psiManager = element.manager
            module.rootManager.contentRoots.flatMapTo(contexts) {
                val dir = psiManager.findDirectory(it) ?: return@flatMapTo emptyList()
                listOfNotNull(dir, dir.findSubdirectory("src"), dir.findSubdirectory("lib"))
            }
            contexts.addIfNotNull(module.project.crystalWorkspaceSettings.stdlibRootDirectory?.toPsi(psiManager))
            return contexts
        }
    }

    override fun reparse(str: String?, startInElement: Int): MutableList<FileReference> {
        val literal = element
        val escaper = literal.createLiteralTextEscaper()
        val valueRange = literal.valueRangeInElement
        val references = SmartList<FileReference>()
        val sb = StringBuilder()
        escaper.decode(valueRange, sb)
        val pathSize = sb.length
        var refStart = 0
        var refIndex = 0
        while (true) {
            while (refStart < pathSize && sb[refStart] == '/') refStart++

            val sepPos = sb.indexOf('/', refStart + 1)
            val refEnd = if (sepPos >= 0) sepPos else pathSize
            if (refEnd > refStart) {
                val refText = sb.substring(refStart, refEnd)
                val refRange = TextRange(
                    escaper.getOffsetInHost(refStart, valueRange),
                    escaper.getOffsetInHost(refEnd, valueRange)
                )
                references += createFileReference(refRange, refIndex++, refText)
            }
            refStart = refEnd + 1
            if (refStart >= pathSize) break
        }
        return references
    }

    val resolveResults: List<Array<ResolveResult>> by lazy {
        computeResolveResults()
    }

    override fun equals(other: Any?): Boolean {
        return other === this || other is CrRequireReferenceSet && element == other.element
    }

    override fun hashCode() = element.hashCode()

    private fun computeResolveResults(): List<Array<ResolveResult>> {
        val references = allReferences
        val refCount = references.size
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
        val refCount = allReferences.size
        val name = lastReference!!.text
        val isWildCard = name == "*"
        val isRecursiveWildCard = name == "**"
        if (isWildCard || isRecursiveWildCard) {
            builder.extendPath(refCount - 1)
            val rootFile = builder.file.takeIf { builder.valid }
            builder.finishPath()
            if (rootFile == null) return false
            val currentFile = element.containingFile
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

            val shardName = allReferences.first().text
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

    private inner class ResolvedPathBuilder(
        private val rootFile: VirtualFile,
        private val results: MutableList<SmartList<PsiFileSystemItem>>
    ) {
        var file = rootFile
            private set
        private val totalSize = allReferences.size
        private val path = ArrayList<PsiFileSystemItem>(totalSize)
        val psiManager: PsiManagerEx = element.manager
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
                val name = allReferences[i].text
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
}