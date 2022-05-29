package org.crystal.intellij.util

sealed class Result<out T, out E> {
    data class Ok<T>(val ok: T) : Result<T, Nothing>()
    data class Err<E>(val err: E) : Result<Nothing, E>()
}

inline fun <T, E> Result<T, E>.unwrapOrElse(op: (E) -> T): T = when (this) {
    is Result.Ok -> ok
    is Result.Err -> op(err)
}