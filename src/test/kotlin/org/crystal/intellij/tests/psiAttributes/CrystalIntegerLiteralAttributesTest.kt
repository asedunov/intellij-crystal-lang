package org.crystal.intellij.tests.psiAttributes

import org.crystal.intellij.psi.CrIntegerKind
import org.crystal.intellij.psi.CrIntegerLiteralExpression

class CrystalIntegerLiteralAttributesTest : CrystalPsiAttributeTest() {
    private fun checkIntKind(text: String, kind: CrIntegerKind) {
        checkFirst(text, CrIntegerLiteralExpression::kind, kind)
    }

    private fun checkIntRadix(text: String, value: Int) {
        checkFirst(text, CrIntegerLiteralExpression::radix, value)
    }

    private fun checkIntValue(text: String, value: Any?) {
        checkFirst<CrIntegerLiteralExpression>(text, { value?.toString() }, value?.toString())
    }

    fun testIntegerKinds() {
        checkIntKind("1_i8", CrIntegerKind.I8)
        checkIntKind("1_u8", CrIntegerKind.U8)
        checkIntKind("1_i16", CrIntegerKind.I16)
        checkIntKind("1_u16", CrIntegerKind.U16)
        checkIntKind("1_i32", CrIntegerKind.I32)
        checkIntKind("1_u32", CrIntegerKind.U32)
        checkIntKind("1_i64", CrIntegerKind.I64)
        checkIntKind("1_u64", CrIntegerKind.U64)
        checkIntKind("1_i128", CrIntegerKind.I128)
        checkIntKind("1_u128", CrIntegerKind.U128)
        checkIntKind("1", CrIntegerKind.I32)
        checkIntKind("-1", CrIntegerKind.I32)
        checkIntKind("0x7FFFFFFF", CrIntegerKind.I32)
        checkIntKind("-0x8000000", CrIntegerKind.I32)
        checkIntKind("0x80000000", CrIntegerKind.I64)
        checkIntKind("0x7FFFFFFFFFFFFFFF", CrIntegerKind.I64)
        checkIntKind("-0x8000000000000000", CrIntegerKind.I64)
        checkIntKind("0x8000000000000000", CrIntegerKind.U64)
    }

    fun testIntegerRadix() {
        checkIntRadix("123", 10)
        checkIntRadix("0x123", 16)
        checkIntRadix("0o123", 8)
        checkIntRadix("0b101", 2)
    }

