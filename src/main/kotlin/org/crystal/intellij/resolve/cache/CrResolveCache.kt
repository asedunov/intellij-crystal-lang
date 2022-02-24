@file:Suppress("UnstableApiUsage", "DEPRECATION")

package org.crystal.intellij.resolve.cache

import com.intellij.openapi.Disposable
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.*
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.AnyPsiChangeListener
import com.intellij.psi.impl.PsiManagerImpl
import com.intellij.psi.util.PsiUtilCore
import com.intellij.util.IdempotenceChecker
import com.intellij.util.ObjectUtils
import com.intellij.util.containers.ConcurrentWeakKeySoftValueHashMap
import org.crystal.intellij.resolve.resolveFacade
import java.lang.ref.ReferenceQueue

/**
 * Based on [com.intellij.psi.impl.source.resolve.ResolveCache]
 * Major differences:
 * - keys are not restricted to PsiElement
 * - multiple cache slices are maintained simultaneously
 */
class CrResolveCache(project: Project) : Disposable {
    private val caches = object : UserDataHolderBase() {
        public override fun clearUserData() {
            super.clearUserData()
        }
    }

    init {
        project.messageBus.connect().subscribe(PsiManagerImpl.ANY_PSI_CHANGE_TOPIC, object : AnyPsiChangeListener {
            override fun beforePsiChanged(isPhysical: Boolean) {
                caches.clearUserData()
            }
        })
        LowMemoryWatcher.register({ caches.clearUserData() }, this)
    }

    fun <K : Any, V : Any> getOrCompute(slice: CrResolveSlice<K, V>, key: K, resolve: (K) -> V?): V? {
        ProgressManager.checkCanceled()

        val cache = caches.getUserData(slice)!!
        val cachedResult = cache[key]
        if (cachedResult != null) {
            if (IdempotenceChecker.areRandomChecksEnabled()) {
                IdempotenceChecker.applyForRandomCheck(
                    cachedResult,
                    key,
                    loggingResolver(key, resolve)
                )
            }
            return cachedResult
        }

        val stamp = RecursionManager.markStack()
        val loggingResolver = loggingResolver(key, resolve)
        val result = RecursionManager.doPreventingRecursion(Pair.create(key, cache), true, loggingResolver)
        ensureValidResult(result)
        if (stamp.mayCacheNow()) {
            cache(key, cache, result, loggingResolver)
        }
        return result
    }

    @Suppress("UNCHECKED_CAST")
    private fun <K : Any, V : Any> cache(
        key: K,
        map: MutableMap<K, V>,
        result: V?,
        doResolve: () -> V?
    ) {
        // optimization: less contention
        var cached = map[key]
        if (cached != null) {
            if (cached === result) {
                return
            }
            IdempotenceChecker.checkEquivalence(cached, result, key.javaClass, doResolve)
        }
        cached = (result ?: NULL_RESULT) as V // no use in creating SoftReference to null
        map[key] = cached
    }

    override fun dispose() {}
}

private class StrongValueReference<K, V>(
    private val myValue: V
) : ConcurrentWeakKeySoftValueHashMap.ValueReference<K, V> {
    override fun getKeyReference(): ConcurrentWeakKeySoftValueHashMap.KeyReference<K, V> {
        // will never GC so this method will never be called so no implementation is necessary
        throw UnsupportedOperationException()
    }

    override fun get() = myValue
}

private val NULL_RESULT = ObjectUtils.sentinel("CrResolveCache.NULL_RESULT")
private val NULL_VALUE_REFERENCE: StrongValueReference<*, *> = StrongValueReference<Any, Any>(NULL_RESULT)
private val EMPTY_RESOLVE_RESULT: StrongValueReference<*, *> = StrongValueReference<Any, Array<ResolveResult>>(ResolveResult.EMPTY_ARRAY)

@Suppress("UNCHECKED_CAST")
private fun <K, V> createStrongReference(value: V): StrongValueReference<K, V> {
    return when {
        value === NULL_RESULT -> NULL_VALUE_REFERENCE as StrongValueReference<K, V>
        value === ResolveResult.EMPTY_ARRAY -> EMPTY_RESOLVE_RESULT as StrongValueReference<K, V>
        else -> StrongValueReference(value)
    }
}

private fun <K : Any, V : Any> createWeakMap(): MutableMap<K, V> {
    return object : ConcurrentWeakKeySoftValueHashMap<K, V>(
        100,
        0.75f,
        Runtime.getRuntime().availableProcessors()
    ) {
        override fun createValueReference(value: V, queue: ReferenceQueue<in V>): ValueReference<K, V> {
            return if (value === NULL_RESULT || value is Array<*> && value.isEmpty()) {
                // no use in creating SoftReference to null
                createStrongReference(value)
            } else {
                super.createValueReference(value, queue)
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun get(key: K): V? {
            val v = super.get(key)
            return if (v === NULL_RESULT) null else v
        }

        override fun equals(other: Any?): Boolean {
            // The map instance is used as recursion prevention key.
            // Each instance is determined by several flags: physical, incomplete, poly;
            // Each instance is unique, so we don't need to store flags to check equality.
            return this === other
        }

        override fun hashCode() = System.identityHashCode(this)
    }
}

private fun <K : Any, V> loggingResolver(key: K, resolver: (K) -> V): () -> V = {
    if (IdempotenceChecker.isLoggingEnabled()) {
        IdempotenceChecker.logTrace("Resolving " + key + " of " + key::class)
    }
    resolver(key)
}

private fun ensureValidResult(result: Any?): Unit = when (result) {
    is ResolveResult -> ensureValidPsi(result)
    is Array<*> -> ensureValidResults(result)
    is List<*> -> ensureValidResults(result)
    is PsiElement -> PsiUtilCore.ensureValid(result)
    else -> Unit
}

private fun ensureValidResults(result: Array<*>) =
    result.forEach { ensureValidResult(it) }

private fun ensureValidResults(result: List<*>) =
    result.forEach { ensureValidResult(it) }

private fun ensureValidPsi(resolveResult: ResolveResult) {
    val element = resolveResult.element
    if (element != null) {
        PsiUtilCore.ensureValid(element)
    }
}

fun <K : Any, V : Any> newResolveSlice(
    name: String
): CrResolveSlice<K, V> = KeyWithDefaultValue.create(name, ::createWeakMap)

val Project.resolveCache: CrResolveCache
    get() = resolveFacade.resolveCache