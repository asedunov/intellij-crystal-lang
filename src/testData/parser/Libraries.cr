lib LibC
end

lib LibC
fun getchar
end

lib LibC
fun getchar(...)
end

lib LibC
fun getchar : Int
end

lib LibC
fun getchar : (->)?
end

lib LibC
fun getchar(Int, Float)
end

lib LibC
fun getchar(a : Int, b : Float)
end

lib LibC
fun getchar(a : Int)
end

lib LibC
fun getchar(a : Int, b : Float) : Int
end

lib LibC; fun getchar(a : Int, b : Float) : Int; end

lib LibC; fun foo(a : Int*); end

lib LibC; fun foo(a : Int**); end

lib LibC; fun foo : Int*; end

lib LibC; fun foo : Int**; end

lib LibC; fun foo(a : ::B, ::C -> ::D); end

lib LibC; type A = B; end

lib LibC; type A = B*; end

lib LibC; type A = B**; end

lib LibC; type A = B.class; end

lib LibC; struct Foo; end end

lib LibC; struct Foo; x : Int; y : Float; end end

lib LibC; struct Foo; x : Int*; end end

lib LibC; struct Foo; x : Int**; end end

lib LibC; struct Foo; x, y, z : Int; end end

lib LibC; union Foo; end end

lib LibC; enum Foo; A
B; C
D = 1; end end

lib LibC; enum Foo; A = 1; B; end end

lib LibC; Foo = 1; end

lib LibC
fun getch = GetChar
end

lib LibC
fun getch = "get.char"
end

lib LibC
fun getch = "get.char" : Int32
end

lib LibC
fun getch = "get.char"(x : Int32)
end

lib LibC
$errno : Int32
$errno2 : Int32
end

lib LibC
$errno : B, C -> D
end

lib LibC
$errno = Foo : Int32
end

lib LibC
alias Foo = Bar
end

lib LibC; struct Foo; include Bar; end; end

lib LibC
fun SomeFun
end

lib LibFoo
end
if true
end

lib LibC
$Errno : Int32
end

lib LibFoo
  fun foo(x : Int32
        y : Float64)
end

lib Foo::Bar
end