    fun testBinIntegers() {
        checkIntValue("0b1_i8", 1)
        checkIntValue("0b1_1010_i8", 26)
        checkIntValue("0b1111111_i8", 127)
        checkIntValue("0b10000000_i8", null)
        checkIntValue("0b10101010_i8", null)
        checkIntValue("-0b11010_i8", -26)
        checkIntValue("-0b1_1010_i8", -26)
        checkIntValue("-0b10000000_i8", -128)
        checkIntValue("-0b10101010_i8", null)

        checkIntValue("0b1_u8", 1)
        checkIntValue("0b1_1010_u8", 26)
        checkIntValue("0b11111111_u8", 255)
        checkIntValue("0b100000000_u8", null)
        checkIntValue("-0b1_u8", null)

        checkIntValue("0b100000000_i16", 256)
        checkIntValue("0b_1_0000_0000_i16", 256)
        checkIntValue("0b111111111111111_i16", 32767)
        checkIntValue("0b1000000000000000_i16", null)
        checkIntValue("0b1010101010101010_i16", null)
        checkIntValue("-0b100000000_i16", -256)
        checkIntValue("-0b1_0000_0000_i16", -256)
        checkIntValue("-0b1000000000000000_i16", -32768)
        checkIntValue("-0b1010101010101010_i16", null)

        checkIntValue("0b100000000_u16", 256)
        checkIntValue("0b_1_0000_0000_u16", 256)
        checkIntValue("0b1111111111111111_u16", 65535)
        checkIntValue("0b10000000000000000_u16", null)
        checkIntValue("-0b1_u16", null)

        checkIntValue("0b1_i32", 1)
        checkIntValue("0b1111111_11111111_11111111_11111111_i32", 2147483647)
        checkIntValue("0b1111111111111111111111111111111_i32", 2147483647)
        checkIntValue("0b10000000000000000000000000000000_i32", null)
        checkIntValue("0b10101010101010101010101010101010_i32", null)
        checkIntValue("-0b10000_0000_0000_0000_i32", 65535)
        checkIntValue("-0b10000000000000000000000000000000_i32", -2147483648)
        checkIntValue("-0b10101010101010101010101010101010_i32", null)

        checkIntValue("0b100000000_u32", 256)
        checkIntValue("0b10000_0000_0000_0000_u32", 65536)
        checkIntValue("0b1111_1111_1111_1111_1111_1111_1111_1111_u32", 4294967295)
        checkIntValue("0b11111111111111111111111111111111_u32", 4294967295)
        checkIntValue("0b100000000000000000000000000000000_u32", null)
        checkIntValue("-0b1_u32", null)

        checkIntValue("0b1_i64", 1)
        checkIntValue(
            "0b1111111_11111111_11111111_11111111_11111111_11111111_11111111_11111111_i64",
            9223372036854775807
        )
        checkIntValue(
            "0b111111111111111111111111111111111111111111111111111111111111111_i64",
            9223372036854775807
        )
        checkIntValue("0b1000000000000000000000000000000000000000000000000000000000000000_i64", null)
        checkIntValue("0b1010101010101010101010101010101010101010101010101010101010101010_i64", null)
        checkIntValue("-0b1_0000_0000_0000_0000_i64", -65536)
        checkIntValue(
            "-0b1000000000000000000000000000000000000000000000000000000000000000_i64",
            "-9223372036854775808"
        )
        checkIntValue("-0b1010101010101010101010101010101010101010101010101010101010101010_i64", null)

        checkIntValue("0b100000000_u64", 256)
        checkIntValue("0b1_0000_0000_0000_0000_u64", 65536)
        checkIntValue(
            "0b11111111_11111111_11111111_11111111_11111111_11111111_11111111_11111111_u64",
            "18446744073709551615"
        )
        checkIntValue(
            "0b1111111111111111111111111111111111111111111111111111111111111111_u64",
            "18446744073709551615"
        )
        checkIntValue("0b10000000000000000000000000000000000000000000000000000000000000000_u64", null)
        checkIntValue("0b11111111111111111111111111111111111111111111111111111111111111111_u64", null)
        checkIntValue("-0b1_u64", null)

        checkIntValue("0b1_i128", 1)
        checkIntValue(
            "0b1111111_11111111_11111111_11111111_11111111_11111111_11111111_11111111_i128",
            9223372036854775807
        )
        checkIntValue(
            "0b111111111111111111111111111111111111111111111111111111111111111_i128",
            9223372036854775807
        )
        checkIntValue("0b1000000000000000000000000000000000000000000000000000000000000000_i128", null)
        checkIntValue("0b1010101010101010101010101010101010101010101010101010101010101010_i128", null)
        checkIntValue("-0b1_0000_0000_0000_0000_i128", -65536)
        checkIntValue(
            "-0b1000000000000000000000000000000000000000000000000000000000000000_i128",
            "-9223372036854775808"
        )
        checkIntValue("-0b1010101010101010101010101010101010101010101010101010101010101010_i128", null)

        checkIntValue("0b100000000_u128", 256)
        checkIntValue("0b1_0000_0000_0000_0000_u128", 65536)
        checkIntValue(
            "0b11111111_11111111_11111111_11111111_11111111_11111111_11111111_11111111_u128",
            "18446744073709551615"
        )
        checkIntValue(
            "0b1111111111111111111111111111111111111111111111111111111111111111_u128",
            "18446744073709551615"
        )
        checkIntValue("0b10000000000000000000000000000000000000000000000000000000000000000_u128", null)
        checkIntValue("0b11111111111111111111111111111111111111111111111111111111111111111_u128", null)
        checkIntValue("-0b1_u128", null)
    }

