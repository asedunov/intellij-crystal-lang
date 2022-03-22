foo

foo()

foo(1)

foo 1

foo 1;

foo 1, 2

foo (1 + 2), 3

foo(1 + 2)

foo -1.0, -2.0

foo(
1)

::foo

foo + 1

foo +1

foo +1.0

foo +1_i64

foo = 1; foo +1

foo = 1; foo(+1)

foo = 1; foo -1

foo = 1; foo(-1)

foo = 1; b = 2; foo -b

foo = 1; b = 2; foo +b

def foo(x)
 x
end; foo = 1; b = 2; foo -b

def foo(x)
 x
end; foo = 1; b = 2; foo +b

f.[]= do |a| end

f.[]= { |bar| }

x { |*a, *b| }

foo(&block)

foo &block

a.foo &block

a.foo(&block)

foo(&.block)

foo &.block

foo &./(1)

foo &.%(1)

foo &.block(1)

foo &.+(1)

foo &.bar.baz

foo(&.bar.baz)

foo &.block[0]

foo &.block=(0)

foo &.block = 0

foo &.block[0] = 1

foo &.[0]

foo &.[0] = 1

foo(&.is_a?(T))

foo(&.!)

foo(&.responds_to?(:foo))

foo &.each {
}

foo &.each do
end

foo &.@bar

foo(&.as(T))

foo(&.as(T).bar)

foo &.as(T)

foo &.as(T).bar

foo(&.as?(T))

foo(&.as?(T).bar)

foo &.as?(T)

foo &.as?(T).bar

foo(
  &.block
)

foo.[0]

foo.[0] = 1

foo(a: 1, b: 2)

foo(1, a: 1, b: 2)

foo a: 1, b: 2

foo 1, a: 1, b: 2

foo 1, a: 1, b: 2
1

foo(a: 1
)

foo(
a: 1,
)

foo("foo bar": 1, "baz": 2)

foo "foo bar": 1, "baz": 2

foo(Foo: 1, Bar: 2)

x.foo(a: 1, b: 2)

x.foo a: 1, b: 2

x[a: 1, b: 2]

x[a: 1, b: 2,]

x[{1}]

x[+ 1]

foo(a: 1, &block)

foo a: 1, &block

foo a: b(1) do
end

Foo.bar x.y do
end

x = 1; foo x do
end

x = 1; foo x { }

x = 1; foo x {
}

foo x do
end

foo x, y do
end

foo(bar do
end)

foo(bar { })

(bar do
end)

(foo bar do
end)

(baz; bar do
end)

(bar {})

1.x; foo do
end

x = 1; foo.bar x do
end

foo do
//
end

foo x do
//
end

foo(x) do

end

foo !false

foo.bar.baz

f.x Foo.new

f.x = Foo.new

f.x = - 1

foo do; 1; end

foo do |a|; 1; end

foo { 1 }

foo { |a| 1 }

foo { |a, b| 1 }

foo { |a, b, | 1 }

1.foo do; 1; end

a b() {}

foo(&block) {}

foo { |a, (b, c), (d, e)| a; b; c; d; e }

foo { |(_, c)| c }

foo { |(_, c, )| c }


foo { |a b| }


foo { |(a b)| }


puts _


x = 2; foo do bar x end


puts ::foo


Int


self


@foo


@foo = 1


-@foo


var.@foo


var.@foo.@bar


@@foo


@@foo = 1


-@@foo


call @foo.bar


call "foo"


def foo; end; if false; 1; else; 2; end


A.new("x", B.new("y"))


foo.bar [1]


class Foo; end
while true; end


while true; end
if true; end


(1)
if true; end


begin
1
end
if true; end


Foo::Bar


::A::B


$foo


foo &.baz.qux do
end


foo.!


foo.!.!


foo.!(  )


foo &.!


$~


$~.foo


$0


$1


$1?


foo $1


$~ = 1


$?


$?.foo


foo $?


$? = 1


foo out x; x


foo(out x); x


foo out @x; @x


foo(out @x); @x


foo out _


foo z: out x; x


foo(
1
)


a : Foo


a : Foo | Int32


@a : Foo


@a : Foo | Int32


@@a : Foo


a : Foo = 1


@a : Foo = 1


@@a : Foo = 1


a = uninitialized Foo; a


@a = uninitialized Foo


@@a = uninitialized Foo


1.!(
)


foo
.bar


foo
   .bar

foo
  #comment
  .bar


foo { a = 1 }; a


foo.bar(1).baz


Foo(_)


foo *bar


foo(*bar)


foo x, *bar


foo(x, *bar, *baz, y)


foo.bar=(*baz)


foo.bar= *baz


foo.bar = (1).abs


foo[*baz]


foo[*baz] = 1


foo **bar


foo(**bar)


foo 1, **bar


foo(1, **bar)


foo 1, **bar, &block


foo(1, **bar, &block)


foo **bar, 1


foo(**bar, 1)


foo **bar, *x


foo(**bar, *x)


foo **bar, out x


foo(**bar, out x)


foo(Bar) { 1 }


foo Bar { 1 }


foo(Bar { 1 })


1.[](2)


1.[]?(2)


1.[]=(2, 3)


a @b-1
c


4./(2)


foo[
1
]


foo[
foo[
1
]
]


my_def def foo
loop do
end
end


foo(*{1})


foo *{1}


a.b/2


a.b /2/


a.b / 2


a/b


T/1


T::U/1


::T/1


foo begin
bar do
end
end


foo 1.bar do
end


return 1.bar do
end


call(foo : A, end : B)


call foo : A, end : B


Foo.foo(count: 3).bar { }


Foo?


Foo::Bar?


Foo(T)?


Foo??


foo bar.baz(1) do
end


foo.Bar


foo(bar:"a", baz:"b")


foo(bar:a, baz:b)


def foo(x = 1, y); end


foo {1, 2}


{"x": [] of Int32,
}
1.foo(


foo x: 1, x: 1


foo { |x, x| }


foo { |x, (x)| }


foo { |(x, x)| }


foo 1,


if 1
  foo 1,
end


foo.responds_to?


foo.||


foo.&&


foo.||()


foo.&&()


foo &.||


foo &.&&


foo &.||()


foo &.&&()


foo(1
,2)


foo(a: 1
,b: 2)


foo { 1 + 2 }; 1


foo do
  1 + 2
end; 1


foo = 1; foo a: 1


foo = 1; foo {}


foo = 1; foo &x


あ.い, う.え.お = 1, 2


typeof(xyz = 1); xyz +1


A ? 1 : 0


A? 1 : 0