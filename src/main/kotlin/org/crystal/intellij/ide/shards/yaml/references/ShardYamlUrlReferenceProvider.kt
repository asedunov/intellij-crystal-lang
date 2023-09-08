package org.crystal.intellij.ide.shards.yaml.references

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.paths.WebReference
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext
import org.jetbrains.yaml.psi.YAMLKeyValue
import org.jetbrains.yaml.psi.YAMLScalar

object ShardYamlUrlReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        val value = element as? YAMLScalar ?: return emptyArray()
        val key = (element.parent as? YAMLKeyValue)?.name ?: return emptyArray()
        val text = value.textValue
        val url = getUrl(text, key)
        return arrayOf(WebReference(element, url))
    }

    private fun getUrl(refText: String, key: String): String {
        val prefix = when(key) {
            "github" -> "https://github.com/"
            "gitlab" -> "https://gitlab.com/"
            "bitbucket" -> "https://bitbucket.org/"
            else -> if (BrowserUtil.isAbsoluteURL(refText)) "" else "https://"
        }
        return prefix + refText
    }
}