    fun testOctalIntegers() {
        checkIntValue("0o1_i8", 1)
        checkIntValue("0o3_2_i8", 26)
        checkIntValue("0o177_i8", 127)
        checkIntValue("0o200_i8", null)
        checkIntValue("0o252_i8", null)
        checkIntValue("-0o32_i8", -26)
        checkIntValue("-0o3_2_i8", -26)
        checkIntValue("-0o200_i8", -128)
        checkIntValue("-0o252_i8", -128)

        checkIntValue("0o1_u8", 1)
        checkIntValue("0o3_2_u8", 26)
        checkIntValue("0o377_u8", 255)
        checkIntValue("0o400_u8", null)
        checkIntValue("-0o1_u8", null)

        checkIntValue("0o400_i16", 256)
        checkIntValue("0o_4_0_0_i16", 256)
        checkIntValue("0o77777_i16", 32767)
        checkIntValue("0o100000_i16", null)
        checkIntValue("0o125252_i16", null)
        checkIntValue("-0o400_i16", -256)
        checkIntValue("-0o4_0_0_i16", -256)
        checkIntValue("-0o100000_i16", -32768)
        checkIntValue("-0o125252_i16", null)

        checkIntValue("0o400_u16", 256)
        checkIntValue("0o_4_0_0_u16", 256)
        checkIntValue("0o177777_u16", 65535)
        checkIntValue("0o200000_u16", null)
        checkIntValue("-0o1_u16", null)

        checkIntValue("0o1_i32", 1)
        checkIntValue("0o17_777_777_777_i32", 2147483647)
        checkIntValue("0o17777777777_i32", 2147483647)
        checkIntValue("0o20000000000_i32", null)
        checkIntValue("0o25252525252_i32", null)
        checkIntValue("-0o200000_i32", -65536)
        checkIntValue("-0o20000000000_i32", -2147483648)
        checkIntValue("-0o25252525252_i32", null)

        checkIntValue("0o400_u32", 256)
        checkIntValue("0o200000_u32", 65536)
        checkIntValue("0o37_777_777_777_u32", 4294967295)
        checkIntValue("0o37777777777_u32", 4294967295)
        checkIntValue("0o40000000000_u32", null)
        checkIntValue("-0o1_u32", null)

        checkIntValue("0o1_i64", 1)
        checkIntValue("0o777_777_777_777_777_777_777_i64", 9223372036854775807)
        checkIntValue("0o777777777777777777777_i64", 9223372036854775807)
        checkIntValue("0o1000000000000000000000_i64", null)
        checkIntValue("0o1252525252525252525252_i64", null)
        checkIntValue("-0o20_00_00_i64", -65536)
        checkIntValue("-0o1000000000000000000000_i64", "-9223372036854775808")
        checkIntValue("-0o1252525252525252525252_i64", null)

        checkIntValue("0o400_u64", 256)
        checkIntValue("0o200000_u64", 65536)
        checkIntValue("0o1_777_777_777_777_777_777_777_u64", "18446744073709551615")
        checkIntValue("0o1777777777777777777777_u64", "18446744073709551615")
        checkIntValue("0o2000000000000000000000_u64", null)
        checkIntValue("0o3777777777777777777777_u64", null)
        checkIntValue("-0o1_u64", null)

        checkIntValue("0o1_i128", 1)
        checkIntValue("0o777_777_777_777_777_777_777_i128", 9223372036854775807)
        checkIntValue("0o777777777777777777777_i128", 9223372036854775807)
        checkIntValue("0o1000000000000000000000_i128", null)
        checkIntValue("0o1252525252525252525252_i128", null)
        checkIntValue("-0o20_00_00_i128", -65536)
        checkIntValue("-0o1000000000000000000000_i128", "-9223372036854775808")
        checkIntValue("-0o1252525252525252525252_i128", null)

        checkIntValue("0o400_u128", 256)
        checkIntValue("0o200000_u128", 65536)
        checkIntValue("0o1_777_777_777_777_777_777_777_u128", "18446744073709551615")
        checkIntValue("0o1777777777777777777777_u128", "18446744073709551615")
        checkIntValue("0o2000000000000000000000_u128", null)
        checkIntValue("0o3777777777777777777777_u128", null)
        checkIntValue("-0o1_u128", null)
    }

