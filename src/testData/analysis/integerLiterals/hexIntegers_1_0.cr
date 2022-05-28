# LANGUAGE_LEVEL: 1.0

0x7F_i8

<error descr="The value is out of Int8 range">0x80_i8</error>

-0x80_i8

<error descr="The value is out of Int8 range">-0xAA_i8</error>

0xFF_u8

<error descr="The value is out of UInt8 range">0x100_u8</error>

<error descr="The value is out of UInt8 range">-0x1_u8</error>

0x7FFF_i16

<error descr="The value is out of Int16 range">0x8000_i16</error>

-0x8000_i16

<error descr="The value is out of Int16 range">-0xAAAA_i16</error>

0xFFFF_u16

<error descr="The value is out of UInt16 range">0x10000_u16</error>

<error descr="The value is out of UInt16 range">-0x1_u16</error>

0x7FFFFFFF_i32

<error descr="The value is out of Int32 range">0x80000000_i32</error>

-0x80000000_i32

<error descr="The value is out of Int32 range">-0xAAAAAAAA_i32</error>

0xFFFFFFFF_u32

<error descr="The value is out of UInt32 range">0x100000000_u32</error>

<error descr="The value is out of UInt32 range">-0x1_u32</error>

0x7FFFFFFFFFFFFFFF_i64

<error descr="The value is out of Int64 range">0x8000000000000000_i64</error>

-0x8000000000000000_i64

<error descr="The value is out of Int64 range">-0xAAAAAAAAAAAAAAAA_i64</error>

0xFFFFFFFFFFFFFFFF_u64

<error descr="The value is out of UInt64 range">0x10000000000000000_u64</error>

<error descr="The value is out of UInt64 range">0x11111111111111111_u64</error>

<error descr="The value is out of UInt64 range">-0x0_u64</error>

<error descr="The value is out of UInt64 range">-0x0u64</error>

<error descr="The value is out of UInt64 range">-0x1_u64</error>

<error descr="The value is out of UInt64 range">0x10000_0000_0000_0000_u64</error>

<error descr="The value is out of UInt64 range">0xfffffffffffffffff_u64</error>

0x7FFFFFFFFFFFFFFF_i128

<error descr="The value is out of Int64 range. Int128 literals that don't fit in an Int64 are currently not supported">0x8000000000000000_i128</error>

-0x8000000000000000_i128

<error descr="The value is out of Int64 range. Int128 literals that don't fit in an Int64 are currently not supported">-0xAAAAAAAAAAAAAAAA_i128</error>

0xFFFFFFFFFFFFFFFF_u128

<error descr="The value is out of UInt64 range. UInt128 literals that don't fit in an UInt64 are currently not supported">0x10000000000000000_u128</error>

<error descr="The value is out of UInt64 range. UInt128 literals that don't fit in an UInt64 are currently not supported">-0x1_u128</error>

<error descr="The value is out of UInt64 range">0xFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF</error>

<error descr="The value is out of UInt64 range. UInt128 literals that don't fit in an UInt64 are currently not supported">0xFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF_u128</error>

<error descr="The value is out of Int64 range. Int128 literals that don't fit in an Int64 are currently not supported">-0x80000000000000000000000000000000_i128</error>

<error descr="The value is out of UInt64 range">0xFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF</error>

<error descr="The value is out of UInt64 range">-0x80000000000000000000000000000000</error>

<error descr="The value is out of UInt64 range">-0x80000000000000000000000000000001</error>

<error descr="The value is out of Int64 range. Int128 literals that don't fit in an Int64 are currently not supported">0x10000_0000_0000_0000_i128</error>

<error descr="The value is out of UInt64 range">0x1afafafafafafafafafafaf</error>

<error descr="The value is out of UInt64 range">0x1afafafafafafafafafafafu64</error>

<error descr="The value is out of Int32 range">0x1afafafafafafafafafafafi32</error>

<error descr="The value is out of UInt64 range">0o1234567123456712345671234567u64</error>

<error descr="The value is out of UInt64 range">0o1234567123456712345671234567</error>

<error descr="The value is out of Int8 range">0o12345671234567_12345671234567_i8</error>