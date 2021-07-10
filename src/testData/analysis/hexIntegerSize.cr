#EXTRA_ATTRIBUTE: CrIntegerLiteralExpression.getValue

<warning descr="1">0x1_i8</warning>

<warning descr="26">0x1_A_i8</warning>

<warning descr="127">0x7F_i8</warning>

<error descr="The value is out of Int8 range"><warning descr="">0x80_i8</warning></error>

<error descr="The value is out of Int8 range"><warning descr="">0xAA_i8</warning></error>

<warning descr="-26">-0x1A_i8</warning>

<warning descr="-26">-0x1_A_i8</warning>

<warning descr="-128">-0x80_i8</warning>

<error descr="The value is out of Int8 range"><warning descr="">-0xAA_i8</warning></error>

<warning descr="1">0x1_u8</warning>

<warning descr="26">0x1_A_u8</warning>

<warning descr="255">0xFF_u8</warning>

<error descr="The value is out of UInt8 range"><warning descr="">0x100_u8</warning></error>

<error descr="The value is out of UInt8 range"><warning descr="">-0x1_u8</warning></error>

<warning descr="256">0x100_i16</warning>

<warning descr="256">0x_1_0_0_i16</warning>

<warning descr="32767">0x7FFF_i16</warning>

<error descr="The value is out of Int16 range"><warning descr="">0x8000_i16</warning></error>

<error descr="The value is out of Int16 range"><warning descr="">0xAAAA_i16</warning></error>

<warning descr="-256">-0x100_i16</warning>

<warning descr="-256">-0x1_0_0_i16</warning>

<warning descr="-32768">-0x8000_i16</warning>

<error descr="The value is out of Int16 range"><warning descr="">-0xAAAA_i16</warning></error>

<warning descr="256">0x100_u16</warning>

<warning descr="256">0x_1_0_0_u16</warning>

<warning descr="65535">0xFFFF_u16</warning>

<error descr="The value is out of UInt16 range"><warning descr="">0x10000_u16</warning></error>

<error descr="The value is out of UInt16 range"><warning descr="">-0x1_u16</warning></error>

<warning descr="1">0x1_i32</warning>

<warning descr="2147483647">0x7F_FF_FF_FF_i32</warning>

<warning descr="2147483647">0x7FFFFFFF_i32</warning>

<error descr="The value is out of Int32 range"><warning descr="">0x80000000_i32</warning></error>

<error descr="The value is out of Int32 range"><warning descr="">0xAAAAAAAA_i32</warning></error>

<warning descr="-65536">-0x10_000_i32</warning>

<warning descr="-2147483648">-0x80000000_i32</warning>

<error descr="The value is out of Int32 range"><warning descr="">-0xAAAAAAAA_i32</warning></error>

<warning descr="256">0x100_u32</warning>

<warning descr="65536">0x10_000_u32</warning>

<warning descr="4294967295">0xFF_FF_FF_FF_u32</warning>

<warning descr="4294967295">0xFFFFFFFF_u32</warning>

<error descr="The value is out of UInt32 range"><warning descr="">0x100000000_u32</warning></error>

<error descr="The value is out of UInt32 range"><warning descr="">-0x1_u32</warning></error>

<warning descr="1">0x1_i64</warning>

<warning descr="9223372036854775807">0x7F_FF_FF_FF_FF_FF_FF_FF_i64</warning>

<warning descr="9223372036854775807">0x7FFFFFFFFFFFFFFF_i64</warning>

<error descr="The value is out of Int64 range"><warning descr="">0x8000000000000000_i64</warning></error>

<error descr="The value is out of Int64 range"><warning descr="">0xAAAAAAAAAAAAAAAA_i64</warning></error>

<warning descr="-65536">-0x10_000_i64</warning>

<warning descr="-9223372036854775808">-0x8000000000000000_i64</warning>

<error descr="The value is out of Int64 range"><warning descr="">-0xAAAAAAAAAAAAAAAA_i64</warning></error>

<warning descr="256">0x100_u64</warning>

<warning descr="65536">0x10_000_u64</warning>

<warning descr="18446744073709551615">0xFF_FF_FF_FF_FF_FF_FF_FF_u64</warning>

<warning descr="18446744073709551615">0xFFFFFFFFFFFFFFFF_u64</warning>

<error descr="The value is out of UInt64 range"><warning descr="">0x10000000000000000_u64</warning></error>

<error descr="The value is out of UInt64 range"><warning descr="">0x11111111111111111_u64</warning></error>

<error descr="The value is out of UInt64 range"><warning descr="">-0x1_u64</warning></error>

<warning descr="1">0x1_i128</warning>

<warning descr="9223372036854775807">0x7F_FF_FF_FF_FF_FF_FF_FF_i128</warning>

<warning descr="9223372036854775807">0x7FFFFFFFFFFFFFFF_i128</warning>

<error descr="The value is out of Int128 range"><warning descr="">0x8000000000000000_i128</warning></error>

<error descr="The value is out of Int128 range"><warning descr="">0xAAAAAAAAAAAAAAAA_i128</warning></error>

<warning descr="-65536">-0x10_000_i128</warning>

<warning descr="-9223372036854775808">-0x8000000000000000_i128</warning>

<error descr="The value is out of Int128 range"><warning descr="">-0xAAAAAAAAAAAAAAAA_i128</warning></error>

<warning descr="256">0x100_u128</warning>

<warning descr="65536">0x10_000_u128</warning>

<warning descr="18446744073709551615">0xFF_FF_FF_FF_FF_FF_FF_FF_u128</warning>

<warning descr="18446744073709551615">0xFFFFFFFFFFFFFFFF_u128</warning>

<error descr="The value is out of UInt128 range"><warning descr="">0x10000000000000000_u128</warning></error>

<error descr="The value is out of UInt128 range"><warning descr="">0x11111111111111111_u128</warning></error>

<error descr="The value is out of UInt128 range"><warning descr="">-0x1_u128</warning></error>