    fun testDecimalIntegers() {
        checkIntValue("1_i8", 1)
        checkIntValue("1_0_0_i8", 100)
        checkIntValue("127_i8", 127)
        checkIntValue("128_i8", null)
        checkIntValue("200_i8", null)
        checkIntValue("-100_i8", -100)
        checkIntValue("-1_0_0_i8", -100)
        checkIntValue("-128_i8", -128)
        checkIntValue("-200_i8", null)

        checkIntValue("1_u8", 1)
        checkIntValue("1_0_0_u8", 100)
        checkIntValue("255_u8", 255)
        checkIntValue("300_u8", null)
        checkIntValue("-1_u8", null)

        checkIntValue("100_i16", 100)
        checkIntValue("10_000_i16", 10000)
        checkIntValue("32767_i16", 32767)
        checkIntValue("32768_i16", null)
        checkIntValue("50000_i16", null)
        checkIntValue("-100_i16", -100)
        checkIntValue("-10_000_i16", -10000)
        checkIntValue("-32768_i16", -32768)
        checkIntValue("-50000_i16", null)

        checkIntValue("100_u16", 100)
        checkIntValue("10_000_u16", 10000)
        checkIntValue("65535_u16", 65535)
        checkIntValue("70000_u16", null)
        checkIntValue("-1_u16", null)

        checkIntValue("1_i32", 1)
        checkIntValue("2_147_483_647_i32", 2147483647)
        checkIntValue("2147483647_i32", 2147483647)
        checkIntValue("2147483648_i32", null)
        checkIntValue("3000000000_i32", null)
        checkIntValue("-10_000_i32", -10000)
        checkIntValue("-2147483648_i32", -2147483648)
        checkIntValue("-3000000000_i32", null)

        checkIntValue("100_u32", 100)
        checkIntValue("10_000_u32", 10000)
        checkIntValue("4_294_967_295_u32", 4294967295)
        checkIntValue("4294967295_u32", 4294967295)
        checkIntValue("4294967296_u32", null)
        checkIntValue("-1_u32", null)

        checkIntValue("1_i64", 1)
        checkIntValue("9_223_372_036_854_775_807_i64", 9223372036854775807)
        checkIntValue("9223372036854775807_i64", 9223372036854775807)
        checkIntValue("9223372036854775808_i64", null)
        checkIntValue("9333333333333333333_i64", null)
        checkIntValue("-10_000_i64", -10000)
        checkIntValue("-9223372036854775808_i64", "-9223372036854775808")
        checkIntValue("-9333333333333333333_i64", null)

        checkIntValue("100_u64", 100)
        checkIntValue("10_000_u64", 10000)
        checkIntValue("18_446_744_073_709_551_615_u64", "18446744073709551615")
        checkIntValue("18446744073709551615_u64", "18446744073709551615")
        checkIntValue("18446744073709551616_u64", null)
        checkIntValue("19000000000000000000_u64", null)
        checkIntValue("-1_u64", null)

        checkIntValue("1_i128", 1)
        checkIntValue("9_223_372_036_854_775_807_i128", 9223372036854775807)
        checkIntValue("9223372036854775807_i128", 9223372036854775807)
        checkIntValue("9223372036854775808_i128", null)
        checkIntValue("9333333333333333333_i128", null)
        checkIntValue("-10_000_i128", -10000)
        checkIntValue("-9223372036854775808_i128", "-9223372036854775808")
        checkIntValue("-9333333333333333333_i128", null)

        checkIntValue("100_u128", 100)
        checkIntValue("10_000_u128", 10000)
        checkIntValue("18_446_744_073_709_551_615_u128", "18446744073709551615")
        checkIntValue("18446744073709551615_u128", "18446744073709551615")
        checkIntValue("18446744073709551616_u128", null)
        checkIntValue("19000000000000000000_u128", null)
        checkIntValue("-1_u128", null)
    }

