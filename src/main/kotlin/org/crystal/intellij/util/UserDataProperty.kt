package org.crystal.intellij.util

import com.intellij.openapi.util.Key
import com.intellij.openapi.util.UserDataHolder
import kotlin.reflect.KProperty

class UserDataProperty<in R : UserDataHolder, T : Any>(val key: Key<T>) {
    operator fun getValue(thisRef: R, desc: KProperty<*>) = thisRef.getUserData(key)

    operator fun setValue(thisRef: R, desc: KProperty<*>, value: T?) = thisRef.putUserData(key, value)
}