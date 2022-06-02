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

a.b = *c + 1