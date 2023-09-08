package org.crystal.intellij.lang.psi

import org.crystal.intellij.lang.config.CrystalLevel
import java.math.BigInteger

enum class CrIntegerKind(
    val typeName: String,
    val parser: (text: String, radix: Int, level: CrystalLevel) -> Any?
) {
    I8("Int8", { s, radix, _ -> s.toByteOrNull(radix) }),
    U8("UInt8", { s, radix, _ -> s.toUByteOrNull(radix) }),
    I16("Int16", { s, radix, _ -> s.toShortOrNull(radix) }),
    U16("UInt16", { s, radix, _ -> s.toUShortOrNull(radix) }),
    I32("Int32", { s, radix, _ -> s.toIntOrNull(radix) }),
    U32("UInt32", { s, radix, _ -> s.toUIntOrNull(radix) }),
    I64("Int64", { s, radix, _ -> s.toLongOrNull(radix) }),
    U64("UInt64", { s, radix, _ -> s.toULongOrNull(radix) }),
    I128("Int128", { s, radix, ll -> if (ll >= CrystalLevel.CRYSTAL_1_3) s.toInt128OrNull(radix) else s.toLongOrNull(radix) }),
    U128("UInt128", { s, radix, ll -> if (ll >= CrystalLevel.CRYSTAL_1_3) s.toUInt128OrNull(radix) else s.toULongOrNull(radix) });
}

val INT128_MIN: BigInteger = BigInteger("-170141183460469231731687303715884105728")
val INT128_MAX: BigInteger = BigInteger("170141183460469231731687303715884105727")
val INT128_RANGE = INT128_MIN..INT128_MAX
val UINT128_MIN: BigInteger = BigInteger.ZERO
val UINT128_MAX: BigInteger = BigInteger("340282366920938463463374607431768211455")
val UINT128_RANGE = UINT128_MIN..UINT128_MAX

fun String.toInt128OrNull(radix: Int): BigInteger? {
    if (length > 128) return null
    return toBigIntegerOrNull(radix)?.takeIf { it in INT128_RANGE }
}

fun String.toUInt128OrNull(radix: Int): BigInteger? {
    if (length > 128) return null
    return toBigIntegerOrNull(radix)?.takeIf { it in UINT128_RANGE }
}