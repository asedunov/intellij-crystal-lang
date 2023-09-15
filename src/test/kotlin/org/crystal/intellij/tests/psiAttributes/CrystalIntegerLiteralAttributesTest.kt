package org.crystal.intellij.tests.psiAttributes

import org.crystal.intellij.lang.psi.CrIntegerKind
import org.crystal.intellij.lang.psi.CrIntegerLiteralExpression

class CrystalIntegerLiteralAttributesTest : CrystalPsiAttributeTest() {
    private infix fun String.hasKind(expectedKind: CrIntegerKind) {
        checkFirst(this, CrIntegerLiteralExpression::kind, expectedKind)
    }

    private infix fun String.hasRadix(value: Int) {
        checkFirst(this, CrIntegerLiteralExpression::radix, value)
    }

    private infix fun String.hasValue(value: Any?) {
        checkFirst<CrIntegerLiteralExpression>(this, { this.value?.toString() }, value?.toString())
    }

    fun testIntegerKinds() {
        "1_i8" hasKind CrIntegerKind.I8
        "1_u8" hasKind CrIntegerKind.U8
        "1_i16" hasKind CrIntegerKind.I16
        "1_u16" hasKind CrIntegerKind.U16
        "1_i32" hasKind CrIntegerKind.I32
        "1_u32" hasKind CrIntegerKind.U32
        "1_i64" hasKind CrIntegerKind.I64
        "1_u64" hasKind CrIntegerKind.U64
        "1_i128" hasKind CrIntegerKind.I128
        "1_u128" hasKind CrIntegerKind.U128
        "1" hasKind CrIntegerKind.I32
        "-1" hasKind CrIntegerKind.I32
        "0x7FFFFFFF" hasKind CrIntegerKind.I32
        "-0x8000000" hasKind CrIntegerKind.I32
        "0x80000000" hasKind CrIntegerKind.I64
        "0x7FFFFFFFFFFFFFFF" hasKind CrIntegerKind.I64
        "-0x8000000000000000" hasKind CrIntegerKind.I64
        "0x8000000000000000" hasKind CrIntegerKind.U64
    }

    fun testIntegerRadix() {
        "123" hasRadix 10
        "0x123" hasRadix 16
        "0o123" hasRadix 8
        "0b101" hasRadix 2
    }

