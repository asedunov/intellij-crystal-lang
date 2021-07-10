#EXTRA_ATTRIBUTE: CrIntegerLiteralExpression.getValue

<warning descr="1">0o1_i8</warning>

<warning descr="26">0o3_2_i8</warning>

<warning descr="127">0o177_i8</warning>

<error descr="The value is out of Int8 range"><warning descr="">0o200_i8</warning></error>

<error descr="The value is out of Int8 range"><warning descr="">0o252_i8</warning></error>

<warning descr="-26">-0o32_i8</warning>

<warning descr="-26">-0o3_2_i8</warning>

<warning descr="-128">-0o200_i8</warning>

<error descr="The value is out of Int8 range"><warning descr="">-0o252_i8</warning></error>

<warning descr="1">0o1_u8</warning>

<warning descr="26">0o3_2_u8</warning>

<warning descr="255">0o377_u8</warning>

<error descr="The value is out of UInt8 range"><warning descr="">0o400_u8</warning></error>

<error descr="The value is out of UInt8 range"><warning descr="">-0o1_u8</warning></error>

<warning descr="256">0o400_i16</warning>

<warning descr="256">0o_4_0_0_i16</warning>

<warning descr="32767">0o77777_i16</warning>

<error descr="The value is out of Int16 range"><warning descr="">0o100000_i16</warning></error>

<error descr="The value is out of Int16 range"><warning descr="">0o125252_i16</warning></error>

<warning descr="-256">-0o400_i16</warning>

<warning descr="-256">-0o4_0_0_i16</warning>

<warning descr="-32768">-0o100000_i16</warning>

<error descr="The value is out of Int16 range"><warning descr="">-0o125252_i16</warning></error>

<warning descr="256">0o400_u16</warning>

<warning descr="256">0o_4_0_0_u16</warning>

<warning descr="65535">0o177777_u16</warning>

<error descr="The value is out of UInt16 range"><warning descr="">0o200000_u16</warning></error>

<error descr="The value is out of UInt16 range"><warning descr="">-0o1_u16</warning></error>

<warning descr="1">0o1_i32</warning>

<warning descr="2147483647">0o17_777_777_777_i32</warning>

<warning descr="2147483647">0o17777777777_i32</warning>

<error descr="The value is out of Int32 range"><warning descr="">0o20000000000_i32</warning></error>

<error descr="The value is out of Int32 range"><warning descr="">0o25252525252_i32</warning></error>

<warning descr="-65536">-0o200000_i32</warning>

<warning descr="-2147483648">-0o20000000000_i32</warning>

<error descr="The value is out of Int32 range"><warning descr="">-0o25252525252_i32</warning></error>

<warning descr="256">0o400_u32</warning>

<warning descr="65536">0o200000_u32</warning>

<warning descr="4294967295">0o37_777_777_777_u32</warning>

<warning descr="4294967295">0o37777777777_u32</warning>

<error descr="The value is out of UInt32 range"><warning descr="">0o40000000000_u32</warning></error>

<error descr="The value is out of UInt32 range"><warning descr="">-0o1_u32</warning></error>

<warning descr="1">0o1_i64</warning>

<warning descr="9223372036854775807">0o777_777_777_777_777_777_777_i64</warning>

<warning descr="9223372036854775807">0o777777777777777777777_i64</warning>

<error descr="The value is out of Int64 range"><warning descr="">0o1000000000000000000000_i64</warning></error>

<error descr="The value is out of Int64 range"><warning descr="">0o1252525252525252525252_i64</warning></error>

<warning descr="-65536">-0o20_00_00_i64</warning>

<warning descr="-9223372036854775808">-0o1000000000000000000000_i64</warning>

<error descr="The value is out of Int64 range"><warning descr="">-0o1252525252525252525252_i64</warning></error>

<warning descr="256">0o400_u64</warning>

<warning descr="65536">0o200000_u64</warning>

<warning descr="18446744073709551615">0o1_777_777_777_777_777_777_777_u64</warning>

<warning descr="18446744073709551615">0o1777777777777777777777_u64</warning>

<error descr="The value is out of UInt64 range"><warning descr="">0o2000000000000000000000_u64</warning></error>

<error descr="The value is out of UInt64 range"><warning descr="">0o3777777777777777777777_u64</warning></error>

<error descr="The value is out of UInt64 range"><warning descr="">-0o1_u64</warning></error>

<warning descr="1">0o1_i128</warning>

<warning descr="9223372036854775807">0o777_777_777_777_777_777_777_i128</warning>

<warning descr="9223372036854775807">0o777777777777777777777_i128</warning>

<error descr="The value is out of Int128 range"><warning descr="">0o1000000000000000000000_i128</warning></error>

<error descr="The value is out of Int128 range"><warning descr="">0o1252525252525252525252_i128</warning></error>

<warning descr="-65536">-0o20_00_00_i128</warning>

<warning descr="-9223372036854775808">-0o1000000000000000000000_i128</warning>

<error descr="The value is out of Int128 range"><warning descr="">-0o1252525252525252525252_i128</warning></error>

<warning descr="256">0o400_u128</warning>

<warning descr="65536">0o200000_u128</warning>

<warning descr="18446744073709551615">0o1_777_777_777_777_777_777_777_u128</warning>

<warning descr="18446744073709551615">0o1777777777777777777777_u128</warning>

<error descr="The value is out of UInt128 range"><warning descr="">0o2000000000000000000000_u128</warning></error>

<error descr="The value is out of UInt128 range"><warning descr="">0o3777777777777777777777_u128</warning></error>

<error descr="The value is out of UInt128 range"><warning descr="">-0o1_u128</warning></error>