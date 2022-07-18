# LANGUAGE_LEVEL: 1.5

def foo(<error descr="Parameter external name may not be empty">""</error> y); y; end

macro foo(<error descr="Parameter external name may not be empty">""</error> y); end

foo(<error descr="Named argument name may not be empty">""</error>: 1)

Foo(<error descr="Named argument name may not be empty">""</error>: T)

fun foo(<error descr="Top-level function parameter must have a name">Int32</error>); end

lib Lib
  fun foo(Int32)
end

{<error descr="Tuple entry name may not be empty">""</error>: 1}

{a: 1, <error descr="Tuple entry name may not be empty">""</error>: 2}

@[Foo(""<error descr="Expected: ')'"><error descr="Expected: <expression>">:</error> 1</error>)]