    fun testBinIntegers() {
        "0b1_i8" hasValue 1
        "0b1_1010_i8" hasValue 26
        "0b1111111_i8" hasValue 127
        "0b10000000_i8" hasValue null
        "0b10101010_i8" hasValue null
        "-0b11010_i8" hasValue -26
        "-0b1_1010_i8" hasValue -26
        "-0b10000000_i8" hasValue -128
        "-0b10101010_i8" hasValue null

        "0b1_u8" hasValue 1
        "0b1_1010_u8" hasValue 26
        "0b11111111_u8" hasValue 255
        "0b100000000_u8" hasValue null
        "-0b1_u8" hasValue null

        "0b100000000_i16" hasValue 256
        "0b1_0000_0000_i16" hasValue 256
        "0b111111111111111_i16" hasValue 32767
        "0b1000000000000000_i16" hasValue null
        "0b1010101010101010_i16" hasValue null
        "-0b100000000_i16" hasValue -256
        "-0b1_0000_0000_i16" hasValue -256
        "-0b1000000000000000_i16" hasValue -32768
        "-0b1010101010101010_i16" hasValue null

        "0b100000000_u16" hasValue 256
        "0b1_0000_0000_u16" hasValue 256
        "0b1111111111111111_u16" hasValue 65535
        "0b10000000000000000_u16" hasValue null
        "-0b1_u16" hasValue null

        "0b1_i32" hasValue 1
        "0b1111111_11111111_11111111_11111111_i32" hasValue 2147483647
        "0b1111111111111111111111111111111_i32" hasValue 2147483647
        "0b10000000000000000000000000000000_i32" hasValue null
        "0b10101010101010101010101010101010_i32" hasValue null
        "-0b10000_0000_0000_0000_i32" hasValue -65536
        "-0b10000000000000000000000000000000_i32" hasValue -2147483648
        "-0b10101010101010101010101010101010_i32" hasValue null

        "0b100000000_u32" hasValue 256
        "0b10000_0000_0000_0000_u32" hasValue 65536
        "0b1111_1111_1111_1111_1111_1111_1111_1111_u32" hasValue 4294967295
        "0b11111111111111111111111111111111_u32" hasValue 4294967295
        "0b100000000000000000000000000000000_u32" hasValue null
        "-0b1_u32" hasValue null

        "0b1_i64" hasValue 1
        "0b1111111_11111111_11111111_11111111_11111111_11111111_11111111_11111111_i64" hasValue 
            9223372036854775807
        
        "0b111111111111111111111111111111111111111111111111111111111111111_i64" hasValue 
            9223372036854775807
        
        "0b1000000000000000000000000000000000000000000000000000000000000000_i64" hasValue null
        "0b1010101010101010101010101010101010101010101010101010101010101010_i64" hasValue null
        "-0b1_0000_0000_0000_0000_i64" hasValue -65536
        "-0b1000000000000000000000000000000000000000000000000000000000000000_i64" hasValue 
            "-9223372036854775808"
        
        "-0b1010101010101010101010101010101010101010101010101010101010101010_i64" hasValue null

        "0b100000000_u64" hasValue 256
        "0b1_0000_0000_0000_0000_u64" hasValue 65536
        "0b11111111_11111111_11111111_11111111_11111111_11111111_11111111_11111111_u64" hasValue 
            "18446744073709551615"
        
        "0b1111111111111111111111111111111111111111111111111111111111111111_u64" hasValue 
            "18446744073709551615"
        
        "0b10000000000000000000000000000000000000000000000000000000000000000_u64" hasValue null
        "0b11111111111111111111111111111111111111111111111111111111111111111_u64" hasValue null
        "-0b1_u64" hasValue null

        "0b1_i128" hasValue 1
        "0b1111111_11111111_11111111_11111111_11111111_11111111_11111111_11111111_i128" hasValue 
            9223372036854775807
        
        "0b111111111111111111111111111111111111111111111111111111111111111_i128" hasValue 
            9223372036854775807
        
        "0b1000000000000000000000000000000000000000000000000000000000000000_i128" hasValue "9223372036854775808"
        "0b1010101010101010101010101010101010101010101010101010101010101010_i128" hasValue "12297829382473034410"
        "-0b1_0000_0000_0000_0000_i128" hasValue -65536
        "-0b1000000000000000000000000000000000000000000000000000000000000000_i128" hasValue "-9223372036854775808"
        
        "-0b1010101010101010101010101010101010101010101010101010101010101010_i128" hasValue "-12297829382473034410"

        "0b100000000_u128" hasValue 256
        "0b1_0000_0000_0000_0000_u128" hasValue 65536
        "0b11111111_11111111_11111111_11111111_11111111_11111111_11111111_11111111_u128" hasValue "18446744073709551615"
        
        "0b1111111111111111111111111111111111111111111111111111111111111111_u128" hasValue "18446744073709551615"
        
        "0b10000000000000000000000000000000000000000000000000000000000000000_u128" hasValue "18446744073709551616"
        "0b11111111111111111111111111111111111111111111111111111111111111111_u128" hasValue "36893488147419103231"
        "-0b1_u128" hasValue null
    }

    fun testOctalIntegers() {
        "0o1_i8" hasValue 1
        "0o3_2_i8" hasValue 26
        "0o177_i8" hasValue 127
        "0o200_i8" hasValue null
        "0o252_i8" hasValue null
        "-0o32_i8" hasValue -26
        "-0o3_2_i8" hasValue -26
        "-0o200_i8" hasValue -128
        "-0o252_i8" hasValue null

        "0o1_u8" hasValue 1
        "0o3_2_u8" hasValue 26
        "0o377_u8" hasValue 255
        "0o400_u8" hasValue null
        "-0o1_u8" hasValue null

        "0o400_i16" hasValue 256
        "0o4_0_0_i16" hasValue 256
        "0o77777_i16" hasValue 32767
        "0o100000_i16" hasValue null
        "0o125252_i16" hasValue null
        "-0o400_i16" hasValue -256
        "-0o4_0_0_i16" hasValue -256
        "-0o100000_i16" hasValue -32768
        "-0o125252_i16" hasValue null

        "0o400_u16" hasValue 256
        "0o4_0_0_u16" hasValue 256
        "0o177777_u16" hasValue 65535
        "0o200000_u16" hasValue null
        "-0o1_u16" hasValue null

        "0o1_i32" hasValue 1
        "0o17_777_777_777_i32" hasValue 2147483647
        "0o17777777777_i32" hasValue 2147483647
        "0o20000000000_i32" hasValue null
        "0o25252525252_i32" hasValue null
        "-0o200000_i32" hasValue -65536
        "-0o20000000000_i32" hasValue -2147483648
        "-0o25252525252_i32" hasValue null

        "0o400_u32" hasValue 256
        "0o200000_u32" hasValue 65536
        "0o37_777_777_777_u32" hasValue 4294967295
        "0o37777777777_u32" hasValue 4294967295
        "0o40000000000_u32" hasValue null
        "-0o1_u32" hasValue null

        "0o1_i64" hasValue 1
        "0o777_777_777_777_777_777_777_i64" hasValue 9223372036854775807
        "0o777777777777777777777_i64" hasValue 9223372036854775807
        "0o1000000000000000000000_i64" hasValue null
        "0o1252525252525252525252_i64" hasValue null
        "-0o20_00_00_i64" hasValue -65536
        "-0o1000000000000000000000_i64" hasValue "-9223372036854775808"
        "-0o1252525252525252525252_i64" hasValue null

        "0o400_u64" hasValue 256
        "0o200000_u64" hasValue 65536
        "0o1_777_777_777_777_777_777_777_u64" hasValue "18446744073709551615"
        "0o1777777777777777777777_u64" hasValue "18446744073709551615"
        "0o2000000000000000000000_u64" hasValue null
        "0o3777777777777777777777_u64" hasValue null
        "-0o1_u64" hasValue null

        "0o1_i128" hasValue 1
        "0o777_777_777_777_777_777_777_i128" hasValue 9223372036854775807
        "0o777777777777777777777_i128" hasValue 9223372036854775807
        "0o1000000000000000000000_i128" hasValue "9223372036854775808"
        "0o1252525252525252525252_i128" hasValue "12297829382473034410"
        "-0o20_00_00_i128" hasValue -65536
        "-0o1000000000000000000000_i128" hasValue "-9223372036854775808"
        "-0o1252525252525252525252_i128" hasValue "-12297829382473034410"

        "0o400_u128" hasValue 256
        "0o200000_u128" hasValue 65536
        "0o1_777_777_777_777_777_777_777_u128" hasValue "18446744073709551615"
        "0o1777777777777777777777_u128" hasValue "18446744073709551615"
        "0o2000000000000000000000_u128" hasValue "18446744073709551616"
        "0o3777777777777777777777_u128" hasValue "36893488147419103231"
        "-0o1_u128" hasValue null
    }

