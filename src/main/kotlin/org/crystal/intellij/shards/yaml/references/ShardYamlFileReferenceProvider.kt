package org.crystal.intellij.shards.yaml.references

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet
import com.intellij.util.ProcessingContext
import org.jetbrains.yaml.psi.YAMLScalar

object ShardYamlFileReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<out PsiReference> {
        val value = element as? YAMLScalar ?: return emptyArray()
        val referenceSet = FileReferenceSet(value.textValue, value, 0, this, true)
        return referenceSet.allReferences
    }
}