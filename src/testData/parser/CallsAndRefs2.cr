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


a.b +
1


previous_def


previous_def(1, 2, 3)


previous_def 1, 2, 3