    fun testDecimalIntegers() {
        "1_i8" hasValue 1
        "1_0_0_i8" hasValue 100
        "127_i8" hasValue 127
        "128_i8" hasValue null
        "200_i8" hasValue null
        "-100_i8" hasValue -100
        "-1_0_0_i8" hasValue -100
        "-128_i8" hasValue -128
        "-200_i8" hasValue null

        "1_u8" hasValue 1
        "1_0_0_u8" hasValue 100
        "255_u8" hasValue 255
        "300_u8" hasValue null
        "-1_u8" hasValue null

        "100_i16" hasValue 100
        "10_000_i16" hasValue 10000
        "32767_i16" hasValue 32767
        "32768_i16" hasValue null
        "50000_i16" hasValue null
        "-100_i16" hasValue -100
        "-10_000_i16" hasValue -10000
        "-32768_i16" hasValue -32768
        "-50000_i16" hasValue null

        "100_u16" hasValue 100
        "10_000_u16" hasValue 10000
        "65535_u16" hasValue 65535
        "70000_u16" hasValue null
        "-1_u16" hasValue null

        "1_i32" hasValue 1
        "2_147_483_647_i32" hasValue 2147483647
        "2147483647_i32" hasValue 2147483647
        "2147483648_i32" hasValue null
        "3000000000_i32" hasValue null
        "-10_000_i32" hasValue -10000
        "-2147483648_i32" hasValue -2147483648
        "-3000000000_i32" hasValue null

        "100_u32" hasValue 100
        "10_000_u32" hasValue 10000
        "4_294_967_295_u32" hasValue 4294967295
        "4294967295_u32" hasValue 4294967295
        "4294967296_u32" hasValue null
        "-1_u32" hasValue null

        "1_i64" hasValue 1
        "9_223_372_036_854_775_807_i64" hasValue 9223372036854775807
        "9223372036854775807_i64" hasValue 9223372036854775807
        "9223372036854775808_i64" hasValue null
        "9333333333333333333_i64" hasValue null
        "-10_000_i64" hasValue -10000
        "-9223372036854775808_i64" hasValue "-9223372036854775808"
        "-9333333333333333333_i64" hasValue null

        "100_u64" hasValue 100
        "10_000_u64" hasValue 10000
        "18_446_744_073_709_551_615_u64" hasValue "18446744073709551615"
        "18446744073709551615_u64" hasValue "18446744073709551615"
        "18446744073709551616_u64" hasValue null
        "19000000000000000000_u64" hasValue null
        "-1_u64" hasValue null

        "1_i128" hasValue 1
        "9_223_372_036_854_775_807_i128" hasValue 9223372036854775807
        "9223372036854775807_i128" hasValue 9223372036854775807
        "9223372036854775808_i128" hasValue "9223372036854775808"
        "9333333333333333333_i128" hasValue "9333333333333333333"
        "-10_000_i128" hasValue -10000
        "-9223372036854775808_i128" hasValue "-9223372036854775808"
        "-9333333333333333333_i128" hasValue "-9333333333333333333"

        "100_u128" hasValue 100
        "10_000_u128" hasValue 10000
        "18_446_744_073_709_551_615_u128" hasValue "18446744073709551615"
        "18446744073709551615_u128" hasValue "18446744073709551615"
        "18446744073709551616_u128" hasValue "18446744073709551616"
        "19000000000000000000_u128" hasValue "19000000000000000000"
        "-1_u128" hasValue null
    }