    fun testHexIntegers() {
        checkIntValue("0x1_i8", 1)
        checkIntValue("0x1_A_i8", 26)
        checkIntValue("0x7F_i8", 127)
        checkIntValue("0x80_i8", null)
        checkIntValue("0xAA_i8", null)
        checkIntValue("-0x1A_i8", -26)
        checkIntValue("-0x1_A_i8", -26)
        checkIntValue("-0x80_i8", -128)

        checkIntValue("0x1_u8", 1)
        checkIntValue("0x1_A_u8", 26)
        checkIntValue("0xFF_u8", 255)
        checkIntValue("0x100_u8", null)
        checkIntValue("-0x1_u8", null)

        checkIntValue("0x100_i16", 256)
        checkIntValue("0x_1_0_0_i16", 256)
        checkIntValue("0x7FFF_i16", 32767)
        checkIntValue("0x8000_i16", null)
        checkIntValue("0xAAAA_i16", null)
        checkIntValue("-0x100_i16", -256)
        checkIntValue("-0x1_0_0_i16", -256)
        checkIntValue("-0x8000_i16", -32768)
        checkIntValue("-0xAAAA_i16", null)

        checkIntValue("0x100_u16", 256)
        checkIntValue("0x_1_0_0_u16", 256)
        checkIntValue("0xFFFF_u16", 65535)
        checkIntValue("0x10000_u16", null)
        checkIntValue("-0x1_u16", null)

        checkIntValue("0x1_i32", 1)
        checkIntValue("0x7F_FF_FF_FF_i32", 2147483647)
        checkIntValue("0x7FFFFFFF_i32", 2147483647)
        checkIntValue("0x80000000_i32", null)
        checkIntValue("0xAAAAAAAA_i32", null)
        checkIntValue("-0x10_000_i32", -65536)
        checkIntValue("-0x80000000_i32", -2147483648)
        checkIntValue("-0xAAAAAAAA_i32", null)

        checkIntValue("0x100_u32", 256)
        checkIntValue("0x10_000_u32", 65536)
        checkIntValue("0xFF_FF_FF_FF_u32", 4294967295)
        checkIntValue("0xFFFFFFFF_u32", 4294967295)
        checkIntValue("0x100000000_u32", null)
        checkIntValue("-0x1_u32", null)

        checkIntValue("0x1_i64", 1)
        checkIntValue("0x7F_FF_FF_FF_FF_FF_FF_FF_i64", 9223372036854775807)
        checkIntValue("0x7FFFFFFFFFFFFFFF_i64", 9223372036854775807)
        checkIntValue("0x8000000000000000_i64", null)
        checkIntValue("0xAAAAAAAAAAAAAAAA_i64", null)
        checkIntValue("-0x10_000_i64", -65536)
        checkIntValue("-0x8000000000000000_i64", "-9223372036854775808")
        checkIntValue("-0xAAAAAAAAAAAAAAAA_i64", null)

        checkIntValue("0x100_u64", 256)
        checkIntValue("0x10_000_u64", 65536)
        checkIntValue("0xFF_FF_FF_FF_FF_FF_FF_FF_u64", "18446744073709551615")
        checkIntValue("0xFFFFFFFFFFFFFFFF_u64", "18446744073709551615")
        checkIntValue("0x10000000000000000_u64", null)
        checkIntValue("0x11111111111111111_u64", null)
        checkIntValue("-0x1_u64", null)

        checkIntValue("0x1_i128", 1)
        checkIntValue("0x7F_FF_FF_FF_FF_FF_FF_FF_i128", 9223372036854775807)
        checkIntValue("0x7FFFFFFFFFFFFFFF_i128", 9223372036854775807)
        checkIntValue("0x8000000000000000_i128", null)
        checkIntValue("0xAAAAAAAAAAAAAAAA_i128", null)
        checkIntValue("-0x10_000_i128", -65536)
        checkIntValue("-0x8000000000000000_i128", "-9223372036854775808")
        checkIntValue("-0xAAAAAAAAAAAAAAAA_i128", null)

        checkIntValue("0x100_u128", 256)
        checkIntValue("0x10_000_u128", 65536)
        checkIntValue("0xFF_FF_FF_FF_FF_FF_FF_FF_u128", "18446744073709551615")
        checkIntValue("0xFFFFFFFFFFFFFFFF_u128", "18446744073709551615")
        checkIntValue("0x10000000000000000_u128", null)
        checkIntValue("0x11111111111111111_u128", null)
        checkIntValue("-0x1_u128", null)
    }
}