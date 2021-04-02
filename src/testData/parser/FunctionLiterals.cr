foo = 1; ->foo.[](Int32)

foo = 1; ->foo.[]=(Int32)

->Foo.[](Int32)

->Foo.[]=(Int32)

-> do end

-> { }

->() { }

->(x : Int32) { }

->(x : Int32) { x }

->(x) { x }

x = 1; ->{ x }

f ->{ a do
 end
 }

->foo

->Foo.foo

->Foo::Bar::Baz.foo

->foo(Int32, Float64)

foo = 1; ->foo.bar(Int32)

->foo(Void*)

call ->foo

[] of ->

->foo=

foo = 1; ->foo.foo=

->@foo.foo

->@@foo.foo

foo &->bar

a { |x| x } / b

->(x : Int32, x : Int32) {}

{1, ->{ |x| x } }

{1, ->do
|x| x
end }

->(x y) { }

->(x, y z) { }