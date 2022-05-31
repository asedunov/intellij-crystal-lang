foo(*c)

a.b=*c
a.b= *c
a.b =*c
a.b = *c

a.b=(*c)
a.b =(*c)
a.b= (<error descr="Splat argument is not allowed here">*</error>c)
a.b = (<error descr="Splat argument is not allowed here">*</error>c)

a.b = *c + 1