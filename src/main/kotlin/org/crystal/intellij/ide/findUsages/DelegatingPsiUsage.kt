package org.crystal.intellij.ide.findUsages

import com.intellij.find.usages.api.PsiUsage
import com.intellij.model.Pointer
import com.intellij.usages.impl.rules.UsageType

@Suppress("UnstableApiUsage")
internal class DelegatingPsiUsage(
    private val delegate: PsiUsage,
    private val type: UsageType
) : PsiUsage by delegate {
    override fun createPointer(): Pointer<out PsiUsage> {
        return Pointer.delegatingPointer(delegate.createPointer()) { delegate: PsiUsage ->
            DelegatingPsiUsage(delegate, usageType)
        }
    }

    override val usageType: UsageType
        get() = type
}