def foo(var : Int); end

def foo(var : self); end

def foo(var : self?); end

def foo(var : self.class); end

def foo(var : self*); end

def foo(var : Int | Double); end

def foo(var : Int?); end

def foo(var : Int*); end

def foo(var : Int**); end

def foo(var : Int -> Double); end

def foo(var : Int, Float -> Double); end

def foo(var : (Int, Float -> Double)); end

def foo(var : (Int, Float) -> Double); end

def foo(var : Char[256]); end

def foo(var : Char[N]); end

def foo(var : Int32 = 1); end

def foo(var : Int32 -> = 1); end

def foo(a, &block : Int, Float -> Double); end

def foo(a, &block : Int, self -> Double); end

def foo(a, &block : -> Double); end

def foo(a, &block : Int -> ); end

def foo(a, &block : self -> self); end

def foo(a, &block : Foo); end

x : ::A::B = 1

class ::A::B
end

x : Foo(A, *B, C)

x : *T -> R

def foo(x : *T -> R); end

foo :: Foo

@foo :: Foo

@@foo :: Foo

$foo :: Foo

def foo(var : Foo+); end

Foo(T U)

Foo(T, U V)

Foo(X
,Y)

Foo(x: X
,y: Y)

def foo(x : *Int32); end

def foo(x : (*Int32)); end

def foo(x : Int32, Int32); end

def foo(x : (Int32, Int32)); end

def foo(x : (Int32, Int32) | Int32); end

def foo(x : Int32 | (Int32, Int32)); end

def foo(x : {Int32, (Int32, Int32)}); end

def foo(x : 1); end

def foo(x : {sizeof(Int32), 2}); end

def foo(x : Array({sizeof(Int32), 2})); end