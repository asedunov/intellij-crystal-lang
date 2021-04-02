def foo
1
end

def downto(n)
1
end

def foo ; 1 ; end

def foo; end

def foo(var); end

def foo(
var); end

def foo(
var
); end

def foo(var1, var2); end

def foo; 1; 2; end

def foo=(value); end

def foo(n); foo(n -1); end

def type(type); end

def foo!=; end

def foo?=(x); end

def foo=(a,b); end

def foo=(a = 1, b = 2); end

def foo=(*args); end

def foo=(**kwargs); end

def foo=(&block); end

def []=(&block); end

def self.foo
1
end

def self.foo()
1
end

def self.foo=
1
end

def self.foo=()
1
end

def Foo.foo
1
end

def Foo::Bar.foo
1
end

def foo; a; end

def foo(a); a; end

def foo; a = 1; a; end

def foo; a = 1; a {}; end

def foo; a = 1; x { a }; end

def foo; x { |a| a }; end

def foo; x { |_| 1 }; end

def foo; x { |a, *b| b }; end

def foo(var = 1); end

def foo(a, b = a); end

def foo(&block); end

def foo(&); end

def foo(&
); end

def foo(a, &block); end

def foo(a, &block : Int -> Double); end

def foo(a, & : Int -> Double); end

def foo(@var); end

def foo(@var); 1; end

def foo(@var = 1); 1; end

def foo(@@var); end

def foo(@@var); 1; end

def foo(@@var = 1); 1; end

def foo(&@block); end

def foo(
&block
); end

def foo(&block :
 Int ->); end

def foo(&block : Int ->
); end

def foo(a, &block : *Int -> ); end

def foo(x, *args, y = 2); 1; end

def foo(x, *args, y = 2, w, z = 3); 1; end

def foo(x, *, y); 1; end

def foo(x, *); 1; end

def foo(x, *, y, &); 1; end

def foo(var = 1 : Int32); end

def foo(var = x : Int); end

def foo(**args)
1
end

def foo(x, **args)
1
end

def foo(x, **args, &block)
1
end

def foo(**args)
args
end

def foo(x = 1, **args)
1
end

def foo(**args : Foo)
1
end

def foo(**args : **Foo)
1
end

def foo(**args, **args2); end

def foo(**args, x); end

def foo(**args, *x); end

def foo(x y); y; end

def foo(x @var); end

def foo(x @@var); end

def foo(_ y); y; end

def foo("bar qux" y); y; end

def foo(x x); 1; end

def foo(x @x); 1; end

def foo(x @@x); 1; end

def foo(*a foo); end

def foo(**a foo); end

def foo(&a foo); end

abstract def foo

abstract def foo; 1

abstract def foo
1

abstract def foo(x)

def foo var; end

def foo var
 end

def foo &block ; end

def foo &block : Int -> Double ; end

def foo @var, &block; end

def foo @@var, &block; end

def foo *y; 1; end

def foo(x : U) forall U; end

def foo(x : U) forall T, U; end

def foo(x : U) : Int32 forall T, U; end

def foo(x : U) forall; end

def foo(x : U) forall U,; end

def []; end

def []?; end

def []=(value); end

def self.[]; end

def self.[]?; end

def [](x); end

fun foo(x : Int32) : Int64
x
end

fun Foo : Int64
end

def foo : Int32
1
end

def foo(x) : Int32
1
end

abstract def foo : Int32

abstract def foo(x) : Int32

def foo(x = / /); end

def =~; end

def foo(x); end; x

def foo
1
end
if 1
end

fun foo : Int32; 1; end; 2

def foo(x, *y); 1; end

def foo(x, *y : Int32); 1; end

def foo(*y : *T); 1; end

private def foo; end

protected def foo; end

def `(cmd); 1; end

def foo(bar = 1
); 2; end

def foo 1; end

def foo x y; end

def foo(x, x); end

def foo(*x, **x); end

def foo(*x, &x); end

def foo(**x, &x); end

def foo(x, **x); end

def =
end

def foo; A = 1; end

def foo(x: Int32); end

def foo(x :Int32); end

def f end

fun foo
class

fun foo
Foo = 1

def foo:String
end

def foo :String
end

def foo():String
end

def foo() :String
end

def !; end

def self.!; end

def is_a?; end

def self.is_a?; end

def as; end

def self.as; end

def as?; end

def self.as?; end

def responds_to?; end

def self.responds_to?; end

def nil?; end

def self.nil?; end

def foo(*args = 1); end

def foo(**args = 1); end

def foo("bar #{1} qux" y); y; end

def Foo(Int32).bar;end

def foo("bar");end

def foo(x
,y); 1; end