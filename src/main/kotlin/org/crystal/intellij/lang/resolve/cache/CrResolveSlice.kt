package org.crystal.intellij.lang.resolve.cache

import com.intellij.openapi.util.KeyWithDefaultValue

typealias CrResolveSlice<K, V> = KeyWithDefaultValue<MutableMap<K, V>>