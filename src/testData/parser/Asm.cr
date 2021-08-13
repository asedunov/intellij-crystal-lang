asm("nop"
)

asm("nop" : : )

asm("nop" ::)

asm("nop" :: : :)

asm("nop" ::: :)

asm("nop" ::::)

asm("nop" : "a"(0))

asm("nop" : "a"(0) : "b"(1))

asm("nop" : "a"(0) : "b"(1), "c"(2))

asm("nop" : "a"(0), "b"(1) : "c"(2), "d"(3))

asm("nop" :: "b"(1), "c"(2))

asm("nop" :: "b"(1), "c"(2) ::)

asm(
"nop"
:
"a"(0)
:
"b"(1),
"c"(2)
)

asm(
"nop"
:
"a"(0),
"b"(1)
:
"c"(2),
"d"(3)
)

asm("nop" :: "b"(1), "c"(2) : "eax", "ebx" : "volatile", "alignstack", "intel")

asm("nop" :: "b"(1), "c"(2) : "eax", "ebx"
: "volatile", "alignstack"
,
"intel"
)

asm("nop" :::: "volatile")

asm("nop" ::: "#{foo}")

asm("nop" :::: "#{volatile}")

asm("" ::: ""(var))

asm("" : 1)