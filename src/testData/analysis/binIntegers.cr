#EXTRA_ATTRIBUTE: CrIntegerLiteralExpression.getValue

<warning descr="1">0b1_i8</warning>

<warning descr="26">0b1_1010_i8</warning>

<warning descr="127">0b1111111_i8</warning>

<error descr="The value is out of Int8 range"><warning descr="">0b10000000_i8</warning></error>

<error descr="The value is out of Int8 range"><warning descr="">0b10101010_i8</warning></error>

<warning descr="-26">-0b11010_i8</warning>

<warning descr="-26">-0b1_1010_i8</warning>

<warning descr="-128">-0b10000000_i8</warning>

<error descr="The value is out of Int8 range"><warning descr="">-0b10101010_i8</warning></error>

<warning descr="1">0b1_u8</warning>

<warning descr="26">0b1_1010_u8</warning>

<warning descr="255">0b11111111_u8</warning>

<error descr="The value is out of UInt8 range"><warning descr="">0b100000000_u8</warning></error>

<error descr="The value is out of UInt8 range"><warning descr="">-0b1_u8</warning></error>

<warning descr="256">0b100000000_i16</warning>

<warning descr="256">0b_1_0000_0000_i16</warning>

<warning descr="32767">0b111111111111111_i16</warning>

<error descr="The value is out of Int16 range"><warning descr="">0b1000000000000000_i16</warning></error>

<error descr="The value is out of Int16 range"><warning descr="">0b1010101010101010_i16</warning></error>

<warning descr="-256">-0b100000000_i16</warning>

<warning descr="-256">-0b1_0000_0000_i16</warning>

<warning descr="-32768">-0b1000000000000000_i16</warning>

<error descr="The value is out of Int16 range"><warning descr="">-0b1010101010101010_i16</warning></error>

<warning descr="256">0b100000000_u16</warning>

<warning descr="256">0b_1_0000_0000_u16</warning>

<warning descr="65535">0b1111111111111111_u16</warning>

<error descr="The value is out of UInt16 range"><warning descr="">0b10000000000000000_u16</warning></error>

<error descr="The value is out of UInt16 range"><warning descr="">-0b1_u16</warning></error>

<warning descr="1">0b1_i32</warning>

<warning descr="2147483647">0b1111111_11111111_11111111_11111111_i32</warning>

<warning descr="2147483647">0b1111111111111111111111111111111_i32</warning>

<error descr="The value is out of Int32 range"><warning descr="">0b10000000000000000000000000000000_i32</warning></error>

<error descr="The value is out of Int32 range"><warning descr="">0b10101010101010101010101010101010_i32</warning></error>

<warning descr="-65536">-0b10000_0000_0000_0000_i32</warning>

<warning descr="-2147483648">-0b10000000000000000000000000000000_i32</warning>

<error descr="The value is out of Int32 range"><warning descr="">-0b10101010101010101010101010101010_i32</warning></error>

<warning descr="256">0b100000000_u32</warning>

<warning descr="65536">0b10000_0000_0000_0000_u32</warning>

<warning descr="4294967295">0b1111_1111_1111_1111_1111_1111_1111_1111_u32</warning>

<warning descr="4294967295">0b11111111111111111111111111111111_u32</warning>

<error descr="The value is out of UInt32 range"><warning descr="">0b100000000000000000000000000000000_u32</warning></error>

<error descr="The value is out of UInt32 range"><warning descr="">-0b1_u32</warning></error>

<warning descr="1">0b1_i64</warning>

<warning descr="9223372036854775807">0b1111111_11111111_11111111_11111111_11111111_11111111_11111111_11111111_i64</warning>

<warning descr="9223372036854775807">0b111111111111111111111111111111111111111111111111111111111111111_i64</warning>

<error descr="The value is out of Int64 range"><warning descr="">0b1000000000000000000000000000000000000000000000000000000000000000_i64</warning></error>

<error descr="The value is out of Int64 range"><warning descr="">0b1010101010101010101010101010101010101010101010101010101010101010_i64</warning></error>

<warning descr="-65536">-0b1_0000_0000_0000_0000_i64</warning>

<warning descr="-9223372036854775808">-0b1000000000000000000000000000000000000000000000000000000000000000_i64</warning>

<error descr="The value is out of Int64 range"><warning descr="">-0b1010101010101010101010101010101010101010101010101010101010101010_i64</warning></error>

<warning descr="256">0b100000000_u64</warning>

<warning descr="65536">0b1_0000_0000_0000_0000_u64</warning>

<warning descr="18446744073709551615">0b11111111_11111111_11111111_11111111_11111111_11111111_11111111_11111111_u64</warning>

<warning descr="18446744073709551615">0b1111111111111111111111111111111111111111111111111111111111111111_u64</warning>

<error descr="The value is out of UInt64 range"><warning descr="">0b10000000000000000000000000000000000000000000000000000000000000000_u64</warning></error>

<error descr="The value is out of UInt64 range"><warning descr="">0b11111111111111111111111111111111111111111111111111111111111111111_u64</warning></error>

<error descr="The value is out of UInt64 range"><warning descr="">-0b1_u64</warning></error>

<warning descr="1">0b1_i128</warning>

<warning descr="9223372036854775807">0b1111111_11111111_11111111_11111111_11111111_11111111_11111111_11111111_i128</warning>

<warning descr="9223372036854775807">0b111111111111111111111111111111111111111111111111111111111111111_i128</warning>

<error descr="The value is out of Int128 range"><warning descr="">0b1000000000000000000000000000000000000000000000000000000000000000_i128</warning></error>

<error descr="The value is out of Int128 range"><warning descr="">0b1010101010101010101010101010101010101010101010101010101010101010_i128</warning></error>

<warning descr="-65536">-0b1_0000_0000_0000_0000_i128</warning>

<warning descr="-9223372036854775808">-0b1000000000000000000000000000000000000000000000000000000000000000_i128</warning>

<error descr="The value is out of Int128 range"><warning descr="">-0b1010101010101010101010101010101010101010101010101010101010101010_i128</warning></error>

<warning descr="256">0b100000000_u128</warning>

<warning descr="65536">0b1_0000_0000_0000_0000_u128</warning>

<warning descr="18446744073709551615">0b11111111_11111111_11111111_11111111_11111111_11111111_11111111_11111111_u128</warning>

<warning descr="18446744073709551615">0b1111111111111111111111111111111111111111111111111111111111111111_u128</warning>

<error descr="The value is out of UInt128 range"><warning descr="">0b10000000000000000000000000000000000000000000000000000000000000000_u128</warning></error>

<error descr="The value is out of UInt128 range"><warning descr="">0b11111111111111111111111111111111111111111111111111111111111111111_u128</warning></error>

<error descr="The value is out of UInt128 range"><warning descr="">-0b1_u128</warning></error>