    fun testHexIntegers() {
        "0x1_i8" hasValue 1
        "0x1_A_i8" hasValue 26
        "0x7F_i8" hasValue 127
        "0x80_i8" hasValue null
        "0xAA_i8" hasValue null
        "-0x1A_i8" hasValue -26
        "-0x1_A_i8" hasValue -26
        "-0x80_i8" hasValue -128

        "0x1_u8" hasValue 1
        "0x1_A_u8" hasValue 26
        "0xFF_u8" hasValue 255
        "0x100_u8" hasValue null
        "-0x1_u8" hasValue null

        "0x100_i16" hasValue 256
        "0x1_0_0_i16" hasValue 256
        "0x7FFF_i16" hasValue 32767
        "0x8000_i16" hasValue null
        "0xAAAA_i16" hasValue null
        "-0x100_i16" hasValue -256
        "-0x1_0_0_i16" hasValue -256
        "-0x8000_i16" hasValue -32768
        "-0xAAAA_i16" hasValue null

        "0x100_u16" hasValue 256
        "0x1_0_0_u16" hasValue 256
        "0xFFFF_u16" hasValue 65535
        "0x10000_u16" hasValue null
        "-0x1_u16" hasValue null

        "0x1_i32" hasValue 1
        "0x7F_FF_FF_FF_i32" hasValue 2147483647
        "0x7FFFFFFF_i32" hasValue 2147483647
        "0x80000000_i32" hasValue null
        "0xAAAAAAAA_i32" hasValue null
        "-0x10_000_i32" hasValue -65536
        "-0x80000000_i32" hasValue -2147483648
        "-0xAAAAAAAA_i32" hasValue null

        "0x100_u32" hasValue 256
        "0x10_000_u32" hasValue 65536
        "0xFF_FF_FF_FF_u32" hasValue 4294967295
        "0xFFFFFFFF_u32" hasValue 4294967295
        "0x100000000_u32" hasValue null
        "-0x1_u32" hasValue null

        "0x1_i64" hasValue 1
        "0x7F_FF_FF_FF_FF_FF_FF_FF_i64" hasValue 9223372036854775807
        "0x7FFFFFFFFFFFFFFF_i64" hasValue 9223372036854775807
        "0x8000000000000000_i64" hasValue null
        "0xAAAAAAAAAAAAAAAA_i64" hasValue null
        "-0x10_000_i64" hasValue -65536
        "-0x8000000000000000_i64" hasValue "-9223372036854775808"
        "-0xAAAAAAAAAAAAAAAA_i64" hasValue null

        "0x100_u64" hasValue 256
        "0x10_000_u64" hasValue 65536
        "0xFF_FF_FF_FF_FF_FF_FF_FF_u64" hasValue "18446744073709551615"
        "0xFFFFFFFFFFFFFFFF_u64" hasValue "18446744073709551615"
        "0x10000000000000000_u64" hasValue null
        "0x11111111111111111_u64" hasValue null
        "-0x1_u64" hasValue null

        "0x1_i128" hasValue 1
        "0x7F_FF_FF_FF_FF_FF_FF_FF_i128" hasValue 9223372036854775807
        "0x7FFFFFFFFFFFFFFF_i128" hasValue 9223372036854775807
        "0x8000000000000000_i128" hasValue "9223372036854775808"
        "0xAAAAAAAAAAAAAAAA_i128" hasValue "12297829382473034410"
        "-0x10_000_i128" hasValue -65536
        "-0x8000000000000000_i128" hasValue "-9223372036854775808"
        "-0xAAAAAAAAAAAAAAAA_i128" hasValue "-12297829382473034410"

        "0x100_u128" hasValue 256
        "0x10_000_u128" hasValue 65536
        "0xFF_FF_FF_FF_FF_FF_FF_FF_u128" hasValue "18446744073709551615"
        "0xFFFFFFFFFFFFFFFF_u128" hasValue "18446744073709551615"
        "0x10000000000000000_u128" hasValue "18446744073709551616"
        "0x11111111111111111_u128" hasValue "19676527011956855057"
        "-0x1_u128" hasValue null
    }
}