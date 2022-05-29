class Foo; end


class Foo
end


class Foo
def foo; end; end


class Foo < Bar; end


class Foo(T); end


class Foo(T1); end


class Foo(Type); end


abstract class Foo; end


abstract struct Foo; end


module Foo(*T); end


class Foo(*T); end


class Foo(T, *U); end


class Foo(*T, *U); end


module Foo; end


module Foo
def foo; end; end


module Foo(T); end


alias Foo = Bar


alias Foo::Bar = Baz


alias Foo?


a = 1; class Foo; @x = a; end


enum Foo; A
B; C
D = 1; end


enum Foo; A = 1; B; end


enum Foo : UInt16; end


enum Foo; def foo; 1; end; end


enum Foo; A = 1
def foo; 1; end; end


enum Foo; A = 1
def foo; 1; end
def bar; 2; end
end


enum Foo; A = 1
def self.foo; 1; end
end


enum Foo::Bar; A = 1; end


enum Foo; @@foo = 1
 A 
 end


enum Foo; private def foo; 1; end; end


enum Foo; protected def foo; 1; end; end


enum Foo; @[Bar]; end


enum Foo; A B; end


enum Foo
  A,   B,   C
end


class Foo
  def bar
    print as Foo
  end
end


class Foo(T, T); end


enum Foo < UInt16; end


class Foo; require "bar"; end


module Foo; require "bar"; end


def foo; require "bar"; end


class Foo(T U)


class Foo(T, U V)


class Foo(X
,Y); 1; end


annotation Foo; end


annotation Foo

end


annotation Foo::Bar

end


annotation Foo
end
require "bar"


enum Foo; A; def bar; end; end


class Foo(); end


class Foo(x); end