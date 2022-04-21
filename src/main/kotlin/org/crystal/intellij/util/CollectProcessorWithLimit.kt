package org.crystal.intellij.util

import com.intellij.util.CommonProcessors.CollectProcessor
import java.util.concurrent.atomic.AtomicInteger

// Based on com.intellij.psi.search.PsiElementProcessor.CollectElementsWithLimit but for any element type
internal class CollectProcessorWithLimit<T>(
    private val limit: Int
) : CollectProcessor<T>(HashSet()) {
    private val count = AtomicInteger(0)

    @Volatile
    var isOverflow = false
        private set

    override fun process(element: T): Boolean {
        if (count.get() == limit) {
            isOverflow = true
            return false
        }
        count.incrementAndGet()
        return super.process(element)
    }
}