# LANGUAGE_LEVEL: 1.3

foo(*c)

a = <error descr="Splat argument is not allowed here">*</error>b
a, a.x = <error descr="Splat argument is not allowed here">*</error>b
a.x, a.y = *b
a =(<error descr="Splat argument is not allowed here">*</error>b)
a = (<error descr="Splat argument is not allowed here">*</error>b)
a[0] = *b
a[0], a = <error descr="Splat argument is not allowed here">*</error>b
a[0], a.x = *b

a.b=*c
a.b= *c
a.b =*c
a.b = *c

a.b=(*c)
a.b =(*c)
a.b= (<error descr="Splat argument is not allowed here">*</error>c)
a.b = (<error descr="Splat argument is not allowed here">*</error>c)
a.b=( <error descr="Splat argument is not allowed here">*</error>c )
a.b= ( <error descr="Splat argument is not allowed here">*</error>c )

a.b = *c + 1

*a = 1
*a = 1, 2
*_ = 1, 2

*a, b = 1
a, *b = 1
a, *b = 1, 2
*a, b = 1, 2, 3, 4
a, b, *c = 1
a, b, *c = 1, 2
_, *_, _, _ = 1, 2, 3

*a.foo, a.bar = 1
a.foo, *a.bar = 1

<error descr="Splat argument is not allowed here">*</error>a
<error descr="Splat argument is not allowed here">*</error>a if true
<error descr="Splat argument is not allowed here">*</error>a if <error descr="This expression is not allowed as assignment left-hand side">true</error> = 2
*a, <error descr="This expression is not allowed as assignment left-hand side">1</error> = 2
*<error descr="This expression is not allowed as assignment left-hand side">1</error>, a = 2
*a, <error descr="Splat assignment already specified">*</error>b = 1

<error descr="Multiple assignment count mismatch">*a, b, c, d = 1, 2</error>
<error descr="Multiple assignment count mismatch">a, b, *c, d = 1, 2</error>
<error descr="Multiple assignment count mismatch">*a, b, c, d, e = 1, 2</error>
<error descr="Multiple assignment count mismatch">a, b, c, d, *e = 1, 2, 3</error>

a.x += <error descr="Splat argument is not allowed here">*</error>b
<error descr="Splat argument is not allowed here">*</error>a += b.x