# LANGUAGE_LEVEL: 1.0

def foo("" y); y; end

macro foo("" y); end

foo("": 1)

Foo("": T)

fun foo(Int32); end

lib Lib
  fun foo(Int32)
end

{"": 1}

{a: 1, "": 2}

@[Foo(""<error descr="Expected: ')'"><error descr="Expected: <expression>">:</error> 1</error>)]