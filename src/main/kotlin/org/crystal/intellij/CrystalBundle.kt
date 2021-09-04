package org.crystal.intellij

import com.intellij.DynamicBundle
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey

@NonNls
private const val BUNDLE = "messages.CrystalBundle"

object CrystalBundle : DynamicBundle(BUNDLE) {
    @Nls
    @JvmStatic
    fun message(
        @NonNls @PropertyKey(resourceBundle = BUNDLE) key: String,
        vararg params: Any
    ): String = getMessage(key, *params)
}